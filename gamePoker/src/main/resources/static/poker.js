const api = "/api/poker";
const $ = id => document.getElementById(id);
let roomId = "", token = "", game = null, socket = null, pollTimer = null;
let draftChoices = new Map(), saveChain = Promise.resolve(), lastRound = 0;
let selectedOrder = [], sortByPoint = false;

document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(location.search);
  $("roomInput").value = params.get("roomId") || "room-1";
  $("computerButton").onclick = () => join("COMPUTER", newComputerRoomId());
  $("playerButton").onclick = () => join("PLAYER", $("roomInput").value.trim());
  $("clearButton").onclick = clearCurrentRound;
  $("sortButton").onclick = changeSort;
  $("autoButton").onclick = autoSelect;
  $("confirmButton").onclick = confirmRound;
  $("nextButton").onclick = nextRound;
  $("restartButton").onclick = restart;
  $("leaveButton").onclick = leave;
  if (params.get("mode") === "player" && params.get("roomId")) join("PLAYER", params.get("roomId"));
  if (params.get("mode") === "computer") join("COMPUTER", newComputerRoomId());
});

function newComputerRoomId() { return `computer-${Date.now()}-${Math.floor(Math.random() * 1000000)}`; }

async function request(path, options = {}) {
  options.headers = { "Content-Type": "application/json", ...(token ? { "X-Player-Token": token } : {}), ...(options.headers || {}) };
  const response = await fetch(api + path, options);
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: "伺服器連線失敗" }));
    throw new Error(error.message || "操作失敗");
  }
  if (response.status === 204) return null;
  return response.json();
}

async function join(mode, id) {
  if (!id) return showMessage("請輸入房間編號", true);
  try {
    const joined = await request("/join", { method: "POST", body: JSON.stringify({ roomId: id, mode }) });
    roomId = joined.roomId; token = joined.token; game = joined.game; syncDraft();
    $("lobby").classList.add("hidden"); $("game").classList.remove("hidden");
    connectSocket(); render();
    clearInterval(pollTimer); pollTimer = setInterval(refresh, 800);
  } catch (error) { showMessage(error.message, true); }
}

function connectSocket() {
  const protocol = location.protocol === "https:" ? "wss" : "ws";
  socket = new WebSocket(`${protocol}://${location.host}/ws/poker?roomId=${encodeURIComponent(roomId)}&token=${encodeURIComponent(token)}`);
  socket.onopen = () => $("connectionBadge").textContent = "已連線";
  socket.onclose = () => { if (token) $("connectionBadge").textContent = "連線已中斷"; };
}

async function refresh() {
  if (!token) return;
  try {
    const latest = await request(`/rooms/${encodeURIComponent(roomId)}`);
    const roundChanged = game && latest.currentRound !== game.currentRound;
    const handChanged = game && latest.hand.length !== game.hand.length;
    game = latest;
    if (roundChanged || handChanged || lastRound === 0) syncDraft();
    render();
  } catch (error) { showMessage(error.message); }
}

function syncDraft() {
  draftChoices = new Map((game?.hand || []).map(card => [card.id, card.slot]));
  selectedOrder = (game?.hand || []).filter(card => card.slot === game?.currentRound).map(card => card.id);
  lastRound = game?.currentRound || 0;
}

function render() {
  if (!game) return;
  $("roomLabel").textContent = game.mode === "COMPUTER" ? "電腦對戰" : `房間 ${game.roomId}`;
  $("seatLabel").textContent = `你是玩家 ${game.seat} · 第 ${game.currentRound} 輪`;
  seatStatus("player1Status", game.player1Connected, game.player1Confirmed);
  seatStatus("player2Status", game.player2Connected, game.player2Confirmed);
  if (game.status === "WAITING") $("gameMessage").textContent = "等待另一位玩家加入";
  else if (game.status === "PLAYING") $("gameMessage").textContent = ownConfirmed() ? `第 ${game.currentRound} 輪已確認，等待對手` : `第 ${game.currentRound} 輪選牌中`;
  else if (game.status === "ROUND_RESULT") $("gameMessage").textContent = `第 ${game.currentRound} 輪結果已公布`;
  else $("gameMessage").textContent = "遊戲結束";
  renderCards();
  const canEdit = game.status === "PLAYING" && !ownConfirmed();
  $("clearButton").disabled = !canEdit;
  $("sortButton").disabled = !canEdit;
  $("autoButton").disabled = !canEdit || game.currentRound !== 1;
  $("confirmButton").disabled = !canEdit || selectedCount() !== cardsNeeded();
  $("confirmButton").classList.toggle("hidden", game.status === "ROUND_RESULT" || game.status === "FINISHED");
  $("nextButton").classList.toggle("hidden", game.status !== "ROUND_RESULT");
  $("restartButton").classList.toggle("hidden", game.status !== "FINISHED");
  renderResults();
}

function renderCards() {
  const current = game.currentRound;
  document.querySelectorAll(".slot").forEach(slot => slot.classList.toggle("active-slot", Number(slot.dataset.slot) === current));
  for (let slot = 0; slot <= 3; slot++) {
    const cards = orderedCards(game.hand.filter(card => draftChoices.get(card.id) === slot), slot === current);
    const clickable = slot === current && game.status === "PLAYING" && !ownConfirmed();
    $("slot" + slot).innerHTML = cards.map(card => miniCard(card, clickable)).join("");
  }
  document.querySelectorAll(".mini-card.selectable").forEach(card => card.onclick = () => removeCard(Number(card.dataset.id)));
  const available = orderedCards(game.hand.filter(card => draftChoices.get(card.id) === 0));
  $("hand").innerHTML = available.map(card => `<button class="playing-card ${isRed(card) ? "red" : ""}" data-id="${card.id}">${card.name}</button>`).join("");
  const canEdit = game.status === "PLAYING" && !ownConfirmed();
  document.querySelectorAll(".playing-card").forEach(card => { card.disabled = !canEdit; card.onclick = () => selectCard(Number(card.dataset.id)); });
  for (let slot = 1; slot <= 3; slot++) {
    const preview = game.preview[slot];
    const localCount = [...draftChoices.values()].filter(value => value === slot).length;
    $("preview" + slot).textContent = `${localCount} / ${preview.need}${slot === current && preview.type ? " · " + preview.type : ""}`;
  }
}

function selectCard(cardId) {
  if (selectedCount() >= cardsNeeded()) {
    const first = selectedOrder.shift();
    if (first !== undefined) draftChoices.set(first, 0);
  }
  draftChoices.set(cardId, game.currentRound); selectedOrder.push(cardId); queueSave(); render();
}
function removeCard(cardId) { draftChoices.set(cardId, 0); selectedOrder = selectedOrder.filter(id => id !== cardId); queueSave(); render(); }
function clearCurrentRound() {
  for (const [id, slot] of draftChoices) if (slot === game.currentRound) draftChoices.set(id, 0);
  selectedOrder = [];
  queueSave(); showMessage("本輪選牌已清除"); render();
}

function changeSort() {
  sortByPoint = !sortByPoint;
  showMessage(sortByPoint ? "依點數整理手牌" : "依花色整理手牌");
  render();
}

function orderedCards(cards, preserveSelectionOrder = false) {
  if (preserveSelectionOrder) return [...cards].sort((a, b) => selectedOrder.indexOf(a.id) - selectedOrder.indexOf(b.id));
  if (sortByPoint) return [...cards].sort((a, b) => a.rank - b.rank || b.id - a.id);
  return [...cards].sort((a, b) => a.id - b.id);
}

async function autoSelect() {
  try {
    game = await request(`/rooms/${encodeURIComponent(roomId)}/auto-select`, { method: "POST" });
    syncDraft(); showMessage("自動選牌完成"); render();
  } catch (error) { showMessage(error.message); }
}

function queueSave() {
  const snapshot = Object.fromEntries(draftChoices);
  saveChain = saveChain.then(async () => {
    game = await request(`/rooms/${encodeURIComponent(roomId)}/selection`, { method: "PUT", body: JSON.stringify({ choices: snapshot }) });
    render();
  }).catch(error => showMessage(error.message));
  return saveChain;
}

async function confirmRound() {
  if (selectedCount() !== cardsNeeded()) return showMessage(`第 ${game.currentRound} 輪需要選 ${cardsNeeded()} 張牌`);
  $("confirmButton").disabled = true;
  try {
    await saveChain;
    game = await request(`/rooms/${encodeURIComponent(roomId)}/confirm`, { method: "POST" });
    showMessage(game.status === "ROUND_RESULT" || game.status === "FINISHED" ? "雙方已確認，本輪開牌" : "本輪已確認，等待對手");
    render();
  } catch (error) { showMessage(error.message); render(); }
}

async function nextRound() {
  try { game = await request(`/rooms/${encodeURIComponent(roomId)}/next-round`, { method: "POST" }); syncDraft(); showMessage(`開始第 ${game.currentRound} 輪`); render(); }
  catch (error) { showMessage(error.message); }
}
async function restart() {
  try { game = await request(`/rooms/${encodeURIComponent(roomId)}/restart`, { method: "POST" }); syncDraft(); render(); }
  catch (error) { showMessage(error.message); }
}

function renderResults() {
  const panel = $("resultPanel");
  if (!game.results.length) { panel.classList.add("hidden"); return; }
  panel.classList.remove("hidden");
  $("winnerTitle").textContent = game.status === "FINISHED" ? (game.winner === 0 ? "本局平手" : `玩家 ${game.winner} 獲勝`) : `第 ${game.currentRound} 輪結果`;
  $("roundResults").innerHTML = game.results.map(r => `<div class="result-row"><strong>第 ${r.round} 輪</strong><span>玩家一：${r.player1Cards.join(" ")}（${r.player1Type}）</span><span>玩家二：${r.player2Cards.join(" ")}（${r.player2Type}）</span><strong>${r.winner === 0 ? "平手" : "玩家 " + r.winner + " 勝"}</strong></div>`).join("");
}

function seatStatus(id, connected, confirmed) { $(id).textContent = !connected ? "離線／空缺" : confirmed ? "本輪已確認" : "在線"; }
function ownConfirmed() { return game.seat === 1 ? game.player1Confirmed : game.player2Confirmed; }
function cardsNeeded() { return game.currentRound === 1 ? 3 : 5; }
function selectedCount() { return [...draftChoices.values()].filter(value => value === game.currentRound).length; }
function isRed(card) { return card.suit === "♥" || card.suit === "♦" || card.suit === "♡" || card.suit === "♢"; }
function miniCard(card, selectable) { return `<button class="mini-card ${isRed(card) ? "red" : ""} ${selectable ? "selectable" : ""}" data-id="${card.id}" ${selectable ? "" : "disabled"}>${card.name}</button>`; }

function leave() {
  if (!token) return location.replace("poker_client.html");
  const leaveToken = token;
  fetch(`${api}/rooms/${encodeURIComponent(roomId)}/leave`, { method: "DELETE", headers: { "X-Player-Token": leaveToken }, keepalive: true }).catch(() => {});
  token = ""; clearInterval(pollTimer); if (socket) socket.close();
  setTimeout(() => location.replace("poker_client.html"), 30);
}
function showMessage(message, lobby = false) { $(lobby ? "lobbyMessage" : "actionMessage").textContent = message; }
