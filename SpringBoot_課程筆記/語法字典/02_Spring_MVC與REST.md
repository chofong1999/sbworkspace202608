# Spring MVC與REST

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `@RestController` | 類別方法直接回傳JSON／文字Body | `@RestController` | [說明](#restcontroller) |
| `@Controller` | 類別方法通常回傳View名稱 | `@Controller` | [說明](#restcontroller) |
| `@RequestMapping` | 設定共同路徑或完整映射條件 | `@RequestMapping("/api/users")` | [說明](#restcontroller) |
| `@GetMapping` | 對應GET請求 | `@GetMapping("/{id}")` | [說明](#http-mapping) |
| `@PostMapping` | 對應POST請求 | `@PostMapping` | [說明](#http-mapping) |
| `@PutMapping` | 對應PUT請求 | `@PutMapping("/{id}")` | [說明](#http-mapping) |
| `@PatchMapping` | 對應PATCH請求 | `@PatchMapping("/{id}")` | [說明](#http-mapping) |
| `@DeleteMapping` | 對應DELETE請求 | `@DeleteMapping("/{id}")` | [說明](#http-mapping) |
| `@PathVariable` | 讀取URL路徑佔位值 | `@PathVariable Long id` | [說明](#pathvariable) |
| `@RequestParam` | 讀取Query／表單單一參數 | `@RequestParam String name` | [說明](#requestparam) |
| `@RequestBody` | 把JSON body轉成Java物件 | `@RequestBody UserRequest body` | [說明](#requestbody) |
| `@ModelAttribute` | 把表單欄位綁到JavaBean | `@ModelAttribute UserForm form` | [說明](#modelattribute) |
| `@RequestHeader` | 讀取HTTP Header | `@RequestHeader("User-Agent") String ua` | [說明](#requestheader) |
| `ResponseEntity<T>` | 自訂狀態碼、Header與Body | `ResponseEntity.ok(data)` | [說明](#responseentity) |

<a id="restcontroller"></a>
## `@RestController`與`@RequestMapping`

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
}
```

| 語法 | 可放位置 | 定義 |
|---|---|---|
| `@RestController` | 類別 | 方法回傳值直接寫入HTTP response body，通常轉成JSON |
| `@Controller` | 類別 | 方法回傳字串通常視為View名稱 |
| `@RequestMapping` | 類別或方法 | 宣告共同路徑，也能限制HTTP method、內容型別與Header |

`@RequestMapping`常用參數：`path`／`value`、`method`、`consumes`、`produces`、`params`、`headers`。

<a id="http-mapping"></a>
## HTTP方法映射

| 註解 | HTTP Method | 慣用目的 |
|---|---|---|
| `@GetMapping` | GET | 查詢 |
| `@PostMapping` | POST | 新增或執行動作 |
| `@PutMapping` | PUT | 完整更新 |
| `@PatchMapping` | PATCH | 部分更新 |
| `@DeleteMapping` | DELETE | 刪除 |

```java
@GetMapping("/{id}")
public ResponseEntity<User> getById(@PathVariable String id) {
    return service.findById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
}
```

這些註解都可使用`path`／`value`、`params`、`headers`、`consumes`、`produces`；其中GET通常不使用`consumes`。

<a id="pathvariable"></a>
## `@PathVariable`

**定義**：把URL路徑樣板中的變數綁定到方法參數。

```java
@GetMapping("/{id}")
public User get(@PathVariable("id") String userId) { ... }
```

**成立條件**：映射路徑必須有相同佔位符`{id}`。若Java參數名也是`id`且編譯保留參數名稱，可簡寫成`@PathVariable String id`；明寫名稱較不受編譯設定影響。

常用參數：`name`／`value`、`required`。路徑本身缺少該段時通常不會命中映射，因此`required = false`只適合搭配多個可匹配路徑的特殊設計。

<a id="requestparam"></a>
## `@RequestParam`

**定義**：讀取查詢字串或表單格式請求中的單一參數，例如`/api/greet?name=Amy`。

```java
@GetMapping("/greet")
public String greet(
        @RequestParam(name = "name", defaultValue = "Guest") String name) {
    return "Hello " + name;
}
```

| 參數 | 用途 |
|---|---|
| `name`／`value` | HTTP參數名稱 |
| `required` | 是否必填，預設`true` |
| `defaultValue` | 缺少或空值時使用預設；設定後等同不再必填 |

<a id="requestbody"></a>
## `@RequestBody`

**定義**：用HTTP message converter把request body反序列化成Java物件；JSON通常由Jackson處理。

```java
@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<User> create(@Valid @RequestBody UserRequest request) { ... }
```

**成立條件**：`Content-Type`要與內容一致；JSON欄位需能對應Java屬性。`required`預設`true`。

**不要混淆**：`@RequestBody`讀body；`@RequestParam`讀查詢／表單參數；`@PathVariable`讀URL路徑。

<a id="modelattribute"></a>
## `@ModelAttribute`

**定義**：建立或取得一個Model物件，並把請求參數依欄位名稱綁定進去。常用於傳統HTML表單。

```java
@PostMapping("/form")
public String save(@ModelAttribute UserForm form) { ... }
```

**成立條件**：表單`name`需對應JavaBean屬性；型別轉換失敗會產生綁定錯誤。JSON body不使用它，JSON改用`@RequestBody`。

<a id="requestheader"></a>
## `@RequestHeader`

```java
@GetMapping("/client")
public String client(@RequestHeader(name = "User-Agent", required = false) String agent) {
    return agent;
}
```

常用參數同`@RequestParam`：`name`／`value`、`required`、`defaultValue`。

<a id="responseentity"></a>
## `ResponseEntity<T>`

**定義**：不是註解，而是同時表示HTTP狀態碼、Headers與Body的回應物件。

```java
return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
return ResponseEntity.ok(user);
return ResponseEntity.notFound().build();
return ResponseEntity.badRequest().build();
return ResponseEntity.noContent().build();
```

`T`是body型別；沒有body時常用`ResponseEntity<Void>`。若方法永遠只回傳正常資料且狀態固定為200，可直接回傳物件；需要依結果切換404、201等狀態時使用`ResponseEntity`。

## 參數來源選擇

| 資料在哪裡 | 範例 | Java端 |
|---|---|---|
| URL路徑 | `/users/10` | `@PathVariable` |
| 查詢字串 | `/users?page=0` | `@RequestParam` |
| HTTP Header | `Authorization: ...` | `@RequestHeader` |
| JSON body | `{"name":"Amy"}` | `@RequestBody` |
| HTML form欄位 | `name=Amy&age=20` | `@ModelAttribute`或個別`@RequestParam` |

深入比較：[Spring MVC參數綁定選擇](延伸閱讀/Spring_MVC參數綁定選擇.md)
