# JPA實體與欄位映射

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

> 本頁使用`jakarta.persistence.*`。Spring Boot 3以上不要誤匯入舊的`javax.persistence.*`。

<a id="entity-table"></a>
## `@Entity`與`@Table`

```java
@Entity
@Table(name = "products")
public class Product {
}
```

| 註解 | 定義 | 常用參數 |
|---|---|---|
| `@Entity` | 宣告類別是JPA實體 | `name`：JPQL使用的實體名稱，不是資料表名稱 |
| `@Table` | 指定主要資料表 | `name`、`schema`、`catalog`、`uniqueConstraints`、`indexes` |

**成立條件**：實體需有主鍵；通常提供`public`或`protected`無參數建構子。未寫`@Table`時，表名由命名策略與實體名稱推導。

<a id="id-generatedvalue"></a>
## `@Id`與`@GeneratedValue`

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

| `GenerationType` | 用途 |
|---|---|
| `AUTO` | 讓provider依資料庫選擇策略 |
| `IDENTITY` | 使用資料庫identity／auto-increment欄位 |
| `SEQUENCE` | 使用資料庫sequence，常搭配`@SequenceGenerator` |
| `TABLE` | 使用額外資料表模擬序號 |
| `UUID` | 產生UUID（需專案使用支援此列舉值的Jakarta Persistence版本） |

主鍵型別與資料庫策略必須相容。使用`IDENTITY`時，新增前通常是`null`，寫入後才取得值。

<a id="column"></a>
## `@Column(...)`

**定義**：指定持久化欄位／getter對應的資料庫欄位。沒寫`@Column`也會依預設規則映射。

```java
@Column(
    name = "email",
    nullable = false,
    unique = true,
    length = 120,
    updatable = true
)
private String email;
```

### 常用參數

| 參數 | 型別／預設 | 使用條件與影響 |
|---|---|---|
| `name` | `String`／`""` | 指定資料庫欄位名；空字串時依Java屬性名與命名策略推導 |
| `nullable` | `boolean`／`true` | 產生DDL時決定是否允許`NULL`；不能取代Java端輸入驗證 |
| `unique` | `boolean`／`false` | 產生單欄唯一限制；複合唯一限制用`@Table(uniqueConstraints = ...)` |
| `length` | `int`／`255` | 適用`varchar`、`varbinary`等有長度型別；不是所有資料庫型別都採用 |
| `insertable` | `boolean`／`true` | `false`時，JPA產生`INSERT`不包含此欄位 |
| `updatable` | `boolean`／`true` | `false`時，JPA產生`UPDATE`不包含此欄位；Java物件仍可能被setter改值 |
| `precision` | `int`／`0` | `decimal`／`numeric`總有效位數；`0`讓provider推導 |
| `scale` | `int`／`0` | 小數點後位數；只適用精確數值型別 |
| `table` | `String`／`""` | 欄位位於次要資料表時指定；一般主表不填 |
| `columnDefinition` | `String`／`""` | 直接提供資料庫原生DDL片段；會綁定資料庫方言，非必要不使用 |

### Jakarta Persistence 3.2新增參數

下列參數只可在專案實際依賴Jakarta Persistence 3.2時使用；較舊版本會編譯失敗。

| 參數 | 預設 | 用途 |
|---|---|---|
| `options` | `""` | 在產生欄位DDL後附加原生SQL片段；不可與`columnDefinition`並用 |
| `secondPrecision` | `-1` | `time`／`timestamp`的小數秒位數 |
| `check` | `{}` | 產生資料表時加入欄位CHECK限制 |
| `comment` | `""` | 產生資料表時加入欄位註解 |

### 常見組合

```java
@Column(nullable = false, length = 100)
private String name;

@Column(precision = 12, scale = 2)
private BigDecimal price;

@Column(name = "created_at", insertable = false, updatable = false)
private LocalDateTime createdAt;
```

**重要限制**：`nullable`、`unique`等主要影響JPA schema generation。若資料表已由SQL或管理工具建立，仍須在資料庫本身建立相同限制。詳見[@Column參數與資料庫限制](延伸閱讀/JPA_Column參數與資料庫限制.md)。

官方規格：[Jakarta Persistence 3.2 `Column`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/column)

<a id="transient"></a>
## `@Transient`

**定義**：告訴JPA不要把該欄位／getter持久化到資料庫。

```java
@Transient
private BigDecimal displayTotal;
```

不要和Java關鍵字`transient`混淆：Java的`transient`控制Java序列化；JPA的`@Transient`控制ORM映射。

<a id="creationtimestamp"></a>
## `@CreationTimestamp`

**來源**：`org.hibernate.annotations.CreationTimestamp`，是Hibernate功能，不是Jakarta Persistence標準。

```java
@CreationTimestamp
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;
```

新增實體時由Hibernate設定建立時間。若要由資料庫預設值負責，需配合欄位DDL與`insertable = false`，不要同時讓兩邊互相覆蓋。

## 欄位存取位置

JPA會依`@Id`放置位置決定存取方式：

- `@Id`放欄位：field access，其他映射註解通常也放欄位。
- `@Id`放getter：property access，其他映射註解通常也放getter。

同一個實體不要無意間混用兩種位置。

