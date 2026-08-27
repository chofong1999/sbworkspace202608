package com.example.demo.config;

import java.util.*;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtility {
    private static final String SECRET = "MySecretKey";
    // ↑ 密鑰，用於簽名驗證。實際專案應存放在環境變數或配置檔中

    // 生成 Token
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)           // 設定使用者名稱（Subject）
                .setIssuedAt(new Date())        // 設定發行時間
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                // ↑ 設定過期時間：當前時間 + 10分鐘（600000毫秒）
                .signWith(SignatureAlgorithm.HS512, SECRET)
                // ↑ 使用 HS512 演算法與密鑰進行簽名
                .compact();                     // 壓縮成 JWT 字串
    }

    // 驗證 Token
    public static boolean validateToken(String token) {
        try {
            String name = Jwts.parser()
                    .setSigningKey(SECRET)       // 設定密鑰
                    .parseClaimsJws(token)      // 解析並驗證 Token
                    .getBody()                  // 取得 Payload
                    .getSubject();              // 取得使用者名稱
            return name != null;                // 有使用者名稱表示有效
        } catch (Exception e) {
            System.out.println("validateToken error " + e.getMessage());
            return false;                       // 任何異常都表示無效
        }
    }

    // 從 Token 提取使用者名稱
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
