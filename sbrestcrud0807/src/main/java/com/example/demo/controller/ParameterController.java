package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/params")
public class ParameterController {
    
    // @PathVariable：從 URL 路徑取值
    // 範例：GET /api/params/path/123
    @GetMapping("/path/{id}")
    public ResponseEntity<Map<String, Object>> pathVariable(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
            "type", "PathVariable",
            "value", id,
            "description", "從 URL 路徑取值"
        ));
    }
    // @RequestParam：從 URL 查詢參數取值
    // 範例：GET /api/params/query?name=Alice&age=25
    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> requestParam(
            @RequestParam String name,
            @RequestParam(defaultValue = "10") int age,
            @RequestParam(required = false) String email) {
        
        return ResponseEntity.ok(Map.of(
            "type", "RequestParam",
            "name", name,
            "age", age,
            "email", email != null ? email : "未提供",
            "description", "從 URL 查詢參數取值"
        ));
    }
    @GetMapping("/header")
    public ResponseEntity<Map<String, Object>> requestHeader(
            @RequestHeader(value="Authorization",required = false) String authorization,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        
        return ResponseEntity.ok(Map.of(
            "type", "RequestHeader",
            "authorization", authorization!= null ? authorization : "未提供",
            "userAgent", userAgent != null ? userAgent : "未提供",
            "description", "從 HTTP 標頭取值"
        ));
    }
 // 範例：POST /api/params/body
    @PostMapping("/body")
    public ResponseEntity<Map<String, Object>> requestBody(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "type", "RequestBody",
            "receivedData", body,
            "description", "從 HTTP Request Body 取值"
        ));
    }
}