# Spring Boot：Model／Entity／DTO語法入口

[返回字典首頁](../README.md)｜[依目的快速查找](../00_快速索引.md)

寫`model/`、`entity/`或`dto/`中的Java類別時，先由本頁尋找。分類依實際使用位置安排；「來源」欄保留每個註解真正所屬的規格或函式庫。

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 本頁分類 |
|---|---|---|---|
| `@Entity` | 宣告JPA實體 | `@Entity` | [欄位](#entity-fields) |
| `@Table` | 指定資料表 | `@Table(name="users")` | [欄位](#entity-fields) |
| `@Id` | 宣告主鍵 | `@Id` | [欄位](#entity-fields) |
| `@GeneratedValue` | 設定主鍵產生方式 | `@GeneratedValue(strategy=IDENTITY)` | [欄位](#entity-fields) |
| `@Column` | 設定資料庫欄位屬性 | `@Column(nullable=false)` | [欄位](#entity-fields) |
| `@Transient` | 不把Java欄位寫入資料庫 | `@Transient` | [欄位](#entity-fields) |
| `@CreationTimestamp` | 自動填入建立時間 | `@CreationTimestamp` | [欄位](#entity-fields) |
| `@ManyToOne` | 多筆資料指向同一筆 | `@ManyToOne(fetch=LAZY)` | [關聯](#entity-relations) |
| `@OneToMany` | 一筆資料持有多筆集合 | `@OneToMany(mappedBy="department")` | [關聯](#entity-relations) |
| `@JoinColumn` | 指定外鍵欄位 | `@JoinColumn(name="dept_id")` | [關聯](#entity-relations) |
| `mappedBy` | 指定另一端的Java關聯屬性 | `mappedBy="department"` | [關聯](#entity-relations) |
| `CascadeType` | 傳遞持久化操作 | `cascade=PERSIST` | [關聯](#entity-relations) |
| `@JsonFormat` | 設定日期／數值JSON格式 | `@JsonFormat(pattern="yyyy-MM-dd")` | [JSON](#json-output) |
| `@JsonIgnore` | JSON忽略單一屬性 | `@JsonIgnore` | [JSON](#json-output) |
| `@JsonIgnoreProperties` | JSON依名稱忽略屬性 | `@JsonIgnoreProperties("department")` | [JSON](#json-output) |
| `@JsonManagedReference`／`@JsonBackReference` | 控制雙向關聯JSON方向 | `@JsonBackReference` | [JSON](#json-output) |
| `@JsonInclude` | 排除`null`或空值 | `@JsonInclude(NON_NULL)` | [JSON](#json-output) |
| `@NotBlank`／`@Email`／`@Min`／`@Size` | 驗證DTO欄位 | `@NotBlank` | [DTO](#dto-validation-lombok) |
| `@Data`／`@Getter`／`@Setter` | 產生常用方法 | `@Data` | [DTO](#dto-validation-lombok) |
| `@NoArgsConstructor`等建構子註解 | 產生建構子 | `@NoArgsConstructor` | [DTO](#dto-validation-lombok) |

<a id="entity-fields"></a>
## Entity與資料表欄位

| 想設定什麼 | 語法 | 來源 | 字典條目 | 課程實例 |
|---|---|---|---|---|
| 宣告實體與資料表 | `@Entity`、`@Table` | Jakarta Persistence | [定義與參數](../03_JPA實體與欄位映射.md#entity-table) | [第13章Employee](../../純文字版/13_Spring_Data_JPA與MySQL.md#employee-entity-example) |
| 主鍵與自動產生值 | `@Id`、`@GeneratedValue` | Jakarta Persistence | [策略與條件](../03_JPA實體與欄位映射.md#id-generatedvalue) | [第13章Employee](../../純文字版/13_Spring_Data_JPA與MySQL.md#employee-entity-example) |
| 欄位名稱、長度、NULL、唯一、可寫性 | `@Column(...)` | Jakarta Persistence | [完整參數表](../03_JPA實體與欄位映射.md#column) | [第13章Employee](../../純文字版/13_Spring_Data_JPA與MySQL.md#employee-entity-example) |
| 不把Java欄位存入資料庫 | `@Transient` | Jakarta Persistence | [條目](../03_JPA實體與欄位映射.md#transient) | — |
| 自動記錄建立時間 | `@CreationTimestamp` | Hibernate | [條目](../03_JPA實體與欄位映射.md#creationtimestamp) | [第16章Employee](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#employee-manytoone-example) |

<a id="entity-relations"></a>
## Entity關聯

| 想設定什麼 | 語法 | 來源 | 字典條目 | 課程實例 |
|---|---|---|---|---|
| 多筆資料指向同一筆 | `@ManyToOne` | Jakarta Persistence | [定義與參數](../04_JPA關聯映射.md#manytoone-onetomany) | [Employee.department](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#employee-manytoone-example) |
| 一筆資料持有多筆集合 | `@OneToMany` | Jakarta Persistence | [定義與參數](../04_JPA關聯映射.md#manytoone-onetomany) | [Department.employees](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#department-onetomany-example) |
| 指定外鍵欄位 | `@JoinColumn` | Jakarta Persistence | [參數表](../04_JPA關聯映射.md#joincolumn) | [dept_id](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#employee-manytoone-example) |
| 指定關聯擁有端 | `mappedBy` | Jakarta Persistence | [成立條件](../04_JPA關聯映射.md#mappedby) | [mappedBy="department"](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#department-onetomany-example) |
| 傳遞新增、更新或刪除操作 | `CascadeType` | Jakarta Persistence | [可選值](../04_JPA關聯映射.md#cascade) | [第16章PERSIST](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#department-onetomany-example) |

<a id="json-output"></a>
## JSON輸入與輸出

| 想設定什麼 | 語法 | 來源 | 字典條目 | 課程實例 |
|---|---|---|---|---|
| 日期時間或數值的JSON格式 | `@JsonFormat(...)` | Jackson | [參數表](../07_驗證_Jackson_Lombok.md#jsonformat) | — |
| 忽略指定JSON欄位 | `@JsonIgnore`、`@JsonIgnoreProperties` | Jackson | [關聯JSON](../07_驗證_Jackson_Lombok.md#jackson-relations) | [Department／Employee](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#jackson-ignore-example) |
| 控制雙向關聯輸出方向 | `@JsonManagedReference`、`@JsonBackReference` | Jackson | [關聯JSON](../07_驗證_Jackson_Lombok.md#jackson-relations) | [Category／Product](../../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#jackson-reference-example) |
| 排除`null`或空值 | `@JsonInclude` | Jackson | [關聯JSON](../07_驗證_Jackson_Lombok.md#jackson-relations) | — |

<a id="dto-validation-lombok"></a>
## DTO驗證與Lombok

| 想設定什麼 | 語法 | 來源 | 字典條目 |
|---|---|---|---|
| 必填、Email、最小值、長度 | `@NotBlank`、`@Email`、`@Min`、`@Size` | Jakarta Validation | [驗證條目](../07_驗證_Jackson_Lombok.md#validation) |
| 產生getter、setter與建構子 | `@Data`、`@Getter`、`@NoArgsConstructor`等 | Lombok | [Lombok條目](../07_驗證_Jackson_Lombok.md#lombok) |

## 查找原則

- 要完成目前專案：先看「課程實例」，保留該章真正使用的參數與執行結果。
- 要確認某註解還能填什麼：進入「字典條目」。
- 要理解資料庫限制、擁有端、級聯、N+1或JSON循環：再讀條目所連結的延伸閱讀。
