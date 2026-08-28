# Spring Boot 學習筆記 37：JWT 登入、Token 驗證與受保護 API

- 後端範例：`sbjwt0827`
- 前端範例：`jwt-login-app`
- 學習目標：登入後取得 JWT、保存 Token，並在 Authorization Header 中呼叫受保護 API

## 本章新增語法快速表

| 想完成的事情 | 寫法 |
|---|---|
| 產生 JWT | `Jwts.builder()...compact()` |
| 驗證及解析 JWT | `Jwts.parser()...parseClaimsJws(token)` |
| 讀取 Bearer Token | `@RequestHeader("Authorization")` |
| 去除 `Bearer ` 前綴 | `authHeader.substring(7)` |
| 前端保存 Token | `localStorage.setItem(key, token)` |
| Request 帶 JWT | `Authorization: 'Bearer ' + token` |
| 保存只需本分頁工作階段的資料 | `sessionStorage` |

## 1. JWT 流程

```text
帳號密碼
   │ POST /api/user/login
   ▼
後端驗證成功 ──> 簽發 JWT ──> 前端保存 token
                                  │
                                  │ Authorization: Bearer <token>
                                  ▼
                         GET /api/user/protected
                                  │
                         後端驗證簽章與期限
                                  ▼
                              回傳資料
```

JWT 本身不是加密保險箱。Payload 可以被解碼查看；安全性來自簽章可驗證內容是否被竄改，以及 HTTPS 防止傳輸途中被截取。

## 2. 加入套件

課堂範例使用：

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>

<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
```

第二個套件是為舊版 JJWT 在新版 Java 中缺少 JAXB 類別時補上相容性。新專案可改用新版 JJWT 的拆分套件，但不能只替換版本而保留完全相同寫法。

## 3. 產生 Token

```java
public static String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 600_000))
        .signWith(SignatureAlgorithm.HS512, SECRET)
        .compact();
}
```

| 設定 | 本例內容 |
|---|---|
| Subject | 使用者名稱 |
| Issued At | Token 發行時間 |
| Expiration | 目前時間加 10 分鐘 |
| Signature | HS512 加上伺服器密鑰 |

課堂密鑰直接寫在 Java 類別只適合練習；正式環境應放在環境變數或祕密管理服務，且使用足夠長度的隨機密鑰。

## 4. 驗證 Token

```java
public static boolean validateToken(String token) {
    try {
        String username = Jwts.parser()
            .setSigningKey(SECRET)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();

        return username != null;
    } catch (Exception ex) {
        return false;
    }
}
```

簽章錯誤、格式錯誤或已過期都會在解析時拋出例外，本例統一視為無效。

## 5. 登入 API

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
    String username = payload.get("username");
    String password = payload.get("password");

    if (帳號密碼正確) {
        String token = JwtUtility.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token));
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", "帳號或密碼錯誤"));
}
```

Request：

```json
{
  "username": "admin",
  "password": "1234"
}
```

成功 Response：

```json
{
  "token": "eyJ..."
}
```

## 6. 受保護 API 讀取 Bearer Token

```java
@GetMapping("/protected")
public ResponseEntity<?> protectedData(
        @RequestHeader("Authorization") String authHeader) {

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = authHeader.substring(7);

    if (!JwtUtility.validateToken(token)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = jwtUtility.extractUsername(token);
    return ResponseEntity.ok(Map.of("user", username));
}
```

`Bearer ` 共 7 個字元，包含尾端空白，因此 `substring(7)` 會留下純 Token。

## 7. 前端 API 模組

```javascript
const url = 'http://localhost:8080/api/user'

export async function login(username, password) {
  const response = await fetch(`${url}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })

  if (!response.ok) throw new Error('帳號或密碼錯誤')
  return response.json()
}

export async function fetchProtectedData(token) {
  const response = await fetch(`${url}/protected`, {
    headers: { Authorization: `Bearer ${token}` }
  })

  if (response.status === 401) throw new Error('登入已過期')
  if (!response.ok) throw new Error('請求失敗')
  return response.json()
}
```

頁面只負責互動；Request 細節集中在 `src/api/authApi.js`。

## 8. 保存登入資料

```javascript
localStorage.setItem('token', response.token)
sessionStorage.setItem('username', username)
```

| 儲存位置 | 關閉分頁／瀏覽器後 | 本例用途 |
|---|---|---|
| `localStorage` | 仍保留，直到程式主動刪除 | JWT |
| `sessionStorage` | 分頁工作階段結束後清除 | 顯示使用者名稱 |

登出：

```javascript
localStorage.removeItem('token')
sessionStorage.removeItem('username')
```

若網站存在 XSS，JavaScript 可讀取 `localStorage` 中的 JWT；正式系統必須同時考量 XSS 防護，或評估改用 `HttpOnly` Cookie 的驗證架構。

## 9. 啟動與驗證

1. 啟動後端 `sbjwt0827`，預設使用 8080。
2. 在 `jwt-login-app` 執行 `npm install`、`npm run dev`。
3. 以 `admin / 1234` 登入。
4. DevTools → Application → Local Storage 確認有 `token`。
5. 按受保護資料按鈕；Network Request Headers 應出現 `Authorization: Bearer ...`。
6. 把 Token 改壞或等待超過 10 分鐘，受保護 API 應回 401。

## 10. Session 與 JWT 的差異

| 比較 | Session | JWT 課堂範例 |
|---|---|---|
| 主要狀態位置 | 伺服器 | Token 由前端保存 |
| 每次辨識方式 | Session Cookie | Authorization Header |
| 伺服器重啟 | 記憶體 Session 可能消失 | 未過期 Token 仍可驗證，前提是密鑰不變 |
| 主動失效 | 可刪除伺服器 Session | 通常需黑名單、短期限或更新 Token 機制 |

## 11. 本機與老師版本差異

- 老師完整版本另有 `/protected/users`，並使用 `User.java` 組成使用者清單。
- 目前本機 `sbjwt0827` 沒有 `User.java`，Controller 也只保留一般 `/protected`；這不影響本章核心登入、驗證與 Header Token 流程，但不能直接重現「Protected Users」。
- 目前本機前端可完成 build；老師完整版本的額外使用者清單屬於後續延伸，不應在缺檔狀態下描述成已完成。

