# React列表、useState與受控表單

本章接續第30章的Vite、JSX、元件與Props，練習把Array轉成畫面、用State保存會變動的資料，以及用受控表單同步輸入欄位。

## 1. 功能快速索引

| 想完成的事情 | 主要寫法 |
|---|---|
| 把Array的每一項轉成JSX | `array.map(item => <Element />)` |
| 替清單項目提供穩定識別 | `key={item.id}` |
| 建立會觸發重新渲染的資料 | `const [state, setState] = useState(initialValue)` |
| 根據舊值計算新值 | `setState(previous => nextValue)` |
| 更新Object中的一個欄位 | `setState(previous => ({ ...previous, field: value }))` |
| 讓輸入框與State同步 | `value={state.field}`搭配`onChange` |
| 取得輸入事件的新值 | `event.target.value` |
| 按鈕觸發函式 | `onClick={handler}` |

## 2. 前置條件

先完成第30章並確認Vite專案可以執行：

```bash
npm run dev
```

本章課堂工作副本位於：

```text
C:\jscode\my-react-app
```

範例元件放在`src/`，再由`src/main.jsx`選擇目前要顯示的元件。

## 3. 使用map產生列表

React不使用另一套迴圈標籤。要把JavaScript Array顯示成多個JSX元素，可直接在大括號中呼叫`map()`：

```jsx
function ArrayList() {
  const products = [
    { id: 101, name: 'Keyboard', price: 1200 },
    { id: 102, name: 'Mouse', price: 650 },
    { id: 103, name: 'Monitor', price: 5200 }
  ]

  return (
    <table>
      <thead>
        <tr>
          <th>名稱</th>
          <th>價格</th>
        </tr>
      </thead>
      <tbody>
        {products.map(product => (
          <tr key={product.id}>
            <td>{product.name}</td>
            <td>{product.price}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default ArrayList
```

執行順序：

1. `map()`依序取得每一個`product`。
2. Arrow Function傳回一個`<tr>`。
3. React把所有`<tr>`組成畫面。
4. 資料改變而重新渲染時，React利用`key`辨認每一列。

### key的使用條件

同一層清單中，每個項目的`key`必須唯一且盡量保持不變。資料本身有ID時，優先使用ID：

```jsx
key={product.id}
```

只有在清單固定、不重新排序、不插入或刪除項目時，才適合暫時使用索引：

```jsx
items.map((item, index) => <li key={index}>{item}</li>)
```

`key`是React用來比對清單的特殊屬性，不會像一般Props一樣傳進子元件。子元件若也需要ID，必須另外傳入：

```jsx
<ProductCard key={product.id} productId={product.id} />
```

## 4. useState的結構

先匯入Hook：

```jsx
import { useState } from 'react'
```

再於元件最上層呼叫：

```jsx
const [count, setCount] = useState(0)
```

這行同時包含三個部分：

- `count`：目前這次渲染使用的State值。
- `setCount`：要求React更新State並重新渲染的函式。
- `0`：元件第一次建立時的初始值。

左側的`[count, setCount]`是Array解構。`useState()`會依序回傳「目前值」與「更新函式」。

Hook必須在React函式元件或自訂Hook的最上層呼叫，不可放入`if`、迴圈或一般巢狀函式，否則每次渲染的呼叫順序可能不同。

## 5. Counter：事件與更新函式

```jsx
import { useState } from 'react'

function Counter() {
  const [count, setCount] = useState(0)

  return (
    <section>
      <p>Count: {count}</p>
      <button onClick={() => setCount(previous => previous + 1)}>+1</button>
      <button onClick={() => setCount(previous => previous - 1)}>-1</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </section>
  )
}

export default Counter
```

`onClick`需要的是函式，而不是函式執行結果：

```jsx
onClick={handler}       // 點擊時才執行
onClick={() => handler()} // 點擊時執行包裝函式
```

若新值依賴舊值，使用更新函式形式：

```jsx
setCount(previous => previous + 1)
```

這裡的`previous`是React提供的最新State。它比直接寫`setCount(count + 1)`更適合連續更新或批次處理的情況。

## 6. State不可直接修改

下列寫法會直接修改原Object，再把同一個參考交還React：

```jsx
data.count = data.count + 1
setData(data)
```

React可能判定新舊值是同一個Object，因此沒有重新渲染；直接修改也會讓State變化難以追蹤。

正確方式是回傳新的Object：

```jsx
setData(previous => ({
  ...previous,
  count: previous.count + 1
}))
```

`...previous`先複製原有欄位，後面的`count`再覆寫指定欄位。

### 兩個相依數字的範例

若要顯示費氏數列，可把「目前值」與「下一個值」一起存入State：

```jsx
const [data, setData] = useState({
  count: 0,
  nextCount: 1
})

const clickCount = () => {
  setData(previous => ({
    count: previous.nextCount,
    nextCount: previous.count + previous.nextCount
  }))
}
```

每次點擊都只讀取`previous`，並產生一個全新的Object。結果依序為`0、1、1、2、3、5...`。

## 7. 受控表單

受控輸入元件的畫面值來自State；使用者輸入時，再由`onChange`更新State：

```jsx
import { useState } from 'react'

function ProfileForm() {
  const [user, setUser] = useState({
    name: '',
    email: ''
  })

  return (
    <form>
      <label>
        姓名
        <input
          type="text"
          value={user.name}
          onChange={event => {
            setUser(previous => ({
              ...previous,
              name: event.target.value
            }))
          }}
        />
      </label>

      <label>
        Email
        <input
          type="email"
          value={user.email}
          onChange={event => {
            setUser(previous => ({
              ...previous,
              email: event.target.value
            }))
          }}
        />
      </label>

      <p>姓名：{user.name}</p>
      <p>Email：{user.email}</p>
    </form>
  )
}

export default ProfileForm
```

單一欄位的資料流為：

```text
鍵盤輸入
  → onChange事件
  → event.target.value取得新文字
  → setUser建立新State
  → React重新渲染
  → value顯示新State
```

更新`name`時仍要保留`email`，所以需要`...previous`。若直接回傳`{ name: value }`，原本的`email`欄位就會消失。

## 8. 切換與測試元件

測試Counter時：

```jsx
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import Counter from './Counter.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Counter />
  </StrictMode>
)
```

測試ProfileForm時，只需替換import與JSX：

```jsx
import ProfileForm from './ProfileForm.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ProfileForm />
  </StrictMode>
)
```

成功判定：

- Counter按鈕每按一次都會立即更新數字。
- ProfileForm輸入姓名或Email時，下方文字同步更新。
- 瀏覽器Console沒有元件匯入、Hook或`key`警告。

## 9. 課堂範例與目前工作副本

老師的`src/`已包含`ArrayList.jsx`、`Counter.jsx`與`ProfileForm.jsx`，並由`main.jsx`切換示範元件。

本機`C:\jscode\my-react-app`保留個人State練習，因此`App.jsx`與老師範例不完全相同；其他已核對的教學元件與老師版本一致。筆記採用可重現且不直接修改State的寫法。

後續課堂原始碼已加入`TodoList`、`LoginForm`與`UserList`。其中Array CRUD與提交表單延續本章概念；`useEffect`、`fetch()`及API載入狀態集中整理於[第32章](32_React_TodoList_LoginForm與useEffect_API資料擷取.md)。

## 10. 常見錯誤

| 現象 | 常見原因 | 修正方式 |
|---|---|---|
| 點按鈕沒有更新 | 直接修改State Object並傳回同一參考 | 使用更新函式並回傳新Object |
| 第一個數字一直是0 | 兩個初始值都設為0 | 使用`{ count: 0, nextCount: 1 }` |
| Object其他欄位消失 | 更新時只回傳單一欄位 | 先`...previous`再覆寫欄位 |
| 清單出現key警告 | `map()`產生的同層項目沒有key | 使用資料本身穩定且唯一的ID |
| 輸入框無法輸入 | 設定`value`卻沒有更新State的`onChange` | 在`onChange`呼叫Setter |
| 畫面空白 | `main.jsx`元件名稱或import路徑不一致 | 核對export、import與render中的元件名稱 |

## 11. 完成檢查

- [ ] 能用`map()`把Array轉成JSX清單或表格。
- [ ] 每個動態清單項目都有穩定`key`。
- [ ] 能說明`useState()`回傳的兩個值。
- [ ] State依賴舊值時使用Updater Function。
- [ ] 不直接修改State中的Object或Array。
- [ ] 表單欄位同時設定`value`與`onChange`。
- [ ] 能在`main.jsx`切換並驗證Counter與ProfileForm。
