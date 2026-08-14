# 驗證、Jackson與Lombok

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `@Valid` | 觸發物件與巢狀欄位驗證 | `@Valid @RequestBody DTO body` | [驗證](#validation) |
| `@NotNull` | 值不可為`null` | `@NotNull` | [驗證](#validation) |
| `@NotEmpty` | 字串／集合不可為空 | `@NotEmpty` | [驗證](#validation) |
| `@NotBlank` | 字串不可為`null`、空或純空白 | `@NotBlank` | [驗證](#validation) |
| `@Email` | 檢查Email格式 | `@Email` | [驗證](#validation) |
| `@Min` | 設定整數最小值 | `@Min(0)` | [驗證](#validation) |
| `@Size` | 限制字串／集合長度 | `@Size(max=100)` | [驗證](#validation) |
| `@JsonFormat` | 設定日期、時間或值的JSON外形 | `@JsonFormat(pattern="yyyy-MM-dd")` | [完整參數](#jsonformat) |
| `@JsonIgnore` | JSON完全忽略單一屬性 | `@JsonIgnore` | [Jackson](#jackson-relations) |
| `@JsonIgnoreProperties` | 依名稱忽略多個屬性 | `@JsonIgnoreProperties("department")` | [Jackson](#jackson-relations) |
| `@JsonInclude` | 排除`null`、空值等內容 | `@JsonInclude(NON_NULL)` | [Jackson](#jackson-relations) |
| `@JsonManagedReference` | 輸出雙向關聯正向端 | `@JsonManagedReference` | [Jackson](#jackson-relations) |
| `@JsonBackReference` | 省略雙向關聯反向端 | `@JsonBackReference` | [Jackson](#jackson-relations) |
| `@Getter`／`@Setter` | 產生getter／setter | `@Getter` | [Lombok](#lombok) |
| `@Data` | 產生常用存取與物件方法 | `@Data` | [Lombok](#lombok) |
| `@NoArgsConstructor` | 產生無參數建構子 | `@NoArgsConstructor` | [Lombok](#lombok) |
| `@AllArgsConstructor` | 產生全欄位建構子 | `@AllArgsConstructor` | [Lombok](#lombok) |
| `@RequiredArgsConstructor` | 產生必要欄位建構子 | `@RequiredArgsConstructor` | [Lombok](#lombok) |
| `@Builder` | 產生Builder API | `@Builder` | [Lombok](#lombok) |

<a id="validation"></a>
## Bean Validation

Controller先用`@Valid`觸發物件欄位上的驗證規則：

```java
public record UserRequest(
    @NotBlank(message = "姓名不可空白") String name,
    @Email(message = "Email格式錯誤") String email,
    @Min(value = 0, message = "年齡不可小於0") Integer age
) {}

@PostMapping
public ResponseEntity<User> create(@Valid @RequestBody UserRequest request) { ... }
```

| 語法 | 適用型別／條件 | 判斷 |
|---|---|---|
| `@Valid` | 方法參數、欄位等 | 觸發巢狀物件的標準驗證 |
| `@NotNull` | 任意參考型別 | 不可為`null`，但字串可為空 |
| `@NotEmpty` | 字串、集合、陣列、Map | 不可`null`且長度／大小大於0 |
| `@NotBlank` | `CharSequence` | 不可`null`，去除空白後仍要有字元 |
| `@Email` | `CharSequence` | 符合Email格式；若還要必填需另加`@NotBlank` |
| `@Min(value)` | 整數型數值 | 不小於指定值 |
| `@Size(min, max)` | 字串、集合、陣列、Map | 限制長度／大小 |

Spring Boot專案需有Validation依賴；只寫欄位註解但Controller未觸發驗證時，不會自動拒絕請求。

<a id="jsonformat"></a>
## `@JsonFormat(...)`

**來源**：`com.fasterxml.jackson.annotation.JsonFormat`。它控制Jackson如何序列化與反序列化某個值，不會改變資料庫欄位格式。

```java
@JsonFormat(
    shape = JsonFormat.Shape.STRING,
    pattern = "yyyy-MM-dd HH:mm:ss",
    timezone = "Asia/Taipei"
)
private LocalDateTime createdAt;
```

| 參數 | 預設 | 使用條件與影響 |
|---|---|---|
| `pattern` | `""` | 型別相關格式；`java.time.*`通常採`DateTimeFormatter`格式規則 |
| `shape` | `ANY` | 指定JSON外形，例如`STRING`、`NUMBER`、`ARRAY`或`OBJECT`；實際支援值依資料型別而異 |
| `timezone` | `"##default"` | 指定時區ID，例如`Asia/Taipei`；對不含時區概念的型別是否生效要依serializer確認 |
| `locale` | `"##default"` | 格式需要語系時指定，例如月份名稱 |
| `lenient` | `DEFAULT` | 反序列化時是否允許較寬鬆輸入，實際預設依型別的deserializer |
| `with` | `{}` | 明確開啟指定`JsonFormat.Feature` |
| `without` | `{}` | 明確停用指定`JsonFormat.Feature` |

**使用時機**：API契約要求固定日期時間字串、數字外形或特定序列化特性。若整個專案都使用同一格式，優先評估全域Jackson設定；只有特定欄位例外時再放欄位註解。

**常見陷阱**：`LocalDateTime`本身沒有時區。若資料真正代表全球時間點，應先判斷是否應改用`Instant`或`OffsetDateTime`，不要只加`timezone`掩蓋模型選擇。

官方參考：[Jackson `JsonFormat` API](https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations/latest/com/fasterxml/jackson/annotation/JsonFormat.html)

<a id="jackson-relations"></a>
## Jackson與關聯JSON

| 註解 | 可放位置 | 用途 |
|---|---|---|
| `@JsonIgnore` | 欄位／getter | 完全忽略該屬性 |
| `@JsonIgnoreProperties` | 類別／屬性 | 依名稱忽略多個屬性；可設`allowGetters`、`allowSetters` |
| `@JsonInclude` | 類別／屬性 | 控制何時輸出，例如`NON_NULL`、`NON_EMPTY` |
| `@JsonManagedReference` | 關聯的一方 | 輸出正向關聯 |
| `@JsonBackReference` | 關聯的反向方 | 序列化時略過反向關聯，避免循環 |

```java
@JsonManagedReference
@OneToMany(mappedBy = "department")
private List<Employee> employees;

@JsonBackReference
@ManyToOne
private Department department;
```

`@JsonManagedReference`／`@JsonBackReference`是一組Jackson序列化策略，不會改變JPA外鍵。若API需要不同畫面、版本或權限下的欄位組合，優先建立DTO，不要讓Entity同時承擔所有API格式。

實際案例：[第16章`@JsonIgnoreProperties`](../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#jackson-ignore-example)與[`@JsonManagedReference`／`@JsonBackReference`](../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#jackson-reference-example)。

<a id="lombok"></a>
## Lombok

| 註解 | 產生內容 | 注意事項 |
|---|---|---|
| `@Getter`／`@Setter` | getter／setter | 可放類別或單一欄位 |
| `@Data` | getter、setter、`toString`、`equals`／`hashCode`、必要欄位建構子 | JPA Entity與雙向關聯可能造成過大的`toString`或遞迴，需審慎使用 |
| `@NoArgsConstructor` | 無參數建構子 | JPA Entity常需要 |
| `@AllArgsConstructor` | 全欄位建構子 | 欄位變更會改變參數順序與數量 |
| `@RequiredArgsConstructor` | `final`及`@NonNull`欄位建構子 | 適合Spring建構子注入 |
| `@Builder` | Builder API | 建立DTO方便；Entity集合預設值需搭配`@Builder.Default` |

```java
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
}
```

Lombok是編譯期產生程式碼。Eclipse除了Maven依賴外，通常還需安裝Lombok整合，否則IDE可能顯示不存在的方法。
