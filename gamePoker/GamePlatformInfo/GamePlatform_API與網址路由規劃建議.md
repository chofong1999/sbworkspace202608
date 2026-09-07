# GamePlatform API 與網址路由規劃建議

- 整理日期：`2026-09-06`
- 文件性質：團隊討論用規劃草案
- 實作狀態：尚未套用；目前程式仍以既有路由為準

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
| 加入房間 | `POST /api/lobby/join-room` | `POST /api/rooms/{roomId}/players/me` |
| 離開房間 | `POST /api/lobby/room/{roomId}/leave` | `DELETE /api/rooms/{roomId}/players/me` |
| 踢除玩家 | `POST /api/lobby/room/{roomId}/kick` | `DELETE /api/rooms/{roomId}/players/{account}` |
| 開始遊戲 | `POST /api/lobby/room/{roomId}/start` | `POST /api/rooms/{roomId}/start` |
| 我的進行中房間 | `GET /api/lobby/my-active-rooms` | `GET /api/rooms?mine=true&status=active` |

房間內部目前以 account 核對成員的實作可以另外討論；對外 API 中，使用者自己的加入與離開操作仍建議由 JWT 判定身分，不必讓前端同時傳 account 與 userId。

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

### 6. Poker 與 Tjpoker

正式 Poker 目前的 `/api/games/poker` 方向合理，可以整理為：

```text
POST   /api/games/poker/rooms/{roomId}/join
GET    /api/games/poker/rooms/{roomId}
PUT    /api/games/poker/rooms/{roomId}/selection
POST   /api/games/poker/rooms/{roomId}/confirm
POST   /api/games/poker/rooms/{roomId}/auto-select
DELETE /api/games/poker/rooms/{roomId}/players/me
```

若下一輪已由伺服器統一倒數後自動開始，`next-round` 不應提供成任何前端都能呼叫的公開 API；可以保留為後端內部方法。

Tjpoker 是舊版或測試用途，目前使用 `/api/poker` 容易被誤認為正式版本。若暫時仍需保留，可以改放在清楚的內部範圍：

```text
/api/internal/tjpoker/...
```

確認不再使用後再移除，避免正式版與測試版同時占用近似名稱。

### 7. Chat

`/api/create`、`/api/get`、`/api/auth` 沒有指出操作的是哪一種資源，容易和其他模組衝突。若 Chat 仍需要自己的 session API，可以改為：

```text
POST /api/chat/sessions
GET  /api/chat/sessions/{sessionId}
```

Chat 的登入驗證長期應共用平台 JWT；若只是將平台使用者同步進 Chat，不宜再命名成另一套通用的 `/api/auth`。

## 五、WebSocket 路由建議

目前同時存在 `/ws/chat`、`/ws/room/*`、`/ws/games/poker` 與 `/ws/poker`。建議先依責任整理為：

```text
/ws/chat/rooms/{roomId}
/ws/lobby/rooms/{roomId}
/ws/games/poker/rooms/{roomId}
/ws/board
```

如果 Chat 與 Lobby 的連線對象、驗證方式及生命週期完全相同，也可以整合成：

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

WebSocket 同樣應由 JWT 辨識使用者，不建議在網址或 query parameter 再傳可被自行修改的 account、userId 或 seat。

## 六、前端網址管理

目前不同前端模組各自組合 `http://{hostname}:8080` 或自行建立 WebSocket 網址，容易出現連接埠、協定與部署環境不一致。

建議建立一份全平台共用的網址工具，例如 `PlatformApi` 或 `ApiClient`，集中負責：

- REST API base URL。
- WebSocket base URL。
- `http`／`https` 與 `ws`／`wss` 的對應。
- JWT Header。
- 共用錯誤處理。

Board 已經有使用 `UserApi.API_BASE` 的情況，可作為集中化的起點，但共用工具不應再以單一模組 `User` 命名。

## 七、建議的修改順序

這是多人合作專案，不建議一次移除全部舊網址。較安全的順序是：

1. 團隊先確認本文件的共同命名原則與最終路由表。
2. 各組只在自己負責的 Controller 增加新路由，舊路由暫時保留為相容入口。
3. 逐組更新前端呼叫、Spring Security、CORS 與測試。
4. 全專案搜尋並確認沒有程式再使用舊路由。
5. 最後才移除舊路由。

共用設定與跨模組契約必須由相關組員共同確認；不能由單一組直接替其他模組改名。

## 八、需要團隊共同決定的事項

- 是否現在就加入 `/api/v1`；本文件建議目前先不加入。
- `/api/auth/me` 與 `/api/users/me` 的回傳內容界線。
- Room 對外成員識別統一使用 userId 或 account，以及內部資料表是否維持 account。
- Chat 與 Lobby 要共用一條房間 WebSocket，或維持兩條但清楚區分責任。
- Quiz 作答是否需要建立獨立的 attempt 資源。
- Tjpoker 的保留期限與內部路由名稱。
- 新舊 API 並存多久，以及由哪一版正式移除舊路由。

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
/ws/...
```

先統一規則及責任邊界，再由各組分別修改自己的路由；這樣可以改善現有命名，同時避免一次性改動導致其他組員的前端、權限設定與測試全部中斷。
