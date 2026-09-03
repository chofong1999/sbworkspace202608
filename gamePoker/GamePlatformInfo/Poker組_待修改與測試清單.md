# Poker 組待修改與測試清單

- 更新日期：2026-09-03
- 正式程式 repository：`GamePlatform_Project`
- 檢查基準：`main 19d064f`
- 負責範圍：Poker、Tjpoker、game/management、上述模組前端及直接對應測試
- 目前修改：尚未 commit

> 程式路徑均為 GamePlatform repository 相對路徑，不依賴工作者所在電腦。其他組員需要確認的問題另見 `GamePlatform整合_各組待確認與修正事項.md`。
>
> 最近三次檢查的完整去重分析與修正方向，以 `三次檢查問題分析與修正方案.md` 為準。

## Checklist（對應下方同編號章節）

- [x] 1. 移除「重新開始」完整前後端路徑。
- [x] 2. 整理遊戲操作按鈕與結束畫面。
- [x] 3. 修正真人玩家的單輪自動選牌。
- [x] 4. 統一三秒伺服器倒數，避免每次輪詢重畫手牌。
- [x] 5. 補齊並通過 Poker／Tjpoker 直接測試及前端靜態檢查。
- [ ] 6. 修正離場請求、WebSocket 關線與輪詢備援的衝突。
- [ ] 7. 取得 Lobby 契約後完成房間結束、返回大廳與記憶體 session 清理。
- [ ] 8. 確認停用中的 Tjpoker 測試版是否保留，以及 Chat 是否仍應載入它。
- [ ] 9. 完成 Poker／Tjpoker 雙玩家實際瀏覽器流程驗證。
- [ ] 10. 讓完整 Maven 測試全綠；目前受 User／Board 跨組測試資料衝突阻擋。
- [x] 11. 電腦 AI 維持現有演算法與難度。
- [x] 12. GameMode `modeId` 已由 `IDENTITY` 改成 `AUTO`。

## 1. 移除「重新開始」

已從 Poker／Tjpoker 移除：

- 前端按鈕與 `restart()`。
- Controller 的 `/rooms/{roomId}/restart` endpoint。
- Service interface／implementation 的 `restart()`。

任一玩家不再能讓同一 roomId 直接開始第二局。正式設計仍是完成一局後返回平台大廳，需要再玩時建立新房間。

## 2. 操作按鈕與結束畫面

- 「清除本輪」已改為「清空出牌區」。
- 常用操作保留為排序、清空、自動選牌、確認出牌。
- 中途離開會先詢問確認。
- `FINISHED` 後隱藏選牌操作，只保留「返回遊戲大廳／離開遊戲」。
- 平台房間目前返回 `Lobby/jquery_lobby.html`；Lobby Room 的結束與刪除仍屬第 7 項契約問題。

## 3. 真人玩家單輪自動選牌

新增 `GameTool.auto_choose_current_round(...)`，Poker 與 Tjpoker 行為一致：

1. 保留以前已完成輪次。
2. 清除目前輪次及舊版可能殘留的未來輪次選擇。
3. 從剩餘手牌找出目前輪次戰力最高的牌組。
4. 第一輪只放 3 張，第二、三輪各放 5 張。
5. 清空後可再次使用，且第二、三輪也能使用。
6. 本輪確認後仍不可修改。

沿用既有 `find_best_cards(...)` 與 `Round.determine_hand_strength()`；沒有改寫牌型或電腦 AI。

## 4. 三秒伺服器倒數與畫面刷新

- Poker 與 Tjpoker 都由後端保存 `roundResultEndsAt`，結果停留時間統一為 3000 ms。
- 前端只顯示後端回傳的剩餘毫秒，不再各自啟動四秒換輪計時器。
- 到期後由後端在下一次狀態請求中推進輪次，兩位玩家以同一個伺服器時間為準。
- 輪詢仍約每 800 ms 取得狀態，但狀態簽章忽略單純倒數變化；倒數變化只更新文字，不會重建手牌 DOM，因此不再出現約每秒手牌刷新／閃動。

## 5. 已完成驗證

### 後端

- Poker 直接測試：15 tests，全部通過。
- 新增 Tjpoker service 測試，確認三秒倒數、提前換輪被拒絕，以及真人只選目前輪次。
- 使用獨立 SQLite 執行完整 Maven：34 tests，29 passed，5 errors。
- 5 個 errors 全在 `BoardSessionIntegrationTests`，原因是測試 account／password 含 `-`，但 `UserService` 只允許英數字；Poker／Tjpoker 與主 context test 均通過。

### 前端

- Poker／Tjpoker JavaScript 通過 `node --check`。
- 兩個 Poker JavaScript 通過限定範圍 ESLint。
- Vite build 可執行，但只建置根 React 範例，沒有包含多頁式遊戲頁；此為前端共用設定問題，已記錄在跨組文件。

## 6. 離場與斷線備援

- 離場不再送出 DELETE 後固定等待 30 ms 就跳頁；改為請求完成後再導向，避免瀏覽器在跳頁時取消離場請求。
- 前端雖在 WebSocket 斷線後保留輪詢並顯示備援訊息，但重新檢查發現後端 `afterConnectionClosed()` 會立刻呼叫 `leave()` 並清除 seat token，後續輪詢實際會失效。
- 正常離場同時關閉 WebSocket 並送 DELETE，兩條路徑可能重複呼叫 `leave()`；目前尚未做成冪等操作。
- WebSocket 目前只送出 `connected`，沒有傳送遊戲狀態；實際同步仍完全依賴 800 ms REST 輪詢。

因此本項重新標記為未完成。修正方向與其他新發現詳見 `三次檢查問題分析與修正方案.md` 的 A7－A8。

## 7. Lobby 房間生命週期（待跨組契約）

目前 Lobby Room 與 Poker `GameRoom` 是兩份狀態。Poker `leave()` 只清 connected／token，`FINISHED` 也尚未更新或刪除 Lobby Room，Poker 記憶體 room 亦未移除。

需要與 Lobby 組確認：

- 正常結束、中途離開、房主離開時由哪一方更新或刪除 Lobby Room。
- Poker 何時可以安全移除記憶體 `GameRoom`。
- 返回 Lobby 後如何避免舊 roomId 再次加入同一局。

未取得契約前不修改 Lobby 組檔案。

## 8. Tjpoker 測試版定位（待確認）

- Game Management 將 `TJPOKER` 標記為停用的「測試版」，正式平台使用 `POKER`。
- Tjpoker 仍維持另一套 `/api/poker`、`/ws/poker` 與未驗證的 standalone join 契約。
- Chat 主畫面目前預設 iframe 卻指向 `Games/tjpoker/poker_client.html`，所以實際畫面可能仍在使用停用測試版。

在決定刪除、保留或整併 Tjpoker 前，不把它誤當成正式 Lobby Poker 契約。

## 9. 尚待實際瀏覽器驗證

- 兩位真人隨機座位及重連維持座位。
- 雙方逐輪確認、三秒結果停留及自動換輪。
- 清空後重新手動／自動選牌。
- 遊戲結束按鈕與返回 Lobby。
- Chat 容器修正後，最右側兩張牌均可點擊。

## 10. 完整測試尚未全綠

目前唯一剩餘錯誤是其他組員範圍的 User／Board 格式契約衝突。刪除 `gameplatform.db` 不會解決，因為該測試使用自己的臨時 SQLite。未取得明確授權前不修改 User 或 Board 檔案。

## 11. 電腦 AI 維持現狀

電腦繼續使用 `GameTool.auto_choose_best()`，一次安排完整三輪。本次不加入難度、隨機失誤或新牌型計算；玩家實測已反映目前難度偏高，因此不提高強度。

## 12. GameMode 主鍵

`GameMode.modeId` 使用 `GenerationType.AUTO`，讓 Hibernate 在 SQLite 上使用相容的序列策略，避免 `IDENTITY` 產生缺少欄位型態的 `mode_id`。既有錯誤 schema 仍需重建一次，之後不應再靠每次刪 DB 才能啟動。

## 修改權責

可直接修改：

```text
backend/src/main/java/com/example/demo/modules/game/management
backend/src/main/java/com/example/demo/modules/game/poker
backend/src/main/java/com/example/demo/modules/game/tjpoker
Poker／Tjpoker 前端
上述模組的直接對應測試
```

未再次取得使用者明確授權前不得修改：Lobby、Chat、Board、User、Quiz、`pom.xml`、shared／共用設定及其他組員檔案。
