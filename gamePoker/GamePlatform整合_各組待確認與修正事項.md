# GamePlatform 整合：各組已發現問題

- 更新日期：2026-09-03
- 正式程式 repository：`GamePlatform_Project`
- 檢查基準：`main 19d064f`
- 文件用途：讓各組知道目前發現的問題、問題位置及衝突原因。

> 路徑均為 GamePlatform repository 相對路徑。本文件只指出問題，不替各組決定修改方式，也不列驗收條件。Poker 負責人沒有修改本文所列的其他組員檔案。
>
> 最近三次檢查新增的 Board／Poker ID 命名空間、Board request 身分、Quiz 計分完整性及跨模組刪除關聯等問題，已合併到 `三次檢查問題分析與修正方案.md`；後續討論先讀該文件，本文件保留原先依組別整理的定位資料。

## 問題 Checklist（對應下方同編號章節）

- [ ] 1. Lobby 組：房間與遊戲生命週期、身分、模式限制及 WebSocket 狀態尚未統一。
- [ ] 2. Chat 組：透明容器攔截遊戲點擊，主畫面與 WebSocket／登入流程仍是不同測試版本的組合。
- [ ] 3. User／Board 組：帳密格式與測試資料衝突，Board 主鍵產生器另有 Hibernate 淘汰警告。
- [ ] 4. User／共用安全設定：初始化、API 授權與 JWT secret 仍含開發階段行為。
- [ ] 5. CORS：REST 與 WebSocket 的允許來源分散且不一致。
- [ ] 6. 共用 POM：仍有重複依賴及找不到目前用途的 driver／starter。
- [ ] 7. Quiz 組：考卷會回傳正解，題庫管理公開，富文字內容可直接進入 `innerHTML`。
- [ ] 8. 前端共用：production build、lint 與 API base 尚未涵蓋實際多頁結構。
- [ ] 9. 測試／資料庫：主 context test 會使用開發 SQLite，測試環境未完全隔離。

## 1. Lobby 組

共同根目錄：`backend/src/main/java/com/example/demo/modules/`

### Lobby Room 與 Poker GameRoom 是兩份生命週期

- `lobby/entity/Room.java:23-27`

  Lobby 保存 `modeId`、`hostAccount`，玩家則保存 account；Room 自己另有 `status`。

- `lobby/controller/LobbyController.java:245-267、388-439`

  Lobby 的離房會更新或刪除 Lobby Room，開始遊戲會把 Lobby Room 改成 `PLAYING`。

- `game/poker/service/PokerPlatformRoomService.java:32-41`

  Poker 讀取 Lobby Room，使用 JWT account 核對 `hostAccount`／players。

- `game/poker/service/impl/PokerGameServiceImpl.java:102-107、261-297`

  Poker 離開只清 connected／seat token；第三輪完成只把 Poker `GameRoom` 改成 `FINISHED`，沒有更新或刪除 Lobby Room。

- `game/poker/model/GameRoom.java`

  Poker 另有一份只在記憶體中的房間狀態，仍以同一個 roomId 作為對局識別，而且目前沒有移除時機。

因此正常結束、中途離場、返回大廳及兩份房間資料的清理責任目前不明確，舊 roomId 可能仍指向已結束的對局。

### Lobby 狀態與 Game Management 契約不一致

- `lobby/controller/LobbyController.java:89-98`

  `/api/lobby/status` 固定回傳 `hasActiveGame=false`、`roomId=null`、`gameType=null`，沒有查詢玩家是否已有房間。

- `lobby/controller/LobbyController.java:36-41、50-75、115`

  Lobby 直接注入並使用 `GameRepository`／`GameModeRepository`；game/management 的串接文件則以 `GameManagementService` 與 DTO 作為模組邊界，兩者不一致。

- `lobby/controller/LobbyController.java:112-142`

  建房時 `playerCount` 由 request body 傳入即可覆蓋 GameMode 的 `maxPlayers`，沒有檢查是否介於該模式 min／max 範圍。

- `lobby/controller/LobbyController.java:280-308`

  房間設定可直接替換任意 `modeId` 與 `maxPlayers`，沒有重新核對 mode 所屬 game、是否 enabled、模式 min／max，或目前玩家數。

### Lobby 身分與 WebSocket 仍信任前端字串

- `lobby/controller/LobbyController.java:104-168、245-349、388-429`

  建房、加房、離房、設定、踢人及開始遊戲都從 request body 取得 `hostAccount`／`playerAccount`，沒有用 JWT 登入者核對；在目前全域 `permitAll` 下，呼叫者可以填入其他 account。

- `lobby/server/RoomWebSocketHandler.java:20-29`

  WebSocket 使用 `query.split("=")[1]` 取得 player；缺少等號、多參數或編碼內容時可能解析錯誤，也沒有用 JWT 或 Room players 核對身分。

- `lobby/server/RoomWebSocketHandler.java:35-38`

  關線時只輸出訊息，沒有從 `roomSessions` 移除 session，已關閉連線及空房 map 會繼續保留。

- `lobby/server/RoomWebSocketConfig.java:1、18-20`

  檔案仍有「請改成你的 package 路徑」及測試性註解，WebSocket 允許所有 origin。

## 2. Chat 組

後端根目錄：`backend/src/main/java/com/example/demo/modules/chat/`

前端根目錄：`frontend/src/pages/Chat/`

### 透明容器攔截 Poker 點擊

- `chatclient.html:82-84、141-148`

  關閉聊天視窗時，`pointer-events-none` 只加在內層 chatWindow。外層 `absolute ... z-50 flex` 仍有寬高與 `pointer-events:auto`。

瀏覽器 hit-test 已確認 Poker 最右側兩張牌的位置命中此外層 DIV，而非遊戲 iframe；切換排序後不能點的牌也跟著位置移動，因此是覆蓋位置問題，不是特定牌或 Poker 選牌規則。

### 主畫面仍指向停用的測試 Poker

- `chatclient.html:77`

  主 iframe 預設載入 `../Games/tjpoker/poker_client.html`。Game Management 把 `TJPOKER` 標成停用測試版，正式平台則使用 `Games/poker` 與 JWT／Lobby 契約，入口來源不一致。

### Chat 登入、Session 與 WebSocket 有多份未接合流程

- `controller/ChatAuthController.java:16-36`

  `/api/auth` 使用固定 `admin`／`1234`，且把收到的明文密碼輸出到 console。

- `controller/SessionController.java:11-27`

  `/api/create`、`/api/get` 是公開的 HttpSession 測試 API；共用 SecurityConfig 同時宣告 JWT `STATELESS`。

- `server/ChatWebSocketConfig.java:20-21`

  Spring WebSocket 註冊 `/ws/chat`。

- `server/ChatRoomServer.java:13-16`

  另有 Jakarta `@ServerEndpoint("/ws/chat")` 使用同一路徑及 static `ArrayList`；專案中找不到 `ServerEndpointExporter`，是否實際啟用不明。

- `chatclient.html:228-263`

  主畫面送出訊息只 append 在目前瀏覽器，實際 WebSocket send 被註解，其他玩家不會收到。

- `js/client.js:49-104`

  另一份 client.js 才有 WebSocket 邏輯，但 chatclient.html 沒有載入它；收到的 message 又直接插入 `innerHTML`。

## 3. User／Board 組

主程式根目錄：`backend/src/main/java/com/example/demo/modules/`

測試根目錄：`backend/src/test/java/com/example/demo/modules/`

### 帳號／密碼格式與測試資料衝突

- `user/service/UserService.java:25-26、226-250`

  account 與 password 都必須符合 `^[A-Za-z0-9]+$`，包含 `-` 即被拒絕。

- `board/BoardSessionIntegrationTests.java:46、110`

  建立資料使用 `"u" + UUID.randomUUID()`、密碼 `Session-test-123`，更新又使用 `"renamed-" + UUID.randomUUID()`，全部包含 `-`。

- `board/BoardSessionIntegrationTests.java:63、83`

  `legacy-...` 直接建立 Board Member；`another-account` 是刻意不同的 JWT claim，兩者用途與真正呼叫 UserService 的失敗案例不同。

使用隔離 SQLite 的完整測試得到 34 tests、29 passed、5 errors，五個 error 全在 `BoardSessionIntegrationTests`。此測試有自己的臨時 DB，刪除主程式 `gameplatform.db` 不會解決格式衝突。

### Board 主鍵產生器使用即將移除的 Hibernate API

- `board/entity/Member.java:13-16`
- `board/entity/TeamPost.java:13-16`
- `board/entity/JoinRequest.java:13-16`
- `board/entity/Notification.java:13-16`

  四個 entity 都使用 `org.hibernate.annotations.GenericGenerator`。目前 Maven 編譯警告標示此 API deprecated 且 marked for removal；現在仍可編譯，但後續 Hibernate 升級可能失效。

## 4. User／共用安全設定

共同根目錄：`backend/src/main/`

- `java/com/example/demo/modules/user/database/UserDatabaseInitializer.java:76-101`

  每次啟動都直接新增 account=`admin`，沒有先查詢或忽略重複。資料庫已有 admin 時會得到 `SQLITE_CONSTRAINT_UNIQUE: users.account`；刪 DB 只讓下一次啟動成功，再啟動仍會重現。

- `java/com/example/demo/modules/user/security/SecurityConfig.java:37-49`

  只有 `/api/user/admin/**` 要求 ADMIN；Board 登入 matcher 寫成 `/api/auth/**`，實際路徑是 `/board/auth/**`。接著的 `/**.permitAll()` 使其他所有 API 公開，最後 `anyRequest().authenticated()` 沒有剩餘路徑。

- `java/com/example/demo/modules/game/management/controller/GameAdminManagementController.java:24、39-71`

  管理 API 是 `/api/admin/game-management/**`，不在 `/api/user/admin/**` 下，因此新增、修改、刪除遊戲及模式目前全部落入 `permitAll`。

- `java/com/example/demo/modules/board/controller/*.java`
- `java/com/example/demo/modules/game/quiz/controller/QuestionsController.java`

  Board 操作與 Quiz 題庫異動也落在同一個全域 `permitAll`；前端是否隱藏按鈕不會形成後端授權。

- `resources/application.properties:16`

  JWT secret 是 repository 內固定字串 `gameplatform-jwt-secret-key-2026-change-this-key`，所有使用相同程式碼的環境會共用簽章金鑰。

## 5. CORS

共同根目錄：`backend/src/main/java/com/example/demo/modules/`

- `board/config/CorsConfig.java:8-10`

  全域 `WebMvcConfigurer` 只套 `/board/**`，允許任意 origin pattern、常用 methods 與所有 headers。

- `lobby/controller/LobbyController.java:29`
- `chat/controller/SessionController.java:11`
- `chat/controller/ChatAuthController.java:13`
- `game/management/controller/*.java`
- `game/poker/controller/PokerGameController.java`
- `game/tjpoker/controller/TjpokerGameController.java`

  各 Controller 分別使用 wildcard `@CrossOrigin`；其中 origins、originPatterns 及 credentials 設定並不相同。

- `chat/server/ChatWebSocketConfig.java`
- `lobby/server/RoomWebSocketConfig.java`
- `game/poker/server/PokerWebSocketConfig.java`
- `game/tjpoker/server/TjpokerWebSocketConfig.java`

  各 WebSocket 又各自 `.setAllowedOrigins("*")`。

- `user/security/SecurityConfig.java:24-56`

  共用 SecurityFilterChain 沒有共同 `CorsConfigurationSource`。目前 REST、WebSocket、credentials 與路徑範圍沒有單一一致規則。

## 6. 共用 POM

共同根目錄：`backend/`

- `pom.xml:72-80`

  同時包含 MySQL 與 SQLite runtime driver，但 resources 只找到 SQLite datasource，沒有 MySQL profile。

- `pom.xml:101-110`

  `spring-security-crypto` 與 `spring-boot-starter-security` 同時直接加入；dependency tree 顯示 starter 已帶入相同版本的 crypto。

- `pom.xml:53-57`

  直接加入 `jackson-databind`，其他 Jackson／JJWT 依賴也會帶入，dependency tree 顯示重複。

- `pom.xml:45-48、134-153`

  包含 Thymeleaf 主程式／測試 starter 及 H2 test dependency；目前沒找到 server-side template 使用或 H2 測試 datasource，既有整合測試都使用 SQLite。

- `pom.xml:93、106、140-158`

  仍有「來自 POM 1／POM 2」合併註解，並同時加入完整 Spring Boot test 與多個細分 test starter；目前用途沒有在 POM 中說明。

POM 現在可解析並編譯，但混有確定重複與未找到使用位置的依賴；單看 POM 無法判斷所有項目都可安全移除。

## 7. Quiz 組

後端根目錄：`backend/src/main/java/com/example/demo/modules/game/quiz/`

前端根目錄：`frontend/src/pages/Games/quiz/`

### 考卷 API 直接回傳正解

- `controller/QuestionsController.java:35-43`

  `/exam` 與題庫查詢直接回傳 `Question` entity。

- `model/Question.java:29-34`
- `model/Option.java:30-35`

  Question 會序列化 options，而 Option 的 `isCorrect` 沒有設為只寫或忽略。因此玩家在交卷前即可從考卷 JSON 看到每個正確選項。

### 題庫異動公開，富文字未隔離可信來源

- `controller/QuestionsController.java:53-67`

  新增、修改、刪除題目 endpoint 沒有 Controller 級角色限制，且全域 SecurityConfig 讓它們落入 `permitAll`。

- `service/QuizService.java:78-116`

  title、explanation、optionText 直接保存，未看到 HTML 清理或允許標籤檢查。

- `js/app.js:175、184-187、357、364-377、419-420`

  前端以 Quill HTML 儲存內容，並把 API 回傳的 title／optionText／explanation 直接放入 `innerHTML`。富文字本身可能是設計需求，但寫入 API 公開時，來源不再只是可信管理員，會形成儲存型 HTML／script 注入風險。

## 8. 前端共用

共同根目錄：`frontend/`

### Production build 沒有包含實際多頁畫面

- `index.html:10-11`
- `vite.config.js:5-10`

  唯一入口是 root `src/main.jsx`，沒有多頁 inputs 或複製 `src/pages/**` HTML 的設定。

- `src/App.jsx:4、44-101`

  root App 仍是 Vite／React 範例與社群連結，不是平台主畫面。

實際 `vite build` 只轉換 16 個 module，輸出 root `dist/index.html` 與一組 CSS／JS，沒有 Lobby、Chat、Board、User、Poker、Quiz 頁面。

### ESLint 設定與頁面型態不一致

- `eslint.config.js:8-18`

  對所有 JS／JSX 套同一組 module 規則，只忽略 dist，沒有排除 Quiz 的第三方 Quill，也沒有宣告 jQuery、Quill、UserApi 等頁面 globals。

完整 `eslint .` 得到 261 errors；其中包含第三方檔與未宣告 globals，也混有 Chat、Lobby 原始碼的真正警告，因此目前 lint 數字無法直接代表專案品質。

### API base 分散且固定使用 HTTP 8080

- `src/pages/User/api/userApi.js:5`
- `src/pages/Lobby/jquery_lobby.html:90`
- `src/pages/Games/quiz/js/app.js:2`
- `src/pages/Games/poker/poker.js:2-4`
- `src/pages/Chat/js/client.js:53`

  各頁各自組合 `http://<hostname>:8080` 或 WebSocket URL，沒有共同環境設定。若前端改由 HTTPS 或非 8080 後端部署，會出現 mixed content 或需逐頁修改的情況。

## 9. 測試／資料庫

共同根目錄：`backend/`

- `src/main/resources/application.properties:6、11-12、19`

  預設 datasource 是相對路徑 `jdbc:sqlite:gameplatform.db`，並啟用 `ddl-auto=update`、SQL 輸出與 Board demo data。

- `src/test/java/com/example/demo/GameplatformApplicationTests.java:6-10`

  `@SpringBootTest` 沒有 test profile 或 test datasource。從 backend 執行 Maven test 時會直接開啟 `backend/gameplatform.db`，執行 schema update 與 initializer。

- `src/test/java/com/example/demo/modules/board/BoardSessionIntegrationTests.java:27-33`

  BoardSessionIntegrationTests 另有自己的臨時 SQLite，因此其帳密格式錯誤與主 DB 無關。

未覆寫 datasource 的第一次完整測試曾得到 32 tests、6 errors：五項為 Board 帳密格式衝突，另一項為主 DB 已有 admin 時 initializer 重複新增。改用獨立 SQLite 後，主 context test 通過，完整結果為 34 tests、5 errors，證實測試隔離與 initializer 是彼此獨立的問題。
