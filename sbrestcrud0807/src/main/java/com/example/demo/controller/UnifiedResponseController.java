package com.example.demo.controller;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/unified")
public class UnifiedResponseController {
    
    private final List<Product> products = new ArrayList<>();
    
    public UnifiedResponseController() {
        // 初始化測試資料
        products.add(new Product("產品1", "描述1", new BigDecimal("100"), 10));
        products.add(new Product("產品2", "描述2", new BigDecimal("200"), 20));
        products.add(new Product("產品3", "描述3", new BigDecimal("300"), 30));
    }
    
    // 成功回應
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Product>> successResponse() {
        Product product = products.get(0);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }
 // 成功回應（自訂訊息）
    @GetMapping("/success-message")
    public ResponseEntity<ApiResponse<Product>> successWithMessage() {
        Product product = products.get(0);
        return ResponseEntity.ok(ApiResponse.ok("取得產品成功", product));
    }
}