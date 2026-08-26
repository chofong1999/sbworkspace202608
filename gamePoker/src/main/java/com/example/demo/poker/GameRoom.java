package com.example.demo.poker;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Card;

public class GameRoom {
    public enum Mode { PLAYER, COMPUTER }
    public enum Status { WAITING, PLAYING, ROUND_RESULT, FINISHED }

    private final String id;
    private final Mode mode;
    private final PlayerState[] players = { new PlayerState(), new PlayerState() };
    private final String[] seatTokens = new String[2];
    private final boolean[] connected = new boolean[2];
    private Status status = Status.WAITING;
    private int currentRound = 1;
    private final boolean[] roundConfirmed = new boolean[2];
    private List<RoundResult> results = new ArrayList<>();
    private Integer winner;

    public GameRoom(String id, Mode mode) { this.id = id; this.mode = mode; }
    public String getId() { return id; }
    public Mode getMode() { return mode; }
    public PlayerState[] getPlayers() { return players; }
    public String[] getSeatTokens() { return seatTokens; }
    public boolean[] getConnected() { return connected; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getCurrentRound() { return currentRound; }
    public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }
    public boolean[] getRoundConfirmed() { return roundConfirmed; }
    public List<RoundResult> getResults() { return results; }
    public void setResults(List<RoundResult> results) { this.results = results; }
    public Integer getWinner() { return winner; }
    public void setWinner(Integer winner) { this.winner = winner; }

    public record RoundResult(int round, List<Card> player1Cards, String player1Type,
            List<Card> player2Cards, String player2Type, int winner) { }
}
