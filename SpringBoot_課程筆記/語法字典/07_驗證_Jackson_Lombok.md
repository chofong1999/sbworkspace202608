# 驗證、Jackson與Lombok

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

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

