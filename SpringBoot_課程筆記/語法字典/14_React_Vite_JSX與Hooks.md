# React、Vite、JSX與Hooks

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)｜[React基礎課程](../純文字版/30_React_Vite_JSX元件與Props.md)｜[State課程](../純文字版/31_React列表_useState與受控表單.md)｜[useEffect課程](../純文字版/32_React_TodoList_LoginForm與useEffect_API資料擷取.md)｜[Timer與URL參數實作](../純文字版/33_React_Timer_cleanup與URL參數查詢.md)

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `npm create vite@latest` | 建立Vite React專案 | `npm create vite@latest app -- --template react` | [Vite](#vite) |
| `createRoot(...).render(...)` | 把React元件掛到HTML root | `createRoot(root).render(<App />)` | [入口](#react-entry) |
| `function Component()` | 宣告Function Component | `function App(){ return <h1 /> }` | [元件](#component) |
| JSX `{...}` | 在標記中執行JavaScript expression | `<p>{name}</p>` | [JSX](#jsx) |
| `props` | 由父元件傳資料給子元件 | `<Card title="A" />` | [Props](#props) |
| `map(... key=...)` | 將陣列轉成元件清單 | `items.map(x => <li key={x.id}>...)` | [清單](#list-render) |
| `useState` | 保存會影響畫面的元件State | `const [n, setN] = useState(0)` | [State](#usestate) |
| `setState(prev => next)` | 依前一版State計算下一版 | `setN(n => n + 1)` | [State](#usestate) |
| `onChange`／`onSubmit` | 處理受控欄位與表單 | `onChange={e => setName(e.target.value)}` | [表單](#controlled-form) |
| `useEffect` | 同步React外部系統或擷取資料 | `useEffect(() => {...}, [])` | [Effect](#useeffect) |
| `fetch`＋State | 將API資料寫入畫面 | `setItems(await r.json())` | [API](#react-fetch) |
| Default Export／Import | 匯出模組唯一的主要值 | `export default App` | [模組](#module) |
| Named Export／Import | 匯出多個有名稱的值 | `import { useState } from 'react'` | [模組](#module) |
| Mixed Import | 同時接收Default與Named項目 | `import App, { helper } from './file.js'` | [模組](#module) |
| Namespace Import | 把Named Export收進物件 | `import * as math from './math.js'` | [模組](#module) |
| Side-effect Import | 只載入並執行模組 | `import './index.css'` | [模組](#module) |

<a id="vite"></a>
## 建立與啟動Vite React專案

```powershell
npm create vite@latest my-react-app -- --template react
cd my-react-app
npm install
npm run dev
```

`my-react-app`同時是建立的資料夾名稱；`--`表示後面的參數交給Vite建立程式，`--template react`直接選擇React＋JavaScript模板。

<a id="react-entry"></a>
## React入口

```jsx
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.jsx';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>
);
```

`index.html`必須有`<div id="root"></div>`。畫面空白時先檢查匯入的元件名稱、JSX錯誤及瀏覽器Console。

<a id="component"></a>
## Function Component

```jsx
function Greeting() {
  return <h1>Hello</h1>;
}

export default Greeting;
```

元件名稱必須以大寫字母開頭；必須回傳JSX或`null`。相鄰多個根元素要放在共同Element或Fragment `<>...</>`中。

<a id="jsx"></a>
## JSX

```jsx
const name = 'Amy';
return <h1 className="title">Hello {name}</h1>;
```

- `{...}`內只能直接放expression，不能直接放`if`statement。
- HTML的`class`在JSX寫`className`，`for`寫`htmlFor`。
- 事件接收函式：`onClick={handleClick}`；若要傳參數則寫`onClick={() => remove(id)}`。

<a id="props"></a>
## Props

```jsx
function Card({ title, price = 0 }) {
  return <p>{title}: {price}</p>;
}

<Card title="Book" price={100} />
```

Props由父元件傳入，子元件不可直接修改。String literal可直接使用引號；Number、Boolean、Object與expression放在`{}`中。

<a id="list-render"></a>
## 條件與清單渲染

```jsx
{loggedIn ? <Dashboard /> : <Login />}
{items.map(item => <li key={item.id}>{item.name}</li>)}
```

`key`必須在同層清單中穩定且唯一，用來辨識項目；資料可增刪或排序時，不應使用陣列index當key。

<a id="usestate"></a>
## `useState`

```jsx
const [count, setCount] = useState(0);
setCount(previous => previous + 1);
```

State更新是排程行為；需要依前一版計算時使用updater function。不要直接改Object／Array後把同一個參照傳回：

```jsx
setData(previous => ({
  ...previous,
  preCount: previous.count,
  count: previous.preCount + previous.count
}));

setTodos(previous =>
  previous.map(todo => todo.id === id ? { ...todo, text: newText } : todo)
);
```

React以參照變化判斷State是否更新；建立新Object／Array才能穩定觸發重新渲染。

<a id="controlled-form"></a>
## 受控表單

```jsx
const [name, setName] = useState('');

function submit(event) {
  event.preventDefault();
  console.log(name);
}

return (
  <form onSubmit={submit}>
    <input value={name} onChange={e => setName(e.target.value)} />
    <button type="submit">送出</button>
  </form>
);
```

受控欄位的畫面值來自State；`onChange`必須同步更新State，否則欄位會無法輸入。

<a id="useeffect"></a>
## `useEffect`

```jsx
useEffect(() => {
  document.title = `Count ${count}`;
}, [count]);
```

| dependency寫法 | 執行時機 |
|---|---|
| 省略第二參數 | 每次render後 |
| `[]` | 元件mount後；開發模式StrictMode可能額外執行一次以檢查清理 |
| `[count]` | mount後及`count`改變後 |

Effect用來同步React外部系統，例如網路、計時器、DOM API或訂閱；可由事件直接完成的工作通常放在事件函式，不必多繞一層Effect。

多個Request、過期回應與StrictMode細節見[延伸閱讀：React useEffect與Fetch穩定性](../純文字版/延伸閱讀/32_React_useEffect與Fetch穩定性.md)。

有清理需求時回傳cleanup函式：

```jsx
useEffect(() => {
  const id = setInterval(tick, 1000);
  return () => clearInterval(id);
}, []);
```

<a id="react-fetch"></a>
## 在React擷取API資料

```jsx
useEffect(() => {
  const controller = new AbortController();

  async function load() {
    const response = await fetch(url, { signal: controller.signal });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    setItems(await response.json());
  }

  load().catch(error => {
    if (error.name !== 'AbortError') setError(error.message);
  });

  return () => controller.abort();
}, [url]);
```

至少要分開保存loading、data與error狀態。依dependency重抓資料時，取消舊請求可避免舊回應晚到而覆蓋新結果。

<a id="module"></a>
## ES Module匯入與匯出

```jsx
export default App;
import App from './App.jsx';

export function Profile() { ... }
import { Profile } from './Profile.jsx';
```

| 類型 | 匯出 | 匯入條件 |
|---|---|---|
| Default | `export default App` | 不加大括號；本地名稱可自訂；一個模組最多一個 |
| Named | `export const helper = ...` | 使用`{ helper }`；名稱需一致，可用`as`取別名；一個模組可有多個 |
| Mixed | 同一模組同時有Default與Named | `import App, { helper } from './file.js'` |
| Namespace | 多個Named Export | `import * as math from './math.js'`，使用`math.add()` |
| Side effect | 不接收匯出值 | `import './index.css'`；只要求模組被載入與執行 |

常見React例子：

```jsx
import App from './App.jsx';                 // App.jsx的default export
import { useEffect, useState } from 'react'; // react的named exports
import './index.css';                        // side-effect import
```

Named Import取別名：

```jsx
import { Profile as UserProfile } from './Profile.jsx';
```

大括號不是偏好或簡寫，而是匯出契約的一部分。匯出端使用`export default`就以Default Import接收；匯出端使用Named Export就以`{...}`接收。

`&#x20;`是HTML entity表示空格，不是JavaScript或JSX語法，從聊天或網頁複製程式碼時應還原成普通空白。
