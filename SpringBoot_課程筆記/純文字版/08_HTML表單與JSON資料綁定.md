# Spring Boot 學習筆記 08：HTML 表單與 JSON 資料綁定

- 整理日期：2026-08-06
- 範例專案：`sbfirstapp`
- HTML：`src/main/resources/static/userform.html`
- Controller：`SubmitController.java`

## 0. 前置條件與兩階段重現方式

先使用已加入Spring Web、而且能正常啟動的`sbfirstapp`。本章分成兩個完成層級：

1. **只重現資料綁定：**建立`User`、`SubmitController`與`userform.html`，送出後直接回傳新的User。
2. **重現建立後可由CRUD API查回：**還必須完成第9章的`UserRepository`與`UserService`，再使用第7節的Service版本。

若尚未完成第9章，不應期待`GET /api/users/{id}`能查回表單資料；那是加入共用Repository後才成立的結果。

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

送出成功時應收到`200 OK`，回傳內容包含新UUID：

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

## 7. 銜接第9章：把綁定結果保存至共用Repository

本章第9節的最小版本只驗證`@ModelAttribute`與`@RequestBody`能否建立User物件，沒有保存資料。要讓表單與JSON建立的User能被CRUD API再次查到，接著完成第9章，讓`SubmitController`改由`UserService.createUser(...)`保存。

完成後的資料流是：

```text
HTML 表單／JSON
    -> SubmitController
    -> UserService.createUser(...)
    -> UserRepository.save(...)
    -> 共用的記憶體 List
```

實作程式集中放在第9章第14.4節，本章不重複貼出同一份建構子與Service呼叫。完成後，送出資料會取得UUID，可用回傳ID呼叫：

```text
GET http://localhost:8080/api/users/{id}
```

可用表單建立`Daniel Chen`，再以回傳ID查詢；若能查回相同姓名、Email與年齡，就代表資料綁定入口與CRUD API共用同一個Repository。

補充：

- 目前 `SubmitController` 回傳的是 `200 OK`；若要更完整地表達「建立成功」，日後可改為 `201 Created`。
- Repository 仍是記憶體資料，重新啟動程式後資料會消失。
- Controller應只依賴`UserService`；若仍有未使用的`UserRepository` import可移除。

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

## 9. 可直接重現的最小完整版本

先用本節版本完成「資料綁定」；完成第9章後，再把Controller中的`new User(...)`替換成`UserService.createUser(...)`。

### 9.1 `model/User.java`

```java
package com.example.demo.model;

import java.util.UUID;

public class User {
    private String id;
    private String name;
    private String email;
    private int age;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public User(String name, String email, int age) {
        this();
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
```

### 9.2 `controller/SubmitController.java`

```java
package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submit")
public class SubmitController {

    @PostMapping("/form")
    public ResponseEntity<User> receiveForm(
            @ModelAttribute User user) {
        if (user.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                new User(user.getName(), user.getEmail(), user.getAge()));
    }

    @PostMapping("/json")
    public ResponseEntity<User> receiveJson(
            @RequestBody User user) {
        if (user.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                new User(user.getName(), user.getEmail(), user.getAge()));
    }
}
```

### 9.3 `resources/static/userform.html`

```html
<!DOCTYPE html>
<html lang="zh-Hant">
<head>
    <meta charset="UTF-8">
    <title>User Form</title>
</head>
<body>
    <form action="/api/submit/form" method="post">
        Name: <input type="text" name="name" value="Daniel Chen"><br>
        Email: <input type="text" name="email" value="daniel@demo.com"><br>
        Age: <input type="number" name="age" value="20"><br>
        <button type="submit">送出</button>
    </form>
</body>
</html>
```

### 9.4 驗證

1. 開啟`http://localhost:8080/userform.html`並送出，應收到含UUID的User JSON。
2. 用Postman把第5節JSON送到`POST /api/submit/json`，也應收到含UUID的User JSON。
3. 此階段沒有Repository；重新請求或使用`GET /api/users/{id}`都不能查回剛才資料，這是預期限制。

## 檢查表

- [ ] `userform.html`放在`static`資料夾
- [ ] form 欄位名稱與 User 屬性一致
- [ ] 表單送到`POST /api/submit/form`
- [ ] `@ModelAttribute`接收表單資料
- [ ] Postman 使用 raw JSON
- [ ] JSON 送到`POST /api/submit/json`
- [ ] `@RequestBody`接收 JSON
- [ ] 成功時回傳`200 OK`與帶 UUID 的 User
- [ ] 能分辨第8章的純綁定版本與完成第9章後的持久化至記憶體版本
