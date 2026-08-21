# JavaScript現代語法與Fetch API

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章接續第27章，先練習Arrow Function、Object解構、Spread／Rest，再使用瀏覽器內建的`fetch()`呼叫外部API與自己的Spring Boot商品API。課堂來源依學習順序為`week6\day4\arrow_function.html`、`destructure_data.html`、`spread_function.html`及`fetch.html`。

## 1. 前置條件

- 已完成第21章，知道Object與JSON的差別。
- 已完成第27章的`sbstatus0821`商品API。
- 後端具有`GET /api/products/{id}`，且Controller已設定`@CrossOrigin`。
- 以VS Code與Live Server執行前端HTML；老師原檔維持只讀，需要練習時在自己的工作資料夾建立副本。

## 2. 本章功能快速索引

| 想完成的事情 | 主要寫法 |
|---|---|
| 建立Arrow Function | `const fn = (參數) => { ... }` |
| 在字串中插入變數 | `` `${value}` `` |
| 從Object取出指定屬性 | `const { name, age } = person` |
| 在函式參數直接解構Object | `({ name, age }) => { ... }` |
| 複製Object並新增或覆寫欄位 | `{ ...person, country: "Taiwan" }` |
| 取出部分欄位並收集其餘欄位 | `const { name, ...rest } = person` |
| 使用瀏覽器內建API發送Request | `fetch(url)` |
| 宣告可使用`await`的函式 | `async function name() { ... }` |
| 等待Promise完成 | `await` |
| 將JSON Response解析成JavaScript資料 | `await response.json()` |
| 捕捉非同步流程拋出的錯誤 | `try { ... } catch (error) { ... }` |

## 3. Arrow Function計算次方

建立`arrow_function.html`，載入jQuery後加入按鈕：

```html
<button id="b1">x 的 y 次方</button>
```

計算函式：

```javascript
const power = (x, y) => {
    let result = 1;
    for (let i = 0; i < y; i++) {
        result *= x;
    }
    return result;
};
```

一般函式的等效寫法是：

```javascript
function power(x, y) {
    let result = 1;
    for (let i = 0; i < y; i++) {
        result *= x;
    }
    return result;
}
```

按鈕事件：

```javascript
const start = () => {
    $("#b1").click(function () {
        const x = parseFloat(prompt("請輸入底數 x："));
        const y = parseInt(prompt("請輸入指數 y："));
        alert(`${x} 的 ${y} 次方是：` + power(x, y));
    });
};

$(document).ready(start);
```

輸入`2`與`3`時，預期顯示`2 的 3 次方是：8`。本例的`power()`只適合非負整數指數；負數或小數指數需要不同演算法。

## 4. Object解構賦值

建立`destructure_data.html`：

```javascript
const greet = ({ name, age }) => {
    alert(`${name} is ${age} years old.`);
};

const person = {
    name: "Alice",
    city: "Taipei",
    age: 30
};

greet(person);
```

函式收到完整`person`，但參數位置的`{ name, age }`只取出同名屬性。`city`仍存在於原Object，只是這個函式沒有使用。

也可以先解構，再傳入其他處理：

```javascript
const { name, age } = person;
console.log(name);
console.log(age);
```

解構時依「屬性名稱」配對，不依Object中的排列順序。屬性不存在時，得到`undefined`。

## 5. Spread：複製並更新Object

建立`spread_function.html`。第一個按鈕執行：

```javascript
let person = {
    name: "Alice",
    city: "Taipei",
    age: 30
};

person = {
    ...person,
    country: "Taiwan",
    age: 18
};

console.log(person);
```

`...person`將原Object的可列舉屬性展開到新Object。後面再次出現`age`，因此後面的`18`覆蓋原本的`30`。

預期結果：

```javascript
{
    name: "Alice",
    city: "Taipei",
    age: 18,
    country: "Taiwan"
}
```

順序會影響結果：

```javascript
{ ...person, age: 18 } // age最後是18
{ age: 18, ...person } // person原本的age會覆蓋18
```

Spread建立的是淺層複製；若屬性中還有Object或Array，內層資料仍可能和原Object共用參考。

## 6. Rest：收集解構後剩餘的屬性

第二個按鈕執行：

```javascript
const person = {
    name: "Alice",
    city: "Taipei",
    age: 30
};

const { name, ...rest } = person;

console.log("name:", name);
console.log("rest:", rest);
```

預期結果：

```text
name: Alice
rest: { city: "Taipei", age: 30 }
```

同樣的`...`符號會依位置扮演不同角色：

| 位置 | 名稱 | 功能 |
|---|---|---|
| 新Object內的`{ ...person }` | Spread | 把既有屬性展開出去 |
| 解構左側的`{ name, ...rest }` | Rest | 把尚未取出的屬性收集成新Object |

Rest必須放在解構格式的最後面。

## 7. 使用Fetch API取得外部資料

建立`fetch.html`並加入按鈕：

```html
<button id="b1">Fetch User 1</button>
<button id="b2">Fetch Product 1</button>
```

外部使用者API：

```javascript
async function getUser(id) {
    try {
        const response = await fetch(
            `https://jsonplaceholder.typicode.com/users/${id}`
        );

        const { name, email } = await response.json();
        console.log(`姓名：${name}，Email：${email}`);
    } catch (error) {
        console.error("錯誤：", error);
    }
}
```

執行流程：

1. `async`使函式可以使用`await`。
2. `fetch(url)`發送HTTP Request並回傳Promise。
3. 第一個`await`等待取得HTTP Response。
4. `response.json()`讀取Response Body並解析JSON，也會回傳Promise。
5. 第二個`await`等待JSON解析完成。
6. `{ name, email }`從解析後的Object取出需要的欄位。
7. 網路層錯誤或程式主動拋出的錯誤由`catch`處理。

`fetch()`是瀏覽器內建API，不需要jQuery。課堂頁面仍載入jQuery，是因為按鈕事件與`document.ready`沿用jQuery寫法。

## 8. 使用Fetch API呼叫自己的Spring Boot API

後端先啟動`sbstatus0821`，確認以下網址能取得商品：

```text
http://localhost:8080/api/products/1
```

前端函式：

```javascript
async function getProduct(id) {
    try {
        const response = await fetch(
            `http://localhost:8080/api/products/${id}`
        );

        const { name, price } = await response.json();
        console.log(`產品名稱：${name}，價格：${price}`);
    } catch (error) {
        console.error("錯誤：", error);
    }
}
```

按鈕事件：

```javascript
const start = () => {
    $("#b1").click(function () {
        getUser(1);
    });

    $("#b2").click(function () {
        getProduct(1);
    });
};

$(document).ready(start);
```

成功時，Chrome DevTools Console應看到外部使用者的姓名／Email，以及本機第1號商品的名稱／價格。

## 9. `fetch()`處理404／500時的限制

課堂基本範例直接執行`response.json()`，適合測試確定存在的ID。不過`fetch()`收到404或500時，Promise通常仍會正常完成，不會只因HTTP錯誤自動進入`catch`。

需要完整處理HTTP錯誤時，在解析Body前檢查：

```javascript
const response = await fetch(url);

if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
}

const data = await response.json();
```

- `response.ok`在狀態碼200～299時為`true`。
- `response.status`保存實際HTTP狀態碼。
- `throw`主動建立失敗流程，讓`catch`統一處理。

這是建立在課堂範例上的錯誤處理補強；課堂原始`fetch.html`尚未加入`response.ok`判斷。

## 10. 完整測試順序

1. 以Spring Boot App啟動`sbstatus0821`。
2. 瀏覽器開啟`http://localhost:8080/api/products/1`，確認回200與單筆商品JSON。
3. 在自己的VS Code工作資料夾建立四份HTML，填入本章對應程式。
4. 以Live Server依序開啟四份HTML。
5. 測試`power()`輸入`2`、`3`是否得到`8`。
6. 測試解構範例是否顯示Alice與30。
7. 測試Spread／Rest後，在Console確認覆寫與剩餘欄位結果。
8. 開啟`fetch.html`，按兩個按鈕並檢查Console。

## 11. 常見錯誤

| 現象 | 優先檢查 |
|---|---|
| `${name}`原樣顯示 | 外層是否使用反引號，而不是單引號或雙引號 |
| `name`是`undefined` | Object是否真的有同名屬性、大小寫是否一致 |
| Spread後欄位沒有預期值 | 相同欄位的先後順序，後面的值會覆蓋前面 |
| `await is only valid...` | `await`是否位於`async`函式內 |
| `response.json()`失敗 | Response是否真的是JSON；204沒有Body，HTML錯誤頁也不是JSON |
| 外部API無法取得 | 網路、第三方服務狀態與瀏覽器Network紀錄 |
| 本機商品API連線失敗 | Spring Boot是否啟動、Port與URL是否正確 |
| 瀏覽器顯示CORS錯誤 | Spring Boot Controller是否有`@CrossOrigin`或全域CORS設定 |

## 12. 本章檢查表

- [ ] 能以Arrow Function接收參數並回傳結果。
- [ ] 能用Object解構只取出需要的欄位。
- [ ] 能區分Spread與Rest的出現位置及用途。
- [ ] 知道Object Spread是淺層複製，且後面的同名欄位會覆蓋前面。
- [ ] 能說明`async`、兩個`await`及`response.json()`的執行順序。
- [ ] 能用`fetch()`呼叫外部API與`GET /api/products/{id}`。
- [ ] 知道`fetch()`不會只因404／500自動進入`catch`，完整錯誤處理需要檢查`response.ok`。
