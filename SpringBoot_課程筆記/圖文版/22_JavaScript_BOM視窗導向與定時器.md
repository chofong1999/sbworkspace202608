# JavaScript BOM：視窗、頁面導向與定時器

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章整理BOM。BOM（Browser Object Model）是瀏覽器提供給JavaScript操作視窗、網址、定時器等瀏覽器環境的API；可選調整會另外標示。

> 語法速查：[JavaScript核心](../語法字典/11_JavaScript核心_陣列與物件.md)｜[BOM與計時器](../語法字典/12_DOM_BOM表單與Fetch.md#bom)

## 本章快速索引

- [1. BOM與DOM的差異](#1-bom與dom的差異)
- [2. `window.open`開啟視窗或分頁](#2-windowopen開啟視窗或分頁)
- [3. `location.href`導向另一個網址](#3-locationhref導向另一個網址)
- [4. `setInterval`建立週期性工作](#4-setinterval建立週期性工作)
- [5. `clearInterval`停止週期性工作](#5-clearinterval停止週期性工作)
- [6. 定時切換圖片](#6-定時切換圖片)
- [7. 防止重複啟動多個Timer](#7-防止重複啟動多個timer)
- [8. 執行與驗證](#8-執行與驗證)
- [9. 常見錯誤](#9-常見錯誤)
- [10. 本章檢查表](#10-本章檢查表)

## 1. BOM與DOM的差異

| 類別 | 主要操作對象 | 常見入口 |
|---|---|---|
| DOM | HTML文件與頁面元素 | `document` |
| BOM | 瀏覽器視窗、網址、歷史與定時器 | `window`、`location`、`history`等 |

在瀏覽器一般Script中，`window`是全域物件。下列兩種寫法通常等價：

```javascript
window.setInterval(callback, 1000);
setInterval(callback, 1000);
```

Node.js並沒有瀏覽器的`window`與`location`，因此本章範例應在瀏覽器中執行。

## 2. `window.open`開啟視窗或分頁

範例檔案：`bom_window.html`

```javascript
function openwin() {
    window.open(
        'http://tw.yahoo.com/',
        '_blank',
        'toolbar=no,location=0,status=no,menubar=no,' +
        'scrollbars=no,resizable=no,width=800,height=600'
    );
}
```

基本定義：

```javascript
window.open(url, targetName, windowFeatures);
```

| 參數 | 本例 | 作用 |
|---|---|---|
| `url` | Yahoo網址 | 要開啟的資源 |
| `targetName` | `_blank` | 要求建立新的瀏覽Context |
| `windowFeatures` | 工具列、尺寸等設定字串 | 建議的視窗特徵 |

瀏覽器可能依使用者設定，把它開成新分頁、忽略部分尺寸設定，或阻擋不是由使用者操作直接觸發的Popup。回傳值可能是新視窗的Window參照，也可能因被阻擋而是`null`。

## 3. `location.href`導向另一個網址

```javascript
function nav() {
    const locations = [
        'http://tw.yahoo.com/',
        'http://www.google.com.tw/',
        'http://24h.pchome.com.tw/'
    ];

    const index = Math.floor(Math.random() * locations.length);
    location.href = locations[index];
}
```

此程式從Array隨機選出一個網址，再把目前分頁導向該位置。它與`window.open`的差異是：

- `location.href = ...`使用目前分頁。
- `window.open(...)`嘗試建立或重用另一個瀏覽Context。

導向外部網站後，原本Live Server頁面會離開；要繼續測試可使用瀏覽器「上一頁」。

## 4. `setInterval`建立週期性工作

範例檔案：`bom_clock.html`

```javascript
let intervalId;

function startclock() {
    intervalId = window.setInterval(function () {
        document.getElementById('time').textContent =
            new Date().toLocaleTimeString();
    }, 1000);
}
```

`setInterval(callback, delay)`會要求瀏覽器每隔指定毫秒執行一次callback。`1000`毫秒約等於1秒。

```javascript
new Date().toLocaleTimeString();
```

建立目前時間，再依使用者所在環境格式化成時間String。

`setInterval`回傳Timer ID，必須保存它才能停止對應工作：

```javascript
intervalId = window.setInterval(...);
```

## 5. `clearInterval`停止週期性工作

```javascript
function stopclock() {
    window.clearInterval(intervalId);
}
```

基本寫法停止Timer後會保留畫面上的最後時間。若希望介面清楚顯示狀態，可自行改成：

```javascript
function stopclock() {
    window.clearInterval(intervalId);
    intervalId = undefined;
    document.getElementById('time').textContent = '已停止';
}
```

若另外把`intervalId`寫入頁面，按Stop後看到的數字是Timer ID，不是倒數秒數或執行次數。

## 6. 定時切換圖片

範例檔案：`bom_images.html`

```javascript
let index = 1;
let intervalId;

const pics = [
    'images/banana.png',
    'images/grape.png',
    'images/guava.png',
    'images/mango.png',
    'images/orange.png'
];

function startimages() {
    intervalId = window.setInterval(changeImage, 3000);
}

function stopimages() {
    window.clearInterval(intervalId);
}

function changeImage() {
    document.getElementById('img').src = pics[index];
    index++;
    index = index % pics.length;
}
```

HTML初始圖片是`banana.png`，而`index`從1開始，因此第一次Timer觸發時會切到`pics[1]`，也就是`grape.png`。

循環索引：

```javascript
index = index % pics.length;
```

當`index`等於5時，`5 % 5`得到0，索引回到第一張圖片。

圖片相對路徑以`bom_images.html`所在的`day2`為基準，實際結構需為：

```text
day2/bom_images.html
day2/images/banana.png
day2/images/grape.png
...
```

## 7. 防止重複啟動多個Timer

目前範例每按一次Start就建立一個新的Interval，並只把最新ID存入`intervalId`。若連按多次：

- 多個Timer會同時執行。
- Stop只會清除最後保存的Timer ID。
- 圖片或時鐘可能持續變化。

可在開始前先停止舊Timer：

```javascript
function startimages() {
    window.clearInterval(intervalId);
    intervalId = window.setInterval(changeImage, 3000);
}
```

或設定啟動狀態：

```javascript
function startimages() {
    if (intervalId !== undefined) {
        return;
    }

    intervalId = window.setInterval(changeImage, 3000);
}

function stopimages() {
    window.clearInterval(intervalId);
    intervalId = undefined;
}
```

## 8. 執行與驗證

### 8.1 視窗與導向

1. 對`day2/bom_window.html`選擇`Open with Live Server`。
2. 按`Open Yahoo Window`，觀察新視窗／分頁及Popup提示。
3. 回到原頁，按`Navigate to Location`。
4. 確認目前分頁被導向Array中的其中一個網址。

### 8.2 時鐘

1. 開啟`bom_clock.html`。
2. 按`Start Clock`，確認時間約每秒更新。
3. 按`Stop Clock`，確認更新停止。
4. 基本寫法會保留停止當下的最後時間；若自行把`intervalId`顯示在頁面，看到的數字是Timer ID。

### 8.3 圖片輪播

1. 開啟`bom_images.html`。
2. 確認初始圖片為banana。
3. 按`Start Image`，確認圖片依`setInterval()`設定的毫秒數週期切換。
4. 按`Stop Image`，確認輪播停止。

## 9. 常見錯誤

| 現象 | 原因 | 修正或判讀方式 |
|---|---|---|
| 新視窗沒有出現 | Popup被瀏覽器阻擋 | 允許該網站Popup，並確保呼叫直接來自按鈕事件 |
| 設定寬高卻開成分頁 | 瀏覽器可忽略部分Window Features | 這是瀏覽器政策，不代表JavaScript語法錯誤 |
| 按導向後找不到原頁 | `location.href`取代目前分頁 | 使用上一頁或重新開啟Live Server網址 |
| 時鐘沒有停止 | 多次Start建立多個Timer | 每次Start前清除舊Interval，或禁止重複啟動 |
| Stop後顯示一個數字 | 程式把Timer ID寫入DOM | 若要狀態文字，改寫成`已停止` |
| 圖片顯示破圖 | 相對路徑或檔名錯誤 | 檢查`day2\images`與檔案大小寫 |
| 用Node.js執行出現`window is not defined` | Node.js不是瀏覽器BOM環境 | 使用Live Server在瀏覽器執行 |

## 10. 本章檢查表

- [ ] 能分辨BOM與DOM
- [ ] 能說明`window.open`三個參數
- [ ] 能分辨新視窗與`location.href`目前頁面導向
- [ ] 能用`setInterval`建立週期性工作
- [ ] 能保存Timer ID並以`clearInterval`停止
- [ ] 能用餘數運算讓圖片索引循環
- [ ] 能說明為何第一次切換會從grape開始
- [ ] 能防止多次Start建立無法全部停止的Timer
- [ ] 能辨認BOM程式必須在瀏覽器環境執行
