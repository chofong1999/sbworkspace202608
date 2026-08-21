# JavaScript課程功能快速索引

這份索引用來回答：「目前JavaScript課程教過哪些功能？想完成某件事時應該閱讀哪一章？」

索引只負責導向，不重複各章的完整解釋。需要知道成立條件、執行流程、完整範例與常見錯誤時，再進入對應章節。

## 1. 十秒找到對應章節

| 正在處理的範圍 | 主要功能 | 對應章節 |
|---|---|---|
| HTML與CSS | 頁面結構、CSS套用、Selector、背景圖片 | [第19章](19_HTML_CSS與JavaScript基礎.md) |
| JavaScript語言基礎 | 變數、型別、輸入、條件、亂數、迴圈 | [第20章](20_JavaScript變數型別條件與迴圈.md) |
| 資料集合與資料格式 | Array、Object、JSON、map／filter／reduce | [第21章](21_JavaScript陣列_物件與JSON.md) |
| 瀏覽器視窗功能 | 開新視窗、換網址、Timer、圖片輪播 | [第22章](22_JavaScript_BOM視窗導向與定時器.md) |
| 操作網頁內容 | 選取元素、改內容／CSS、建立與刪除節點、事件 | [第23章](23_JavaScript_DOM元素選取_節點與動態修改.md) |
| 表單送出 | 取得欄位、submit、取消送出、前端驗證 | [第24章](24_JavaScript表單事件與送出驗證.md) |
| 使用jQuery操作畫面 | Selector、事件、內容、樣式、節點、表格 | [第25章](25_jQuery_DOM操作_事件與樣式.md) |
| 前後端分離 | jQuery GET／POST呼叫Spring Boot REST API | [第26章](26_jQuery_AJAX與SpringBoot前後端分離.md) |
| AJAX資料與錯誤處理 | 本機JSON、外部API、JSON POST、HTTP狀態碼 | [第27章](27_jQuery_AJAX_JSON資料來源與HTTP狀態處理.md) |
| 現代JavaScript與Fetch | Arrow Function、解構、Spread／Rest、async／await、Fetch API | [第28章](28_JavaScript現代語法與Fetch_API.md) |

建議首次學習依第19章到第28章的順序閱讀；實作時則依功能直接跳至需要的章節。

## 2. HTML、CSS與執行環境

對應：[第19章：HTML、CSS與JavaScript基礎](19_HTML_CSS與JavaScript基礎.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 建立HTML頁面 | `<!DOCTYPE html>`、`html`、`head`、`body` |
| 載入外部CSS | `<link href="..." rel="stylesheet">` |
| 直接替單一元素設定樣式 | `style="..."` |
| 依class選取元素 | `.className` |
| 依id選取元素 | `#idName` |
| 依標籤選取元素 | `h1`、`p`等標籤名稱 |
| 設定背景圖片 | `background-image: url(...)` |
| 在HTML中執行JavaScript | `<script>...</script>` |
| 載入外部JavaScript | `<script src="..."></script>` |
| 在瀏覽器查看除錯輸出 | `console.log(...)`、Chrome DevTools |
| 顯示彈出訊息 | `alert(...)` |
| 預覽本機HTML | VS Code Live Server |
| 不透過瀏覽器執行JS | `node 檔名.js` |

## 3. 變數、型別、條件與迴圈

對應：[第20章：JavaScript變數、型別、條件與迴圈](20_JavaScript變數型別條件與迴圈.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 宣告可重新指定的變數 | `let`、`var` |
| 宣告不可重新指定的名稱 | `const` |
| 查看值的型別 | `typeof value` |
| 讓使用者輸入文字 | `prompt(...)` |
| 讓使用者確認或取消 | `confirm(...)` |
| 將文字轉成數字 | `Number(...)`、`parseInt(...)` |
| 判斷條件 | `if`、`else if`、`else` |
| 用一個表達式二選一 | `條件 ? 成立值 : 不成立值` |
| 重複固定次數 | `for` |
| 條件成立期間持續執行 | `while` |
| 判斷Truthy與Falsy | 條件位置中的自動Boolean判定 |
| 建立函式 | `function name(...)` |
| 傳回結果 | `return` |
| 產生亂數 | `Math.random()` |
| 去除小數部分 | `Math.floor(...)` |

## 4. Array、Object與JSON

對應：[第21章：JavaScript陣列、物件與JSON](21_JavaScript陣列_物件與JSON.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 建立Array | `[value1, value2]` |
| 依索引讀取項目 | `array[index]` |
| 取得Array長度 | `array.length` |
| 逐項處理 | `forEach(...)` |
| 尾端新增／刪除 | `push(...)`、`pop()` |
| 前端新增／刪除 | `unshift(...)`、`shift()` |
| 尋找項目索引 | `indexOf(...)` |
| 刪除或替換指定範圍 | `splice(...)` |
| 轉換每一項並建立新Array | `map(...)` |
| 保留符合條件的項目 | `filter(...)` |
| 將多項累積成一個結果 | `reduce(...)` |
| 建立Object | `{ key: value }` |
| 讀寫Object屬性 | `object.key`、`object[key]` |
| Object轉成JSON字串 | `JSON.stringify(...)` |
| JSON字串轉成Object | `JSON.parse(...)` |
| 分辨共用同一個Array與建立新Array | 直接指派、`map(...)`等新Array操作 |

## 5. BOM：視窗、網址與Timer

對應：[第22章：JavaScript BOM](22_JavaScript_BOM視窗導向與定時器.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 開啟新視窗或分頁 | `window.open(...)` |
| 導向另一個網址 | `location.href = "..."` |
| 週期性執行函式 | `setInterval(function, milliseconds)` |
| 停止週期性工作 | `clearInterval(timerId)` |
| 顯示目前時間 | `new Date().toLocaleTimeString()` |
| 定時切換圖片 | Timer搭配`img.src`與Array索引 |
| 避免重複建立Timer | 保存Timer ID並檢查目前狀態 |

## 6. DOM：查找與修改網頁元素

對應：[第23章：JavaScript DOM](23_JavaScript_DOM元素選取_節點與動態修改.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 依CSS Selector取得第一個元素 | `querySelector(...)` |
| 取得全部符合元素 | `querySelectorAll(...)` |
| 依id取得元素 | `getElementById(...)` |
| 讀寫純文字內容 | `textContent` |
| 讀寫含HTML標籤的內容 | `innerHTML` |
| 取得表單欄位目前值 | `value` |
| 動態修改CSS | `element.style.property` |
| 建立元素節點 | `document.createElement(...)` |
| 建立文字節點 | `document.createTextNode(...)` |
| 加入子節點 | `appendChild(...)` |
| 查看子節點 | `childNodes` |
| 動態建立表格列與儲存格 | `insertRow(...)`、`insertCell(...)` |
| 讀取或設定HTML Attribute | `getAttribute(...)`、`setAttribute(...)` |
| 移除節點 | `removeChild(...)` |
| 刪除表格列 | `deleteRow(...)` |
| 直接設定HTML事件 | `onclick="..."` |
| 使用JavaScript註冊事件 | `addEventListener(...)` |
| 取得目前觸發事件的元素 | inline handler中的`this` |
| 處理鍵盤事件 | Keyboard Event、`event.key` |

## 7. 表單事件與送出驗證

對應：[第24章：JavaScript表單事件與送出驗證](24_JavaScript表單事件與送出驗證.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 監聽表單送出 | `form.addEventListener('submit', handler)` |
| 取得輸入欄位 | `querySelector('#id')` |
| 取得使用者輸入內容 | `input.value` |
| 判斷任一欄為空 | `value === "" || otherValue === ""` |
| 排除只有空白的輸入 | `value.trim()` |
| 取消目前這次送出 | `event.preventDefault()` |
| 指定表單提交目標 | `<form action="...">` |
| 指定GET或POST | `<form method="...">` |
| 使用HTML內建驗證 | `type="email"`、`required` |
| 設計完整驗證 | HTML基本限制＋JavaScript互動＋後端最終驗證 |

## 8. jQuery畫面操作

對應：[第25章：jQuery DOM操作、事件與樣式](25_jQuery_DOM操作_事件與樣式.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 等待DOM完成 | `$(document).ready(...)` |
| 以jQuery Selector取得元素 | `$("tag")`、`$("#id")`、`$(".class")` |
| 註冊點擊事件 | `.click(...)` |
| 隱藏或顯示 | `.hide()`、`.show()` |
| 讀寫文字與表單值 | `.text()`、`.val()` |
| 修改CSS或class | `.css()`、`.addClass()`、`.toggleClass()`、`.removeClass()` |
| 動態建立表格 | `$.each()`、`$("<tr>")`、`.appendTo()` |
| 包裝節點 | `.wrap()`、`.wrapAll()` |
| 處理滑鼠及焦點事件 | `.hover()`、`.focus()`、`.blur()` |

## 9. jQuery AJAX與Spring Boot API

對應：[第26章：jQuery AJAX與Spring Boot前後端分離](26_jQuery_AJAX與SpringBoot前後端分離.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 前端發送GET | `$.get(...)` |
| 前端發送POST | `$.post(...)` |
| 建立REST API | `@RestController` |
| 接收表單格式資料 | `@ModelAttribute` |
| 允許Live Server呼叫後端 | `@CrossOrigin` |
| 回傳200與Body | `ResponseEntity.ok(...)` |

## 10. jQuery AJAX資料來源與狀態處理

對應：[第27章：jQuery AJAX、JSON資料來源與HTTP狀態](27_jQuery_AJAX_JSON資料來源與HTTP狀態處理.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 載入線上jQuery | `<script src="https://...jquery.min.js"></script>` |
| 完整設定AJAX Request | `$.ajax({ method, url, ... })` |
| 讀取同資料夾JSON | `url: "air.json"` |
| 指定預期Response格式 | `dataType: "json"` |
| 傳送JSON Request Body | `JSON.stringify(...)`、`contentType: "application/json"` |
| 成功後處理資料 | `success` |
| 依404或500分別處理 | `statusCode` |
| 處理共通AJAX錯誤 | `error` |

## 11. 現代JavaScript語法與Fetch API

對應：[第28章：JavaScript現代語法與Fetch API](28_JavaScript現代語法與Fetch_API.md)

| 想完成的事情 | 主要寫法或功能 |
|---|---|
| 建立Arrow Function | `const fn = (參數) => { ... }` |
| 在字串插入變數 | `` `${value}` `` |
| 從Object取出指定屬性 | `const { name, age } = person` |
| 在函式參數直接解構 | `({ name, age }) => { ... }` |
| 複製Object並新增或覆寫欄位 | `{ ...person, age: 18 }` |
| 收集解構後剩餘欄位 | `const { name, ...rest } = person` |
| 使用瀏覽器內建Request API | `fetch(url)` |
| 等待非同步結果 | `async`、`await` |
| 解析JSON Response | `await response.json()` |
| 判斷HTTP Request是否成功 | `response.ok`、`response.status` |
| 捕捉非同步錯誤 | `try...catch` |

## 12. 容易找錯章節的情況

| 問題 | 應先閱讀 |
|---|---|
| 不知道CSS為何沒有套用 | 第19章 |
| 不知道值為何是String／Boolean | 第20章 |
| 要新增、刪除、篩選或轉換一組資料 | 第21章 |
| 要控制分頁、網址、時間或輪播 | 第22章 |
| 要改變目前畫面中的HTML內容 | 第23章 |
| 要在表單送出前檢查資料 | 第24章 |
| `#name`看不懂 | 第19章先理解CSS ID Selector；第24章看它如何傳入`querySelector()` |
| 要修改元素樣式 | 靜態樣式讀第19章；執行期間動態修改讀第23章 |
| 想用較短寫法操作DOM | 第25章 |
| 想從前端呼叫Spring Boot API | 第26章 |
| 想讀本機JSON、外部API或處理404 | 第27章 |
| 想用JavaScript內建`fetch()`取代jQuery AJAX | 第28章 |
| 看不懂`{ name, ...rest }`或`{ ...person }` | 第28章；兩者分別是Rest與Spread |
