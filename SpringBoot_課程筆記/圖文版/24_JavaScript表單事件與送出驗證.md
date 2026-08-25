# JavaScript表單事件與送出驗證

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章接續DOM與Event，使用`day3/DOM_FormCheck.html`示範：在表單真正送出以前讀取欄位、檢查資料，並在驗證失敗時取消送出。

> 語法速查：[表單與事件](../語法字典/12_DOM_BOM表單與Fetch.md#form)

## 本章快速索引

- [1. 完成後的效果](#1-完成後的效果)
- [2. 建立完整範例](#2-建立完整範例)
- [3. `submit`事件](#3-submit事件)
- [4. 取得表單欄位值](#4-取得表單欄位值)
- [5. 判斷是否有空欄位](#5-判斷是否有空欄位)
- [6. `event.preventDefault()`的確切作用](#6-eventpreventdefault的確切作用)
- [7. `action`與`method`](#7-action與method)
- [8. HTML內建驗證與JavaScript驗證的分工](#8-html內建驗證與javascript驗證的分工)
- [9. 執行與驗證](#9-執行與驗證)
- [10. preventDefault放置位置比較](#10-preventdefault放置位置比較)
- [11. 本章檢查表](#11-本章檢查表)

## 1. 完成後的效果

頁面包含姓名、Email與送出按鈕。依輸入內容會有三種結果：

| 操作 | 結果 |
|---|---|
| 姓名或Email留空 | 顯示`Please fill in all fields.`，並取消送出 |
| 兩欄都有值且Email格式有效 | 先顯示`Form submitted successfully!`，再執行表單的`action` |
| Email有內容但格式不符合Email規則 | 瀏覽器顯示內建格式提示，表單不送出 |

第三種結果來自`<input type="email">`的瀏覽器內建constraint validation，不是`formCheck()`內的空字串判斷。

## 2. 建立完整範例

在`day3/`建立`DOM_FormCheck.html`：

```html
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8" />
    <title>Form Check</title>
</head>

<body>
    <form id="myForm"
          action="javascript:alert('表單傳送成功!');"
          method="post">
        Name: <input type="text" name="fname" id="fname"><br><br>
        Email: <input type="email" name="email" id="email"><br><br>
        <input type="submit" value="Submit">
    </form>

    <script>
        const formCheck = (event) => {
            var fname = document.querySelector('#fname').value;
            var email = document.querySelector('#email').value;

            if (fname === "" || email === "") {
                alert("Please fill in all fields.");
                event.preventDefault();
            } else {
                alert("Form submitted successfully!");
            }
        }

        var form = document.querySelector('#myForm');
        form.addEventListener('submit', formCheck);
    </script>
</body>

</html>
```

應把`<script>`放在`</body>`之前，讓表單元素建立完成後才執行查找。即使部分瀏覽器能容錯執行寫在`</body>`之後的Script，也不是建議的HTML結構。

## 3. `submit`事件

```javascript
form.addEventListener('submit', formCheck);
```

`submit`是表單的送出事件。按下submit按鈕、在欄位中按Enter，或由其他可觸發提交的操作送出表單時，都應在表單層統一處理驗證。

不要只監聽按鈕的`click`：`click`只表示按鈕被按下，不等於涵蓋表單所有送出方式。

事件發生時，瀏覽器會把`Event`物件傳給處理函式：

```javascript
const formCheck = (event) => {
    // event代表本次submit事件
}
```

## 4. 取得表單欄位值

```javascript
var fname = document.querySelector('#fname').value;
var email = document.querySelector('#email').value;
```

### 4.1 `#`是CSS的ID Selector

`querySelector()`接收的是**CSS Selector字串**。其中`#`表示「依HTML元素的`id`選取」：

```html
<input id="fname">
```

對應的Selector為：

```css
#fname
```

放入JavaScript函式時必須寫成字串：

```javascript
document.querySelector('#fname')
```

這段語法可拆成：

| 部分 | 所屬語法 | 定義 |
|---|---|---|
| `document` | DOM API | 目前載入的HTML文件物件 |
| `.querySelector(...)` | DOM方法 | 依CSS Selector尋找第一個符合的元素 |
| `'...'` | JavaScript字串 | 把Selector文字當成函式參數傳入 |
| `#` | CSS Selector | 選取具有指定`id`的元素 |
| `fname` | ID名稱 | 必須對應HTML中的`id="fname"` |

成立條件：

- HTML中必須存在相符的`id`。
- 名稱及大小寫必須一致。
- 同一份HTML中的`id`應保持唯一。
- 若找不到元素，`querySelector()`會回傳`null`；再讀取`.value`便會發生錯誤。

常見Selector比較：

```javascript
document.querySelector('#fname');    // #：依id選取
document.querySelector('.required'); // .：依class選取
document.querySelector('input');     // 無前綴：依標籤名稱選取
```

若使用專門依ID查找的方法，參數只填ID名稱，不加`#`：

```javascript
document.getElementById('fname');
```

`#`只在CSS Selector寫法中代表ID。它不是元素ID本身的一部分，因此HTML仍寫`id="fname"`，不能寫成`id="#fname"`。

### 4.2 `.value`取得輸入內容

完整敘述包含兩個步驟：

1. `querySelector('#fname')`依ID取得`<input>`元素。
2. `.value`讀取使用者目前輸入的字串。

只寫`document.querySelector('#fname')`得到的是元素物件，不是輸入內容。

## 5. 判斷是否有空欄位

```javascript
if (fname === "" || email === "") {
```

- `=== ""`：判斷值是否為空字串。
- `||`：邏輯OR；任一條件成立，整個條件就成立。

因此姓名或Email任一欄未填，就進入驗證失敗分支。

若還要把只含空白的輸入視為空值，可改用：

```javascript
if (fname.trim() === "" || email.trim() === "") {
```

`trim()`會移除字串前後的空白後再判斷。

## 6. `event.preventDefault()`的確切作用

```javascript
event.preventDefault();
```

它會取消這一次事件的瀏覽器預設行為。對`submit`事件而言，預設行為就是依`action`與`method`送出表單。

本例只在驗證失敗時呼叫：

```text
觸發submit
  → 執行formCheck(event)
  → 欄位有空值：preventDefault()，停止送出
  → 欄位皆有值：不取消，繼續執行action
```

若把`event.preventDefault()`放在函式第一行，無論資料是否正確都會取消送出。這種寫法適合「完全由JavaScript接管後續處理」的情況，例如改用`fetch()`傳送JSON；若仍要讓瀏覽器依`action`送出，就只能在失敗分支取消。

## 7. `action`與`method`

```html
<form action="javascript:alert('表單傳送成功!');" method="post">
```

- `action`：表單通過驗證後要前往或提交的目標。
- `method="post"`：指定使用HTTP POST送出。

為了不連接後端也能觀察「表單確實繼續送出」，練習時可暫時把JavaScript寫入`action`。正式網站應把`action`設為伺服器端URL，例如：

```html
<form action="/api/users" method="post">
```

不要把`javascript:`形式當成正式專案的表單提交方式。

## 8. HTML內建驗證與JavaScript驗證的分工

```html
<input type="email" name="email" id="email">
```

`type="email"`會要求非空輸入符合基本Email格式，但它本身沒有要求必填。若要先由HTML處理必填，可加入`required`：

```html
<input type="text" name="fname" id="fname" required>
<input type="email" name="email" id="email" required>
```

兩層驗證的定位：

| 層級 | 適合處理 | 能否取代後端驗證 |
|---|---|---|
| HTML屬性 | `required`、輸入型別、長度等基本限制 | 否 |
| JavaScript | 跨欄位條件、即時提示、自訂互動 | 否 |
| 後端 | 資料正確性、安全性與最終規則 | 不可省略 |

前端驗證是改善操作體驗；使用者仍能繞過前端直接送出request，所以後端必須再次驗證。

## 9. 執行與驗證

1. 在VS Code開啟包含`day3`的練習資料夾。
2. 對`day3\DOM_FormCheck.html`選擇`Open with Live Server`。
3. 先讓其中一欄空白並送出，確認只出現缺欄提示，沒有執行`action`。
4. 輸入不符合Email格式的內容，確認瀏覽器顯示格式提示。
5. 姓名與Email都輸入有效內容後送出，確認成功分支與`action`都會執行。

## 10. preventDefault放置位置比較

`event.preventDefault()`可以只在驗證失敗時呼叫，讓資料正確時維持正常提交；若在函式開頭無條件呼叫，則所有送出都會被取消，適合尚未連接後端時觀察資料，但完成版必須另行處理提交。

兩者沒有誰在所有情況下都比較正確，差別取決於需求：

| 寫法 | 成功時是否執行表單`action` | 使用時機 |
|---|---|---|
| 只在驗證失敗時取消 | 會 | 成功後仍採用一般表單送出 |
| 一進函式就取消 | 不會 | JavaScript要自行處理資料或只做畫面示範 |

本章主線採條件式取消，讓資料正確時可以繼續執行表單`action`。

## 11. 本章檢查表

- 能說明為何應監聽表單的`submit`，而不只監聽按鈕`click`。
- 能以`.value`取得輸入欄位內容。
- 能用`event.preventDefault()`取消驗證失敗的送出。
- 知道把`preventDefault()`放在不同位置會改變成功分支行為。
- 能區分HTML、JavaScript與後端驗證的責任。
- 能依本章步驟重現空值、錯誤Email與成功送出三種結果。
