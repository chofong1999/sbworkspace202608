# JavaScript核心、陣列與物件

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)｜[課程第20章](../純文字版/20_JavaScript變數型別條件與迴圈.md)｜[第21章](../純文字版/21_JavaScript陣列_物件與JSON.md)

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `let`／`const` | 宣告可重新指派／不可重新指派的變數 | `const name = "Amy"` | [變數](#variables) |
| `` `${value}` `` | Template Literal內插值 | `` `Hi ${name}` `` | [字串](#template-literal) |
| `===`／`!==` | 不進行型別轉換的比較 | `value === 0` | [條件](#condition) |
| `&&`／`||`／`??` | 邏輯合取、fallback與nullish fallback | `oldText ?? ""` | [條件](#condition) |
| `condition ? a : b` | 條件運算式 | `ok ? "yes" : "no"` | [條件](#condition) |
| `function`／`=>` | 宣告函式 | `(x) => x * 2` | [函式](#function) |
| `...` | 展開或收集剩餘值 | `{ ...user, name }` | [Spread／Rest](#spread-rest) |
| `map`／`filter`／`find`／`reduce` | 轉換、篩選、尋找與彙總陣列 | `items.map(x => x.name)` | [陣列](#array-methods) |
| `?.` | 左側為null／undefined時停止取值 | `user?.name` | [安全取值](#safe-access) |
| `JSON.parse`／`JSON.stringify` | JSON字串與JavaScript值互轉 | `JSON.parse(text)` | [JSON](#json) |

<a id="variables"></a>
## 變數與型別

```javascript
const user = { name: "Amy" };
let count = 0;
count += 1;
```

- `const`禁止變數名稱改指向另一個值，但Object／Array內部仍可能被修改。
- `let`用於之後需要重新指派的變數。
- 新程式通常不使用`var`，避免函式作用域與提升造成混淆。
- 常見型別：String、Number、Boolean、BigInt、undefined、Object；`null`表示刻意沒有值。

<a id="template-literal"></a>
## Template Literal

```javascript
const message = `Hello ${user.name}, count = ${count}`;
```

必須使用反引號`` ` ``；`${...}`內可放JavaScript expression。這和Thymeleaf `${...}`外觀相似，但執行位置與規則不同。

<a id="condition"></a>
## 比較、fallback與條件運算式

```javascript
if (count === 0) { ... }
const label = count > 0 ? "有資料" : "沒有資料";
const a = value || "預設";
const b = value ?? "預設";
```

| 運算式 | 何時使用右側 |
|---|---|
| `value || fallback` | 左側是falsy：`false`、`0`、`""`、`null`、`undefined`、`NaN` |
| `value ?? fallback` | 左側只有`null`或`undefined` |

需要保留`0`或空字串時，通常使用`??`。`prompt()`回傳String或`null`；輸入畫面中的`0`是字串`"0"`，本身是truthy。

<a id="function"></a>
## 函式與Arrow Function

```javascript
function add(a, b) {
  return a + b;
}

const add2 = (a, b) => a + b;
const double = numbers.map(number => number * 2);
```

Arrow Function省略大括號時會直接回傳單一expression；加上大括號後若要回傳值，必須明寫`return`。Arrow Function沒有自己的`this`，事件或物件方法需要動態`this`時應先確認需求。

<a id="spread-rest"></a>
## Spread與Rest

```javascript
const copy = [...items];
const updated = { ...user, name: "Bob" };

function sum(...numbers) {
  return numbers.reduce((total, n) => total + n, 0);
}
```

同一個`...`依位置有兩種角色：在Array／Object／呼叫參數中展開值；在函式參數或解構中收集剩餘值。

<a id="array-methods"></a>
## Array常用方法

| 方法 | 回傳 | 是否改動原陣列 | 最小範例 |
|---|---|---|---|
| `map` | 等長新陣列 | 否 | `items.map(x => x.name)` |
| `filter` | 符合條件的新陣列 | 否 | `items.filter(x => x.active)` |
| `find` | 第一個符合值或`undefined` | 否 | `items.find(x => x.id === id)` |
| `some` | 是否至少一筆符合 | 否 | `items.some(x => x.id === id)` |
| `reduce` | 單一累積結果 | 否 | `nums.reduce((a, n) => a + n, 0)` |
| `push` | 新長度 | 是 | `items.push(value)` |
| `splice` | 被移除項目的陣列 | 是 | `items.splice(index, 1)` |

React State應優先使用回傳新陣列的方法，不直接`push()`或`splice()`舊State。

<a id="safe-access"></a>
## Optional Chaining

```javascript
const oldText = todos.find(todo => todo.id === id)?.text;
const initialText = oldText ?? "";
```

`?.`只在左側為`null`或`undefined`時回傳`undefined`，不會把`0`或空字串視為缺值。

<a id="json"></a>
## Object與JSON

```javascript
const user = { id: 1, name: "Amy" };
const text = JSON.stringify(user);
const restored = JSON.parse(text);
```

Object是JavaScript執行期的資料結構；JSON是文字格式。JSON鍵與String必須用雙引號，也不能保存函式、`undefined`或循環參照。
