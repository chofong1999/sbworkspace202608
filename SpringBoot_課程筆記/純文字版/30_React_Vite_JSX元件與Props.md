# React與Vite：JSX、元件與Props

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章建立第一個React／Vite專案，說明JSX、元件、`import`／`export`及Props。React的State、列表與表單接續第31章。

範例專案：`my-react-app`；主要程式位於`src/`。

> 語法速查：[React、Vite與JSX](../語法字典/14_React_Vite_JSX與Hooks.md)

## 1. 本章功能快速索引

| 功能 | 主要寫法 |
|---|---|
| 建立Vite React專案 | `npm create vite@latest ... -- --template react` |
| 啟動開發伺服器 | `npm run dev` |
| 在DOM掛載React | `createRoot(...).render(...)` |
| 建立函式元件 | `function Component() { return (...) }` |
| 在JSX插入表達式 | `{expression}` |
| 條件二選一 | `{condition ? A : B}` |
| 條件成立才顯示 | `{condition && A}` |
| 傳遞Props | `<ProductCard name="..." price={999} />` |
| 接收Props | `function ProductCard({ name, price })` |
| 預設匯出／匯入 | `export default`、`import Name from` |
| 具名匯出／匯入 | `export function`、`import { Name } from` |
| 同時匯入Default與Named項目 | `import App, { helper } from` |
| 只執行模組、不接收匯出值 | `import './index.css'` |

## 2. 前置條件

- 已完成第18章的VS Code與終端機設定。
- 已完成第28章，能閱讀Arrow Function、解構與Spread。
- 已安裝Node.js與npm。

確認版本：

```powershell
node -v
npm -v
```

若Windows PowerShell禁止執行`npm.ps1`，可依第18章設定：

```powershell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

## 3. 建立Vite React專案

```powershell
npm create vite@latest my-react-app -- --template react
cd my-react-app
npm install
npm run dev
```

終端機顯示網址後開啟：

```text
http://localhost:5173
```

看到Vite／React歡迎畫面代表專案已啟動。修改`src/`檔案並儲存後，HMR會讓瀏覽器更新，不必每次重新啟動伺服器。

## 4. 專案結構

```text
my-react-app/
├─ public/              靜態資源
├─ src/
│  ├─ assets/           元件匯入的圖片等資源
│  ├─ App.jsx           預設根元件
│  ├─ App.css           App樣式
│  ├─ main.jsx          React進入點
│  └─ index.css         全域樣式
├─ index.html           提供id="root"的掛載點
├─ package.json         scripts與套件版本
└─ vite.config.js       Vite設定
```

`package.json`中最重要的scripts：

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  }
}
```

## 5. `main.jsx`如何顯示元件

```jsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import App from './App.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>
);
```

流程：

```text
index.html的<div id="root">
→ document.getElementById("root")
→ createRoot建立React Root
→ render(<App />)
→ App回傳的JSX顯示在頁面
```

使用`<VarTest />`、`<Counter />`等元件前，必須先正確匯入。元件沒有匯入時，瀏覽器Console通常會出現`ReferenceError: 元件名稱 is not defined`，React畫面可能整頁空白。

## 6. JSX的基本規則

JSX是JavaScript的語法擴充，外觀類似HTML，但由React工具鏈轉換成JavaScript。

| HTML | JSX |
|---|---|
| `class` | `className` |
| `for` | `htmlFor` |
| `onclick` | `onClick` |
| `<br>` | `<br />` |
| `style="color:red"` | `style={{ color: "red" }}` |

元件必須回傳單一外層節點；不想增加實際HTML元素時可用Fragment：

```jsx
return (
  <>
    <h1>標題</h1>
    <p>內容</p>
  </>
);
```

JSX註解必須寫成：

```jsx
{/* 這是JSX註解 */}
```

直接把`// 註解`放在JSX標籤之間，可能成為畫面文字，不是可靠的JSX註解方式。

## 7. 在JSX中使用JavaScript表達式

```jsx
const name = "Alice";
const price = 99.9;

export function VarTest() {
  return (
    <div>
      <h1>Hello, {name}!</h1>
      <p>含稅價格：{price * 1.05}</p>
      <p>{name.toUpperCase()}</p>
    </div>
  );
}
```

`{}`中可以放會產生值的Expression，例如變數、運算、函式呼叫或三元運算。不能直接放`if`、`for`等Statement。

## 8. 三種條件渲染

二選一：

```jsx
{isLoggedIn ? <p>已登入</p> : <p>請登入</p>}
```

只有成立時顯示：

```jsx
{isLoggedIn && <button>登出</button>}
```

整個元件分支：

```jsx
function UserGreeting({ isLoggedIn, username }) {
  if (!isLoggedIn) {
    return <h1>請先登入</h1>;
  }
  return <h1>歡迎回來，{username}！</h1>;
}
```

## 9. 函式元件

React函式元件的條件：

- 名稱以大寫字母開頭。
- 回傳JSX。
- 使用時寫成`<ComponentName />`。

```jsx
function Welcome() {
  return <h1>Welcome</h1>;
}

function App() {
  return <Welcome />;
}
```

小寫的`<welcome>`會被當成一般HTML標籤，而不是自訂元件。

## 10. Default Export與Named Export

預設匯出每個模組只能有一個，引入時可自訂名稱：

```jsx
// ProductCard.jsx
export default function ProductCard() {
  return <div>Product</div>;
}
```

```jsx
import ProductCard from './ProductCard.jsx';
```

具名匯出可以有多個，引入名稱必須對應，並使用大括號：

```jsx
// ArrayList.jsx
export function FruitList() {
  return <div>Fruits</div>;
}

export function ProductList() {
  return <div>Products</div>;
}
```

```jsx
import { FruitList, ProductList } from './ArrayList.jsx';
```

常見錯誤：

```jsx
import VarTest from './VarTest.jsx';
```

若檔案只有`export function VarTest()`而沒有`export default`，上面寫法就不成立，應改成：

```jsx
import { VarTest } from './VarTest.jsx';
```

Default Import沒有大括號，名稱可由匯入端自行決定；Named Import使用大括號，名稱必須和匯出端一致，或使用`as`明確取別名：

```jsx
import MainCard from './ProductCard.jsx';
import { ProductList as Products } from './ArrayList.jsx';
```

同一個模組同時提供Default與Named Export時，可以在同一行一起匯入：

```jsx
// tools.js
export default function start() { /* ... */ }
export function stop() { /* ... */ }

// main.js
import start, { stop } from './tools.js';
```

需要把全部Named Export收進同一個物件時，可使用Namespace Import：

```jsx
import * as math from './math.js';
math.add(10, 20);
```

只要求模組執行、不接收任何值時使用Side-effect Import。Vite處理CSS時常見這種寫法：

```jsx
import './index.css';
```

判斷方式不是看有沒有大括號哪個「比較好」，而是必須配合匯出端：

| 匯出端 | 匯入端 |
|---|---|
| `export default App` | `import App from './App.jsx'` |
| `export const helper = ...` | `import { helper } from './file.js'` |
| 同時有Default與Named Export | `import App, { helper } from './file.js'` |
| 只需執行檔案 | `import './index.css'` |

## 11. Props傳遞資料

`ProductCard.jsx`：

```jsx
function ProductCard({ name, price, inStock }) {
  return (
    <div className="card">
      <h2>{name}</h2>
      <p>價格：${price}</p>
      {inStock ? <span>有庫存</span> : <span>缺貨中</span>}
    </div>
  );
}

export default ProductCard;
```

在父元件或`main.jsx`使用：

```jsx
<ProductCard name="iPhone 16" price={999} inStock={true} />
<ProductCard name="AirPods Pro" price={249} inStock={false} />
```

- 字串可直接放在引號內。
- Number、Boolean、Array、Object與函式使用`{}`。
- Props由父元件傳給子元件，子元件不可直接修改。
- 參數`{ name, price, inStock }`是Object解構。

## 12. 切換要測試的元件

範例專案透過修改`main.jsx`切換範例。測試`ProductCard`時：

```jsx
import ProductCard from './ProductCard.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ProductCard name="iPhone 16" price={999} inStock={true} />
  </StrictMode>
);
```

每次切換時要同時完成：

1. 匯入目標元件。
2. 在`render()`內使用相同名稱。
3. 不再使用的匯入可註解或移除。
4. 儲存後查看瀏覽器及DevTools Console。

## 13. 版本與入口元件

- React、Vite與開發套件的小版本會隨建立時間改變；只要專案能正常啟動，本章React與JSX語法不受影響。
- `main.jsx`一次只需渲染目前要觀察的根元件。切換練習時，修改`import`及`render()`中的元件即可。
- `useEffect`屬於後續的副作用與資料擷取主題，統一在第32章處理。

## 14. 驗證

1. 在專案根目錄執行`npm run dev`。
2. 開啟`http://localhost:5173`。
3. 在`main.jsx`匯入並顯示`VarTest`，確認變數、運算與條件內容。
4. 改為`ProductCard`並傳入不同Props，確認庫存文字會切換。
5. 故意移除元件import，觀察Console錯誤，再恢復正確import。

## 15. 檢查表

- [ ] 能建立並啟動Vite React專案。
- [ ] 能說明`main.jsx`、`root`與元件的關係。
- [ ] 能遵守JSX的標籤、屬性與註解規則。
- [ ] 能區分Expression與Statement。
- [ ] 能建立大寫開頭的函式元件。
- [ ] 能區分Default Export與Named Export。
- [ ] 能使用Props傳入String、Number與Boolean。
