# jQuery與AJAX

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)｜[jQuery課程](../純文字版/25_jQuery_DOM操作_事件與樣式.md)｜[AJAX課程](../純文字版/26_jQuery_AJAX與SpringBoot前後端分離.md)

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `$()` | 以CSS選取器建立jQuery集合 | `$(".item")` | [選取](#jquery-select) |
| `$(document).ready(...)` | DOM可操作後執行 | `$(start)` | [啟動](#jquery-ready) |
| `.click()`／`.on()` | 註冊事件 | `$("#b1").on("click", fn)` | [事件](#jquery-event) |
| `.text()`／`.html()`／`.val()` | 讀寫文字、HTML、表單值 | `$("#name").val()` | [內容](#jquery-content) |
| `.css()`／`.addClass()` | 修改style／class | `$("p").addClass("new")` | [樣式](#jquery-style) |
| `.wrap()`／`.wrapAll()` | 個別／整組包裹元素 | `$("p").wrapAll("<div>")` | [包裹](#jquery-wrap) |
| `$.get()`／`$.post()` | GET／表單式POST捷徑 | `$.get(url, success)` | [AJAX捷徑](#ajax-shortcut) |
| `$.ajax()` | 完整設定HTTP請求 | `$.ajax({ url, method: "GET" })` | [AJAX](#ajax) |
| `statusCode`／`error` | 依狀態碼或失敗處理 | `statusCode: { 404() {} }` | [狀態](#ajax-status) |

<a id="jquery-select"></a>
## 載入與選取

```html
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
```

```javascript
const items = $(".item");
```

必須先成功載入jQuery，才有全域`$`與`jQuery`。`$()`回傳jQuery集合，不是單一DOM Element；原生property與jQuery method不可混用。

<a id="jquery-ready"></a>
## DOM Ready

```javascript
const start = () => { ... };
$(document).ready(start);
// 簡寫：$(start);
```

DOM Ready代表HTML已解析、可以選取元素；不保證所有圖片都已下載完成。

<a id="jquery-event"></a>
## 事件

```javascript
$("#b1").on("click", function (event) {
  console.log(this);
});
```

動態新增的子元素可使用事件委派：

```javascript
$("#list").on("click", ".delete", function () { ... });
```

<a id="jquery-content"></a>
## 文字、HTML與欄位值

```javascript
$("#title").text("Hello");
$("#panel").html("<strong>Hello</strong>");
const name = $("#name").val();
```

不帶參數時通常是讀取；帶參數時是寫入。`.html()`會解析HTML，使用者輸入應優先用`.text()`。

<a id="jquery-style"></a>
## class與style

```javascript
$("p").addClass("new");
$("p").removeClass("new");
$("p").toggleClass("new");
$("p").css("color", "red");
```

<a id="jquery-wrap"></a>
## `.wrap()`與`.wrapAll()`

```javascript
$(".inner").wrap('<div class="new"></div>');
$(".inner").wrapAll('<div class="new"></div>');
```

- `.wrap()`：每個匹配元素各自多一層外殼，中間原有元素仍保留原位置，因此會被分開。
- `.wrapAll()`：把整組匹配元素移到同一個外殼中；若中間原本夾有其他元素，版面順序可能改變。

<a id="ajax-shortcut"></a>
## AJAX捷徑

```javascript
$.get(url, data => console.log(data));
$.post(url, { name: "Amy" }, data => console.log(data));
```

`$.post()`預設常以表單格式送出；若後端使用`@RequestBody`接JSON，應使用`$.ajax()`並設定JSON。

<a id="ajax"></a>
## `$.ajax()`

```javascript
$.ajax({
  url: "http://localhost:8080/api/products",
  method: "POST",
  contentType: "application/json",
  dataType: "json",
  data: JSON.stringify(product),
  success(data) { console.log(data); },
  error(jqXHR, textStatus, errorThrown) {
    console.error(jqXHR.status, textStatus, errorThrown);
  }
});
```

| 選項 | 控制什麼 |
|---|---|
| `contentType` | 送給伺服器的request body格式 |
| `dataType` | 預期伺服器回傳的格式 |
| `data` | 送出的資料；JSON要先`JSON.stringify()` |

<a id="ajax-status"></a>
## HTTP狀態處理

```javascript
statusCode: {
  204() { alert("No Content"); },
  404() { alert("Not Found"); }
}
```

`204 No Content`沒有response body，不應再強迫解析JSON。通用錯誤可放`error`；需要針對特定狀態顯示不同流程時使用`statusCode`。
