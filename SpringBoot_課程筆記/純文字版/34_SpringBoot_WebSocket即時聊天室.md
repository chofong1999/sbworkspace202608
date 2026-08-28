# Spring Boot 學習筆記 34：WebSocket 即時聊天室

- 範例專案：`websocketChat`（老師原始專案名稱為 `sbchat0826`）
- 學習目標：建立可讓多個瀏覽器即時收發文字訊息的聊天室
- 前置閱讀：[第28章：JavaScript 現代語法與 Fetch API](28_JavaScript現代語法與Fetch_API.md)

## 本章新增語法快速表

| 想完成的事情 | 寫法 |
|---|---|
| 啟用 Spring WebSocket | `@Configuration`、`@EnableWebSocket` |
| 註冊 WebSocket 路徑 | `registry.addHandler(handler, "/ws/chat")` |
| 處理文字訊息 | `extends TextWebSocketHandler`、`handleTextMessage(...)` |
| 保存多個連線 | `CopyOnWriteArrayList<WebSocketSession>` |
| 瀏覽器建立連線 | `new WebSocket("ws://主機:連接埠/ws/chat")` |
| 傳送訊息 | `webSocket.send(JSON.stringify(data))` |
| 接收訊息 | `webSocket.onmessage = event => ...` |

## 1. WebSocket 解決什麼問題

一般 HTTP 是「瀏覽器送出 Request，伺服器回一次 Response」。聊天室需要伺服器在任何時刻都能把新訊息推送給已連線的使用者，因此改用保持連線的 WebSocket。

本章資料流如下：

```text
瀏覽器 A ─┐
          ├─ ws://主機:連接埠/ws/chat ─ Spring WebSocket Handler
瀏覽器 B ─┘                              │
                                        └─ 將收到的訊息廣播給所有連線
```

`http://.../ws/chat` 不是一般網頁或 REST API；直接在網址列以 HTTP 開啟通常會得到 404。必須由 JavaScript 使用 `new WebSocket("ws://...")` 發起 WebSocket 握手。

## 2. 加入依賴

`pom.xml` 至少需要 Web MVC 與 Spring WebSocket：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
</dependency>
```

## 3. 建立訊息處理器

```java
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final CopyOnWriteArrayList<WebSocketSession> sessions =
            new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message) throws IOException {

        String payload = message.getPayload();

        for (WebSocketSession current : sessions) {
            if (current.isOpen()) {
                current.sendMessage(new TextMessage(payload));
            }
        }
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status) {
        sessions.remove(session);
    }
}
```

三個生命週期方法分工如下：

| 方法 | 觸發時機 | 本例工作 |
|---|---|---|
| `afterConnectionEstablished` | 連線成功 | 把連線加入清單 |
| `handleTextMessage` | 收到文字訊息 | 廣播給所有仍開啟的連線 |
| `afterConnectionClosed` | 連線關閉 | 從清單移除連線 |

`CopyOnWriteArrayList` 適合本練習，因為連線新增／移除與訊息廣播可能同時發生；它避免一般 `ArrayList` 在併發修改時產生問題。

## 4. 註冊 `/ws/chat`

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/ws/chat")
                .setAllowedOrigins("*");
    }
}
```

- `@EnableWebSocket`：開啟 Spring WebSocket 支援。
- `addHandler(..., "/ws/chat")`：把網址路徑交給指定 Handler。
- `setAllowedOrigins("*")`：課堂練習允許所有來源；正式環境應改成確定的前端網址。

## 5. 建立瀏覽器客戶端

頁面可放在 `src/main/resources/static/chatclient.html`，JavaScript 放在 `static/js/client.js`。

核心連線與事件如下：

```javascript
const webSocket = new WebSocket('ws://localhost:8011/ws/chat')

webSocket.onopen = () => {
  console.log('連線成功')
}

webSocket.onerror = () => {
  console.log('連線失敗')
}

webSocket.onmessage = (event) => {
  const message = JSON.parse(event.data)
  console.log(message.userName, message.message)
}
```

送出訊息：

```javascript
const messageInfo = {
  userName: userNameInput.value,
  message: userinput.value
}

webSocket.send(JSON.stringify(messageInfo))
```

本例伺服器沒有改寫 JSON，只負責把收到的字串原樣廣播；因此前端送出 JSON 字串，接收端再用 `JSON.parse()` 還原 Object。

## 6. 正確設定主機與連接埠

同一台電腦測試可使用：

```javascript
new WebSocket('ws://localhost:8011/ws/chat')
```

讓區域網路其他電腦連線時，才改成伺服器電腦的區網 IP：

```javascript
new WebSocket('ws://10.10.1.213:8011/ws/chat')
```

同時必須確認：

1. Spring Boot 確實使用 `server.port=8011`。
2. IP 是執行後端那台電腦目前的 IP。
3. Windows 防火牆允許該連接埠。
4. 兩台電腦位於可互通的網路。

離開教室網路後，`10.10.1.213` 通常不再有效；請重新查詢當前 IP，或在同機測試時改回 `localhost`。

## 7. 啟動與驗證

1. 啟動 Spring Boot。
2. 開啟 `http://localhost:8011/chatclient.html`。
3. 再開一個分頁或另一個瀏覽器，開啟相同網址。
4. 兩邊分別輸入名字並按「登入」。
5. 任一邊送出訊息，兩邊都應立即出現相同訊息。

後端 Console 應依序看到連線、訊息與關閉紀錄。

## 8. 常見失敗

| 現象 | 檢查項目 |
|---|---|
| 網址列開 `/ws/chat` 得到 404 | 這是 WebSocket Endpoint，不是一般 HTTP 頁面 |
| 頁面顯示「登入失敗」 | 後端未啟動、連接埠／IP 不一致，或防火牆阻擋 |
| 只有自己收到訊息 | Handler 是否迴圈走訪所有 `sessions` |
| 重整後連線數不正確 | 關閉時是否在 `afterConnectionClosed` 移除 Session |
| HTTPS 頁面無法連線 | HTTPS 前端通常必須使用 `wss://`，不能混用不安全的 `ws://` |

## 9. 本例限制

- 連線只存在記憶體中，伺服器重啟即消失。
- 沒有登入驗證、訊息保存、聊天室分組與輸入內容消毒。
- 課堂本機 `ChatRoomServer` 保留了早期 Jakarta WebSocket 寫法的註解；正式執行的是 `TextWebSocketHandler` 那一套，不應把兩套生命週期混著實作。

