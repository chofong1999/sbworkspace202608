# jQuery：DOM操作、事件與樣式

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章接續原生JavaScript DOM與Event，使用jQuery簡化元素選取、事件註冊、內容修改、樣式、節點包裝及表格建立。課堂範例位於`week6\day3`，對應`jquery_basic.html`到`jquery_mouse.html`。

## 1. 本章功能快速索引

| 功能 | jQuery寫法 |
|---|---|
| 等待DOM完成 | `$(document).ready(start)` |
| 依標籤／ID／class選取 | `$("button")`、`$("#b1")`、`$(".inner")` |
| 註冊點擊事件 | `.click(handler)` |
| 隱藏／顯示 | `.hide()`、`.show()` |
| 讀寫文字 | `.text()`、`.text(value)` |
| 讀寫表單值 | `.val()`、`.val(value)` |
| 修改inline style | `.css(...)` |
| 新增／切換／移除class | `.addClass()`、`.toggleClass()`、`.removeClass()` |
| 讀寫Attribute | `.attr(...)` |
| 逐項處理集合 | `$.each(...)` |
| 建立元素並加入父元素 | `$("<td></td>")`、`.appendTo(...)` |
| 分別包裝元素 | `.wrap(...)` |
| 將所有符合元素包在一起 | `.wrapAll(...)` |
| 滑鼠移入／移出 | `.hover(inHandler, outHandler)` |
| 輸入框取得／失去焦點 | `.focus()`、`.blur()` |
| 取得目前事件元素 | `$(this)` |

課堂檔案對照：

| 檔案 | 主題 |
|---|---|
| `jquery_basic.html` | Selector、click、hide |
| `jquery_values.html` | text、val、css |
| `jquery_template.html` | 共用jQuery頁面骨架 |
| `jquery_classattribute.html` | addClass、toggleClass、removeClass |
| `jquery_table.html` | show、attr、each、動態表格 |
| `jquery_wrapclass.html` | wrap |
| `jquery_mouse.html` | hover、focus、blur、this |

## 2. 載入jQuery並建立基本結構

前置條件：

- 已有VS Code與Live Server。
- 瀏覽器能連線至Google CDN；離線時下列jQuery網址無法載入。

建立HTML並在`head`載入jQuery 3.6.4：

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>jQuery Basic</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
</head>
<body>
    <button id="b1">Start</button>

    <script>
        const start = () => {
            // DOM完成後才註冊事件
        };

        $(document).ready(start);
    </script>
</body>
</html>
```

`<script src="...">`會要求瀏覽器下載並立即執行指定的JavaScript檔案。上例是從Google CDN載入壓縮過的jQuery 3.6.4；載入完成後，jQuery提供的全域名稱`$`與`jQuery`才可使用。

它的目的和Java的`import`相似，都是為了使用外部程式碼，但機制不同：

| 寫法 | 定義與效果 |
|---|---|
| `<script src="...jquery.min.js"></script>` | 傳統HTML Script載入；瀏覽器下載並執行整份檔案，jQuery把`$`、`jQuery`放入全域環境 |
| `import value from "./module.js"` | JavaScript Module語法；明確取得模組匯出的名稱，需要Module環境 |
| Java `import package.Type;` | 讓Java原始碼可用簡短類別名稱；不是由瀏覽器下載JavaScript |

載入順序必須是「先載入jQuery，再執行使用`$`的程式」：

```html
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js"></script>
<script>
    // 此處才能安全使用 $
    $(document).ready(start);
</script>
```

若不希望依賴網路，也可先下載jQuery，再以相對路徑載入：

```html
<script src="js/jquery-3.6.4.min.js"></script>
```

`$`是jQuery提供的函式入口。`$(document).ready(start)`表示DOM結構完成後執行`start`；因此`start`適合集中註冊頁面事件。

若Console顯示`$ is not defined`，代表jQuery沒有成功載入，應先檢查網路、CDN網址、檔案路徑及`script`順序。

## 3. Selector、click、hide與show

```html
<button>Start</button>
<button id="b1">Hide id</button>
<button id="b2">Hide class</button>
<button id="b3">Hide element Tag</button>
<span id="test">id Test</span>
<span class="test">class Test</span>
<p>p Test</p>
```

```javascript
const btnClick = () => {
    alert("Button clicked!");
};

const start = () => {
    $("button").click(btnClick);

    $("#b1").click(function () {
        $("#test").hide();
    });

    $("#b2").click(function () {
        $(".test").hide();
    });

    $("#b3").click(function () {
        $("p").hide();
    });
};
```

Selector仍使用CSS規則：

- `$("button")`：所有`button`標籤。
- `$("#b1")`：`id="b1"`。
- `$(".test")`：所有`class`包含`test`的元素。

因為`$("button").click(btnClick)`會替所有按鈕註冊alert，按下`b1`、`b2`或`b3`時會同時執行共用alert及各自的隱藏功能。

`.hide()`把元素隱藏；`.show()`可重新顯示。例如：

```javascript
$("#product").show();
```

## 4. 讀寫文字、輸入值與CSS

```html
<button id="b1">GET</button>
<button id="b2">SET</button>
<button id="b3">Change CSS</button>
<span id="test">id Test</span>
<input type="text" id="input1" value="Input Test">
<p class="p1">p Test</p>
```

```javascript
const start = () => {
    $("#b1").click(() => {
        const text = $("#test").text();
        const inputVal = $("#input1").val();
        console.log(text);
        console.log(inputVal);
    });

    $("#b2").click(() => {
        $("#test").text("New Text");
        $("#input1").val("New Input Value");
    });

    $("#b3").click(() => {
        $(".p1").css({
            "color": "red",
            "font-size": "30px"
        });
    });
};
```

同一方法依有無參數分成讀取與設定：

| 寫法 | 結果 |
|---|---|
| `.text()` | 讀取文字 |
| `.text("New Text")` | 設定文字 |
| `.val()` | 讀取表單欄位值 |
| `.val("New Value")` | 設定表單欄位值 |

`.css({...})`一次設定多個inline style；JavaScript物件中的CSS屬性名稱可使用`"font-size"`這種CSS原名。

## 5. 操作CSS class

先定義CSS：

```css
.big {
    font-size: 30px;
    color: red;
}

.small {
    font-size: 10px;
    color: blue;
}

.background {
    background-color: yellow;
}
```

```javascript
$("#b1").click(function () {
    $(".hello").addClass("big");
    $(".hello").addClass("background");
    $(".goodbye").addClass("small");
});

$("#b2").click(function () {
    $(".hello").toggleClass("big");
});

$("#b3").click(function () {
    $(".hello").removeClass("background");
});
```

- `.addClass()`：沒有指定class時加入；既有class不會被整個覆蓋。
- `.toggleClass()`：有就移除，沒有就加入。
- `.removeClass()`：移除指定class。

若只需要套用既有CSS，使用class通常比逐項呼叫`.css()`更容易維護。

## 6. 由Array動態建立表格

HTML先放一張隱藏的空表格：

```html
<button id="b1">Show Products</button>
<table border="0" width="40%" style="display:none" id="product"></table>
```

```javascript
$("#b1").click(function () {
    $("#product").show();
    $("#product").attr("border", "1");

    var arr = [
        { "name": "Apple", "price": 60 },
        { "name": "Lemon", "price": 90 },
        { "name": "Cherry", "price": 300 }
    ];

    $.each(arr, function (i) {
        var row = $("<tr></tr>");
        $("<td></td>").text(i + 1).appendTo(row);
        $("<td></td>").text(this.name).appendTo(row);
        $("<td></td>").text(this.price).appendTo(row);
        $("#product").append(row);
    });
});
```

處理順序：

```text
顯示table
→ 設定border Attribute
→ $.each逐筆讀取商品
→ 建立tr
→ 建立三個td並加入tr
→ 把tr加入table
```

在`$.each(arr, function (i) {...})`的一般函式內，`this`代表目前處理的Array項目，因此可讀取`this.name`與`this.price`。

重複按下按鈕會再次追加三列。若需求是每次重建表格，可在迴圈前清空：

```javascript
$("#product").empty();
```

## 7. `wrap`、`wrapAll`與只加class

原始HTML：

```html
<p class="inner">Hello 1</p>
<p>Hello X</p>
<p class="inner">Hello 2</p>
```

### 7.1 每個元素分別包裝

```javascript
$(".inner").wrap('<div class="new"></div>');
```

每個`.inner`各自得到一個外層`div`，原本的元素順序不變。

### 7.2 所有元素共同包裝

```javascript
$(".inner").wrapAll('<div class="new"></div>');
```

`.wrapAll()`必須把所有符合元素移到同一個連續容器中。原本位於兩個`.inner`中間的`Hello X`不符合Selector，因此最後會位於共同容器之外，看起來像被移到下方。

### 7.3 只需要樣式時

```javascript
$(".inner").addClass("new");
```

若目的只是套用`.new`樣式，不需要改變DOM階層，直接加入class最合適，也不會重新排列元素。

## 8. Mouse、Focus與Blur事件

```html
<input type="text" id="t1" value="Input Text">
<input type="text" id="t2" value="Input Text">
<div id="div1" style="width:200px;height:100px;border:1px solid blue"></div>
```

```javascript
$("#div1").hover(
    function () {
        $("#div1").css("background-color", "yellow");
    },
    function () {
        $("#div1").css("background-color", "white");
    }
);

$("input").focus(function () {
    $(this).css("background-color", "yellow");
});

$("input").blur(function () {
    $(this).css("background-color", "white");
});
```

- `.hover(in, out)`：滑鼠進入與離開時分別執行兩個函式。
- `.focus()`：欄位取得輸入焦點。
- `.blur()`：欄位失去輸入焦點。
- `this`：目前觸發事件的DOM元素；`$(this)`把它包成jQuery物件，才能呼叫`.css()`等jQuery方法。

## 9. 執行與成功判定

1. 在VS Code開啟HTML檔。
2. 使用Live Server開啟頁面。
3. 開啟Chrome DevTools Console。
4. 逐一測試按鈕、文字讀寫、class切換、表格產生、包裝及滑鼠事件。

成功判定：

- Console沒有`$ is not defined`或Selector相關錯誤。
- 按鈕事件只影響指定元素。
- GET／SET按鈕能讀寫文字與輸入值。
- 表格能顯示三筆商品。
- `wrap`與`wrapAll`呈現不同DOM排列。
- 輸入框與`div`能隨focus、blur及hover切換背景色。

## 10. 常見錯誤

- jQuery CDN尚未載入就先執行`$()`。
- ID Selector漏寫`#`，或class Selector漏寫`.`。
- 把`.text()`與`.val()`的使用對象混淆。
- 重複按下建立表格按鈕，卻沒有先`.empty()`，造成資料重複。
- 只想改樣式卻使用`.wrapAll()`，意外改變元素排列。
- 在Arrow Function中期待`this`代表事件元素；需要事件元素時可使用一般函式，或改從event參數取得目標。

## 11. 本章檢查表

- 能載入jQuery並使用`$(document).ready(...)`。
- 能區分標籤、ID及class Selector。
- 能使用jQuery讀寫文字、欄位值、CSS與class。
- 能由Object Array動態建立表格。
- 能解釋`wrap`與`wrapAll`的DOM差異。
- 能使用hover、focus、blur及`$(this)`。
