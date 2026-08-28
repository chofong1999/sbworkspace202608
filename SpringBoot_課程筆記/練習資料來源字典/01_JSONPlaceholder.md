# JSONPlaceholder：使用者與文章假資料

- 首頁：<https://jsonplaceholder.typicode.com/>
- Base URL：`https://jsonplaceholder.typicode.com`
- 特性：免 API Key、回傳 JSON，適合測試 Fetch、列表與詳情頁。
- 本頁標記原則：先列課堂實際使用，再列額外可用但未在課堂範例中出現的端點。

## 課堂實際使用

### 快速網址

| 用途 | 寫法 | 課堂程式證據 |
|---|---|---|
| 全部使用者 | `https://jsonplaceholder.typicode.com/users` | `jscode/react-day1/src/components/UserList.jsx` |
| 指定使用者 | `https://jsonplaceholder.typicode.com/users/{id}` | `jscode/day4/fetch.html`、`UserList.jsx` |
| 指定文章 | `https://jsonplaceholder.typicode.com/posts/{postId}` | `jscode/react-day1/src/components/PostDetail.jsx` |

實際使用時要把變數放入 template literal：

```javascript
const response = await fetch(
  `https://jsonplaceholder.typicode.com/users/${userId}`
)
```

使用者範例主要讀取 `id`、`name`、`email`；文章詳情範例主要讀取 `id`、`title`、`body`。

課程對照：

- [第32章：React TodoList、LoginForm 與 useEffect API 資料擷取](../純文字版/32_React_TodoList_LoginForm與useEffect_API資料擷取.md)
- [第33章：React Timer、Effect Cleanup 與 URL 參數查詢](../純文字版/33_React_Timer_cleanup與URL參數查詢.md)

## 額外練習補充（非老師範例）

下列端點屬於 JSONPlaceholder，但在這次查核的三個資料夾中，**沒有找到老師範例直接使用的證據**。

### 全部可練習的資源

| 資源 | 路徑 | 適合練習 |
|---|---|---|
| 文章 | `/posts` | 文章列表、作者篩選 |
| 留言 | `/comments` | 一對多、Query 參數 |
| 待辦 | `/todos` | 布林值與篩選 |
| 相簿 | `/albums` | 使用者與相簿關聯 |
| 照片 | `/photos` | 圖片清單與分頁 |

### 額外網址範例

```text
https://jsonplaceholder.typicode.com/posts
https://jsonplaceholder.typicode.com/posts?userId=1
https://jsonplaceholder.typicode.com/posts/1/comments
https://jsonplaceholder.typicode.com/comments?postId=1
https://jsonplaceholder.typicode.com/todos
https://jsonplaceholder.typicode.com/todos?userId=1
https://jsonplaceholder.typicode.com/albums/1/photos
https://jsonplaceholder.typicode.com/photos?albumId=1
```

`/photos` 資料量較大，初次練習可先限制呈現數量，例如：

```javascript
const firstTwenty = photos.slice(0, 20)
```

## 寫入操作限制

這類公開假 API 適合練習 `POST`、`PUT`、`PATCH`、`DELETE` 的 Request 與 Response；不能直接把回傳成功當成資料已永久保存。若要驗證持久化，應改用自己的後端與資料庫。
