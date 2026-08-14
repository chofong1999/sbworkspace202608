# Swagger／OpenAPI語法

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

這些註解只補充API文件，不會建立Controller路由，也不會改變實際HTTP行為。路由仍由Spring MVC註解決定。

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `@Tag` | 替Controller API群組命名 | `@Tag(name="User CRUD")` | [參數](#tag) |
| `@Operation` | 描述單一Controller操作 | `@Operation(summary="新增使用者")` | [參數](#operation) |
| `@Parameter` | 描述Path／Query等參數 | `@Parameter(description="使用者ID")` | [參數](#parameter) |
| `@Schema` | 描述DTO、Model或欄位 | `@Schema(example="399.00")` | [參數](#schema) |
| `@ApiResponse` | 描述單一HTTP回應 | `@ApiResponse(responseCode="200")` | [參數](#apiresponse) |
| `@ApiResponses` | 集合多個回應說明 | `@ApiResponses({...})` | [案例](#apiresponse) |

<a id="tag"></a>
## `@Tag`

**可放位置**：Controller類別。用來替一組API命名與說明。

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User CRUD", description = "使用者資料管理")
public class UserController { }
```

常用參數：`name`、`description`。

<a id="operation"></a>
## `@Operation`

**可放位置**：Controller方法。描述單一操作。

```java
@Operation(
    summary = "新增使用者",
    description = "輸入姓名、Email與年齡後建立使用者"
)
@PostMapping
public ResponseEntity<User> create(@RequestBody UserRequest request) { ... }
```

常用參數：

| 參數 | 用途 |
|---|---|
| `summary` | 列表上顯示的短摘要 |
| `description` | 展開後的詳細說明 |
| `operationId` | OpenAPI操作識別名稱；同份文件中應唯一 |
| `deprecated` | 是否標示已棄用 |
| `responses` | 內嵌`@ApiResponse`陣列 |

<a id="parameter"></a>
## `@Parameter`

```java
public User getById(
    @Parameter(description = "使用者ID", required = true, example = "42")
    @PathVariable Long id) { ... }
```

常用參數：`name`、`description`、`required`、`example`、`hidden`、`schema`。這裡的`required`是文件描述；實際是否必填仍由Spring MVC綁定規則決定。

<a id="schema"></a>
## `@Schema`

**可放位置**：DTO／Model類別或欄位，用來描述資料結構。

```java
@Schema(description = "商品價格", example = "399.00", minimum = "0")
private BigDecimal price;
```

常用參數：`name`、`description`、`example`、`requiredMode`、`allowableValues`、`minimum`、`maximum`、`format`、`hidden`、`nullable`。

文件限制不等於執行期驗證；例如`minimum = "0"`只描述規格，真正拒絕負數仍要Bean Validation的`@DecimalMin`或服務邏輯。

<a id="apiresponse"></a>
## `@ApiResponse`

```java
@Operation(summary = "依ID查詢")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "查詢成功"),
    @ApiResponse(responseCode = "404", description = "找不到資料")
})
@GetMapping("/{id}")
public ResponseEntity<User> get(@PathVariable Long id) { ... }
```

常用參數：`responseCode`、`description`、`content`、`headers`。文件中列出的狀態碼必須和Controller實際回應一致。

## 文件入口

springdoc-openapi預設常用入口：

- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

若專案修改`server.port`或context path，網址也要跟著改。依賴版本與Spring Boot主版本必須相容，詳見[springdoc-openapi官方文件](https://springdoc.org/)。
