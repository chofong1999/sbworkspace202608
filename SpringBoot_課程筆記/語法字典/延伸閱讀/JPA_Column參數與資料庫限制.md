# `@Column`參數與資料庫限制

## 本頁快速索引

- [三個不同層次](#三個不同層次)
- [`nullable`和`@NotNull`的差異](#nullable和notnull的差異)
- [`unique = true`不是重複資料檢查流程](#unique-true不是重複資料檢查流程)
- [`insertable`與`updatable`](#insertable與updatable)
- [`precision`與`scale`](#precision與scale)
- [原生DDL參數](#原生ddl參數)
- [變更欄位後的驗證](#變更欄位後的驗證)

## 三個不同層次

`@Column(nullable = false, length = 100)`可能同時牽涉三個層次，但三者不能互相取代：

1. **Java／API驗證**：例如`@NotBlank`、`@Size(max = 100)`，在資料進入Service前提供友善錯誤。
2. **JPA映射與DDL產生**：`@Column`告訴ORM欄位特性，若啟用schema generation可產生限制。
3. **資料庫Schema**：真正阻止非法資料的`NOT NULL`、`UNIQUE`、欄位長度與CHECK限制。

若`ddl-auto=validate`或`none`，JPA不會替既有資料表新增限制。此時必須用SQL migration或資料庫管理工具建立限制。

## `nullable`和`@NotNull`的差異

| 寫法 | 驗證位置 | 典型結果 |
|---|---|---|
| `@NotNull` | Java Bean Validation | Controller驗證時得到400與欄位錯誤 |
| `@Column(nullable = false)` | ORM／Schema | 產生`NOT NULL`或在寫入時由資料庫拒絕 |

公開API通常兩邊都設，分別改善使用者錯誤訊息與資料完整性。

## `unique = true`不是重複資料檢查流程

Service可以先查`existsByEmail`以提供清楚訊息，但兩個請求仍可能同時通過先查。資料庫UNIQUE constraint才是最後防線；程式還要捕捉唯一限制衝突並轉成合適的HTTP回應。

## `insertable`與`updatable`

它們控制JPA產生SQL時是否包含欄位，不是Java物件的唯讀權限。

- 資料庫自動填建立時間：常用`insertable = false, updatable = false`。
- 建立後不可由JPA更新：`updatable = false`。
- 同一資料庫欄位被兩個屬性映射：通常其中一個需設為不可寫，否則出現重複欄位映射錯誤。

改完setter後，Java物件暫時仍可能顯示新值；重新查資料庫才反映JPA沒有更新該欄位。

## `precision`與`scale`

`precision = 12, scale = 2`代表最多12位有效數字，其中2位在小數點後，例如最多約`9999999999.99`。金額使用`BigDecimal`，不要用`double`承擔需要精確十進位的金額計算。

## 原生DDL參數

`columnDefinition`、Jakarta Persistence 3.2的`options`、`check`、`comment`都會受資料庫與provider支援程度影響。它們適合明確知道目標資料庫且由JPA產生Schema的情境；需要跨MySQL、SQLite等資料庫時，應優先使用標準映射或分資料庫管理migration。

## 變更欄位後的驗證

1. 確認專案使用的Jakarta Persistence版本支援該參數。
2. 確認`ddl-auto`是否真的會改Schema。
3. 直接查看資料庫欄位與constraint，而不是只看Entity。
4. 測試正常值、`null`、過長值、重複值與更新情境。
5. 若部署環境使用既有資料庫，確認migration已一起部署。

官方規格：[Jakarta Persistence 3.2 `Column`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/column)

