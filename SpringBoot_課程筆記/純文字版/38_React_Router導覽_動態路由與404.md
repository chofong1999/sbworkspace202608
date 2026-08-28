# Spring Boot 學習筆記 38：React Router 導覽、動態路由與 404

- 範例專案：`react-routing-app`
- 學習目標：在單頁應用程式中建立多個網址、導覽列、動態商品頁、程式化跳轉與 404
- 練習資料：[Fake Store API](../練習資料來源字典/02_Fake_Store_API.md)
- 前置閱讀：[第32章](32_React_TodoList_LoginForm與useEffect_API資料擷取.md)、[第33章](33_React_Timer_cleanup與URL參數查詢.md)

## 本章新增語法快速表

| 想完成的事情 | 寫法 |
|---|---|
| 啟用瀏覽器路由 | `<BrowserRouter>` |
| 宣告路由表 | `<Routes>`、`<Route path="..." element={...} />` |
| 不重新載入頁面地切換網址 | `<Link to="...">` |
| 依目前網址套用樣式 | `<NavLink className={({ isActive }) => ...}>` |
| 定義動態路徑 | `path="/products/:id"` |
| 讀取動態參數 | `const { id } = useParams()` |
| 在函式中跳頁 | `const navigate = useNavigate()` |
| 回上一頁 | `navigate(-1)` |
| 建立 404 | `<Route path="*" element={<NotFound />} />` |

## 1. 安裝套件

```bash
npm install react-router-dom bootstrap
```

課堂講義標示 React Router v6 基礎；目前專案安裝的是 `react-router-dom 7.18.2`，本章使用的 `BrowserRouter`、`Routes`、`Route`、`Link`、`NavLink`、`useParams` 與 `useNavigate` 寫法仍可使用。

專案 `main.jsx` 另有：

```javascript
import 'bootstrap/dist/css/bootstrap.min.css'
```

因此要重現目前原始碼，除了 Router 也必須安裝 Bootstrap；否則 Vite 會報找不到模組。

## 2. 最外層建立 BrowserRouter

`src/main.jsx`：

```jsx
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import Approute from './Approute.jsx'

createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <Approute />
  </BrowserRouter>
)
```

需要使用 Router Hook 或路由元件的內容，都必須位於 `<BrowserRouter>` 內部；否則會出現「只能在 Router context 中使用」之類的錯誤。

## 3. 建立路由表

`src/Approute.jsx`：

```jsx
import { Routes, Route } from 'react-router-dom'

function Approute() {
  return (
    <>
      <Navbar />

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/products" element={<Products />} />
        <Route
          path="/fakeproductdetail/:id"
          element={<FakeProductDetail />}
        />
        <Route path="/login" element={<LoginForm />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </>
  )
}
```

`Navbar` 放在 `Routes` 外，所以每個頁面都顯示；`Routes` 內依目前 URL 選擇對應的 `element`。

## 4. Link 與 NavLink

一般內容連結：

```jsx
<Link to="/products">商品列表</Link>
```

導覽列需要知道目前頁面是否啟用時，使用 `NavLink`：

```jsx
const linkClass = ({ isActive, isPending }) =>
  'nav-link' +
  (isActive ? ' active' : '') +
  (isPending ? ' pending' : '')

<NavLink to="/about" className={linkClass}>
  關於
</NavLink>
```

首頁連結加 `end`：

```jsx
<NavLink to="/" end className={linkClass}>
  首頁
</NavLink>
```

`/` 是所有路徑的開頭；沒有 `end` 時，開啟 `/about` 也可能讓首頁連結保持 Active。

### 為何不用一般 `<a href>`

```html
<a href="/products">商品</a>
```

一般 `<a>` 會讓瀏覽器重新向伺服器載入整個文件；`Link`／`NavLink` 由 Router 接管，只切換網址與 React 元件，保留 SPA 行為。

外部網站仍應使用 `<a href="https://...">`。

## 5. 動態路由

路由宣告：

```jsx
<Route
  path="/fakeproductdetail/:id"
  element={<FakeProductDetail />}
/>
```

`:id` 是動態片段，因此以下網址都會進入同一元件：

```text
/fakeproductdetail/1
/fakeproductdetail/2
/fakeproductdetail/20
```

商品列表動態產生連結：

```jsx
{products.map(product => (
  <Link
    key={product.id}
    to={`/fakeproductdetail/${product.id}`}
  >
    {product.name}
  </Link>
))}
```

## 6. useParams 讀取 ID 並查 API

```jsx
function FakeProductDetail() {
  const { id } = useParams()
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)

    fetch(`https://fakestoreapi.com/products/${id}`)
      .then(response => response.json())
      .then(data => {
        setProduct(data)
        setLoading(false)
      })
  }, [id])

  if (loading) return <p>載入中...</p>
  if (!product) return <p>找不到商品</p>

  return <h1>{product.title}</h1>
}
```

`id` 放在 Effect 依賴陣列中，因此從商品 1 切換成商品 2 時會重新查詢，不需要重建整個網站。

本章使用 Fake Store API；端點與欄位可查閱[練習資料來源字典](../練習資料來源字典/02_Fake_Store_API.md)。

## 7. useNavigate 程式化導航

按鈕跳到商品頁：

```jsx
const navigate = useNavigate()

<button onClick={() => navigate('/products')}>
  瀏覽商品
</button>
```

登入成功後跳回首頁：

```javascript
await loginAPI(email, password)
navigate('/')
```

回到瀏覽器歷史紀錄的上一頁：

```jsx
<button onClick={() => navigate(-1)}>
  回上一頁
</button>
```

`Link` 適合畫面上本來就存在的導覽；`navigate()` 適合登入成功、儲存完成或條件判斷後才決定跳頁。

## 8. 登入畫面的非同步狀態

課堂 `LoginForm` 使用 Mock API，並未連接第 37 章 JWT 後端：

```javascript
await new Promise(resolve => setTimeout(resolve, 1500))

if (password === 'wrong') {
  throw new Error('帳號或密碼錯誤，請重試')
}
```

元件分別保存：

- `email`、`password`：受控欄位。
- `error`：錯誤訊息。
- `loading`：避免送出期間重複點擊。

```jsx
<button type="submit" disabled={loading}>
  {loading ? '登入中...' : '登入'}
</button>
```

## 9. 404 頁面

路由表最後加入萬用路徑：

```jsx
<Route path="*" element={<NotFound />} />
```

`NotFound` 可同時提供首頁與上一頁：

```jsx
<Link to="/">回到首頁</Link>
<button onClick={() => navigate(-1)}>回上一頁</button>
```

目前 Navbar 有 `/dashboard` 連結，但路由表沒有對應 `/dashboard`，所以點下去會刻意進入 404；若後台頁已完成，必須再加入相應 Route。

## 10. 啟動與驗證

```bash
cd react-routing-app
npm install
npm install react-router-dom bootstrap
npm run dev
```

依序測試：

1. `/` 顯示首頁，首頁導覽有 Active 樣式。
2. 點「商品」不會整頁重新整理。
3. 點商品 1，網址變成 `/fakeproductdetail/1` 並載入 Fake Store 商品。
4. 把網址 ID 改成 2，內容重新查詢。
5. 登入輸入 `wrong` 顯示錯誤；其他非空密碼等待後跳回首頁。
6. 開不存在的網址進入 404。
7. 404 的「回上一頁」可返回先前頁面。

## 11. 直接輸入網址與正式部署

開發伺服器通常會把未知路徑回退到 `index.html`。正式部署 SPA 時也必須設定相同 fallback；否則站內點 Link 正常，但重新整理 `/products/1` 會由 Web Server 回 404。

這個 404 與 React 的 `path="*"` 不同：

| 404 發生位置 | 原因 | 修正 |
|---|---|---|
| React 畫面 | Router 找不到 Route | 加入正確 Route 或顯示 NotFound |
| Web Server | 伺服器不認識前端路徑 | 將非靜態檔 Request fallback 到 `index.html` |

## 12. 目前來源驗證

- 老師與本機 `react-routing-app` 的 26 份有效檔案雜湊一致。
- 本機尚未安裝此專案的 Vite 依賴，因此直接執行 build 會顯示找不到 `vite`；先執行 `npm install` 才能進行編譯驗證。
- 目前 `package.json` 未列出 Bootstrap，但 `main.jsx` 有匯入 Bootstrap CSS；按照第 1 節補裝後才能完整重現。

