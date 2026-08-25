# JavaScript陣列、物件與JSON

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章整理Array、Object與JSON，並把基本寫法與可選的延伸寫法分開說明。

> 語法速查：[JavaScript陣列與物件](../語法字典/11_JavaScript核心_陣列與物件.md)

## 本章快速索引

- [1. Array與索引](#1-array與索引)
- [2. Array中保存Object](#2-array中保存object)
- [3. 前端與尾端增刪](#3-前端與尾端增刪)
- [4. 搜尋、刪除與替換指定項目](#4-搜尋刪除與替換指定項目)
- [5. `map`：逐項轉換並建立新Array](#5-map逐項轉換並建立新array)
- [6. `filter`：保留符合條件的項目](#6-filter保留符合條件的項目)
- [7. `reduce`：把多項資料累積成一個結果](#7-reduce把多項資料累積成一個結果)
- [8. 串接Array操作](#8-串接array操作)
- [9. JavaScript Object](#9-javascript-object)
- [10. `JSON.stringify`與`JSON.parse`](#10-jsonstringify與jsonparse)
- [11. 執行方式](#11-執行方式)
- [12. 常見錯誤](#12-常見錯誤)
- [13. 本章檢查表](#13-本章檢查表)

## 1. Array與索引

本節對應`array1.html`與`array.js`。

```javascript
const values = [1, 2, 3, 4, 5];

console.log(values[0]);     // 1
console.log(values.length); // 5
```

Array索引從0開始，最後一項的索引是`length - 1`。

傳統`for`迴圈：

```javascript
for (let i = 0; i < values.length; i++) {
    console.log(values[i]);
}
```

`forEach`巡覽：

```javascript
values.forEach(function (value, index) {
    console.log(index, value);
});
```

`forEach`用於逐項執行動作，不會根據callback的回傳值建立新Array。

## 2. Array中保存Object

`array1.js`：

```javascript
const students = [
    { name: 'John', score: 90 },
    { name: 'Mary', score: 85 },
    { name: 'Janny', score: 78 }
];

students.forEach(function (student, index) {
    console.log('index:' + index + ' ' + student.name + ' ' + student.score);
});
```

取值規則：

```javascript
students[0];       // 第一個Object
students[0].name;  // John
students[0].score; // 90
```

## 3. 前端與尾端增刪

範例檔案：`arrayaddelete.html`

| Method | 操作位置 | 回傳值 | 是否修改原Array |
|---|---|---|---|
| `push(value)` | 尾端新增 | 新的length | 是 |
| `pop()` | 尾端刪除 | 被刪除的項目；空Array時為`undefined` | 是 |
| `unshift(value)` | 前端新增 | 新的length | 是 |
| `shift()` | 前端刪除 | 被刪除的項目；空Array時為`undefined` | 是 |

```javascript
const fruits = [];

fruits.push('Apple');    // ['Apple']
fruits.unshift('Banana');// ['Banana', 'Apple']
fruits.pop();            // 回傳'Apple'，剩['Banana']
fruits.shift();          // 回傳'Banana'，剩[]
```

基本範例把結果輸出到Console。若希望同步顯示在頁面，可用`JSON.stringify(fruits)`把目前Array轉成文字並寫入DOM：

```javascript
document.getElementById('arrayDisplay').textContent =
    '目前陣列：' + JSON.stringify(fruits);
```

## 4. 搜尋、刪除與替換指定項目

範例檔案：`array_splice.html`

### 4.1 `indexOf`

```javascript
const index = fruits.indexOf(name);
```

- 找到時回傳第一個完全相等項目的索引。
- 找不到時回傳`-1`。
- String比較會區分大小寫。

因此應先確認：

```javascript
if (index >= 0) {
    // 可以操作該索引
}
```

### 4.2 `splice`

```javascript
fruits.splice(index, 1);         // 從index刪除1項
fruits.splice(index, 1, newName);// 刪除1項並在原位置放入newName
```

基本定義：

```javascript
array.splice(start, deleteCount, ...itemsToInsert);
```

`splice`會直接修改原Array，並回傳由「被刪除項目」組成的新Array。

若範例另外加入`showArray()`作為頁面狀態欄，刪除成功後也必須再次呼叫，否則畫面文字不會立刻同步。可在函式結束前補：

```javascript
showArray();
```

這是加入頁面狀態欄後需要注意的同步問題，不是Array刪除本身失敗。

## 5. `map`：逐項轉換並建立新Array

```javascript
const nums = [1, 2, 3, 4, 5];
const doubled = nums.map(n => n * 2);

console.log(nums);    // [1, 2, 3, 4, 5]
console.log(doubled); // [2, 4, 6, 8, 10]
```

`map`的callback每次回傳一個新值，最後形成與原Array相同長度的新Array；原Array通常不會被修改。

範例`array_func.html`將兩個Array組合成Object Array：

```javascript
const names = ['Alice', 'Bob', 'Charlie'];
const scores = [85, 92, 78];

const result = names.map((name, index) => {
    return {
        name: name,
        score: scores[index]
    };
});
```

前提是`scores[index]`存在。若兩個Array長度不同，缺少的位置會得到`undefined`。

## 6. `filter`：保留符合條件的項目

```javascript
const nums = [1, 2, 3, 4, 5];
const evens = nums.filter(n => n % 2 === 0);

console.log(evens); // [2, 4]
```

callback回傳Truthy的項目會被保留。`filter`回傳新Array，不修改原Array。

## 7. `reduce`：把多項資料累積成一個結果

數值加總的標準寫法：

```javascript
const nums = [1, 2, 3, 4, 5];
const total = nums.reduce((acc, n) => acc + n, 0);

console.log(total); // 15
```

參數定義：

```javascript
array.reduce((accumulator, currentValue) => nextAccumulator, initialValue);
```

數值加總時，`array_func.js`將`reduce()`的初始值設為`0`：

```javascript
const sum = nums.reduce((acc, n) => acc + n, 0);
```

結果是數值15。若要保留原Array，可改成：

```javascript
const sum = nums.reduce((acc, n) => acc + ' ' + n, '');
```

該版本是在串接String，實際得到`" 1 2 3 4 5"`，不是15。這個差異適合用來辨認：`reduce`的結果型別取決於初始值與每次回傳的累計值。

## 8. 串接Array操作

```javascript
const result = nums
    .filter(n => n > 2)
    .map(n => n * 10);

console.log(result); // [30, 40, 50]
```

執行順序是：

1. `filter`從`[1,2,3,4,5]`得到`[3,4,5]`。
2. `map`把每項乘10，得到`[30,40,50]`。

### 同一個Array參照與新Array

`array_test.js`用下列方式比較原陣列與新陣列：

```javascript
const a1 = [1, 2, 3, 4, 5];
const b1 = a1;

b1.forEach(function (value, index) {
    b1[index] = value * 2;
});
```

`b1 = a1`不會複製Array；兩個變數指向同一個Array，所以修改`b1`也會讓`a1`變成`[2,4,6,8,10]`。

若要建立新Array，可使用：

```javascript
const b1 = a1.map(value => value * 2);
```

此時`a1`維持原值，`b1`保存轉換結果。

## 9. JavaScript Object

範例檔案：`createobj.html`

```javascript
const person = {};

person.name = prompt('Enter name');
person.age = parseInt(prompt('Enter age'));
```

| 語法 | 用途 |
|---|---|
| `person.name` | 讀寫property |
| `person.age` | 動態新增或更新`age`property |

以下範例先建立空Object，再於取得輸入後動態加入property。

也可以另外加入Object method：

```javascript
person.showobject = function () {
    console.log('Name: ' + this.name + ', Age: ' + this.age);
};
```

這個延伸可用來學習：以`person.showobject()`呼叫時，method中的`this`會指向`person`。若把method取出後單獨呼叫，`this`的結果可能不同。

## 10. `JSON.stringify`與`JSON.parse`

JavaScript Object轉JSON String：

```javascript
const jsonText = JSON.stringify(person);
```

JSON String轉回JavaScript值：

```javascript
const obj = JSON.parse(jsonText);
```

重要差異：

- JSON只保存資料，不保存Function。
- 如果Object含有Function，`JSON.stringify`會省略該Function property；例如`showobject`method不會進入JSON。
- JSON的property名稱與String必須使用雙引號。
- JSON格式錯誤時，`JSON.parse`會拋出`SyntaxError`。

可在解析外部內容時處理錯誤：

```javascript
try {
    const obj = JSON.parse(data);
    console.log(obj);
} catch (error) {
    console.error('JSON格式錯誤', error);
}
```

## 11. 執行方式

HTML範例：

1. 在VS Code開啟包含`day2`的練習資料夾。
2. 對`day2`中的對應HTML選擇`Open with Live Server`。
3. 按下頁面按鈕。
4. 同時觀察頁面內容及DevTools Console。

純JavaScript檔案可在Terminal執行：

```powershell
cd <練習資料夾>\day2
node array.js
node array_func.js
node array_test.js
```

這些檔案只使用JavaScript與`console.log`，不依賴瀏覽器DOM，適合用Node.js執行。

## 12. 常見錯誤

| 現象 | 原因 | 修正或判讀方式 |
|---|---|---|
| `indexOf`回傳`-1` | 沒有完全相等的項目，或大小寫不同 | 在`splice`前先判斷`index >= 0` |
| `pop`／`shift`回傳`undefined` | Array已空 | 刪除前檢查`length > 0` |
| 刪除後Console已更新但頁面沒變 | `ArrayOP()`沒有呼叫`showArray()` | 操作後重新呼叫畫面更新函式 |
| `map`結果中出現`score: undefined` | `scores`比`names`短 | 先驗證兩個Array長度或改用同一份Object資料 |
| `reduce`沒有得到15 | 初始值及運算式在做String串接 | 數值加總使用`(acc,n) => acc+n, 0` |
| JSON轉換後method消失 | JSON不支援Function | JSON只用來傳輸資料；method由程式重新提供 |
| `JSON.parse`發生`SyntaxError` | 輸入不是有效JSON | 檢查雙引號、逗號與括號，必要時使用`try/catch` |

## 13. 本章檢查表

- [ ] 能用索引、`for`與`forEach`巡覽Array
- [ ] 能在Array中保存並讀取Object
- [ ] 能分辨`push`、`pop`、`unshift`與`shift`
- [ ] 能搭配`indexOf`與`splice`刪除或替換項目
- [ ] 能說明`map`、`filter`與`reduce`的回傳結果
- [ ] 能辨認String串接與Number加總的差異
- [ ] 能安全串接`filter`與`map`
- [ ] 能建立含property與method的Object
- [ ] 能說明method中的`this`
- [ ] 能使用`JSON.stringify`與`JSON.parse`
- [ ] 知道JSON不會保存Function
