# JavaScript變數、型別、條件與迴圈

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章以老師的`week6\day1`原檔為主，整理`const`、資料型別、Truthiness、Dialog、輸入轉型、條件判斷、迴圈、函式與外部Script。

## 1. `const`限制的是重新指派

課堂檔案：`const_test.html`

```javascript
const carName = 'Volvo';
alert(carName);
carName = 'Toyota'; // TypeError：不能重新指派const
```

`const`宣告的變數必須在宣告時給值，而且之後不能讓變數指向另一個值。

若值是Array或Object，仍可修改其內部內容：

```javascript
const x = [1, 2, 3];
x[0] = 10;      // 可以，x仍指向原本的Array

const y = [4, 5, 6];
y = [7, 8, 9];  // 不可以，試圖讓y改指向另一個Array
```

| 操作 | `const`是否允許 | 原因 |
|---|---|---|
| 修改Array元素 | 允許 | 沒有重新指派變數 |
| 修改Object property | 允許 | 沒有重新指派變數 |
| 指派另一個Array／Object | 不允許 | 改變了變數保存的參照 |

## 2. `typeof`與JavaScript型別

課堂檔案：`types.html`

```javascript
var a = 1;
var b = '1';
var c = true;
var d = null;
var e;
var f = { name: 'John', age: 30 };
var g = [1, 2, 3];

console.log(typeof a); // number
console.log(typeof b); // string
console.log(typeof c); // boolean
console.log(typeof d); // object
console.log(typeof e); // undefined
console.log(typeof f); // object
console.log(typeof g); // object
```

重要限制：

- `typeof null`得到`"object"`是JavaScript保留至今的歷史行為，不代表`null`真的是可正常操作的Object。
- Array的`typeof`也是`"object"`；辨認Array應使用`Array.isArray(g)`。
- 未給值的變數是`undefined`。

```javascript
Array.isArray([1, 2, 3]); // true
```

## 3. `prompt`回傳String

```javascript
let input = prompt('Please enter a value:');
alert(typeof input); // 使用者輸入內容時通常為string
```

即使輸入`123`，`prompt`回傳的仍是`"123"`。按取消則回傳`null`。

常見轉型：

```javascript
const n1 = Number(input);   // 轉為Number；無法轉換時得到NaN
const n2 = parseInt(input); // 解析整數
const n3 = input * 1;       // 乘法觸發數值轉型，但可讀性較低
```

需要驗證轉型結果時：

```javascript
const n = Number(input);

if (Number.isNaN(n)) {
    alert('請輸入有效數字');
}
```

### `confirm`回傳Boolean

老師的`dialog.html`使用：

```javascript
function show() {
    const result = confirm('Are you sure?');

    if (result) {
        alert('User clicked OK');
    } else {
        alert('User clicked Cancel');
    }
}
```

按「確定」時`confirm(...)`回傳`true`，按「取消」時回傳`false`，因此可以直接作為`if`的條件。

## 4. Truthy與Falsy

課堂檔案：`iftest.html`

```javascript
let x = [];

if (x) {
    console.log('x is true');
} else {
    console.log('x is false');
}
```

執行結果是`x is true`，因為空Array仍是Object，而Object屬於Truthy。

常見Falsy值：

```text
false
0
-0
0n
""（空字串）
null
undefined
NaN
```

空Array`[]`與空Object`{}`都不是Falsy。要判斷Array是否沒有元素，應使用：

```javascript
if (x.length === 0) {
    console.log('陣列是空的');
}
```

## 5. 外部Script與函式回傳值

課堂檔案：

- `useJS.html`
- `scripts\first.js`

HTML載入外部Script：

```html
<script src="./scripts/first.js"></script>
```

外部檔案提供`sum`函式：

```javascript
function sum(n) {
    var value = 0;

    for (var i = 1; i <= n; i++) {
        value += i;
    }

    console.log(value);
    return value;
}
```

頁面呼叫它：

```javascript
function add() {
    const n = prompt('請輸入一個數字', '0');
    const result = sum(n);
    alert(result);
}
```

`return value`把計算結果交回呼叫處，因此`result`可以取得總和。若函式沒有明確`return`，呼叫結果通常是`undefined`。老師原檔直接把`prompt`的String交給`sum`；迴圈中的`i <= n`會發生數值轉型。正式輸入處理仍建議先使用`Number(...)`並驗證結果，避免依賴隱式轉型。

## 6. 產生指定範圍的亂數整數

課堂猜數字範例使用：

```javascript
let randomNumber = Math.floor(Math.random() * 100) + 1;
```

處理過程：

1. `Math.random()`產生`0 <= x < 1`的小數。
2. 乘以100後範圍是`0 <= x < 100`。
3. `Math.floor(...)`無條件捨去，得到0～99。
4. 加1後得到1～100。

通用公式：

```javascript
Math.floor(Math.random() * (max - min + 1)) + min;
```

## 7. `while`猜數字與巢狀三元運算

課堂檔案：`guess.html`

```javascript
while (guess != randomNumber) {
    guess = parseInt(prompt('Guess a number between 1 and 100:'));
    attempts++;

    const message = guess < randomNumber
        ? 'Too low! Try again.'
        : guess > randomNumber
            ? 'Too high! Try again.'
            : `Congratulations! You guessed the number ${randomNumber} in ${attempts} attempts.`;

    alert(message);
}
```

三元運算的基本形式：

```javascript
condition ? valueWhenTrue : valueWhenFalse;
```

課堂程式把第二個三元運算放在false分支，形成「太小／太大／答對」三種結果。雖然可執行，但巢狀三元運算可讀性較低；流程較長時，`if / else if / else`通常更清楚。

目前範例需注意：

- 按下取消時，`parseInt(null)`得到`NaN`，程式沒有提供取消遊戲的分支。
- `while`會持續彈出`prompt`，直到猜中。
- 使用`!=`會發生型別轉換；此處已透過`parseInt`得到Number，使用`!==`會更明確。

## 8. 比較三個亂數的最大值

老師檔案：`maxnumber.html`

```javascript
const x1 = parseInt(Math.random() * 100) + 1;
const x2 = parseInt(Math.random() * 100) + 1;
const x3 = parseInt(Math.random() * 100) + 1;

const max = x1 > x2
    ? (x1 > x3 ? x1 : x3)
    : (x2 > x3 ? x2 : x3);

console.log(`x1=${x1}, x2=${x2}, x3=${x3}, max=${max}`);
```

這裡的`parseInt`會先把Number轉成String再解析整數，能得到0～99，但處理Number時使用`Math.floor(...)`語意更直接。

等價且更直接的內建方法：

```javascript
const max = Math.max(x1, x2, x3);
```

## 9. 執行方式

1. 在VS Code開啟`C:\jscode`。
2. 對要測試的`day1\*.html`按右鍵，選擇`Open with Live Server`。
3. 使用頁面按鈕觸發函式。
4. 需要查看`typeof`、亂數或迴圈內容時，按`F12`開啟Console。
5. 測試會故意產生錯誤的`const`範例時，每次測試後查看Console並重新整理頁面。

## 10. 常見錯誤

| 現象 | 原因 | 修正或判讀方式 |
|---|---|---|
| `Assignment to constant variable` | 對`const`重新指派 | 若本來就要重新指派，改用`let`；否則只修改物件內容 |
| 輸入`10`卻得到String | `prompt`回傳String | 使用`Number`或`parseInt`轉型 |
| `typeof null`是`object` | JavaScript歷史行為 | 判斷空值直接使用`value === null` |
| 空Array進入`if`的true分支 | `[]`是Truthy | 使用`array.length === 0`判斷是否為空 |
| `sum`不存在 | 外部Script路徑錯誤或未載入 | 比對`./scripts/first.js`與實際位置 |
| 猜數字一直要求輸入 | 迴圈條件尚未變成false，或取消得到`NaN` | 每次迭代都要確認狀態朝終止條件前進，並另外處理取消 |

## 11. 本章檢查表

- [ ] 能說明`const`的重新指派限制
- [ ] 能分辨Number、String、Boolean、Undefined、Null、Object與Array
- [ ] 知道`typeof null`與`typeof []`的限制
- [ ] 能把`prompt`結果安全轉成Number
- [ ] 能說明`confirm`的Boolean回傳結果
- [ ] 能說明空Array為何是Truthy
- [ ] 能以`<script src>`載入外部Script並呼叫函式
- [ ] 能說明`return`如何把結果交回呼叫端
- [ ] 能產生指定範圍的亂數整數
- [ ] 能把巢狀三元運算改寫成清楚的條件分支
- [ ] 能確認`while`的狀態更新有機會到達終止條件
