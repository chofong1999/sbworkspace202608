# Spring Boot 學習筆記 08：HTML 表單與 JSON 資料綁定

- 整理日期：2026-08-06
- 範例專案：`sbfirstapp`
- HTML：`src/main/resources/static/userform.html`
- Controller：`SubmitController.java`

## 1. 本章目的

同一份 User 資料可以用不同格式送到 Spring Boot：

- HTML 表單：使用`application/x-www-form-urlencoded`
- JSON：使用`application/json`

Spring MVC 會根據 Controller 參數上的註解，將請求資料轉成`User`物件。

## 2. User 模型

`User`包含：

```java
private String id;
private String name;
private String email;
private int age;
```

無參數建構子會產生 UUID：

```java
public User() {
    this.id = UUID.randomUUID().toString();
}
```

Spring／Jackson 建立並填入 User 時需要無參數建構子與 Getter／Setter。

## 3. HTML 表單

`userform.html`放在`src/main/resources/static`，所以可直接開啟：

`http://localhost:8080/userform.html`

實際表單：

```html
<form action="api/submit/form" method="post">
    Name: <input type="text" name="name" value="Daniel Chen"/><br/>
    Email: <input type="text" name="email" value="daniel@demo.com"/><br/>
    Age: <input type="text" name="age" value="20"/><br/>
    <input type="submit"/><br/>
</form>
```

輸入欄位的`name`必須對應 Java Bean 屬性：

| HTML name | User 屬性 |
|---|---|
| `name` | `setName(...)` |
| `email` | `setEmail(...)` |
| `age` | `setAge(...)` |

### 路徑注意事項

目前`action="api/submit/form"`是相對路徑。在`/userform.html`下會解析成`/api/submit/form`，目前可以使用；更穩定的寫法是以`/`開頭：

```html
<form action="/api/submit/form" method="post">
```

這樣即使 HTML 頁面日後移到其他網址層級，也不會改變送出位置。

## 4. 使用 ModelAttribute 接收表單

```java
@PostMapping("/form")
public ResponseEntity<User> receiveModel(@ModelAttribute User user) {
    if (user.getName() != null) {
        System.out.println("user:" + user);
        User u1 = new User(user.getName(), user.getEmail(), user.getAge());
        return ResponseEntity.ok(u1);
    }
    return ResponseEntity.badRequest().build();
}
```

`@ModelAttribute`適合接收表單欄位或 Query Parameters。Spring 會：

1. 建立 User。
2. 依欄位名稱呼叫 Setter。
3. 把填好的 User 傳入方法。

完整路徑：

`POST /api/submit/form`

## 5. 使用 RequestBody 接收 JSON

```java
@PostMapping("/json")
public ResponseEntity<User> receiveJson(@RequestBody User user) {
    if (user.getName() != null) {
        System.out.println("user:" + user);
        User u1 = new User(user.getName(), user.getEmail(), user.getAge());
        return ResponseEntity.ok(u1);
    }
    return ResponseEntity.badRequest().build();
}
```

`@RequestBody`會透過 HTTP Message Converter（JSON 通常由 Jackson 處理），把 Request Body 轉成 User。

完整路徑：

`POST /api/submit/json`

Postman 設定：

1. Method 選`POST`。
2. URL 輸入`http://localhost:8080/api/submit/json`。
3. 選擇`Body → raw → JSON`。
4. 送出：

```json
{
  "name": "MyName",
  "email": "em@email.com",
  "age": 20
}
```

畫面中收到`200 OK`，回傳內容包含新 UUID：

```json
{
  "name": "MyName",
  "email": "em@email.com",
  "age": 20,
  "id": "產生的 UUID"
}
```

## 6. ModelAttribute 與 RequestBody 比較

| 項目 | `@ModelAttribute` | `@RequestBody` |
|---|---|---|
| 常見來源 | HTML form、Query Parameters | JSON、XML 等 Request Body |
| 畫面範例 Content-Type | `application/x-www-form-urlencoded` | `application/json` |
| 常見工具 | 瀏覽器表單 | Postman、前端 JavaScript、其他 API Client |
| 綁定方式 | 依參數名稱填入屬性 | 反序列化 Body 成物件 |

## 7. 修正：改由 UserService 建立並保存使用者

原本若只在 `SubmitController` 內使用 `new User(...)`，雖然回傳物件會有 ID，但該物件沒有進入 `UserRepository`，後續無法透過 CRUD API 查詢。

修正後，`SubmitController` 使用建構子注入 `UserService`：

```java
final UserService userService;

public SubmitController(UserService userService) {
    this.userService = userService;
}
```

表單與 JSON 兩種接收方式都改成呼叫同一個服務：

```java
User u1 = userService.createUser(
        user.getName(), user.getEmail(), user.getAge());
return ResponseEntity.ok(u1);
```

現在完整流程是：

```text
HTML 表單／JSON
    -> SubmitController
    -> UserService.createUser(...)
    -> UserRepository.save(...)
    -> 共用的記憶體 List
```

`UserService` 內建立 `User` 時會產生 UUID，接著由 Repository 保存。因此送出資料後，可使用回傳的 ID 呼叫：

```text
GET http://localhost:8080/api/users/{id}
```

本次畫面已驗證：表單建立的 `Daniel Chen` 能用 ID 查回完整 JSON，表示新增與 CRUD 查詢已串接成功。

補充：

- 目前 `SubmitController` 回傳的是 `200 OK`；若要更完整地表達「建立成功」，日後可改為 `201 Created`。
- Repository 仍是記憶體資料，重新啟動程式後資料會消失。
- 原始碼中的 `UserRepository` import 已未使用，可以移除；Controller 應只依賴 `UserService`。
- 欄位建議寫成 `private final UserService userService;`，封裝性更清楚。

## 8. 目前驗證的限制

目前只判斷：

```java
user.getName() != null
```

注意：

- 空字串`""`不是 null，因此仍可能通過。
- Email 格式沒有驗證。
- Age 範圍沒有驗證。

這是基礎練習。正式專案通常會使用 Bean Validation，例如`@NotBlank`、`@Email`、`@Min`與`@Valid`。

## 檢查表

- [ ] `userform.html`放在`static`資料夾
- [ ] form 欄位名稱與 User 屬性一致
- [ ] 表單送到`POST /api/submit/form`
- [ ] `@ModelAttribute`接收表單資料
- [ ] Postman 使用 raw JSON
- [ ] JSON 送到`POST /api/submit/json`
- [ ] `@RequestBody`接收 JSON
- [ ] 成功時回傳`200 OK`與帶 UUID 的 User
