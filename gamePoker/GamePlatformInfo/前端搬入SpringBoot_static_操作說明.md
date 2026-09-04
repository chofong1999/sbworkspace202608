# 前端複製到 Spring Boot `static` 操作說明

歸檔日期：2026-09-04

## 一、目的

目前前端由 VS Code Live Server 提供，例如：

```text
http://127.0.0.1:5500/
```

後端則由 Spring Boot 提供，例如：

```text
http://localhost:8080/
```

這種前後端分開來源的方式，需要額外處理 CORS、固定連接埠、區域網路 IP，以及 WebSocket 的連線網址。

將前端搬入 Spring Boot 的 `static` 後，瀏覽器只需要開啟：

```text
http://localhost:8080/
```

前端頁面、REST API 和 WebSocket 都會使用同一個主機與連接埠，可減少 CORS 和部署網址設定問題。

---

## 二、搬移前準備

1. 確認目前程式可以正常啟動。
2. 將尚未提交的修改確認清楚。
3. 建議先建立一次 Git commit，保留搬移前的狀態。
4. 本次採用複製方式，原本的 `frontend` 目錄與內容保留，不做搬移或刪除。

建議搬移前的 commit 訊息：

```text
Prepare frontend for Spring Boot static migration
```

---

## 三、建立目標資料夾

在以下位置建立 `static` 資料夾：

```text
C:\git\gammPlatform\backend\src\main\resources\static
```

預計完成後的結構：

```text
backend/src/main/resources/static/
├─ index.html
├─ pages/
│  ├─ Chat/
│  ├─ Lobby/
│  ├─ Board/
│  ├─ User/
│  ├─ Shop/
│  └─ Games/
│     ├─ poker/
│     ├─ tjpoker/
│     └─ quiz/
└─ assets/
   └─ Games/
      ├─ poker/
      ├─ tjpoker/
      └─ quiz/
```

Spring Boot 會把 `static` 當成網站根目錄，因此網址不會包含 `static`。

例如：

```text
檔案：static/pages/Chat/chatclient.html
網址：http://localhost:8080/pages/Chat/chatclient.html
```

---

## 四、複製前端檔案

依照下表複製，來源檔案仍保留在原位置：

| 現在的位置 | 搬移後的位置 |
|---|---|
| `frontend/index.html` | `backend/src/main/resources/static/index.html` |
| `frontend/src/pages/` | `backend/src/main/resources/static/pages/` |
| `frontend/src/assets/` | `backend/src/main/resources/static/assets/` |

以下內容不要放入 `static`：

```text
frontend/tests/
frontend/README.md
其他開發文件或測試資料
```

原因是 `static` 裡的檔案可能被瀏覽器直接存取。

為了保留相對路徑的相容性，搬移時應保留 `pages` 底下現有的資料夾分層。

---

## 五、修改首頁路徑

如果首頁目前導向：

```javascript
location.replace("/src/pages/Chat/chatclient.html");
```

搬移後改成：

```javascript
location.replace("/pages/Chat/chatclient.html");
```

完成後輸入：

```text
http://localhost:8080/
```

Spring Boot 會載入 `static/index.html`，再進入平台首頁。

---

## 六、修改頁面與圖片路徑

搬入 `static` 後，全部修改：

```text
/src/pages/...
```

改為：

```text
/pages/...
```

以及：

```text
/src/assets/...
```

改為：

```text
/assets/...
```

例如：

```html
<!-- 搬移前 -->
<img src="/src/assets/Games/poker/poker_game_icon.png">

<!-- 搬移後 -->
<img src="/assets/Games/poker/poker_game_icon.png">
```

保留原本資料夾分層後，以下相對路徑通常仍可使用：

```html
<script src="./js/client.js"></script>
<a href="../Lobby/jquery_lobby.html"></a>
```

不過仍須逐一檢查：

```text
../../../assets/...
../../Lobby/...
../User/...
```

如果瀏覽器顯示 `404 Not Found`，通常代表相對路徑層級不正確。

---

## 七、修改遊戲管理初始化資料

遊戲管理系統中的前端頁面與圖片路徑也必須更新。

檔案位置：

```text
backend/src/main/java/com/example/demo/modules/game/management/config/GameDataInitializer.java
```

### Poker

```java
poker.setFrontendPath("/pages/Games/poker/poker_client.html");
poker.setImagePath("/assets/Games/poker/poker_game_icon.png");
```

### TJ Poker

```java
tjpoker.setFrontendPath("/pages/Games/tjpoker/poker_client.html");
tjpoker.setImagePath("/assets/Games/tjpoker/poker_game_icon.png");
```

### Quiz

```java
quiz.setFrontendPath("/pages/Games/quiz/quiz_client.html");
quiz.setImagePath("/assets/Games/quiz/quiz_game_icon.png");
```

### 既有 SQLite 資料的注意事項

如果初始化程式只會在遊戲不存在時建立資料，只修改 Java 程式不會自動更新資料庫裡原本的路徑。

建議保留資料庫，直接更新既有資料：

```sql
UPDATE games
SET frontend_path = '/pages/Games/poker/poker_client.html',
    image_path = '/assets/Games/poker/poker_game_icon.png'
WHERE game_code = 'POKER';

UPDATE games
SET frontend_path = '/pages/Games/tjpoker/poker_client.html',
    image_path = '/assets/Games/tjpoker/poker_game_icon.png'
WHERE game_code = 'TJPOKER';

UPDATE games
SET frontend_path = '/pages/Games/quiz/quiz_client.html',
    image_path = '/assets/Games/quiz/quiz_game_icon.png'
WHERE game_code = 'QUIZ';
```

另一種方式是修改初始化程式，讓它找到既有遊戲後也會重新設定路徑並儲存。

除非資料庫確定只是可拋棄的開發資料，否則不建議用刪除整個 SQLite 資料庫的方式更新路徑。

---

## 八、移除前端寫死的 `localhost` 與 `:8080`

搬入 `static` 後，API 與網頁屬於同一個來源，API 可以直接使用相對路徑。

### 搬移前

```javascript
const API_BASE = "http://" + location.hostname + ":8080";
```

### 搬移後

```javascript
const API_BASE = "";
```

原本的呼叫方式可以保留：

```javascript
fetch(`${API_BASE}/api/user/auth/login`);
```

也可以簡化為：

```javascript
fetch("/api/user/auth/login");
```

瀏覽器會自動使用目前網站所在的主機：

```text
本機：http://localhost:8080/api/user/auth/login
區域網路：http://10.10.2.151:8080/api/user/auth/login
部署：https://實際服務名稱.onrender.com/api/user/auth/login
```

主要應檢查：

```text
pages/User/api/userApi.js
pages/Lobby/jquery_lobby.html
pages/Lobby/waiting_room.html
pages/Chat/js/client.js
pages/Games/poker/poker.js
pages/Games/tjpoker/poker.js
pages/Games/quiz/js/app.js
```

---

## 九、修改 WebSocket 網址

不要寫死：

```javascript
ws://localhost:8080
```

應根據目前頁面自動選擇 `ws` 或 `wss`：

```javascript
const wsProtocol =
    location.protocol === "https:" ? "wss" : "ws";

const WS_BASE =
    `${wsProtocol}://${location.host}`;
```

建立連線時使用：

```javascript
const socket = new WebSocket(
    `${WS_BASE}/ws/room/${roomId}`
);
```

`location.host` 會自動包含正確的主機和連接埠：

```text
本機：localhost:8080
區域網路：10.10.2.151:8080
部署：實際服務名稱.onrender.com
```

如果部署頁面使用 HTTPS，瀏覽器必須使用 `wss://`。若仍寫死 `ws://`，瀏覽器可能以 Mixed Content 為由封鎖連線。

需要檢查的 WebSocket 包括：

1. Chat 聊天 WebSocket。
2. Chat 房間頻道 WebSocket。
3. Lobby 房間 WebSocket。
4. Poker WebSocket。
5. TJ Poker WebSocket。

---

## 十、檢查 Spring Security

如果專案使用 Spring Security，需要允許首頁、頁面、圖片、CSS 和 JavaScript 被讀取。

例如：

```java
.requestMatchers(
    "/",
    "/index.html",
    "/pages/**",
    "/assets/**",
    "/favicon.ico"
).permitAll()
```

主畫面會使用 iframe 載入 Lobby、User、Board 等同來源頁面。Spring Security 預設的 `X-Frame-Options: DENY` 會讓 iframe 顯示空白或損壞圖示，因此還需要設定：

```java
.headers(headers -> headers
    .frameOptions(frameOptions -> frameOptions.sameOrigin())
)
```

這會改成 `X-Frame-Options: SAMEORIGIN`，只允許同一個網站嵌入頁面，外部網站仍不能嵌入。

不要因為搬入 `static`，就把所有 API 都設成公開。

以下 API 是否需要 JWT，仍應依照各模組的安全需求決定：

```text
/api/user/**
/api/lobby/**
/api/poker/**
/api/quiz/**
```

搬入 `static` 解決的是前後端來源與網址設定問題，不等於 JWT 可以刪除。

當前端和後端都由 Spring Boot 提供時，瀏覽器請求屬於同來源，大部分 CORS 問題不會再出現。如果團隊仍要使用 Live Server 或獨立前端主機測試，仍需要保留正確的 CORS 設定。

---

## 十一、本機執行方式

搬移後不再使用 VS Code Live Server 的 `5500` 網址。

在 Eclipse 中可以：

1. Maven → Update Project。
2. Project → Clean。
3. 以 Spring Boot App 執行後端。
4. 使用瀏覽器開啟 `http://localhost:8080/`。

也可以在後端目錄執行：

```powershell
cd C:\git\gammPlatform\backend
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Spring Boot 打包時會自動把 `src/main/resources/static` 放進 JAR，因此部署後不需要另外執行前端伺服器。

---

## 十二、功能驗收清單

搬移後依序測試：

- [ ] 輸入 `http://localhost:8080/` 可以開啟首頁。
- [ ] 註冊功能正常。
- [ ] 登入功能正常。
- [ ] 重新整理後仍能維持登入狀態。
- [ ] 大廳能取得遊戲清單。
- [ ] 遊戲圖示正常顯示。
- [ ] 能建立、加入及開始房間。
- [ ] 房間聊天室可以正常收發訊息。
- [ ] Poker 可以正常開啟。
- [ ] Poker 牌面與牌桌圖片正常顯示。
- [ ] Quiz 可以正常開啟。
- [ ] 所有 WebSocket 可以正常連線。
- [ ] 離開遊戲後，房間狀態與房間頻道正常結束。
- [ ] 管理頁面可以正常使用。
- [ ] 瀏覽器主控台沒有 CORS、Mixed Content 或 404 錯誤。

瀏覽器 Network 中的請求應該都指向同一個主機。例如本機測試時，都應該是：

```text
localhost:8080
```

不應再出現：

```text
localhost:5500
127.0.0.1:5500
寫死的其他電腦 IP
```

---

## 十三、搬移後的全文搜尋

驗收前搜尋整個專案，確認沒有不應存在的舊路徑：

```text
/src/pages
/src/assets
localhost
127.0.0.1
:8080
ws://
```

判斷原則：

- 前端 API 和 WebSocket 程式不應寫死主機或 `:8080`。
- 開發文件與後端連接埠設定中的 `8080` 可以保留。
- `ws://` 若只是說明文字可以保留；實際程式應依 HTTP/HTTPS 動態選擇 `ws` 或 `wss`。

---

## 十四、保留舊前端目錄

本次要求是保留原本前端，因此驗收成功後也不要刪除：

```text
frontend/index.html
frontend/src/pages/
frontend/src/assets/
frontend/tests/
```

後續正式執行時以 `backend/src/main/resources/static` 為準；原本 `frontend` 保留作為備份與對照。若未來要讓兩份內容長期同步，需另外建立同步規則，避免只修改其中一份。

建議完成後的 commit 訊息：

```text
Copy frontend into Spring Boot static resources
```

---

## 十五、完成後的架構

```text
瀏覽器
   ↓ 同一個網址
Spring Boot
   ├─ 靜態前端頁面
   ├─ REST API
   ├─ WebSocket
   └─ SQLite
```

完成後，本機、區域網路與正式部署都不需要在前端程式裡更換固定 IP。前端會依照目前開啟網頁的主機，自動連回同一個 Spring Boot 服務。
