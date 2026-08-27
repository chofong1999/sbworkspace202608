package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.config.JwtUtility;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin // 允許跨來源請求（CORS）
public class UserController {
    List<Map<String, String>> users = new ArrayList<>();
    // ↑ 模擬使用者資料庫（實際專案應連接資料庫）

    @Autowired
    private JwtUtility jwtUtil;
    // ↑ 自動注入 JwtUtility 實例

    public UserController() {
        // 建構子中初始化模擬使用者
        users.add(Map.of("admin", "1234"));
        users.add(Map.of("guest", "1234"));
        users.add(Map.of("mary", "1234"));
        users.add(Map.of("george", "1234"));
        users.add(Map.of("john", "1234"));
    }

    @GetMapping("/login")
    public ModelAndView showLogin() {
        return new ModelAndView("user");
        // ↑ 回傳 Thymeleaf 模板 user.html
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> payload,
            HttpSession session) {
        String username = payload.get("username");
        String password = payload.get("password");

        // 驗證使用者帳密
        Map<String, String> user = users.stream()
            .filter(m -> password.equals(m.get(username)))
            .findAny()
            .orElse(null);

        if (user != null) {
            session.setAttribute("loginname", username);
            String token = jwtUtil.generateToken(username);
            System.out.println(username + " 登入成功");
            return ResponseEntity.ok(Map.of("token", token));
            // ↑ 回傳 Token 給前端
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("message", "帳號或密碼錯誤"));
            // ↑ 帳密錯誤回傳 401
        }
    }

    // 驗證 Token 的 API
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        if (token != null && JwtUtility.validateToken(token)) {
            return ResponseEntity.ok(Map.of("valid", true, "message", "Token 有效"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("valid", false, "message", "Token 無效或已過期"));
        }
    }

    // 受保護的資源
    @GetMapping("/protected")
    public ResponseEntity<?> getProtectedResource(
            @RequestHeader("Authorization") String authHeader) {
        // 1. 檢查 Authorization Header 是否存在且格式正確
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Missing or invalid Authorization header");
        }

        // 2. 提取 Token（去掉 "Bearer " 前綴）
        String token = authHeader.substring(7);

        // 3. 驗證 Token
        if (JwtUtility.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            return ResponseEntity.ok(Map.of(
                "message", "這是受保護的資料",
                "user", username,
                "timestamp", new Date()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid or expired token");
        }
    }
}
