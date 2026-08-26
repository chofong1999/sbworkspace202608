# DOM、BOM、表單與Fetch

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)｜[DOM課程](../純文字版/23_JavaScript_DOM元素選取_節點與動態修改.md)｜[Fetch課程](../純文字版/28_JavaScript現代語法與Fetch_API.md)

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `querySelector`／`querySelectorAll` | 以CSS選取器找Element | `document.querySelector("#title")` | [DOM選取](#dom-select) |
| `textContent`／`innerHTML` | 寫入純文字／HTML | `el.textContent = "Hi"` | [內容](#dom-content) |
| `classList`／`style` | 改class／inline style | `el.classList.add("active")` | [樣式](#dom-style) |
| `createElement`／`append`／`remove` | 建立、加入、移除節點 | `list.append(li)` | [節點](#dom-node) |
| `addEventListener` | 註冊事件處理器 | `btn.addEventListener("click", fn)` | [事件](#events) |
| `event.preventDefault()` | 取消瀏覽器預設行為 | `event.preventDefault()` | [表單](#form) |
| `prompt`／`alert`／`confirm` | 顯示瀏覽器對話框 | `prompt("Name", "")` | [BOM](#bom) |
| `setTimeout`／`setInterval` | 延後／重複執行函式 | `setTimeout(fn, 1000)` | [BOM](#bom) |
| `window.location.pathname` | 取得目前網址的路徑部分 | `const path = location.pathname` | [網址路徑](#location-path) |
| `split('/')` | 依斜線切割路徑字串 | `path.split('/')[1]` | [網址路徑](#location-path) |
| `fetch` | 發送HTTP請求 | `fetch(url)` | [Fetch](#fetch) |
| `async`／`await` | 以循序外觀等待Promise | `const r = await fetch(url)` | [Fetch](#fetch) |

<a id="dom-select"></a>
## 選取DOM元素

```javascript
const title = document.querySelector("#title");
const rows = document.querySelectorAll("table tbody tr");
```

`querySelector()`回傳第一個Element或`null`；`querySelectorAll()`回傳靜態NodeList，可使用`forEach()`。操作前若元素可能不存在，要先判斷`null`。

<a id="dom-content"></a>
## 讀寫內容

```javascript
title.textContent = "安全的純文字";
panel.innerHTML = "<strong>HTML</strong>";
```

`textContent`不解析標籤；`innerHTML`會解析HTML，不可直接放入未經處理的使用者輸入，以免造成XSS。

<a id="dom-style"></a>
## class與style

```javascript
title.classList.add("active");
title.classList.toggle("active");
title.style.color = "red";
title.style.backgroundColor = "yellow";
```

需要多個樣式時，優先在CSS定義class，再用`classList`切換；`style`只改Element的inline style。CSS的`background-color`在JavaScript property中寫成`backgroundColor`。

<a id="dom-node"></a>
## 建立與移除節點

```javascript
const li = document.createElement("li");
li.textContent = "New item";
list.append(li);
li.remove();
```

只要Element時使用`firstElementChild`、`children`等Element API，可避開縮排造成的`#text` Text Node。

<a id="events"></a>
## 事件

```javascript
button.addEventListener("click", event => {
  console.log(event.currentTarget);
});
```

- `target`：真正觸發事件的最內層元素。
- `currentTarget`：目前執行處理器的元素。
- `event.key`：鍵盤按鍵名稱；新程式不使用已淘汰的`keyCode`／`which`。

<a id="form"></a>
## 表單送出

```javascript
form.addEventListener("submit", event => {
  if (!form.checkValidity()) {
    event.preventDefault();
  }
});
```

驗證應掛在`submit`事件，才能涵蓋點擊按鈕、按Enter及程式觸發的送出。`preventDefault()`只取消本次預設送出，不會停止後面的JavaScript；需要時再搭配`return`。

<a id="bom"></a>
## BOM與對話框、計時器

```javascript
const input = prompt("請輸入名稱", ""); // String或null
const ok = confirm("確定刪除？");          // Boolean
alert("完成");

const timerId = setInterval(tick, 1000);
clearInterval(timerId);
```

`prompt()`按取消回傳`null`，直接按確定但沒輸入則回傳空字串。若要數字，應明確使用`Number(input)`並檢查`Number.isNaN()`。

<a id="location-path"></a>
## 網址路徑與字串切割

```javascript
const postId = window.location.pathname.split('/')[1] || 1;
```

- `pathname`只包含網址的Path，不含Origin、Query String與Fragment。
- `split('/')`是String方法，會依斜線建立Array。
- `[1]`只有在路徑格式為`/{id}`時代表ID；`/posts/12`的ID位於`[2]`。
- `|| 1`會在前面的結果是空字串等Falsy值時改用`1`。

路徑層級固定且簡單時可以直接切割；正式多頁React應用通常交給Router解析參數，避免元件自行依賴字串索引。

<a id="fetch"></a>
## Fetch API

```javascript
async function loadProducts() {
  const response = await fetch("http://localhost:8080/api/products");
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.status === 204 ? [] : await response.json();
}
```

`fetch()`只會在網路層失敗時reject；404、500仍會resolve，因此必須檢查`response.ok`或`response.status`。Response body通常只能讀一次。

POST JSON：

```javascript
await fetch(url, {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(data)
});
```

前端與後端Origin不同時，後端必須正確設定CORS；Origin由scheme、host與port共同決定。
