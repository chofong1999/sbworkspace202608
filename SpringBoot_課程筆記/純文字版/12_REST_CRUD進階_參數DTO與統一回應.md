# Spring Boot 學習筆記 12：REST CRUD進階、參數、DTO與統一回應

- 範例專案：`sbrestcrud0807`
- 早期對照專案：`sbrest0722`（User記憶體CRUD、Request參數與`ApiResponse<T>`）
- 主要API前綴：`http://localhost:8080/api/products`

> 語法速查：[MVC與REST](../語法字典/02_Spring_MVC與REST.md)｜[驗證與JSON](../語法字典/07_驗證_Jackson_Lombok.md)

## 本章快速索引

- [0. 前置條件、實作順序與完成判定](#0-前置條件實作順序與完成判定)
- [1. 這個專案比前一個CRUD多了什麼？](#1-這個專案比前一個crud多了什麼)
- [2. Product與ProductDTO不是同一種角色](#2-product與productdto不是同一種角色)
- [3. Lombok註解的確切作用](#3-lombok註解的確切作用)
- [4. 記憶體Store的資料結構](#4-記憶體store的資料結構)
- [5. Product CRUD端點](#5-product-crud端點)
- [6. 目前程式的重要ID不一致問題](#6-目前程式的重要id不一致問題)
- [7. 四種Request資料來源](#7-四種request資料來源)
- [8. 統一API回應`ApiResponse<T>`](#8-統一api回應apiresponse)
- [9. Swagger註解與實際HTTP狀態要分開看](#9-swagger註解與實際http狀態要分開看)
- [10. 本章檢查表](#10-本章檢查表)

## 0. 前置條件、實作順序與完成判定

- 先完成第9章的REST CRUD觀念與第10章的Swagger設定。
- 建立含Spring Web、Lombok與springdoc-openapi的專案；範例專案名稱為`sbrestcrud0807`。
- 依序建立`Product`、`ProductDTO`、`ProductController`，再建立`ParameterController`、`ApiResponse<T>`與`UnifiedResponseController`。
- 啟動後先測Product CRUD，再分別測Path、Query、Header、Body四種參數來源，最後測統一回應端點。

既有編譯產物只能證明程式曾經編譯；必須親自完成下列HTTP測試才能判定重現成功：

```text
GET    /api/products
POST   /api/products
GET    /api/products/{Map key}
PUT    /api/products/{Map key}
DELETE /api/products/{Map key}
GET    /api/params/path/123
GET    /api/params/query?name=Alice&age=25
GET    /api/params/header
POST   /api/params/body
GET    /api/unified/success
```

## 1. 這個專案比前一個CRUD多了什麼？

這個專案仍以記憶體儲存資料，但把REST API常見的幾個主題放在同一個範例中：

```text
sbrestcrud0807
├─ config/SwaggerConfig.java
├─ controller/
│  ├─ ProductController.java
│  ├─ ParameterController.java
│  └─ UnifiedResponseController.java
└─ model/
   ├─ Product.java
   ├─ ProductDTO.java
   └─ ApiResponse.java
```

主要學習重點：

1. 用`ConcurrentHashMap`實作記憶體CRUD。
2. 用DTO限制Request Body可輸入的欄位。
3. 分辨`@PathVariable`、`@RequestParam`、`@RequestHeader`與`@RequestBody`。
4. 用泛型`ApiResponse<T>`統一JSON外層格式。
5. 用Swagger／OpenAPI註解補充API文件。

### 1.1 `sbrest0722`為何不另拆一章

`sbrest0722`較早以`ConcurrentHashMap<Long, User>`與`AtomicLong`實作User CRUD，並示範：

- `@PathVariable`查單筆。
- `@RequestParam`接收`begin`、`end`後用Stream的`skip()`／`limit()`截取資料。
- `@RequestBody`接收新增與更新資料。
- `ResponseEntity.created(location)`回傳`201 Created`及`Location`。
- `ApiResponse<T>`統一成功／錯誤Body。
- Controller實作`CommandLineRunner`加入三筆記憶體種子資料。

這些主題都已由本章後續各節完整說明，因此它是同一學習階段的原始碼對照，不重複建立另一章。它和`sbrestcrud0807`同樣沒有資料庫，重新啟動後只會回到初始化資料。

## 2. Product與ProductDTO不是同一種角色

### 2.1 Product：系統內部完整資料模型

`Product`包含：

| 欄位 | 型別 | 來源／用途 |
|---|---|---|
| `id` | `String` | 建構時產生UUID |
| `name` | `String` | 產品名稱 |
| `description` | `String` | 產品說明 |
| `price` | `BigDecimal` | 金額 |
| `stock` | `int` | 庫存 |
| `createdAt` | `LocalDateTime` | 建立時間 |
| `updatedAt` | `LocalDateTime` | 最後更新時間 |

```java
@Data
public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

目前沒有`@Entity`，也沒有資料庫Repository，因此它只是一般Java資料物件。

### 2.2 ProductDTO：Request Body的輸入模型

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
}
```

DTO是Data Transfer Object。這個DTO刻意不包含：

- `id`
- `createdAt`
- `updatedAt`

### 2.3 使用條件與影響

Controller希望「Client只提供可編輯欄位，系統欄位由伺服器決定」時，可以用DTO接收：

```java
public ResponseEntity<Product> createProduct(
        @RequestBody ProductDTO product) {
    // 由伺服器建立真正的Product
}
```

好處：

- 不讓Client自行指定建立時間或更新時間。
- Request格式不必和內部完整Model完全相同。
- 日後Entity增加內部欄位時，不一定要暴露給API。

目前DTO只有欄位裁減，還沒有Bean Validation註解；例如負數價格、負數庫存仍沒有在DTO層阻擋。要繼續學習Create／Update／Response DTO拆分、`@Valid`及統一驗證錯誤，接著閱讀[第17章](17_Book_API的DTO_Bean_Validation與例外處理.md)。

## 3. Lombok註解的確切作用

| 註解 | 產生內容 | 使用條件 |
|---|---|---|
| `@Data` | Getter、Setter、`toString()`、`equals()`、`hashCode()`等 | 專案必須有Lombok依賴及annotation processor |
| `@NoArgsConstructor` | 無參數建構子 | 框架需要反射建立物件時常用 |
| `@AllArgsConstructor` | 包含全部欄位的建構子 | 要一次傳入全部欄位時使用 |

Lombok在編譯階段產生方法；它不是Spring MVC的資料綁定語法，也不會自動驗證欄位。

## 4. 記憶體Store的資料結構

```java
private static final Map<String, Product> productStore =
        new ConcurrentHashMap<>();
```

### 4.1 ConcurrentHashMap

它是可讓多執行緒並行存取的Map。此處：

- key：Controller使用的查詢ID。
- value：`Product`物件。

但它仍然只是JVM記憶體：

- 程式停止後資料消失。
- 重新啟動時只會重新加入測試資料。
- 不等於資料庫持久化。

### 4.2 `@PostConstruct`

```java
@PostConstruct
public void initTestData() {
    productStore.put("1", new Product(...));
    productStore.put("2", new Product(...));
    productStore.put("3", new Product(...));
}
```

`@PostConstruct`方法會在Spring建立並完成該Bean的依賴注入後執行一次。適合這種練習用初始化；正式系統若要持久化測試資料，通常會改由資料庫migration、SQL初始化或專門的seed流程處理。

## 5. Product CRUD端點

| 功能 | Method | 路徑 | 成功狀態 | 找不到時 |
|---|---|---|---|---|
| 全部查詢 | `GET` | `/api/products` | `200 OK` | 不適用 |
| 單筆查詢 | `GET` | `/api/products/{id}` | `200 OK` | `404 Not Found` |
| 新增 | `POST` | `/api/products` | 程式實際回`201 Created` | name空白時`400 Bad Request` |
| 更新 | `PUT` | `/api/products/{id}` | `200 OK` | `404 Not Found` |
| 刪除 | `DELETE` | `/api/products/{id}` | `204 No Content` | `404 Not Found` |

### 5.1 `ResponseEntity`控制的三個部分

```text
ResponseEntity<T>
├─ HTTP status
├─ response headers
└─ response body（T）
```

例如：

```java
return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
return ResponseEntity.notFound().build();
return ResponseEntity.noContent().build();
```

`build()`適合不需要Body的回應；`body(...)`用來放入回傳物件。

### 5.2 Optional的分支處理

```java
return Optional.ofNullable(productStore.get(id))
        .map(p -> ResponseEntity.ok().body(p))
        .orElse(ResponseEntity.notFound().build());
```

成立條件：

- `productStore.get(id)`不是`null`：執行`map`，回傳`200`與Product。
- 結果是`null`：`ofNullable`形成empty Optional，執行`orElse`回傳`404`。

## 6. 目前程式的重要ID不一致問題

`Product`建構時會產生UUID：

```java
this.id = UUID.randomUUID().toString();
```

但Controller新增時把另一個遞增數字當成Map key：

```java
int value = number.getAndIncrement();
productStore.put("" + value, newProduct);
```

這會形成兩套ID：

```text
Map key："4"
Product.id：例如"550e8400-e29b-41d4-a716-446655440000"
```

而查詢、更新、刪除使用的是Map key：

```java
productStore.get(id)
productStore.containsKey(id)
productStore.remove(id)
```

### 造成的影響

新增API回傳的`Product.id`不一定能直接拿來呼叫：

```http
GET /api/products/{Product.id}
```

因為Map真正使用的key可能是`4`。這不是語法錯誤，而是資料識別設計不一致。

### 一致化方向

可選其中一套：

```java
productStore.put(newProduct.getId(), newProduct); // 全部使用UUID
```

或把遞增ID正式放進Product並以它作為唯一ID。重點是Response中的ID、URL中的ID與Store key必須代表同一件事。

若要得到可直接使用回傳ID查詢的API，必須先選擇一種ID策略並修改程式；既有範例保留兩套ID，用來示範設計不一致會造成的問題。

## 7. 四種Request資料來源

`ParameterController`集中示範四個來源。它們的差別是「資料位於HTTP Request的哪一部分」，不是四種都可以任意互換。

### 7.1 `@PathVariable`

```java
@GetMapping("/path/{id}")
public ResponseEntity<?> pathVariable(@PathVariable Long id)
```

Request：

```http
GET /api/params/path/123
```

定義：把Mapping URL pattern中的路徑變數綁定到方法參數。

使用條件：

- Mapping路徑內必須有對應的`{id}`。
- Request URL在該位置必須有可轉成`Long`的值。
- 常用於「某一筆資源的識別值」。

### 7.2 `@RequestParam`

```java
@GetMapping("/query")
public ResponseEntity<?> requestParam(
        @RequestParam String name,
        @RequestParam(defaultValue = "10") int age,
        @RequestParam(required = false) String email)
```

Request：

```http
GET /api/params/query?name=Alice&age=25
```

三種條件：

| 宣告 | 省略參數時 |
|---|---|
| `@RequestParam String name` | 預設required，通常形成400 |
| `defaultValue="10"` | 使用預設值10 |
| `required=false` | 允許沒有值，此例用`null`判斷 |

適合篩選、搜尋、排序、分頁與可選設定。

### 7.3 `@RequestHeader`

```java
@RequestHeader(value = "Authorization", required = false)
String authorization
```

定義：從HTTP Header取得指定名稱的值。

常見使用時機：

- Authorization token
- User-Agent
- 自訂追蹤ID
- Content negotiation相關header

這個範例只讀出header並回傳，不代表已完成真正的身分驗證。

### 7.4 `@RequestBody`

```java
@PostMapping("/body")
public ResponseEntity<?> requestBody(
        @RequestBody Map<String, Object> body)
```

定義：請Spring透過HTTP message converter把Request Body反序列化成指定Java型別。

若`Content-Type: application/json`，通常由Jackson把JSON轉成`Map`、DTO或Model。

### 7.5 快速判斷表

| 資料位置 | Spring註解 | 範例 |
|---|---|---|
| URL路徑的一部分 | `@PathVariable` | `/products/10` |
| `?`後面的查詢字串 | `@RequestParam` | `?page=2` |
| HTTP Header | `@RequestHeader` | `Authorization: Bearer ...` |
| HTTP Body | `@RequestBody` | JSON物件 |

## 8. 統一API回應`ApiResponse<T>`

```java
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
}
```

### 8.1 泛型`<T>`

`T`是回應中`data`欄位的型別參數：

```java
ApiResponse<Product>
ApiResponse<List<Product>>
ApiResponse<String>
```

這讓外層格式固定，但`data`仍可裝不同型別。

### 8.2 `@JsonInclude(NON_NULL)`

序列化JSON時，值為`null`的欄位不輸出。此專案的`path`沒有被設定，因此通常不會出現在JSON中。

### 8.3 靜態工廠方法

```java
ApiResponse.ok(product)
ApiResponse.ok("取得產品成功", product)
ApiResponse.error("找不到資料")
```

它們把建構回應物件的固定步驟集中起來，減少Controller重複設定`success`、`message`與`timestamp`。

### 8.4 目前`error(int code, ...)`的限制

```java
public static <T> ApiResponse<T> error(int code, String message) {
    return new ApiResponse<>(false, message, null);
}
```

方法接收`code`，但`ApiResponse`沒有code欄位，方法內也沒有使用它。因此傳入的code不會出現在回應物件中，也不會自動改變HTTP status。

要控制HTTP status，仍須由Controller的`ResponseEntity.status(...)`決定。

## 9. Swagger註解與實際HTTP狀態要分開看

`ProductController.createProduct()`實際回傳：

```java
ResponseEntity.status(HttpStatus.CREATED).body(newProduct)
```

也就是`201 Created`；但目前Swagger註解寫：

```java
@ApiResponse(responseCode = "200", description = "產品新增成功")
```

Swagger註解是文件描述，不會改變程式真正回傳的狀態碼。兩者不一致時：

- Client實際收到201。
- Swagger文件可能顯示200。

應讓文件與Controller實作一致，避免測試人員誤判。

## 10. 本章檢查表

- [ ] 能說出Model與DTO的責任差異
- [ ] 能按Request資料位置選擇四種參數註解
- [ ] 知道記憶體Map不是持久化資料庫
- [ ] 能說明`Optional.map(...).orElse(...)`的兩條路徑
- [ ] 能找出Map key與`Product.id`不一致的影響
- [ ] 知道`ApiResponse<T>`只統一Body，不會自行設定HTTP status
- [ ] 知道Swagger文件註解不會改變Controller實際行為
- [ ] 已依第0節逐支呼叫端點，而不是只確認程式可以編譯
