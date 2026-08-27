package com.example.demo.service;

import java.util.Map;

import com.example.demo.dto.GameView;
import com.example.demo.dto.JoinResult;
public interface PokerGameService {
    JoinResult join(String roomId, String mode, String playerName);
    void leave(String roomId, String token);
    GameView view(String roomId, String token);
    GameView select(String roomId, String token, Map<Integer, Integer> choices);
    GameView confirm(String roomId, String token);
    GameView nextRound(String roomId, String token);
    GameView restart(String roomId, String token);
    GameView autoSelect(String roomId, String token);
    boolean tokenIsValid(String roomId, String token);
}
