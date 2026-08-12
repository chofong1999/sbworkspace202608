# Spring Boot 學習筆記 09：分層式 User CRUD API

- 整理日期：2026-08-06
- 範例專案：`sbfirstapp`
- API 前綴：`http://localhost:8080/api/users`

## 0. 前置條件與重現順序

1. 使用已加入Spring Web、且能啟動的`sbfirstapp`。
2. 依第2～4節建立`User`、`UserRepository`與`UserService`。
3. 依第5～11節建立`UserController`的六個端點。
4. 啟動程式，依「Postman測試順序」完成完整CRUD。
5. 若還要讓第8章表單與JSON入口共用資料，再依第12節修改`SubmitController`。

完成判定不是「程式能編譯」，而是同一筆ID能依序完成建立、查詢、更新、刪除，刪除後再次查詢得到404。

## 1. 分層架構

User CRUD 功能分成：

```text
HTTP Request
    ↓
UserController        接收請求、決定狀態碼
    ↓
UserService           處理業務流程
    ↓
UserRepository        儲存與查詢資料
    ↓
List<User>            目前的記憶體資料來源
```

相關檔案：

```text
com.example.demo
├─ controller/UserController.java
├─ service/UserService.java
├─ repository/UserRepository.java
└─ model/User.java
```

## 2. Model：User

User 是資料模型，包含：

- `id`：UUID 字串
- `name`：姓名
- `email`：Email
- `age`：年齡

目前不是 JPA Entity：

- 沒有`@Entity`
- 沒有`@Id`
- 沒有資料庫對應

## 3. Repository：記憶體資料存取

```java
@Repository
public class UserRepository {
    private final List<User> users = new CopyOnWriteArrayList<>();
}
```

`@Repository`表示資料存取元件。這裡沒有連接資料庫，而是使用`CopyOnWriteArrayList`暫存資料。

提供的方法：

| 方法 | 作用 |
|---|---|
| `save(User)` | 新增或取代同 ID 的 User |
| `findById(String)` | 依 ID 查詢，回傳`Optional<User>` |
| `findAll()` | 回傳全部使用者的副本 |
| `deleteById(String)` | 依 ID 刪除 |
| `count()` | 取得數量 |

### 重要限制

資料只存在 JVM 記憶體：

- 程式停止或重新啟動後，資料會全部消失。
- 多台伺服器不會共享資料。
- 適合課堂練習，不是正式持久化方案。

## 4. Service：業務流程

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

Service 透過建構子注入 Repository，提供：

- 建立使用者
- 依 ID 查詢
- 查詢全部
- 更新使用者
- 刪除使用者
- 計算數量

更新時若找不到 ID，會丟出：

```java
throw new RuntimeException("使用者不存在: " + id);
```

Controller 再把它轉成`404 Not Found`。

## 5. Controller 與 ResponseEntity

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    // ...
}
```

`ResponseEntity<T>`可以同時控制：

- HTTP Status
- Response Body
- 必要時的 Headers

## 6. CRUD API 一覽

| 功能 | Method | 路徑 | 成功狀態 |
|---|---|---|---|
| 建立 | `POST` | `/api/users` | `201 Created` |
| 查詢全部 | `GET` | `/api/users` | `200 OK` |
| 依 ID 查詢 | `GET` | `/api/users/{id}` | `200 OK`或`404` |
| 更新 | `PUT` | `/api/users/{id}` | `200 OK`或`404` |
| 刪除 | `DELETE` | `/api/users/{id}` | `204 No Content`或`404` |
| 計算數量 | `GET` | `/api/users/count` | `200 OK` |

CRUD 對應：

- Create → POST
- Read → GET
- Update → PUT
- Delete → DELETE

## 7. 建立使用者

Request：

```http
POST /api/users
Content-Type: application/json
```

```json
{
  "name": "Daniel Chen",
  "email": "daniel@demo.com",
  "age": 20
}
```

成功時：

```java
return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
```

狀態為`201 Created`，Body 是帶有 UUID 的 User。

## 8. 查詢使用者

查詢全部：

```http
GET /api/users
```

依 ID 查詢：

```http
GET /api/users/{id}
```

`Optional`的處理：

```java
return userService.getUserById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
```

找到回傳 200，找不到回傳 404。

## 9. 更新使用者

```http
PUT /api/users/{id}
Content-Type: application/json
```

```json
{
  "name": "Updated Name",
  "email": "updated@example.com",
  "age": 21
}
```

路徑中的 ID 由`@PathVariable`取得，Body 由`@RequestBody`轉成 User。找到資料就更新全部欄位；找不到則回傳 404。

## 10. 刪除使用者

```http
DELETE /api/users/{id}
```

成功時：

```java
return ResponseEntity.noContent().build();
```

`204 No Content`表示操作成功，但回應沒有 Body。

## 11. 取得使用者數量

```http
GET /api/users/count
```

回傳：

```json
{
  "count": 4
}
```

程式使用：

```java
Map.of("count", count)
```

Spring 會把 Map 轉成 JSON Object。

## 12. SubmitController 也改用同一個 UserService

這次修正把表單與 JSON 新增功能接進既有的分層架構。`SubmitController` 不再自行 `new User(...)` 後直接回傳，而是透過建構子取得 `UserService`：

```java
final UserService userService;

public SubmitController(UserService userService) {
    this.userService = userService;
}
```

兩個 POST 方法都呼叫：

```java
userService.createUser(user.getName(), user.getEmail(), user.getAge());
```

因此下列三種新增入口會共用同一份 `UserRepository` 資料：

- `POST /api/users`
- `POST /api/submit/form`
- `POST /api/submit/json`

表單或 JSON 建立成功後，拿回傳的 `id` 呼叫：

```text
GET /api/users/{id}
```

即可查到同一個使用者。可用`Daniel Chen`作為重現資料，確認回傳的ID能由CRUD API查回。

這個修正的重點不是只有「程式能跑」，而是讓不同 Controller 透過 Service 共用同一套商業邏輯與資料來源。

## 13. 目前實作的注意事項

- 沒有 Bean Validation，錯誤資料仍可能進入 Repository。
- `createUser()`捕捉所有 Exception，但沒有回傳錯誤細節。
- 更新採整筆覆蓋的 PUT，不是部分更新 PATCH。
- Repository 會先移除同 ID 資料再加到清單尾端，更新後順序可能改變。
- 沒有資料庫、交易、分頁與排序。
- 這些限制符合目前練習目的，但正式專案需要進一步設計。

## 14. 可直接重現的最小完整分層程式

沿用第8章的`User.java`，再建立以下三個檔案。本節刻意不加入Swagger註解；完成CRUD後，再依第10章加入API文件功能。

### 14.1 `repository/UserRepository.java`

```java
package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class UserRepository {
    private final List<User> users = new CopyOnWriteArrayList<>();

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(java.util.UUID.randomUUID().toString());
        }
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        return user;
    }

    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public boolean deleteById(String id) {
        return users.removeIf(user -> user.getId().equals(id));
    }

    public long count() {
        return users.size();
    }
}
```

### 14.2 `service/UserService.java`

```java
package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, int age) {
        return userRepository.save(new User(name, email, age));
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(
            String id, String name, String email, int age) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("使用者不存在: " + id));
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userRepository.save(user);
    }

    public boolean deleteUser(String id) {
        return userRepository.deleteById(id);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
```

### 14.3 `controller/UserController.java`

```java
package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = userService.createUser(
                user.getName(), user.getEmail(), user.getAge());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable String id,
            @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(
                    id, user.getName(), user.getEmail(), user.getAge()));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return userService.deleteUser(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(
                Map.of("count", userService.getUserCount()));
    }
}
```

### 14.4 把第8章SubmitController接入同一個Service

加入欄位與建構子：

```java
private final UserService userService;

public SubmitController(UserService userService) {
    this.userService = userService;
}
```

把兩個方法中的`new User(...)`改成：

```java
User saved = userService.createUser(
        user.getName(), user.getEmail(), user.getAge());
return ResponseEntity.ok(saved);
```

修改後以表單建立User，再用回傳ID呼叫`GET /api/users/{id}`。能查回同一筆資料才代表兩個Controller確實共用Repository。

## Postman 測試順序

1. POST 建立使用者，保存回傳的 ID。
2. GET 全部，確認新增成功。
3. GET `/{id}`查單筆。
4. PUT `/{id}`更新。
5. GET `/{id}`確認更新結果。
6. GET `/count`確認數量。
7. DELETE `/{id}`刪除。
8. 再 GET `/{id}`，確認回傳 404。

## 檢查表

- [ ] Controller、Service、Repository、Model 分層清楚
- [ ] 使用建構子注入
- [ ] POST 成功回傳 201
- [ ] GET 找不到資料回傳 404
- [ ] PUT 可更新既有 ID
- [ ] DELETE 成功回傳 204
- [ ] `/count`回傳 JSON Object
- [ ] 知道目前資料重啟後會消失
