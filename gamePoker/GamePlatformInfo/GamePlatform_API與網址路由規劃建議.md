# GamePlatform API 與網址路由規劃建議

- 初次整理：`2026-09-06`
- 最近核對：`2026-09-07`
- 文件性質：團隊討論用規劃草案
- 實作狀態：尚未套用；目前程式仍以既有路由為準

> 本版已依 `gammPlatform` 目前的 Controller、Spring Security 與 WebSocket 設定重新核對。下列內容是「建議的目標契約」，不是現行 API 使用說明；在各組完成程式與測試前，不可直接刪除舊路由。

## 一、目的

目前各模組的網址命名方式不一致，例如：

- User 使用 `/api/user/auth`、`/api/user/player`。
- Board 使用 `/board`，缺少統一的 `/api` 前綴。
- Chat 使用 `/api/create`、`/api/get`、`/api/auth` 等過於通用的網址。
- Game Management 使用 `/api/game-management/games`。
- Poker 正式版使用 `/api/games/poker`，Tjpoker 則使用 `/api/poker`。
- Lobby 使用 `/api/lobby/create-room`、`join-room`、`leave` 等大量動詞。

這些路由目前可以運作，但不容易從網址判斷資源、模組與權限範圍，也容易在後續整合時產生命名衝突。本文件提出一套團隊可以共同討論的整理方向。

## 二、建議的共同原則

1. REST API 統一以 `/api` 開頭。
2. WebSocket 統一以 `/ws` 開頭。
3. 前端頁面網址與 API、WebSocket 分開，例如頁面使用 `/games/poker`，API 使用 `/api/games/poker`。
4. 網址以資源為主，使用複數名詞，例如 `users`、`rooms`、`games`、`questions`。
5. 建立、讀取、修改、刪除優先由 HTTP Method 表達，不在網址重複使用 `create`、`get`、`update`、`delete`。
6. `start`、`confirm` 等無法自然表示成 CRUD 的操作，可以保留為命令型子路徑，並使用 `POST`。
7. 個人操作以 JWT 判定身分並使用 `/me`，不要求前端重複傳遞自己的 `userId` 或 `account`。
8. 角色通常由權限判斷，不把 `player` 當成使用者資源名稱；管理員專用功能才集中在 `/api/admin`。
9. 路徑命名統一使用小寫與 kebab-case，例如 `team-posts`、`operation-logs`。
10. 目前是團隊內部開發階段，可以先不加入 `/v1`；若未來對外提供穩定 API，再統一升級為 `/api/v1/...`。
11. 路徑只放穩定識別值。使用者資源長期以 `userId` 識別；`account`、`username`、座位編號不可當作跨模組永久識別值。
12. 同一個請求重送不應造成重複成員或重複資料。已知完整資源位置的操作（例如「我加入此房間」）優先使用具冪等性的 `PUT`。
13. 統一使用正確的 HTTP 狀態碼，並使用同一種錯誤格式，例如 `code`、`message`、`timestamp`、`path`；不要讓各模組自行回傳互不相容的錯誤字串。
14. 正式部署由 Spring Boot 同源提供前端與 API 時，前端應使用相對網址；CORS 不應再使用 `*` 搭配登入憑證。

## 三、建議的整體結構

```text
/api/auth
/api/users
/api/admin/users
/api/games
/api/admin/games
/api/rooms
/api/board
/api/chat
/api/games/quiz
/api/games/poker
/api/games/rpg
/api/games/turing
/ws/...
```

這個結構描述的是平台提供的資源，不直接複製 Java package、Controller 名稱或畫面名稱。

## 四、現行路由與建議路由對照

### 1. 登入與使用者

| 功能 | 現行路由 | 建議路由 |
|---|---|---|
| 登入 | `POST /api/user/auth/login` | `POST /api/auth/login` |
| 註冊 | `POST /api/user/auth/register` | `POST /api/auth/register` |
| 取得登入身分 | `GET /api/user/auth/me` | `GET /api/auth/me` |
| 讀取個人資料 | `GET /api/user/player/me` | `GET /api/users/me` |
| 修改個人資料 | `PUT /api/user/player/me` | `PATCH /api/users/me` |
| 刪除自己的帳號 | `DELETE /api/user/player/me` | `DELETE /api/users/me` |
| 使用者管理 | `/api/user/admin/users` | `/api/admin/users` |
| 管理後台摘要 | `/api/user/admin/dashboard` | `/api/admin/dashboard` |
| 操作紀錄 | `/api/user/admin/operation-logs` | `/api/admin/operation-logs` |

`/api/auth/me` 與 `/api/users/me` 應有不同責任：

- `/api/auth/me`：確認目前 Token 對應的登入身分與權限。
- `/api/users/me`：讀取或修改完整的個人資料。

目前 `SecurityConfig` 最後仍有 `/** permitAll()`，而管理員規則只明確涵蓋 `/api/user/admin/**`。因此新增 `/api/admin/**` 時，必須在同一次修改中把它加入 `hasRole("ADMIN")` 規則，並放在公開規則之前；否則新管理路由可能落入公開放行。路由改名不能只改 Controller 與前端。

### 2. 遊戲與遊戲管理

| 功能 | 現行路由 | 建議路由 |
|---|---|---|
| 遊戲清單 | `GET /api/game-management/games` | `GET /api/games` |
| 遊戲資料 | `GET /api/game-management/games/{gameId}` | `GET /api/games/{gameId}` |
| 管理遊戲 | `/api/admin/game-management/games` | `/api/admin/games` |
| 遊戲模式 | 依目前 Controller 操作 | `/api/games/{gameId}/modes` |
| 管理遊戲模式 | 依目前 Controller 操作 | `/api/admin/games/{gameId}/modes` |

`game-management` 是程式模組名稱；對前端而言，真正操作的資源是 `games`，因此沒有必要把 `management` 放進公開網址。

### 3. Lobby 與房間

| 功能 | 現行路由 | 建議路由 |
|---|---|---|
| 房間清單 | `GET /api/lobby/rooms` | `GET /api/rooms` |
| 建立房間 | `POST /api/lobby/create-room` | `POST /api/rooms` |
| 房間資料 | `GET /api/lobby/room/{roomId}` | `GET /api/rooms/{roomId}` |
| 修改房間設定 | `PUT /api/lobby/room/{roomId}/settings` | `PATCH /api/rooms/{roomId}` |
| 加入房間 | `POST /api/lobby/join-room` | `PUT /api/rooms/{roomId}/players/me` |
| 離開房間 | `POST /api/lobby/room/{roomId}/leave` | `DELETE /api/rooms/{roomId}/players/me` |
| 踢除玩家 | `POST /api/lobby/room/{roomId}/kick` | `DELETE /api/rooms/{roomId}/players/{userId}` |
| 開始遊戲 | `POST /api/lobby/room/{roomId}/start` | `POST /api/rooms/{roomId}/start` |
| 房主解散房間 | `POST /api/lobby/room/{roomId}/abandon` | `DELETE /api/rooms/{roomId}` |
| 我的進行中房間 | `GET /api/lobby/my-active-rooms` | `GET /api/rooms?mine=true&status=PLAYING` |

對外 API 中，使用者自己的加入與離開操作由 JWT 判定身分，不讓前端同時傳 account、userId 或座位。`PUT .../players/me` 可以安全重送，較不容易因網路重試建立重複成員。房主踢人則使用不可變的 `userId`；現有資料表若仍以 account 關聯，可以先在 Service 內轉換，不必在同一次路由整理中強制改表。

房間狀態名稱也應形成跨模組契約，例如：

```text
WAITING -> PLAYING -> FINISHED -> CLOSED
                    └─> ABORTED
```

Poker、Quiz、RPG 等遊戲只回報結束結果；Room 負責決定平台房間狀態並發出 `ROOM_FINISHED`，Chat 不應自行推測某個遊戲是否已結束。

### 4. Board

Board 建議保留清楚的模組範圍，但補上統一的 `/api` 前綴：

```text
/api/board/team-posts
/api/board/team-posts/{postId}
/api/board/team-posts/{postId}/applications
/api/board/team-posts/{postId}/comments
/api/users/me/favorites
/api/users/me/notifications
```

申請加入隊伍可進一步以資源表示：

```text
POST  /api/board/team-posts/{postId}/applications
PATCH /api/board/applications/{applicationId}
```

`PATCH` 的 body 傳遞 `status`，不要把 `APPROVED`、`REJECTED` 等狀態直接放進網址。收藏與通知中的本人身分同樣由 JWT 判定。

Board 自己的 `/board/auth/login`、`/board/auth/register` 與平台 User 登入功能重複，長期建議統一使用平台 JWT，不再維護第二套公開登入入口。

### 5. Quiz

玩家作答與管理員題庫維護應分開，避免公開考試資料與題目管理權限混在同一路徑：

```text
GET  /api/games/quiz/exam
POST /api/games/quiz/submissions
GET  /api/games/quiz/leaderboard

GET    /api/admin/games/quiz/questions
POST   /api/admin/games/quiz/questions
PUT    /api/admin/games/quiz/questions/{questionId}
DELETE /api/admin/games/quiz/questions/{questionId}
```

若日後需要保存每次作答狀態，可以再將一次作答建模為 `quiz-attempts`，本階段不必為了網址整理先增加額外複雜度。

目前 Quiz 的題目讀取與題庫管理集中在相近路徑。遷移時要先補齊管理員權限測試，避免只改網址卻仍讓一般玩家取得新增、修改或刪除題目的能力。

### 6. Poker 與 Tjpoker

正式 Poker 目前的 `/api/games/poker` 方向合理，可以整理為：

```text
PUT    /api/games/poker/rooms/{roomId}/players/me
GET    /api/games/poker/rooms/{roomId}
PUT    /api/games/poker/rooms/{roomId}/players/me/selection
POST   /api/games/poker/rooms/{roomId}/players/me/confirm
POST   /api/games/poker/rooms/{roomId}/players/me/auto-select
DELETE /api/games/poker/rooms/{roomId}/players/me
```

玩家身分與座位由 JWT 及 Room 資料決定，前端不應傳入可自行修改的 account、userId 或 seat。Poker 內部的遊戲房間以平台 `roomId` 關聯，不另外產生一套對外可見的房號。

若下一輪已由伺服器統一倒數後自動開始，`next-round` 不應提供成任何前端都能呼叫的公開 API；可以保留為後端內部方法。

Tjpoker 是舊版或測試用途，目前使用 `/api/poker` 容易被誤認為正式版本。若暫時仍需保留，可以改放在清楚的內部範圍：

```text
/api/internal/tjpoker/...
```

確認不再使用後再移除，避免正式版與測試版同時占用近似名稱。

### 7. RPG 與圖靈測試

原草案漏列目前已存在的 RPG 與圖靈測試。RPG 已使用合理的模組前綴，可保留：

```text
/api/games/rpg/characters
/api/games/rpg/characters/{characterId}/skills
/api/games/rpg/characters/{characterId}/inventory
/api/games/rpg/battles
/api/games/rpg/battles/{battleId}/actions
```

角色仍須由 JWT 驗證所有權，不能只因前端傳來 `characterId` 就允許操作。圖靈測試目前使用過於通用的 `/api/game`，建議改為：

```text
/api/games/turing/...
```

實際子路徑由該遊戲負責人依其遊戲流程補入最終路由表。

### 8. Chat

`/api/create`、`/api/get`、`/api/auth` 沒有指出操作的是哪一種資源，容易和其他模組衝突。若 Chat 仍需要自己的 session API，可以改為：

```text
POST /api/chat/sessions
GET  /api/chat/sessions/{sessionId}
```

Chat 的登入驗證長期應共用平台 JWT；若只是將平台使用者同步進 Chat，不宜再命名成另一套通用的 `/api/auth`。

如果 Chat session 只是在相同 Spring Boot 程序中保存臨時使用者資料，團隊應先確認它是否仍有存在必要。已全面採用平台 JWT 後，可能只需要房間頻道資源，而不需要公開建立第二套 session。

## 五、WebSocket 路由建議

目前同時存在 `/ws/chat`、`/ws/room/*`、`/ws/games/poker` 與 `/ws/poker`。建議先依責任整理為：

```text
/ws/chat/rooms/{roomId}
/ws/lobby/rooms/{roomId}
/ws/games/poker/rooms/{roomId}
/ws/board
```

目前建議先保留分開的連線：

```text
/ws/chat/rooms/{roomId}
/ws/lobby/rooms/{roomId}
```

原因是 Chat 與 Lobby 的訊息頻率、權限及斷線處理不同。現階段先統一 `roomId`、事件名稱、錯誤格式與房間生命週期，風險低於立即合併 Handler。只有在兩者的驗證方式、連線對象與生命週期經測試證實相同後，才考慮整合成：

```text
/ws/rooms/{roomId}
```

再以訊息中的事件類型區分：

```text
CHAT_MESSAGE
ROOM_UPDATED
GAME_STARTED
ROOM_FINISHED
```

WebSocket 同樣應由平台登入身分辨識使用者，不可相信前端傳來的 account、userId 或 seat。

但瀏覽器原生 `WebSocket` API 不能像 `fetch` 一樣自行設定 `Authorization` Header，因此不能只寫「WebSocket 使用 JWT」而省略傳遞方式。團隊需要在下列方案中統一選一種：

1. **同源 HttpOnly Cookie（正式部署優先）**：瀏覽器連線時自動帶入 Cookie，由握手攔截器驗證。
2. **短效 WebSocket ticket**：先用一般 JWT 呼叫 REST API 取得一次性、短效 ticket，再用 ticket 建立 WebSocket。
3. **query parameter 帶 JWT（僅過渡方案）**：實作簡單，但網址可能進入伺服器紀錄或監控工具，安全性較差。

不可只用 WebSocket close 判定遊戲或房間結束；網路瞬斷與頁面暫停也會關線。結束狀態應由後端 Room 生命週期決定。

## 六、前端網址管理

目前不同前端模組各自組合 `http://{hostname}:8080` 或自行建立 WebSocket 網址，容易出現連接埠、協定與部署環境不一致。

建議建立一份全平台共用的網址工具，例如 `PlatformApi` 或 `ApiClient`，集中負責：

- REST API base URL。
- WebSocket base URL。
- `http`／`https` 與 `ws`／`wss` 的對應。
- JWT Header。
- 共用錯誤處理。

Board 已經有使用 `UserApi.API_BASE` 的情況，可作為集中化的起點，但共用工具不應再以單一模組 `User` 命名。

前端已放入 Spring Boot `static` 後，建議直接使用同源相對網址：

```javascript
fetch('/api/rooms');
```

WebSocket base URL 只需依目前頁面的協定產生 `ws` 或 `wss`，不要硬編碼 `localhost:8080`。前端頁面網址則應與 API 分開記錄；第一階段可繼續使用現有 static 檔案路徑，之後若要改成 `/games/poker` 之類的乾淨網址，再由 Spring MVC 統一轉送。

## 七、建議的修改順序

這是多人合作專案，不建議一次移除全部舊網址。較安全的順序是：

1. 團隊先確認共同命名原則、身分識別方式、WebSocket 驗證方式與最終路由表。
2. 建立遷移清單；每條路由至少記錄「舊路由、新路由、負責模組、前端呼叫處、權限、測試、預計移除版本」。
3. 各組只在自己負責的 Controller 增加新路由，舊路由暫時保留為相容入口；兩個入口呼叫同一個 Service，不能複製兩套邏輯。
4. 逐組更新前端呼叫、Spring Security、CORS 與自動化測試，並確認 `401`、`403`、`404`、`409` 等錯誤行為。
5. 全專案搜尋並確認沒有 HTML、JavaScript、Java、測試及文件再使用舊路由。
6. 至少完成一次跨模組整合測試後，才移除舊路由。

共用設定與跨模組契約必須由相關組員共同確認；不能由單一組直接替其他模組改名。

## 八、需要團隊共同決定的事項

- 是否現在就加入 `/api/v1`；本文件建議目前先不加入。
- `/api/auth/me` 與 `/api/users/me` 的回傳內容界線。
- Room 對外成員識別統一使用 userId 或 account，以及內部資料表是否維持 account。
- Chat 與 Lobby 要共用一條房間 WebSocket，或維持兩條但清楚區分責任。
- WebSocket 採用 HttpOnly Cookie、短效 ticket，或暫時沿用 query JWT。
- Quiz 作答是否需要建立獨立的 attempt 資源。
- Tjpoker 的保留期限與內部路由名稱。
- 新舊 API 並存多久，以及由哪一版正式移除舊路由。

本版建議的預設答案是：對外成員使用 `userId`、Chat 與 Lobby 暫時維持兩條 WebSocket、正式部署優先使用同源 HttpOnly Cookie、目前不建立 quiz-attempts、Tjpoker 移入 internal 並訂出移除日期。

## 九、建議結論

本專案現階段最適合先統一成下列骨架：

```text
/api/auth
/api/users
/api/admin/users
/api/games
/api/admin/games
/api/rooms
/api/board
/api/chat
/api/games/quiz
/api/games/poker
/api/games/rpg
/api/games/turing
/ws/...
```

先統一規則、使用者識別、錯誤格式及 Room 生命週期，再由各組分別修改自己的路由。第一階段不要合併 WebSocket Handler，也不要直接刪除舊入口；採用「新增相容路由 → 更新呼叫端與權限 → 整合測試 → 移除舊路由」的順序，才能在改善命名的同時避免多人分工專案一次性中斷。
