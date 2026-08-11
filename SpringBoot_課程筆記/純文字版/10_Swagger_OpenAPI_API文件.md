# Spring Boot 學習筆記 10：Swagger／OpenAPI API 文件

- 整理日期：2026-08-10
- 範例專案：`sbfirstapp`
- Swagger UI：`http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 1. Swagger UI 顏色與 HTTP Method

Swagger UI 使用藍、綠、橘、紅區分不同的 HTTP Method：

| 顏色 | HTTP Method | 常見用途 |
|---|---|---|
| 藍色 | `GET` | 查詢資料 |
| 綠色 | `POST` | 新增資料 |
| 橘色 | `PUT` | 更新資料 |
| 紅色 | `DELETE` | 刪除資料 |

### 容易混淆的兩種顏色情境

同樣看到藍、綠、橘、紅時，要先確認自己正在看哪一種畫面：

| 情境 | 顏色代表什麼 | 是否有固定技術意義 |
|---|---|---|
| Swagger UI 的 API 清單 | HTTP Method，例如 GET、POST、PUT、DELETE | 有，由 Swagger UI 依 Method 配色 |
| Eclipse 的 Java 原始碼 | 關鍵字、字串、註解等語法醒目提示 | 配色會隨編輯器與佈景主題改變 |

例如 Swagger UI 中綠色的`POST`表示新增類型的 HTTP Request；但 Eclipse 中某段 Java 文字剛好顯示綠色，不代表它也是 POST。

如果把兩種情況混在一起，可能會只憑顏色誤判 API 的用途。判斷 Swagger API 時應同時看`GET`／`POST`文字、路徑及說明，不只看色塊。

Swagger UI 的主要用途：

- 由程式自動產生 API 文件。
- 集中查看 Controller 提供的路徑與 HTTP Method。
- 說明 API 的用途、參數與資料格式。
- 在瀏覽器中直接測試 API。

## 2. Swagger 與 OpenAPI 的差別

- **OpenAPI**：描述 REST API 的標準格式。
- **Swagger UI**：讀取 OpenAPI 文件並產生可操作的網頁介面。
- **springdoc-openapi**：掃描 Spring Boot Controller，產生 OpenAPI 文件並提供 Swagger UI。

可以把它們理解成：

```text
Spring Controller 與註解
        ↓ springdoc 掃描
OpenAPI JSON（API 規格）
        ↓ Swagger UI 讀取
瀏覽器中的互動式 API 文件
```

## 3. Maven 依賴

目前`pom.xml`加入：

```xml
<!-- Swagger / OpenAPI 3 文件（Spring Boot 4 相容） -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.1.0</version>
</dependency>
```

這個 starter 會加入：

- OpenAPI 文件產生功能。
- Swagger UI 網頁。
- Spring MVC Controller 掃描與自動設定。

加入依賴後通常要執行：

1. Maven Update Project 或重新載入 Maven。
2. 等待依賴下載完成。
3. 停止並重新啟動 Spring Boot。

## 4. SwaggerConfig 的工作

目前專案建立`config/SwaggerConfig.java`：

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot User CRUD")
                        .description("Spring Boot JPA 練習專案 API 文檔")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("開發者")
                                .email("developer@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
```

各部分作用：

| 程式 | 作用 |
|---|---|
| `@Configuration` | 告訴 Spring 這是設定類別 |
| `@Bean` | 把回傳的`OpenAPI`物件交給 Spring 管理 |
| `title` | 文件標題 |
| `description` | 專案或 API 文件說明 |
| `version` | API 文件版本，不是 Spring Boot 版本 |
| `contact` | 聯絡資訊 |
| `license` | 授權資訊 |

`SwaggerConfig`主要設定文件的基本資料，不負責建立`/api/users`等路由。API 路由仍由 Controller 的`@RequestMapping`、`@GetMapping`等註解決定。

### 問題情境：文件寫 JPA，但程式尚未使用 JPA

SwaggerConfig 的文件描述目前寫成：

```text
Spring Boot JPA 練習專案 API 文檔
```

實際程式則是另一種情況：

| 比較 | 文件描述的情況 | 目前實作的情況 |
|---|---|---|
| 資料存取 | JPA／資料庫 | `CopyOnWriteArrayList`記憶體清單 |
| Model | 通常會有`@Entity`、`@Id` | 目前沒有 JPA Entity 註解 |
| 重新啟動 | 資料庫資料通常仍存在 | 記憶體資料會消失 |

因此這句可能是預定方向或舊描述，和現在的實作不完全一致。

### 會造成什麼影響？

- 閱讀 API 文件的人可能誤以為資料已經持久化。
- 測試時重新啟動程式，資料消失會讓人誤以為 Repository 發生錯誤。
- 後續維護者可能會尋找不存在的 Entity、JPA Repository 或資料庫設定。

### 處理方式

有兩種合理方向：

1. **目前仍要練習記憶體 CRUD：**把描述改成「Spring Boot 記憶體 User CRUD 練習專案 API 文檔」。
2. **課程下一步要導入 JPA：**保留目標方向，但應等 Entity、JPA Repository 與資料庫真正完成後，再把文件寫成目前已使用 JPA。

文件文字應描述目前真實行為，避免文件與程式不一致。

## 5. Controller 為什麼會自動出現在 Swagger UI？

springdoc 會掃描 Spring MVC 的 API 註解，例如：

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public ResponseEntity<User> createUser(...) { ... }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() { ... }
}
```

因此即使沒有加 Swagger 專用註解，`GET /api/users`、`POST /api/users`等路徑仍可自動列在文件中。

Swagger 專用註解的主要作用是改善名稱與說明，不是讓 API 才能執行。

## 6. `@Tag`：替 Controller 分組

`UserController`目前使用：

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User CRUD", description = "User 資料管理")
public class UserController {
    // ...
}
```

結果：Swagger UI 會用`User CRUD`作為這組 API 的標題，並顯示「User 資料管理」。

沒有自訂`@Tag`的 Controller，springdoc 會依類別名稱產生預設分組，例如：

- `SubmitController` → `submit-controller`
- `UtilController` → `util-controller`
- `NotificationController` → `notification-controller`

`@Tag`只改變文件分組與顯示文字，不會改變 URL。

## 7. `@Operation`：說明單一 API

建立使用者的方法目前使用：

```java
@PostMapping
@Operation(
    summary = "新增User",
    description = "輸入User資料新增一筆User"
)
public ResponseEntity<User> createUser(@RequestBody User user) {
    // ...
}
```

- `summary`：列表中顯示的簡短名稱。
- `description`：展開端點後看到的詳細說明。

`SubmitController`的 JSON API 也使用：

```java
@Operation(
    summary = "Json 方式",
    description = "用Json的方式接收User的資料"
)
```

同樣地，`@Operation`只補充文件，不會改變`@RequestBody`的資料綁定方式。

## 8. `@Parameter`：說明參數

目前表單端點使用：

```java
@PostMapping("/form")
@Parameter(name = "user", description = "用戶", required = true)
public ResponseEntity<User> receiveModel(@ModelAttribute User user) {
    // ...
}
```

`@Parameter`可補充參數名稱、說明與是否必填。需要注意：`User`是包含多個欄位的複合物件，日後若希望 Swagger UI 更清楚顯示`name`、`email`、`age`，可以再研究 Model 欄位的`@Schema`或 springdoc 的物件參數支援。

## 9. Swagger UI 畫面代表什麼？

目前畫面已列出：

### User CRUD

- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `GET /api/users`
- `POST /api/users`
- `GET /api/users/count`

### Submit Controller

- `POST /api/submit/json`
- `POST /api/submit/form`

### Util Controller

- `GET /api/util/uuid`
- `GET /api/util/time`
- `GET /api/util/info`

### Notification Controller

- `GET /api/notification/sms`
- `GET /api/notification/email`

這證明 springdoc 已掃描到目前各 Controller 的 Request Mapping。

網址尾端的：

```text
#/User%20CRUD
```

是 Swagger UI 前端用來定位`User CRUD`區塊的頁面片段，不是後端 API 路徑。

## 10. 使用 Swagger UI 測試 API

一般操作流程：

1. 展開一個 API。
2. 按`Try it out`。
3. 輸入路徑參數或 Request Body。
4. 按`Execute`。
5. 查看 Request URL、Response Code 與 Response Body。

例如新增 User：

```json
{
  "name": "Daniel Chen",
  "email": "daniel@demo.com",
  "age": 20
}
```

再拿回傳的`id`測試：

```text
GET /api/users/{id}
```

Swagger UI 與 Postman 都能送 HTTP Request：

- Swagger UI 適合一邊看文件、一邊快速測試。
- Postman 適合保存 Request、建立 Collection、環境變數及較完整的測試流程。

## 11. Swagger 文件不等於測試通過

Swagger UI 能列出端點，代表路由已被掃描並產生文件，但不保證：

- 業務邏輯一定正確。
- 所有輸入都已驗證。
- 所有狀態碼都符合設計。
- Repository 或資料庫一定能正常工作。
- API 已有自動化測試。

仍要實際按`Execute`、使用 Postman，或撰寫測試程式確認結果。

## 12. 開發與正式環境注意事項

Swagger UI 很方便，但正式環境可能會暴露 API 結構。正式專案通常會依需求：

- 限制只有內部人員可以開啟。
- 加入登入與授權。
- 在正式環境停用 Swagger UI。
- 避免在文件填入假的或不適合公開的聯絡資訊。

## 常見問題

### Swagger UI 打不開

依序檢查：

1. Spring Boot 是否成功啟動。
2. 網址是否為`/swagger-ui/index.html`。
3. `pom.xml`是否已加入 springdoc UI starter。
4. Maven 依賴是否下載完成。
5. 是否已重新啟動專案。
6. Console 是否有版本不相容或 Bean 建立失敗訊息。

### API 沒出現在畫面中

檢查：

- 類別是否有`@RestController`。
- 方法是否有`@GetMapping`、`@PostMapping`等 mapping。
- Controller 是否位於 Spring Boot 主類別的套件掃描範圍內。
- 專案是否已重新編譯及重新啟動。

### 中文說明沒有出現

確認`@Tag`或`@Operation`的 import 來自：

```java
io.swagger.v3.oas.annotations...
```

並在修改後重新啟動或等待 DevTools reload。

## 本章檢查表

- [ ] 知道 OpenAPI、Swagger UI、springdoc 的角色不同
- [ ] `pom.xml`已加入 springdoc UI starter
- [ ] 可以開啟`/swagger-ui/index.html`
- [ ] 可以開啟`/v3/api-docs`
- [ ] 知道`SwaggerConfig`設定文件資訊，不負責 API 路由
- [ ] 知道`@Tag`用於分組 Controller
- [ ] 知道`@Operation`用於說明單一端點
- [ ] 知道 Swagger UI 的顏色代表 HTTP Method
- [ ] 能使用`Try it out`執行 API
- [ ] 知道文件成功產生不等於功能已完整測試
- [ ] 知道目前 User Repository 仍是記憶體 List，不是 JPA
