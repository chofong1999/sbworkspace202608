# React TodoList、LoginForm與useEffect API資料擷取

本章把第31章的Array State與受控欄位組合成TodoList、LoginForm，接著使用`useEffect`與`fetch()`在元件顯示後讀取遠端API。

## 1. 功能快速索引

| 想完成的事情 | 主要寫法 |
|---|---|
| 新增Array State項目 | `setTodos([...todos, newItem])` |
| 刪除指定項目 | `setTodos(todos.filter(...))` |
| 更新指定項目且保留其他欄位 | `todos.map(item => condition ? { ...item, field: value } : item)` |
| 取得表單送出事件 | `<form onSubmit={handleSubmit}>` |
| 阻止瀏覽器重新整理 | `event.preventDefault()` |
| 元件掛載後執行副作用 | `useEffect(effect, [])` |
| 指定State改變後重新執行 | `useEffect(effect, [state])` |
| 發送HTTP Request | `await fetch(url)` |
| 解析JSON Response | `await response.json()` |
| 顯示載入、成功或失敗狀態 | `loading`、`data`、`error` State |

## 2. 範例位置與執行方式

老師原始碼：

```text
\\Fs2\306_教室暫存區\職訓202607\week7\react-day1
```

本機練習副本：

```text
C:\jscode\react-day1
```

在專案根目錄安裝套件並啟動：

```bash
npm install
npm run dev
```

瀏覽器開啟終端機顯示的網址，通常是`http://localhost:5173/`。

## 3. TodoList：Array State的新增、刪除與更新

建立`src/components/TodoList.jsx`：

```jsx
import { useState } from 'react'

function TodoList() {
  const [todos, setTodos] = useState([
    { id: 1, text: '買咖啡' },
    { id: 2, text: '學 React' }
  ])

  const addTodo = (text) => {
    const maxId = Math.max(...todos.map(item => item.id))
    setTodos([...todos, { id: maxId + 1, text }])
  }

  const removeTodo = (id) => {
    setTodos(todos.filter(todo => todo.id !== id))
  }

  const updateTodo = (id, newText) => {
    setTodos(
      todos.map(todo =>
        todo.id === id ? { ...todo, text: newText } : todo
      )
    )
  }

  const handleUpdate = (id, oldText) => {
    const newText = prompt('請輸入新的任務內容', oldText ?? '')
    if (newText !== null && newText.trim() !== '') {
      updateTodo(id, newText)
    }
  }

  return (
    <div>
      <button onClick={() => addTodo('新任務')}>新增任務</button>
      <ul>
        {todos.map(todo => (
          <li key={todo.id}>
            {todo.text}
            <button onClick={() => removeTodo(todo.id)}>刪除</button>
            <button onClick={() => handleUpdate(todo.id, todo.text)}>更新</button>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default TodoList
```

三種更新都建立新Array，而不是直接改動原State：

- Spread新增：保留舊項目，再把新Object放到尾端。
- `filter()`刪除：只保留ID不同的項目。
- `map()`更新：目標項目建立新Object，其他項目原樣傳回。

`oldText ?? ''`只在`oldText`為`null`或`undefined`時改用空字串；若舊值是數字`0`，仍會保留`0`。`prompt()`的回傳值是String或`null`，所以取消與空白輸入應分開判斷。

課堂寫法以`Math.max()`產生新ID。若清單可能被刪到完全沒有項目，應補上空清單處理，避免結果成為`-Infinity`：

```jsx
const maxId = todos.length === 0
  ? 0
  : Math.max(...todos.map(item => item.id))
```

## 4. LoginForm：受控欄位與表單送出

建立`src/components/LoginForm.jsx`：

```jsx
import { useState } from 'react'

function LoginForm() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const handleSubmit = (event) => {
    event.preventDefault()
    console.log('送出：', { email, password })
  }

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label htmlFor="email">Email</label>
        <input
          id="email"
          type="email"
          value={email}
          onChange={event => setEmail(event.target.value)}
          placeholder="請輸入 Email"
        />
      </div>

      <div>
        <label htmlFor="password">Password</label>
        <input
          id="password"
          type="password"
          value={password}
          onChange={event => setPassword(event.target.value)}
        />
      </div>

      <button type="submit">登入</button>
    </form>
  )
}

export default LoginForm
```

執行流程：

```text
輸入文字 → onChange更新State → React重新渲染欄位
按登入 → 觸發form的onSubmit → preventDefault阻止重新整理 → 使用State資料
```

`onSubmit`應放在`form`，這樣點按鈕或在欄位按Enter都會使用同一套送出流程。

## 5. useEffect的定義與執行條件

`useEffect`用於「畫面渲染完成後，讓元件與外部系統同步」。常見用途包含HTTP Request、Timer、瀏覽器事件或第三方套件。

先匯入：

```jsx
import { useEffect, useState } from 'react'
```

依賴陣列決定重新執行的時機：

| 寫法 | 執行條件 |
|---|---|
| `useEffect(effect)` | 每次渲染完成後 |
| `useEffect(effect, [])` | 元件初次掛載後 |
| `useEffect(effect, [userId])` | 初次掛載後，以及`userId`改變後 |

開發模式若使用`StrictMode`，React可能額外執行一次Effect的設定與清理，用來找出副作用錯誤；正式Build不會因此重複執行。

Effect函式本身不要直接標示為`async`，因為Effect只能回傳清理函式或不回傳值。需要`await`時，在Effect內宣告並呼叫非同步函式：

```jsx
useEffect(() => {
  async function loadData() {
    const response = await fetch(url)
    const data = await response.json()
    setData(data)
  }

  loadData()
}, [url])
```

## 6. UserList：載入全部使用者與指定使用者

建立`src/components/UserList.jsx`：

```jsx
import { useEffect, useState } from 'react'

function UserList() {
  const [users, setUsers] = useState([])
  const [user, setUser] = useState({})
  const [userId, setUserId] = useState(1)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    async function fetchUsers() {
      try {
        const response = await fetch(
          'https://jsonplaceholder.typicode.com/users'
        )
        if (!response.ok) {
          throw new Error(`HTTP 錯誤：${response.status}`)
        }
        setUsers(await response.json())
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchUsers()
  }, [])

  useEffect(() => {
    async function fetchUserById() {
      try {
        const response = await fetch(
          `https://jsonplaceholder.typicode.com/users/${userId}`
        )
        if (!response.ok) {
          throw new Error(`HTTP 錯誤：${response.status}`)
        }
        setUser(await response.json())
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchUserById()
  }, [userId])

  if (loading) return <p>載入中...</p>
  if (error) return <p>錯誤：{error}</p>

  return (
    <section>
      <label htmlFor="userId">使用者ID：</label>
      <input
        id="userId"
        type="number"
        min="1"
        value={userId}
        onChange={event => setUserId(Number(event.target.value))}
      />

      <h2>指定使用者</h2>
      <p>{user.name}（{user.email}）</p>

      <h2>全部使用者</h2>
      <ul>
        {users.map(item => (
          <li key={item.id}>{item.name}（{item.email}）</li>
        ))}
      </ul>
    </section>
  )
}

export default UserList
```

兩個Effect的責任不同：

1. `[]`的Effect只負責初次載入完整清單。
2. `[userId]`的Effect會在ID改變後重新取得指定使用者。
3. `response.ok`為`false`時主動丟出錯誤，交給`catch`更新錯誤畫面。
4. `finally`不論成功或失敗都關閉第一次載入狀態。

## 7. 在main.jsx切換範例

一次只保留要測試的元件，例如測試`UserList`：

```jsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import UserList from './components/UserList.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <UserList />
  </StrictMode>
)
```

測試`TodoList`或`LoginForm`時，替換import及標籤名稱即可。

## 8. 課堂原始碼與本機副本差異

| 檔案 | 比對結果 |
|---|---|
| `LoginForm.jsx` | 本機與老師版本一致 |
| `UserList.jsx` | 邏輯一致；本機只多一個換行用`<br />` |
| `TodoList.jsx` | 本機加入編輯預設文字、接受空字串的個人測試及舊除錯註解 |
| `main.jsx` | 老師目前顯示`TodoList`；本機目前改為顯示`UserList` |
| 專案檔案 | 本機多出安裝依賴後產生的`node_modules`，屬正常狀態 |

老師在`[userId]`旁的註解若寫成「空依賴陣列」，是註解未同步；實際程式不是空陣列，正確條件是初次掛載及`userId`改變時執行。

## 9. 範例限制與可選改善

課堂`UserList`適合觀察Effect與API流程，但正式程式還要留意：

- 全部清單與單一使用者共用一個`loading`，兩個Request同時執行時，任一個先完成就可能提早關閉載入畫面；可拆成兩個Loading State。
- 兩個Request共用`error`，而且重新查詢前沒有清除錯誤。若錯誤畫面直接提前`return`，輸入框會消失，使用者無法由畫面修改ID重試。
- 數字欄位清空時，`Number('')`會得到`0`，可能發送`/users/0`；可先保留輸入字串，送出前再驗證及轉換。
- 快速連續改變ID可能讓較早的Request較晚完成並覆蓋新結果；正式程式可在Effect清理階段取消舊Request。

這些是穩定性改善，不影響本章先理解`useEffect`、依賴陣列及Fetch流程。

## 10. 完成檢查

- [ ] TodoList可新增、刪除及更新，且沒有直接修改Array State。
- [ ] LoginForm輸入欄位與State同步，送出時頁面不重新整理。
- [ ] 能區分省略依賴陣列、`[]`與`[userId]`的執行條件。
- [ ] API成功時能顯示完整清單與指定使用者。
- [ ] API失敗時能顯示錯誤訊息。
- [ ] 改變使用者ID後，第二個Effect會重新取得資料。
