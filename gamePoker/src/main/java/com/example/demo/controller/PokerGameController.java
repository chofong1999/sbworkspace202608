package com.example.demo.controller;

import java.util.Map;

import com.example.demo.exception.GameException;
import com.example.demo.poker.GameRoom;
import com.example.demo.service.impl.PokerGameService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/poker")
public class PokerGameController {
    private final PokerGameService service;
    public PokerGameController(PokerGameService service) { this.service = service; }

    @PostMapping("/join")
    public PokerGameService.JoinResult join(@RequestBody JoinRequest request) {
        GameRoom.Mode mode;
        try { mode = GameRoom.Mode.valueOf(request.mode().toUpperCase()); }
        catch (Exception e) { throw new GameException("INVALID_MODE", "模式必須是 PLAYER 或 COMPUTER"); }
        return service.join(request.roomId(), mode);
    }

    @GetMapping("/rooms/{roomId}")
    public PokerGameService.GameView state(@PathVariable String roomId, @RequestHeader("X-Player-Token") String token) {
        return service.view(roomId, token);
    }

    @PutMapping("/rooms/{roomId}/selection")
    public PokerGameService.GameView select(@PathVariable String roomId, @RequestHeader("X-Player-Token") String token,
            @RequestBody SelectionRequest request) { return service.select(roomId, token, request.choices()); }

    @PostMapping("/rooms/{roomId}/confirm")
    public PokerGameService.GameView confirm(@PathVariable String roomId, @RequestHeader("X-Player-Token") String token) {
        return service.confirm(roomId, token);
    }

    @PostMapping("/rooms/{roomId}/next-round")
    public PokerGameService.GameView nextRound(@PathVariable String roomId, @RequestHeader("X-Player-Token") String token) {
        return service.nextRound(roomId, token);
    }

    @PostMapping("/rooms/{roomId}/restart")
    public PokerGameService.GameView restart(@PathVariable String roomId, @RequestHeader("X-Player-Token") String token) {
        return service.restart(roomId, token);
    }

    @PostMapping("/rooms/{roomId}/auto-select")
    public PokerGameService.GameView autoSelect(@PathVariable String roomId, @RequestHeader("X-Player-Token") String token) {
        return service.autoSelect(roomId, token);
    }

    @DeleteMapping("/rooms/{roomId}/leave")
    public void leave(@PathVariable String roomId, @RequestHeader("X-Player-Token") String token) { service.leave(roomId, token); }

    @ExceptionHandler(GameException.class)
    public ResponseEntity<Map<String, String>> gameError(GameException e) {
        return ResponseEntity.badRequest().body(Map.of("code", e.getCode(), "message", e.getMessage()));
    }

    public record JoinRequest(String roomId, String mode) { }
    public record SelectionRequest(Map<Integer, Integer> choices) { }
}
