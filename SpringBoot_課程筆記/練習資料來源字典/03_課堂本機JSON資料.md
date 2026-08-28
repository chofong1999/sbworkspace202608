# 課堂本機JSON資料

公開API需要網路；本機JSON則適合練習相對路徑、JSON解析與離線資料呈現。本字典保存課堂使用的空氣品質資料副本：

```text
練習資料來源字典/
├─ 03_課堂本機JSON資料.md
└─ 範例資料/
   └─ air.json
```

## 資料快速索引

| 檔案 | 最外層型別 | 主要欄位 | 適合練習 |
|---|---|---|---|
| [`air.json`](範例資料/air.json) | Array | `Area`、`AQI`、`ForecastDate`、`MajorPollutant`、`Content` | 本機AJAX、表格、日期與數字排序 |

`air.json`是課堂範例資料的原樣副本，不是即時空氣品質。資料中的日期與數值只用於程式練習。

課堂程式證據：`jscode/day4/jquery_ajax.html` 使用 `const dataUrl = "air.json";` 讀取同資料夾的檔案。

## 如何重現

瀏覽器不能把任意本機檔案路徑當成一般網站API。最簡單的方式是建立練習資料夾，將HTML與JSON放在同一個網站目錄，再以Live Server開啟：

```text
practice/
├─ index.html
└─ air.json
```

複製本字典的[`air.json`](範例資料/air.json)到`practice/air.json`後，在JavaScript中使用相對URL：

```javascript
const response = await fetch('air.json')

if (!response.ok) {
  throw new Error(`HTTP ${response.status}`)
}

const records = await response.json()
console.log(records[0].Area, records[0].AQI)
```

如果HTML網址是：

```text
http://127.0.0.1:5500/practice/index.html
```

則`fetch('air.json')`實際要求的是：

```text
http://127.0.0.1:5500/practice/air.json
```

## 常見錯誤

| 現象 | 優先檢查 |
|---|---|
| 404 | HTML與JSON是否同層，或相對路徑是否應改成`data/air.json` |
| `Unexpected token` | 取得的是否其實是404 HTML，而不是JSON |
| 直接雙擊HTML失敗 | 改用Live Server，不要依賴`file:///`讀取 |
| AQI排序不正確 | JSON內的`AQI`是字串，數值排序前用`Number(record.AQI)` |
| 畫面顯示`undefined` | 欄位大小寫是否與JSON一致，例如`Area`不是`area` |

課程對照：[第27章：jQuery AJAX、JSON資料來源與HTTP狀態處理](../純文字版/27_jQuery_AJAX_JSON資料來源與HTTP狀態處理.md)
