# HTML、CSS與瀏覽器載入

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)｜[課程第19章](../純文字版/19_HTML_CSS與JavaScript基礎.md)

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `<!DOCTYPE html>` | 使用HTML5解析模式 | `<!DOCTYPE html>` | [文件骨架](#html-document) |
| `<meta charset>` | 指定文字編碼 | `<meta charset="UTF-8">` | [文件骨架](#html-document) |
| `<link rel="stylesheet">` | 載入外部CSS | `<link rel="stylesheet" href="style.css">` | [載入資源](#load-resource) |
| `<script src>` | 載入並執行外部JavaScript | `<script src="app.js"></script>` | [載入資源](#load-resource) |
| `class`／`id` | 提供CSS與JavaScript選取依據 | `class="card"` | [選取器](#selectors) |
| `.class`／`#id`／`tag` | 選取HTML元素 | `.card { ... }` | [選取器](#selectors) |
| inline／internal／external CSS | 三種套用CSS方式 | `<style>...</style>` | [CSS位置](#css-location) |
| `url(...)` | 在CSS引用圖片等資源 | `background-image: url("img.jpg")` | [資源路徑](#resource-path) |

<a id="html-document"></a>
## HTML文件骨架

```html
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>頁面標題</title>
</head>
<body>
  <h1>內容</h1>
</body>
</html>
```

- `head`保存頁面設定與資源連結；可見內容放在`body`。
- `charset`應在文件前段宣告，避免中文解碼錯誤。
- `viewport`讓行動裝置依裝置寬度排版。

<a id="load-resource"></a>
## 載入外部資源

```html
<link rel="stylesheet" href="./css/site.css">
<script src="./js/app.js" defer></script>
```

| 寫法 | 定義與使用條件 |
|---|---|
| `<link rel="stylesheet">` | 只用於CSS樣式表；`href`填網址或檔案路徑 |
| `<script src>` | 下載並執行JavaScript，作用類似「把外部程式載入頁面」，但不是Java的`import` |
| `defer` | 平行下載Script，等HTML解析完成後依出現順序執行；一般頁面Script適合使用 |
| CDN URL | 從網路服務載入函式庫；離線或CDN失效時無法使用 |

例如載入jQuery後，才可使用`$()`與`$.ajax()`：

```html
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
```

<a id="css-location"></a>
## CSS放置位置

```html
<p style="color: red">inline</p>

<style>
  p { color: red; }
</style>

<link rel="stylesheet" href="style.css">
```

正式頁面通常使用外部CSS，便於多頁共用與維護；inline適合臨時覆寫，不適合大量樣式。

<a id="selectors"></a>
## CSS選取器

| 選取器 | 選取對象 | 範例 |
|---|---|---|
| `tag` | 所有同名標籤 | `p { color: blue; }` |
| `.class` | 所有具有該class的元素 | `.warning { color: red; }` |
| `#id` | 該id元素；同頁id應唯一 | `#title { font-size: 2rem; }` |
| `A B` | A後代中的B | `.card p { ... }` |
| `A > B` | A的直接子元素B | `ul > li { ... }` |
| `:hover` | 指標停留狀態 | `button:hover { ... }` |

同一元素可有多個class：`class="btn btn-primary"`。CSS中的小色塊是編輯器提供的顏色預覽，不是程式碼的一部分。

<a id="resource-path"></a>
## 相對路徑與絕對網址

```css
body {
  background-image: url("./images/background.jpg");
  background-size: cover;
  background-repeat: no-repeat;
}
```

- `./`：目前HTML或CSS檔所在資料夾。
- `../`：上一層資料夾。
- `/images/a.png`：從網站根路徑開始，不是從目前檔案開始。
- `https://...`：完整網路網址。

Live Server看到資料夾清單而非頁面時，通常是網址只開到資料夾且其中沒有`index.html`；直接開`first.html`或把入口命名為`index.html`。
