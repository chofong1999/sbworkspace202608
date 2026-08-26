package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.demo.model.Card;
import com.example.demo.model.Game;
import com.example.demo.model.Player;
import com.example.demo.util.GameTool;
import com.example.demo.util.HandEvaluator;
import com.example.demo.exception.GameException;
import com.example.demo.poker.GameRoom;
import com.example.demo.poker.HandResult;
import com.example.demo.poker.PlayerState;

@Service
public class PokerGameService {
    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final HandEvaluator evaluator;

    public PokerGameService(HandEvaluator evaluator) { this.evaluator = evaluator; }

    public synchronized JoinResult join(String roomId, GameRoom.Mode mode) {
        if (roomId == null || roomId.isBlank()) roomId = "room-1";
        GameRoom room = rooms.computeIfAbsent(roomId, id -> new GameRoom(id, mode));
        if (room.getMode() != mode) throw new GameException("MODE_MISMATCH", "此房間已使用其他遊戲模式");

        int seat;
        if (mode == GameRoom.Mode.COMPUTER) {
            if (room.getConnected()[0]) throw new GameException("ROOM_FULL", "此電腦對戰已有人使用");
            seat = 0;
        } else if (!room.getConnected()[0]) seat = 0;
        else if (!room.getConnected()[1]) seat = 1;
        else throw new GameException("ROOM_FULL", "房間已有兩位玩家");

        String token = UUID.randomUUID().toString();
        room.getSeatTokens()[seat] = token;
        room.getConnected()[seat] = true;
        if (mode == GameRoom.Mode.COMPUTER) room.getConnected()[1] = true;
        if (room.getPlayers()[0].getHand().isEmpty() && (mode == GameRoom.Mode.COMPUTER || room.getConnected()[1])) deal(room);
        return new JoinResult(roomId, token, seat + 1, view(roomId, token));
    }

    public synchronized void leave(String roomId, String token) {
        GameRoom room = requireRoom(roomId);
        int seat = requireSeat(room, token);
        room.getConnected()[seat] = false;
        room.getSeatTokens()[seat] = null;
    }

    public synchronized GameView view(String roomId, String token) {
        GameRoom room = requireRoom(roomId);
        int seat = requireSeat(room, token);
        PlayerState me = room.getPlayers()[seat];
        List<CardView> hand = me.getHand().stream().sorted(cardComparator("SUIT"))
                .map(c -> new CardView(c.id(), c.suit(), c.rank(), c.name(), me.getChoices().getOrDefault(c.id(), 0))).toList();
        List<ResultView> results = room.getResults().stream().map(r -> new ResultView(r.round(),
                r.player1Cards().stream().map(Card::name).toList(), r.player1Type(),
                r.player2Cards().stream().map(Card::name).toList(), r.player2Type(), r.winner())).toList();
        return new GameView(room.getId(), room.getMode().name(), room.getStatus().name(), room.getCurrentRound(), seat + 1,
                room.getConnected()[0], room.getConnected()[1], room.getRoundConfirmed()[0],
                room.getRoundConfirmed()[1], hand, preview(me), results, room.getWinner());
    }

    public synchronized GameView select(String roomId, String token, Map<Integer, Integer> choices) {
        GameRoom room = requirePlaying(roomId);
        int seat = requireSeat(room, token);
        PlayerState player = room.getPlayers()[seat];
        if (room.getRoundConfirmed()[seat]) throw new GameException("ALREADY_CONFIRMED", "本輪已確認，不能再修改");
        Map<Integer, Integer> clean = new HashMap<>();
        for (Card card : player.getHand()) {
            int slot = choices.getOrDefault(card.id(), 0);
            if (slot < 0 || slot > 3) throw new GameException("INVALID_SLOT", "選牌區域錯誤");
            int oldSlot = player.getChoices().getOrDefault(card.id(), 0);
            if (oldSlot > 0 && oldSlot < room.getCurrentRound() && slot != oldSlot)
                throw new GameException("ROUND_LOCKED", "前一輪的牌不能修改");
            if (slot > 0 && slot != room.getCurrentRound() && slot != oldSlot)
                throw new GameException("INVALID_SLOT", "目前只能選擇第 " + room.getCurrentRound() + " 輪");
            clean.put(card.id(), slot);
        }
        player.getChoices().clear();
        player.getChoices().putAll(clean);
        return view(roomId, token);
    }

    public synchronized GameView confirm(String roomId, String token) {
        GameRoom room = requirePlaying(roomId);
        int seat = requireSeat(room, token);
        PlayerState player = room.getPlayers()[seat];
        validateRound(player, room.getCurrentRound());
        room.getRoundConfirmed()[seat] = true;
        if (room.getMode() == GameRoom.Mode.COMPUTER && !room.getRoundConfirmed()[1]) {
            room.getRoundConfirmed()[1] = true;
        }
        if (room.getRoundConfirmed()[0] && room.getRoundConfirmed()[1]) resolveCurrentRound(room);
        return view(roomId, token);
    }

    public synchronized GameView nextRound(String roomId, String token) {
        GameRoom room = requireRoom(roomId);
        requireSeat(room, token);
        if (room.getStatus() != GameRoom.Status.ROUND_RESULT)
            throw new GameException("NOT_ROUND_RESULT", "目前不能進入下一輪");
        room.setCurrentRound(room.getCurrentRound() + 1);
        room.getRoundConfirmed()[0] = false;
        room.getRoundConfirmed()[1] = false;
        room.setStatus(GameRoom.Status.PLAYING);
        return view(roomId, token);
    }

    public synchronized GameView restart(String roomId, String token) {
        GameRoom room = requireRoom(roomId);
        requireSeat(room, token);
        deal(room);
        return view(roomId, token);
    }

    public synchronized GameView autoSelect(String roomId, String token) {
        GameRoom room = requirePlaying(roomId);
        int seat = requireSeat(room, token);
        if (room.getRoundConfirmed()[seat]) throw new GameException("ALREADY_CONFIRMED", "本輪已確認，不能再修改");
        if (room.getCurrentRound() != 1) throw new GameException("AUTO_SELECT_STARTED", "自動選牌需在第一輪確認前使用");
        autoChooseAll(room.getPlayers()[seat]);
        return view(roomId, token);
    }

    public synchronized boolean tokenIsValid(String roomId, String token) {
        try { requireSeat(requireRoom(roomId), token); return true; }
        catch (GameException e) { return false; }
    }

    private void deal(GameRoom room) {
        Game game = new Game();
        for (PlayerState player : room.getPlayers()) {
            player.getHand().clear(); player.getChoices().clear(); player.setConfirmed(false);
        }
        for (int i = 0; i < 15; i++) {
            Card playerOneCard = new Card(); playerOneCard.copy(game.getPlayers()[0].getHand()[i]);
            Card playerTwoCard = new Card(); playerTwoCard.copy(game.getPlayers()[1].getHand()[i]);
            room.getPlayers()[0].getHand().add(playerOneCard);
            room.getPlayers()[1].getHand().add(playerTwoCard);
        }
        room.setResults(new ArrayList<>()); room.setWinner(null); room.setCurrentRound(1);
        room.getRoundConfirmed()[0] = false; room.getRoundConfirmed()[1] = false;
        room.setStatus(GameRoom.Status.PLAYING);
        if (room.getMode() == GameRoom.Mode.COMPUTER) autoChooseAll(room.getPlayers()[1]);
    }

    private void validateRound(PlayerState player, int round) {
        int need = round == 1 ? 3 : 5;
        if (cardsOf(player, round).size() != need)
            throw new GameException("INVALID_SELECTION", "第 " + round + " 輪需要選擇 " + need + " 張牌");
    }

    private Map<Integer, PreviewView> preview(PlayerState player) {
        Map<Integer, PreviewView> answer = new LinkedHashMap<>();
        int[] need = { 0, 3, 5, 5 };
        for (int slot = 1; slot <= 3; slot++) {
            List<Card> cards = cardsOf(player, slot);
            HandResult result = cards.size() == need[slot] ? evaluator.evaluate(cards) : null;
            answer.put(slot, new PreviewView(cards.size(), need[slot], result == null ? null : result.type()));
        }
        return answer;
    }

    private void resolveCurrentRound(GameRoom room) {
        int round = room.getCurrentRound();
        List<Card> one = cardsOf(room.getPlayers()[0], round), two = cardsOf(room.getPlayers()[1], round);
        HandResult a = evaluator.evaluate(one), b = evaluator.evaluate(two);
        int compare = a.compareTo(b), roundWinner = compare > 0 ? 1 : compare < 0 ? 2 : 0;
        List<GameRoom.RoundResult> results = new ArrayList<>(room.getResults());
        results.add(new GameRoom.RoundResult(round, one, a.type(), two, b.type(), roundWinner));
        room.setResults(results);
        if (round < 3) room.setStatus(GameRoom.Status.ROUND_RESULT);
        else {
            long oneWins = results.stream().filter(r -> r.winner() == 1).count();
            long twoWins = results.stream().filter(r -> r.winner() == 2).count();
            room.setWinner(oneWins > twoWins ? 1 : twoWins > oneWins ? 2 : 0);
            room.setStatus(GameRoom.Status.FINISHED);
        }
    }

    private void autoChooseAll(PlayerState state) {
        Player player = new Player();
        for (int i = 0; i < state.getHand().size(); i++) player.getHand()[i].copy(state.getHand().get(i));
        int[] choice = new int[52];
        GameTool.autoChooseBest(player, choice);
        state.getChoices().clear();
        for (Card card : state.getHand()) state.getChoices().put(card.id(), choice[card.id()]);
    }

    private List<Card> cardsOf(PlayerState player, int slot) {
        return player.getHand().stream().filter(c -> player.getChoices().getOrDefault(c.id(), 0) == slot)
                .sorted(Comparator.comparing(Card::highRank).reversed().thenComparing(Comparator.comparing(Card::suitStrength).reversed())).toList();
    }

    private Comparator<Card> cardComparator(String sort) {
        if ("POINT".equals(sort)) return Comparator.comparing(Card::highRank).thenComparing(Card::suitStrength);
        return Comparator.comparing(Card::suitStrength).reversed().thenComparing(Card::highRank);
    }

    private GameRoom requireRoom(String id) {
        GameRoom room = rooms.get(id);
        if (room == null) throw new GameException("ROOM_NOT_FOUND", "找不到房間");
        return room;
    }
    private GameRoom requirePlaying(String id) {
        GameRoom room = requireRoom(id);
        if (room.getStatus() != GameRoom.Status.PLAYING) throw new GameException("NOT_PLAYING", "目前不是選牌階段");
        return room;
    }
    private int requireSeat(GameRoom room, String token) {
        for (int i = 0; i < 2; i++) if (token != null && token.equals(room.getSeatTokens()[i])) return i;
        throw new GameException("INVALID_PLAYER", "玩家連線已失效，請重新加入");
    }

    public record JoinResult(String roomId, String token, int seat, GameView game) { }
    public record CardView(int id, String suit, int rank, String name, int slot) { }
    public record PreviewView(int count, int need, String type) { }
    public record ResultView(int round, List<String> player1Cards, String player1Type,
            List<String> player2Cards, String player2Type, int winner) { }
    public record GameView(String roomId, String mode, String status, int currentRound, int seat, boolean player1Connected,
            boolean player2Connected, boolean player1Confirmed, boolean player2Confirmed, List<CardView> hand,
            Map<Integer, PreviewView> preview, List<ResultView> results, Integer winner) { }
}
