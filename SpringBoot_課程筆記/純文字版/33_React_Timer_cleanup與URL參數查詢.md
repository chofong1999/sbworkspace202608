# React Timer、Effect Cleanup 與 URL 參數查詢

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章接續第32章的`useEffect`與Fetch，完成兩個小型實作：以Timer持續更新畫面，以及依網址中的文章ID向API查詢單筆文章。

範例專案：`react-day1`；新增元件位於`src/components/Timer.jsx`與`src/components/PostDetail.jsx`。

> 語法速查：[React Effect、Cleanup與模組](../語法字典/14_React_Vite_JSX與Hooks.md)｜[網址與Fetch](../語法字典/12_DOM_BOM表單與Fetch.md)

## 1. 功能快速索引

| 想完成的事情 | 主要寫法 |
|---|---|
| 每隔固定時間更新State | `setInterval(..., 1000)` |
| 元件卸載時停止Timer | `return () => clearInterval(intervalId)` |
| 依前一版數字加一 | `setSeconds(previous => previous + 1)` |
| 取得目前網址路徑 | `window.location.pathname` |
| 由路徑字串取出ID | `pathname.split('/')[1]` |
| Prop改變時重新查詢 | `useEffect(..., [postId])` |
| 分開顯示載入、錯誤與成功 | `loading`、`error`、`post` State |
| 安全讀取可能尚未存在的資料 | `post?.title` |

## 2. 前置條件與啟動

- 已完成第30～32章，能建立Vite React專案、撰寫元件並使用`useState`、`useEffect`與`fetch()`。
- 在專案根目錄執行`npm install`與`npm run dev`。
- 本章使用JSONPlaceholder公開測試API；測試時需要網路連線。

## 3. Timer：建立與清除週期性工作

建立`src/components/Timer.jsx`：

```jsx
import { useEffect, useState } from 'react'

function Timer() {
  const [seconds, setSeconds] = useState(0)
  const [time, setTime] = useState(new Date())

  useEffect(() => {
    const intervalId = setInterval(() => {
      setSeconds(previous => previous + 1)
      setTime(new Date())
    }, 1000)

    return () => clearInterval(intervalId)
  }, [])

  return (
    <div>
      <p>已計時：{seconds} 秒</p>
      <button onClick={() => setSeconds(0)}>重置計時器</button>
      <p>當前時間：{time.toLocaleTimeString()}</p>
    </div>
  )
}

export default Timer
```

執行順序：

```text
元件掛載
→ Effect建立setInterval
→ 每秒更新seconds與time
→ State改變後重新渲染
→ 元件卸載時執行cleanup並clearInterval
```

`[]`表示此Effect不依賴會變動的Prop或State。Cleanup不是每秒執行；它會在元件卸載時執行，開發模式的`StrictMode`也可能用一次額外的設定與清理來檢查副作用是否安全。

## 4. PostDetail：依文章ID查詢單筆資料

建立`src/components/PostDetail.jsx`：

```jsx
import { useEffect, useState } from 'react'

function PostDetail({ postId }) {
  const [post, setPost] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    async function fetchData() {
      setLoading(true)
      setError(null)

      try {
        const response = await fetch(
          `https://jsonplaceholder.typicode.com/posts/${postId}`
        )
        if (!response.ok) {
          throw new Error(`HTTP 錯誤：${response.status}`)
        }
        setPost(await response.json())
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [postId])

  if (loading) return <p>載入中...</p>
  if (error) return <p>錯誤：{error}</p>

  return (
    <div>
      <h2>文章詳情</h2>
      <p>文章ID：{postId}</p>
      <p>文章標題：{post?.title}</p>
      <p>文章內容：{post?.body}</p>
    </div>
  )
}

export default PostDetail
```

`[postId]`表示初次掛載後查詢一次，之後只要收到不同的`postId`就重新查詢。重新查詢前先把`loading`設回`true`並清除舊錯誤，才能讓畫面準確反映這一次Request的狀態。

課堂工作副本目前把`setLoading(true)`與`setError(null)`註解掉；在「每次改網址都重新載入整頁」的操作下仍可看到單筆文章，但若同一個已掛載元件直接換`postId`，就可能保留上一輪的載入或錯誤狀態。上方範例保留這兩行，作為可重複查詢的完整版本。

## 5. 從網址取得文章ID

在`src/main.jsx`顯示`PostDetail`：

```jsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import PostDetail from './components/PostDetail.jsx'

const postId = window.location.pathname.split('/')[1] || 1

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <PostDetail postId={postId} />
  </StrictMode>
)
```

這個取值方式的成立條件是網址格式為`/{id}`：

| 網址路徑 | `split('/')`結果 | 取得的`[1]` |
|---|---|---|
| `/12` | `['', '12']` | `'12'` |
| `/` | `['', '']` | 空字串，因此改用`1` |
| `/posts/12` | `['', 'posts', '12']` | `'posts'`，不符合本寫法 |

因此本例可開啟`http://localhost:5173/12`查詢第12篇文章。若路徑設計是`/posts/12`，必須改取`[2]`，或正式導入Router解析路由參數。

`postId`由路徑取得時是String；放進URL沒有問題。若後續要做數值比較或運算，再使用`Number(postId)`轉型。

## 6. ES Module在本章中的角色

`Timer.jsx`與`PostDetail.jsx`各自只有一個主要元件，因此使用Default Export：

```jsx
export default PostDetail
import PostDetail from './components/PostDetail.jsx'
```

React套件同時提供多個Named Export，所以使用大括號選取需要的Hook：

```jsx
import { useEffect, useState } from 'react'
```

Default與Named Import的完整規則見[第30章](30_React_Vite_JSX元件與Props.md#10-default-export與named-export)。

## 7. 驗證

1. 在`main.jsx`先顯示`<Timer />`，確認秒數與時間每秒更新，重置按鈕可把秒數設回0。
2. 改為顯示`<PostDetail postId={postId} />`。
3. 開啟`http://localhost:5173/1`，確認顯示文章ID、標題與內容。
4. 改開`http://localhost:5173/12`，確認取得不同文章。
5. 使用不存在的ID，確認畫面會進入錯誤或無資料處理，而不是永久停在載入中。

## 8. 完成檢查

- [ ] Timer建立後會持續更新State。
- [ ] Effect回傳Cleanup並停止自己的Timer。
- [ ] PostDetail在`postId`改變時重新發送Request。
- [ ] 載入、錯誤與成功畫面彼此分開。
- [ ] 能說明`split('/')[1]`只適用於`/{id}`路徑格式。
- [ ] 能區分本章的Default Import與Named Import。
