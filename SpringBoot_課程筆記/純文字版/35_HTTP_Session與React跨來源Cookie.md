# Spring Boot 學習筆記 35：HTTP Session 與 React 跨來源 Cookie

- 後端範例：`websocketChat` 的 `SessionController`
- 前端範例：`react-day2` 的 `SessionUI.jsx`
- 學習目標：建立 Session、讀回同一份 Session 資料，並理解跨來源時為何會讀到 `null`

## 本章新增語法快速表

| 想完成的事情 | 寫法 |
|---|---|
| 取得目前 HTTP Session | Controller 參數 `HttpSession session` |
| 寫入 Session | `session.setAttribute("data", value)` |
| 讀取 Session | `session.getAttribute("data")` |
| 跨來源請求攜帶 Cookie | `fetch(url, { credentials: "include" })` |
| 後端允許帶憑證的 CORS | `allowCredentials = "true"` 與明確 `origins` |

## 1. Session 如何辨認同一位瀏覽器

Spring Boot 把資料保存在伺服器端，瀏覽器則保存通常名為 `JSESSIONID` 的 Cookie。之後瀏覽器再把這個 Cookie 送回伺服器，後端才知道應讀取哪一份 Session。

```text
第一次 Request
瀏覽器 ───────────────> Spring Boot 建立 Session
瀏覽器 <─ Set-Cookie: JSESSIONID=abc ─ 後端

下一次 Request
瀏覽器 ─ Cookie: JSESSIONID=abc ─────> 讀回同一份 Session
```

因此「先呼叫 create，再呼叫 get」還不夠；兩次 Request 必須攜帶同一個 Session Cookie。

## 2. 後端建立與讀取 Session

```java
@RestController
@RequestMapping("/api")
public class SessionController {

    @GetMapping("/create")
    public ResponseEntity<String> create(HttpSession session) {
        session.setAttribute("data", "Session Value1");
        return ResponseEntity.ok("Session Value1");
    }

    @GetMapping("/get")
    public ResponseEntity<String> get(HttpSession session) {
        Object value = session.getAttribute("data");
        return ResponseEntity.ok(String.valueOf(value));
    }
}
```

`HttpSession` 不需要手動 `new`；Spring MVC 會提供目前 Request 對應的 Session。

## 3. 同來源測試

直接以同一個瀏覽器依序開啟：

```text
http://localhost:8011/api/create
http://localhost:8011/api/get
```

若後端使用 8011，第二個網址應回傳：

```text
Session Value1
```

兩個網址同為 `localhost:8011`，瀏覽器會自然攜帶該站的 Cookie。

## 4. React 跨來源時為何可能讀到 `null`

Vite 前端通常是 `http://localhost:5173`，後端是 `http://localhost:8011`。連接埠不同就屬於不同 Origin。

課堂初始寫法：

```javascript
fetch('http://localhost:8011/api/create')
fetch('http://localhost:8011/api/get')
```

雖然兩次 Request 都可能成功，但跨來源 Fetch 預設不保證保存及送出後端的 Session Cookie。第二次 Request 可能得到新的 Session，因此讀到 `null`。

## 5. 可重現的跨來源完整設定

前端兩次 Fetch 都加入 `credentials: "include"`：

```javascript
const response = await fetch('http://localhost:8011/api/create', {
  credentials: 'include'
})
```

```javascript
const response = await fetch('http://localhost:8011/api/get', {
  credentials: 'include'
})
```

後端必須同時允許確定的前端來源與 Credentials。最小示例可加在 Controller：

```java
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api")
public class SessionController {
    // ...
}
```

不能在允許 Credentials 時把來源設成萬用 `*`；瀏覽器安全規則要求後端回覆明確 Origin。

## 6. React 元件

```jsx
import { useState } from 'react'

function SessionUI() {
  const [data, setData] = useState('')

  async function createSession() {
    const response = await fetch('http://localhost:8011/api/create', {
      credentials: 'include'
    })
    if (!response.ok) throw new Error(`HTTP ${response.status}`)
    setData(await response.text())
  }

  async function getSession() {
    const response = await fetch('http://localhost:8011/api/get', {
      credentials: 'include'
    })
    if (!response.ok) throw new Error(`HTTP ${response.status}`)
    setData(await response.text())
  }

  return (
    <div>
      <button onClick={createSession}>Create Session</button>
      <button onClick={getSession}>Get Session</button>
      <h2>{data}</h2>
    </div>
  )
}

export default SessionUI
```

## 7. 驗證順序

1. 啟動後端 `websocketChat`，確認是 8011。
2. 啟動 `react-day2`，開啟 Vite 顯示的網址。
3. 先按 `Create Session`。
4. 再按 `Get Session`。
5. 畫面應再次顯示 `Session Value1`。
6. DevTools → Network 中檢查兩次 Request 是否使用同一個 `JSESSIONID`。

## 8. Session 與 WebSocket Session 不相同

| 類型 | Java 類別 | 用途 |
|---|---|---|
| HTTP Session | `jakarta.servlet.http.HttpSession` | 跨多次 HTTP Request 保存使用者狀態 |
| WebSocket Session | `org.springframework.web.socket.WebSocketSession` | 代表一條目前開啟的 WebSocket 連線 |

兩者都叫 Session，但不是同一種物件，也不能互換。

## 9. 本例目前狀態

老師與本機初始 `SessionUI.jsx` 只有一般 Fetch；它可示範 API 呼叫，但跨來源時可能出現「create 成功、get 為 null」。要把它變成穩定可重現的跨來源 Session 範例，必須同時完成第 5 節的前後端設定。

