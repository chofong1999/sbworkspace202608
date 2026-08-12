# Spring Boot 學習筆記 11：REST JSON 與 Thymeleaf 模板

- 整理日期：2026-08-10
- 範例專案：`sbrest0810`
- Book API：`http://localhost:8080/api/books`
- Thymeleaf 頁面：`http://localhost:8080/thymeleaf`
- 專案目前設定：Spring Boot 4.1.0、Java 17

## 0. 前置條件與重現路線

- 建立含Spring Web、Thymeleaf與Lombok的Maven／Jar專案，或使用`C:\sbworkspace202608\sbrest0810`作為完整程式對照。
- Java類別放在主要啟動套件`com.example.demo`及其子套件。
- 模板放在`src/main/resources/templates`，圖片放在`src/main/resources/static/images`。
- 先完成第2～13節的Book JSON與基本Thymeleaf，再依第15節以後加入圖片、條件、Session、連結與User頁面。
- 每個功能都應用列出的URL單獨驗證；模板可以開啟，不代表所有動態分支與CRUD都已成功。

## 1. 本章重點

這一章用兩種 Controller 比較 Spring Boot 回應瀏覽器的方式：

| 類型 | Controller | 回傳結果 |
|---|---|---|
| REST API | `@RestController` | Java 物件轉成 JSON |
| MVC 網頁 | `@Controller` | 找到 Thymeleaf 模板並產生 HTML |

```text
GET /api/books
    -> BookController
    -> Book 物件
    -> Jackson 轉 JSON
    -> 瀏覽器顯示 JSON

GET /thymeleaf
    -> ThymeleafController
    -> Model 放資料
    -> templates/index.html
    -> Thymeleaf 處理 th:* 屬性
    -> 瀏覽器收到 HTML
```

Thymeleaf 是伺服器端模板引擎，模板本身是 HTML，使用`th:*`屬性把伺服器資料帶進頁面。

## 2. Maven 依賴

REST API 使用 Spring MVC：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

Thymeleaf 使用：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

Book Model 使用 Lombok：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## 3. Book Model 與 Lombok

目前`Book.java`：

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    int id;
    String bookName;
    int price;
}
```

Lombok 註解的作用：

| 註解 | 自動產生 |
|---|---|
| `@Data` | Getter、Setter、`toString()`、`equals()`、`hashCode()` |
| `@NoArgsConstructor` | 無參數建構子 |
| `@AllArgsConstructor` | 包含全部欄位的建構子 |

因此 Controller 可以寫：

```java
new Book(100, "Java Programming", 600)
```

雖然原始碼沒有手寫 Getter，Lombok 會在編譯時產生。Spring 使用 Jackson 序列化 Book 時，可以透過這些 Getter 取得欄位值。

程式風格上，欄位通常建議明確寫成`private`：

```java
private int id;
private String bookName;
private int price;
```

## 4. `@RestController`回傳 JSON

目前`BookController.java`：

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping
    public ResponseEntity<Book> getBook() {
        Book b = new Book(100, "Java Programming", 600);
        return ResponseEntity.ok(b);
    }
}
```

註解與程式的角色：

| 程式 | 作用 |
|---|---|
| `@RestController` | 表示回傳值直接寫入 HTTP Response Body |
| `@RequestMapping("/api/books")` | 設定 Controller 共用路徑 |
| `@GetMapping` | 接收 GET Request |
| `ResponseEntity<Book>` | 同時控制 HTTP 狀態碼與 Book Body |
| `ResponseEntity.ok(b)` | 回傳`200 OK`及 Book |

`@RestController`可理解成：

```text
@Controller + @ResponseBody
```

它不會把回傳的 Book 當成模板名稱，而是交給 Spring 的 HTTP Message Converter；在 Web 專案中通常由 Jackson 轉成 JSON。

## 5. Book JSON 執行結果

瀏覽器開啟：

```text
http://localhost:8080/api/books
```

得到：

```json
{
  "id": 100,
  "bookName": "Java Programming",
  "price": 600
}
```

這裡不是 Controller 手動組 JSON 字串，而是：

```text
Book Java 物件
    -> Jackson 序列化
    -> JSON Object
```

## 6. `@Controller`與 Model

目前`ThymeleafController.java`：

```java
@Controller
@RequestMapping("/thymeleaf")
public class ThymeleafController {

    @GetMapping
    public String firstThymeleaf(Model model) {
        model.addAttribute("greeting", "<h1>Good Morning</h1>");
        model.addAttribute(
                "htmlContent",
                "<h1 style='color:blue'>Good Morning</h1>");
        return "index";
    }
}
```

`@Controller`表示這是 MVC Controller。方法回傳的：

```java
return "index";
```

不是直接回傳文字`index`，而是 View Name。Thymeleaf 會尋找：

```text
src/main/resources/templates/index.html
```

`Model`則是 Controller 傳給 View 的資料容器：

| Model 名稱 | 值 |
|---|---|
| `greeting` | `<h1>Good Morning</h1>` |
| `htmlContent` | `<h1 style='color:blue'>Good Morning</h1>` |

## 7. Thymeleaf 模板位置

模板放在：

```text
src/main/resources/templates/index.html
```

使用 Thymeleaf namespace：

```html
<html xmlns:th="http://www.thymeleaf.org">
```

`th:*`是 Thymeleaf 屬性。伺服器產生最終 HTML 後，瀏覽器不需要認識 Thymeleaf。

## 8. `th:text`：跳脫 HTML

模板內容：

```html
<h1 th:text="${greeting}">
    Greeting :
</h1>
```

`greeting`的值是：

```html
<h1>Good Morning</h1>
```

`th:text`會把內容當成普通文字並進行 HTML escaping，因此角括號不會被當成真正標籤。概念上的輸出是：

```html
<h1>&lt;h1&gt;Good Morning&lt;/h1&gt;</h1>
```

瀏覽器畫面會看見：

```text
<h1>Good Morning</h1>
```

文字仍然很大、很粗，是因為模板外層本來就是一個真正的`<h1>`；內部那組`<h1>...</h1>`只是被跳脫後的文字。

### 為什麼原本的 `Greeting :` 消失？

#### 問題情境

模板原本寫了`Greeting :`，但經由`/thymeleaf`開啟頁面時，不論 Controller 有沒有傳入`greeting`，這段文字都沒有保留下來：

```html
<h1 th:text="${greeting}">
    Greeting :
</h1>
```

核心誤會是把標籤原本的 body 當成執行時 fallback。實際上，`th:text`會在 Thymeleaf 執行時取代元素原本的**全部 body 內容**。標籤裡的`Greeting :`只是靜態原型／預覽文字，不是`${greeting}`沒有值時的預設值。

執行結果：

| 開啟方式與 Controller 狀態 | Thymeleaf 是否執行 | 最終`<h1>`內容 |
|---|---|---|
| 經 Controller 開啟，而且有傳`greeting` | 是 | 被替換成`greeting`的值 |
| 經 Controller 開啟，但沒傳`greeting` | 是 | 表達式得到 null／空值，原內容被替換成空白 |
| 直接把原始 HTML 當靜態檔預覽 | 否 | 保留原本的`Greeting :` |

概念上的最終 HTML：

```html
<!-- 有傳 greeting="Good Morning" -->
<h1>Good Morning</h1>

<!-- 沒有傳 greeting -->
<h1></h1>

<!-- 沒經過 Thymeleaf，直接預覽原始模板 -->
<h1>Greeting :</h1>
```

#### 會造成什麼影響？

- 把 body 文字誤認為預設值時，會找不到「為什麼 null 時畫面是空白」的原因。
- 同樣規則也會影響`<title th:text="${title}">預設標題</title>`，沒傳`title`時頁籤標題可能變空。
- 如果固定標籤與動態值沒有拆開，日後改文案或樣式會比較難維護。

#### 建議作法一：固定文字與動態資料拆開

如果希望標題固定保留，再接動態資料，建議寫成：

```html
<h1>
    Greeting :
    <span th:text="${greeting}">預設問候語</span>
</h1>
```

Controller 最好只傳純文字：

```java
model.addAttribute("greeting", "Good Morning");
```

這樣 HTML 結構由模板負責，Controller 只負責資料，比把`<h1>`放進 Java 字串更清楚。

#### 建議作法二：在表達式中明確設定 fallback

如果只需要「沒有值時顯示預設字」，可以寫條件運算：

```html
<h1 th:text="${greeting != null ? greeting : 'Greeting :'}">
    Greeting :
</h1>
```

但只要`th:text`存在，標籤原本的 body 就仍然只是預覽內容；真正的預設邏輯必須寫在 Thymeleaf 表達式裡。

## 9. `th:utext`：不跳脫 HTML

模板內容：

```html
<div th:utext="${htmlContent}">HTML 內容</div>
```

`htmlContent`的值：

```html
<h1 style='color:blue'>Good Morning</h1>
```

`th:utext`中的`u`可理解為 unescaped。它會把字串中的 HTML 當成真正標籤插入頁面，因此畫面顯示藍色的大字`Good Morning`。

### 安全警告

不要把使用者輸入直接交給`th:utext`。如果內容包含惡意`<script>`或事件屬性，可能造成 XSS。

原則：

- 一般文字優先使用`th:text`。
- 只有內容可信任且確定需要渲染 HTML 時才使用`th:utext`。

## 10. Thymeleaf 文字模板 `|...|`

模板內容：

```html
<p th:text="|問候語, ${greeting}!|">你好</p>
```

`|...|`是 Thymeleaf 的 literal substitution，可在一段文字中插入`${...}`：

```text
固定文字 + Model 變數 + 固定文字
```

因為外層仍是`th:text`，`greeting`中的 HTML 會被跳脫，所以畫面顯示：

```text
問候語, <h1>Good Morning</h1>!
```

而不會再產生一個新的 H1 標題。

## 11. `${...}`從哪裡取得資料？

Controller：

```java
model.addAttribute("greeting", value);
```

Template：

```html
${greeting}
```

兩邊的名稱必須一致。這裡的`${greeting}`是 Thymeleaf Standard Expression，由 Thymeleaf 模板引擎解析。

## 12. 模板中的預設內容

目前有：

```html
<title th:text="${title}">預設標題</title>
```

直接用瀏覽器開啟未經 Thymeleaf 處理的 HTML 時，可以看到`預設標題`。但經過 Thymeleaf 執行時，`th:text`會取代元素內容。

目前 Controller 沒有加入`title`：

```java
model.addAttribute("title", "...");
```

因此實際產生的頁面標題可能為空。若要設定標題，可在 Controller 補入`title`，或移除該`th:text`只保留固定標題。

## 13. `@RestController`與`@Controller`比較

| 比較 | `@RestController` | `@Controller` |
|---|---|---|
| 主要用途 | REST API | MVC 網頁 |
| 常見回傳 | Java 物件、`ResponseEntity` | View Name 字串 |
| 結果 | JSON／Response Body | HTML View |
| 本章範例 | `BookController` | `ThymeleafController` |
| 網址 | `/api/books` | `/thymeleaf` |

常見錯誤：

- 在`@RestController`方法回傳`"index"`，瀏覽器通常只收到文字`index`，不會尋找模板。
- 在`@Controller`方法想直接回傳 JSON，需額外加`@ResponseBody`，或改用`@RestController`。

## 14. 本章目前實作限制

- Book 資料是在 Controller 內固定建立，不是從 Service、Repository 或資料庫查詢。
- `/api/books`目前每次只回傳同一本 Book。
- 尚未提供新增、更新、刪除 Book 的 API。
- Thymeleaf 內容目前是固定字串，尚未使用表單或資料庫資料。
- 專案`pom.xml`目前仍設定 Java 17；若課程目標為 Java 21，需另外調整專案編譯版本。

## 15. `th:src`載入靜態與動態圖片

新增的`AttributeController`：

```java
@Controller
@RequestMapping("/attribute")
public class AttributeController {

    @GetMapping("/img")
    public String imageAttr(Model model) {
        String[] imgs = {
            "banana.png", "grape.png", "guava.png", "orange.png"
        };
        int index = (int) (Math.random() * imgs.length);
        model.addAttribute("fruitImage", imgs[index]);
        return "showimage";
    }
}
```

瀏覽器路徑：

```text
http://localhost:8080/attribute/img
```

每次 Request 都會：

1. 建立四個水果檔名的陣列。
2. 使用`Math.random()`產生 0～3 的索引。
3. 把選到的檔名放入 Model，名稱為`fruitImage`。
4. 回傳 View Name`showimage`。
5. Thymeleaf 尋找`templates/showimage.html`。

### Spring Boot 靜態資源位置

圖片實際放在：

```text
src/main/resources/static/images/
```

Spring Boot 會把`static`當成靜態資源根目錄，因此：

```text
static/images/mango.png
```

對外網址是：

```text
/images/mango.png
```

網址中不需要也不應再加`/static`。

### 固定圖片

模板第一張圖片：

```html
<img th:src="@{/images/mango.png}"
     width="320" height="240">
```

`@{...}`是 Thymeleaf URL expression。它會產生應用程式可使用的連結，並能處理可能存在的 Context Path。

這張圖片固定為`mango.png`，所以每次開啟都顯示芒果。

### 動態圖片

模板第二張圖片：

```html
<img th:src="@{|/images/${fruitImage}|}"
     width="320" height="240">
```

這裡同時使用三種語法：

| 語法 | 作用 |
|---|---|
| `@{...}` | 建立 URL |
| `|...|` | literal substitution，組合固定文字與變數 |
| `${fruitImage}` | 從 Model 取得圖片檔名 |

假設 Controller 選到：

```java
model.addAttribute("fruitImage", "orange.png");
```

最後產生：

```html
<img src="/images/orange.png" width="320" height="240">
```

畫面上方固定顯示芒果，下方顯示這次隨機選到的橘子。重新整理頁面時會產生新的亂數，因此第二張圖片可能換成香蕉、葡萄、芭樂或橘子。

### 為什麼開發者工具看不到`th:src`？

`th:src`在伺服器端已由 Thymeleaf 處理。瀏覽器收到的是普通 HTML：

```html
<img src="/images/mango.png" ...>
<img src="/images/orange.png" ...>
```

瀏覽器不會收到也不需要理解`th:src`、`${fruitImage}`等 Thymeleaf 語法。

### 圖片尺寸注意事項

同時固定：

```html
width="320" height="240"
```

可能讓原始比例不同的圖片被拉伸。若想保留比例，可以只設定寬度，或搭配 CSS 的`height: auto`／`object-fit`。

## 16. `th:if`與`th:switch`條件判斷

### 問題情境

`/attribute/status`頁面要根據兩份 Model 資料決定內容：

- `isLogin`：顯示「歡迎回來」或「請先登入」。
- `role`：顯示管理員面板、使用者面板或未知角色。

Controller 目前寫法：

```java
@GetMapping("/status")
public String statusAttr(Model model) {
    String[] role = {"user", "admin", "guava", "orange"};
    int index = (int) (Math.random() * role.length);
    model.addAttribute("role", role[index]);
    model.addAttribute("isLogin", "false");
    return "status";
}
```

### `th:if`：判斷登入狀態

```html
<div th:if="${isLogin}">歡迎回來!</div>
<div th:if="${!isLogin}">請先登入</div>
```

兩種情況：

| `isLogin`判斷結果 | 保留的元素 | 畫面 |
|---|---|---|
| `true` | 第一個`div` | 歡迎回來！ |
| `false` | 第二個`div` | 請先登入 |

`th:if`為 false 的元素不是單純用 CSS 隱藏，而是在伺服器產生 HTML 時直接不輸出。因此瀏覽器的最終 DOM 通常只會留下符合條件的元素。

### 範例傳入字串 `"false"`，不是 boolean `false`

目前 Controller 傳入：

```java
model.addAttribute("isLogin", "false");
```

這是`String`。執行範例時，Thymeleaf會把字串`"false"`判定為false，因此應顯示「請先登入」。若要避免依賴型別轉換，Controller應直接傳入boolean值。

但登入狀態本質上是布林資料，建議直接傳：

```java
model.addAttribute("isLogin", false);
```

兩種寫法的差別：

| 寫法 | 型態 | 影響 |
|---|---|---|
| `"false"` | String | 依賴模板引擎進行型態轉換，閱讀時容易誤會 |
| `false` | Boolean | 語意明確，可直接進行布林判斷 |

如果未來資料來自表單或設定檔，常會先得到 String；應在 Controller／Service 明確轉成 boolean，再交給 View，避免不同字串值造成難以預測的判斷。

### `th:switch`與`th:case`：依角色選擇內容

```html
<div th:switch="${role}">
    <h4 th:text="|Role:${role}|"></h4>
    <p th:case="'admin'">管理員面板</p>
    <p th:case="'user'">使用者面板</p>
    <p th:case="*">未知角色</p>
</div>
```

角色結果：

| `role`值 | 符合的 case | 畫面 |
|---|---|---|
| `admin` | `th:case="'admin'"` | 管理員面板 |
| `user` | `th:case="'user'"` | 使用者面板 |
| `guava` | `th:case="*"` | 未知角色 |
| `orange` | `th:case="*"` | 未知角色 |

`th:case="*"`相當於 switch 的 default；當前面沒有任何 case 符合時使用。

### 為什麼`role=orange`會顯示「未知角色」？

當亂數選到`orange`時：

1. `th:text="|Role:${role}|"`組出`Role:orange`。
2. `orange`不等於`admin`。
3. `orange`也不等於`user`。
4. 因此落入`th:case="*"`，顯示「未知角色」。

重新整理時會再次執行亂數，可能看到另外三種角色結果。不過`isLogin`目前固定傳`"false"`，所以登入訊息仍會一直是「請先登入」。

### HTML 結構注意事項

目前模板用`<h2>`包住`div`、`h4`與`p`。Heading 元素適合放標題文字，不適合作為一般區塊容器；瀏覽器可能自動修正 DOM 結構，造成開發者工具看到的階層和原始模板不同。

較清楚的結構是：

```html
<section>
    <h2>Login Status</h2>

    <div th:if="${isLogin}">歡迎回來!</div>
    <div th:if="${!isLogin}">請先登入</div>

    <div th:switch="${role}">
        <h4 th:text="|Role:${role}|"></h4>
        <p th:case="'admin'">管理員面板</p>
        <p th:case="'user'">使用者面板</p>
        <p th:case="*">未知角色</p>
    </div>
</section>
```

## 17. Thymeleaf 3.1 的 Request、Session 與 Model

### 問題情境

舊教材可能使用：

```html
${#request.getParameter('id')}
${#session.getAttribute('user')}
```

在目前 Thymeleaf 3.1 環境中，不應再依賴舊式`#request`與`#session`expression utility object。新版 Web Context 提供的是命名空間：

| 資料來源 | Thymeleaf 3.1寫法 | 說明 |
|---|---|---|
| Request parameter | `${param.id}`或`${param.id[0]}` | Parameter 可能有多個值 |
| Request attribute | `${attributeName}` | Request attribute 會加入 context root |
| Session attribute | `${session.user}`或`${session['user']}` | 讀取 Session 中名為 user 的資料 |
| Application attribute | `${application.name}` | 讀取 ServletContext attribute |

`#lists`、`#maps`、`#dates`、`#strings`、`#numbers`等工具物件仍是另一類用途，用於集合、日期、字串與數字處理，不能和被移除的 Web API 物件混為一談。

### 舊式教材寫法

Controller：

```java
@GetMapping("/session")
public String sessionAttr(Model model, HttpSession session) {
    session.setAttribute("user", "John Lee");
    model.addAttribute("session", session);
    return "session";
}
```

Template：

```html
<p th:text="${session['user']}">Session 資料</p>
```

呼叫`GET /attribute/session`成功時應回傳`200 OK`，最終HTML包含：

```html
<p>John Lee</p>
```

所以這段目前確實能執行；問題不是「能不能跑」，而是每一步是否都有必要。

### 三種寫法分別代表什麼？

#### 情況一：只需要在這一次頁面顯示資料

```java
model.addAttribute("user", "John Lee");
return "session";
```

```html
<p th:text="${user}">使用者</p>
```

特性：

- 最直接、最容易閱讀。
- 資料只供目前這次 Request 的 View 使用。
- 下一次 Request 不會自動保留。
- 如果目的只是讓此頁顯示姓名，這是較合適的做法。

#### 情況二：需要跨 Request 保存登入者資料

```java
session.setAttribute("user", "John Lee");
return "session";
```

```html
<p th:text="${session.user}">Session 資料</p>
```

特性：

- `user`保存在 HttpSession。
- 同一個瀏覽器 Session 的後續 Request 仍可讀取。
- 通常持續到移除 attribute、Session timeout、登出 invalidate 或瀏覽器 Session 改變。
- 適合登入者、購物車等需要跨頁保留的狀態。

Thymeleaf 3.1本來就提供`session`attribute namespace，所以在標準 Web Context 下，通常不必再把整個 HttpSession 物件加入 Model。

#### 情況三：把HttpSession物件另外放入Model

```java
session.setAttribute("user", "John Lee");
model.addAttribute("session", session);
```

這會把 HttpSession 本身以`session`名稱放進 Model，模板再從該物件讀取`user`。它可以執行，但相較情況二：

- 重複使用了 Thymeleaf 已提供的`session`名稱。
- View 直接接觸整個 HttpSession 物件，和 Servlet API 耦合較深。
- `session`這個 Model attribute 可能遮蔽 Thymeleaf 原本提供的 Session namespace，使讀者不易判斷`${session...}`到底指哪一個物件。
- 如果只需要一個姓名，把整個 Session 交給 View 範圍過大。

因此它適合拿來示範「Java 物件可放入 Model」或暫時繞開舊語法問題，但不是此需求下最精簡的設計。

### `model.addAttribute("user", "John Lee")`是不是比較直接？

答案取決於資料生命週期：

| 需求 | 建議方式 | 原因 |
|---|---|---|
| 只在目前頁面顯示 | Model attribute | 最簡單，不建立 Session 狀態 |
| 換頁或下一次 Request 還要使用 | Session attribute | Model 只屬於目前 Request |
| 已有 Session attribute，要在 Controller 讀取 | `HttpSession`或`@SessionAttribute` | 由 Controller 明確取得既有 Session 資料 |
| 多步驟表單暫存 Model | `@SessionAttributes` | Spring MVC 可把指定 Model attribute 暫存到 Session |

所以：

- **只需要目前頁面的輸出：**直接使用Model attribute較簡單。
- **目標是示範Session：**`session.setAttribute(...)`是必要操作。
- **把 Session 再放入 Model：**在標準 Thymeleaf 3.1環境通常可以省略。

### 建議的 Session 示範版本

```java
@GetMapping("/session")
public String sessionAttr(HttpSession session) {
    session.setAttribute("user", "John Lee");
    return "session";
}
```

```html
<p th:text="${session.user}">Session 資料</p>
```

若要證明 Session 能跨 Request 保留，應再建立另一個「只讀取、不重新 set」的端點，或跳到另一頁讀取同一個`session.user`。否則在同一個方法中 set 後立刻顯示，只能證明資料寫入成功，還沒有完整證明跨 Request 的效果。

### 實驗驗證：不把 Session 加入 Model 仍能讀取

後續實驗把兩個 Model attribute 都註解掉：

```java
@GetMapping("/session")
public String sessionAttr(Model model, HttpSession session) {
    session.setAttribute("user", "John Lee");
//  model.addAttribute("session", session);
//  model.addAttribute("user", "John Lee");
    return "session";
}
```

模板維持：

```html
<p th:text="${session['user']}">Session 資料</p>
```

實際結果：

- `GET /attribute/session`回傳`200 OK`。
- 瀏覽器顯示`John Lee`。
- 最終 HTML 為`<p>John Lee</p>`。

這個實驗排除了兩種可能：

| 被註解的程式 | 實驗結果證明 |
|---|---|
| `model.addAttribute("session", session)` | `${session[...]}`不需要靠手動放入 HttpSession 物件 |
| `model.addAttribute("user", "John Lee")` | 畫面資料不是從一般 Model attribute`user`取得 |

因此可以確定資料流程是：

```text
session.setAttribute("user", "John Lee")
    -> HttpSession 保存 user
    -> Thymeleaf 自動提供 session namespace
    -> ${session['user']}讀取
    -> 頁面顯示 John Lee
```

目前方法的`Model model`參數也已沒有被使用，可以進一步簡化成：

```java
public String sessionAttr(HttpSession session)
```

這次驗證證明「把 Session 再加入 Model」確實是多餘的；但`session.setAttribute(...)`仍然不是多餘，因為它負責把資料寫入 Session。

### 官方參考

- [Thymeleaf 3.1：Request、Session 與 Application attribute namespaces](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html#appendix-a-expression-basic-objects)
- [Spring MVC：使用`@SessionAttribute`存取既有 Session attribute](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/sessionattribute.html)

## 18. `th:href`建立超連結與查詢參數

### Controller 提供動態參數

目前`AttributeController`新增：

```java
@GetMapping("/href")
public String hrefAttr(Model model) {
    model.addAttribute("userId", 100);
    return "hyperlink";
}
```

開啟：

```text
http://localhost:8080/attribute/href
```

Spring 會回傳`templates/hyperlink.html`，而`${userId}`取得 Model 中的`100`。

### 模板中的兩種連結

```html
<a th:href="@{home}">首頁</a>
<a th:href="@{user(id=${userId})}">使用者詳情</a>
```

伺服器實際產生的 HTML 已驗證為：

```html
<a href="home">首頁</a>
<a href="user?id=100">使用者詳情</a>
```

第二個網址的括號表示查詢參數：

```text
user(id=${userId})
     -> user?id=100
```

若有多個參數，可以用逗號分隔：

```html
<a th:href="@{user(id=${userId},mode='detail')}">使用者詳情</a>
```

產生：

```text
user?id=100&mode=detail
```

Thymeleaf 會負責適當地組合及編碼查詢參數，不必自己串接`?`與`&`。

### 重要：目前兩個都是相對網址

目前寫的是：

```html
@{home}
@{user(id=${userId})}
```

因為`home`與`user`前面沒有`/`，產生的是**相對於目前路徑**的網址。當目前頁面是：

```text
/attribute/href
```

瀏覽器點擊後會解析成：

| 畫面連結 | 產生的`href` | 實際 Request 路徑 |
|---|---|---|
| 首頁 | `home` | `/attribute/home` |
| 使用者詳情 | `user?id=100` | `/attribute/user?id=100` |

所以原始碼中的「自動加入 Context Path」註解不夠精確。`@{...}`確實是 Thymeleaf URL expression，但**有沒有開頭斜線會改變網址的基準位置**。

比較：

| 寫法 | 類型 | 在目前專案中的結果 |
|---|---|---|
| `@{home}` | 相對目前網址 | `home`，瀏覽器解析為`/attribute/home` |
| `@{/home}` | 相對應用程式根目錄 | `/home` |
| `@{/attribute/home}` | 相對應用程式根目錄 | `/attribute/home` |
| `@{https://example.com}` | 絕對網址 | `https://example.com` |

如果應用程式部署時有 Context Path，例如`/demo`，`@{/home}`才會由 Thymeleaf 產生包含該 Context Path 的應用程式根路徑，例如`/demo/home`。

### 為什麼畫面有連結，但點擊後可能是 404？

目前實際 Controller 只有：

```java
@GetMapping("/href")
```

尚未看到處理`/attribute/home`或`/attribute/user`的`@GetMapping`。因此：

- `/attribute/href`本身可以正常回傳`200 OK`並顯示兩個連結。
- 連結的 HTML 語法也可以正常產生。
- 但若目標 Controller 或靜態資源不存在，點擊後仍會得到`404 Not Found`。

這是兩個不同階段：

```text
th:href 正確產生網址
    -> 瀏覽器送出新的 Request
    -> Spring 尋找能處理目標路徑的 Controller／靜態資源
    -> 找得到才會顯示目標頁；找不到就是 404
```

如果想讓目前兩個相對連結可點擊，至少還要加入相應的端點，例如：

```java
@GetMapping("/home")
public String home() {
    return "home";
}

@GetMapping("/user")
public String user(@RequestParam int id, Model model) {
    model.addAttribute("userId", id);
    return "user";
}
```

同時必須建立對應的`home.html`與`user.html`模板；只有Controller mapping而沒有模板時，請求會在View解析階段失敗。

## 19. Thymeleaf 使用者列表與動態操作連結

瀏覽器開啟：

```text
http://localhost:8080/web/users
```

畫面會顯示使用者總數、建立按鈕、使用者資料表，以及每位使用者的詳情、編輯、刪除操作。

### 列表的資料流程

`UserWebController`使用一般`@Controller`，因為這裡要回傳 HTML View，而不是直接回傳 JSON：

```java
@Controller
@RequestMapping("/web/users")
public class UserWebController {

    private final UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("userCount", userService.getUserCount());
        return "user/list";
    }
}
```

`return "user/list"`對應的模板位置是：

```text
src/main/resources/templates/user/list.html
```

完整流程：

```text
GET /web/users
    -> UserWebController.listUsers()
    -> UserService.getAllUsers()／getUserCount()
    -> UserRepository 取得記憶體資料
    -> Model：users、userCount
    -> templates/user/list.html
    -> Thymeleaf 產生使用者表格
    -> 瀏覽器收到普通 HTML
```

### 三筆初始資料從哪裡來？

`UserRepository`實作`CommandLineRunner`：

```java
@Repository
public class UserRepository implements CommandLineRunner {

    private final List<User> users = new CopyOnWriteArrayList<>();

    @Override
    public void run(String... args) {
        if (users.size() == 0) {
            users.add(new User("Mary", "mary@test.com", 18));
            users.add(new User("George", "george@test.com", 20));
            users.add(new User("John", "john@test.com", 18));
        }
    }
}
```

Spring Boot 啟動完成時會執行`run()`，所以清單一開始就有三名使用者。這些資料目前只存在 JVM 記憶體中：

- 重啟應用程式後會重新建立，ID 也會改變。
- 沒有寫入資料庫。
- `CopyOnWriteArrayList`比一般`ArrayList`更適合這種可能同時讀寫的示範，但仍不等於正式資料庫。

`User`建構子會產生 UUID：

```java
public User() {
    this.id = UUID.randomUUID().toString();
}
```

所以畫面中的長字串 ID，每次重新啟動應用程式時可能不同。

### `th:each`逐筆產生表格列

```html
<tr th:each="user : ${users}">
    <td th:text="${user.id}">ID</td>
    <td th:text="${user.name}">姓名</td>
    <td th:text="${user.email}">Email</td>
    <td th:text="${user.age}">年齡</td>
</tr>
```

語法可拆成：

```text
user        ：目前迴圈中的單一 User
${users}    ：Controller 放入 Model 的使用者集合
```

若`users`有三筆資料，`<tr>`就會輸出三次。`${user.name}`等屬性會透過 User 的 Getter 讀取；本專案使用 Lombok`@Data`產生 Getter。

### 顯示使用者數量

```html
<p>共 <span th:text="${userCount}">0</span> 位使用者</p>
```

`userCount`來自：

```java
model.addAttribute("userCount", userService.getUserCount());
```

目前 Repository 有三筆，所以最終 HTML 是：

```html
<p>共 <span>3</span> 位使用者</p>
```

### 路徑參數與查詢參數的差別

詳情按鈕：

```html
<a th:href="@{/web/users/{id}(id=${user.id})}">詳情</a>
```

因為網址中已有`{id}`占位符，括號裡的`id=${user.id}`會填入該路徑位置。假設 ID 是`abc123`，產生：

```text
/web/users/abc123
```

Controller 以`@PathVariable`接收：

```java
@GetMapping("/{id}")
public String getUserDetail(@PathVariable String id, Model model)
```

和上一節比較：

| Thymeleaf 寫法 | 是否有`{id}`占位符 | 產生結果 | Controller 常見接法 |
|---|---:|---|---|
| `@{/web/users/{id}(id=${user.id})}` | 有 | `/web/users/abc123` | `@PathVariable` |
| `@{user(id=${userId})}` | 沒有 | `user?id=100` | `@RequestParam` |

括號語法看起來相似，但有沒有對應的`{id}`會決定它是填入路徑，還是產生 Query String。

### 建立、詳情與編輯連結

```html
<a th:href="@{/web/users/create}">建立新使用者</a>
<a th:href="@{/web/users/{id}(id=${user.id})}">詳情</a>
<a th:href="@{/web/users/{id}/edit(id=${user.id})}">編輯</a>
```

對應 Controller：

| 功能 | HTTP Method與路徑 | Controller 方法用途 |
|---|---|---|
| 建立頁 | `GET /web/users/create` | 顯示空白表單 |
| 詳情頁 | `GET /web/users/{id}` | 查詢一名使用者並顯示詳情 |
| 編輯頁 | `GET /web/users/{id}/edit` | 查詢使用者並顯示編輯表單 |

### 為什麼刪除使用`form`，不是普通超連結？

```html
<form th:action="@{/web/users/{id}/delete(id=${user.id})}"
      method="post" style="display:inline;">
    <button type="submit"
            onclick="return confirm('確定要刪除嗎？')">
        刪除
    </button>
</form>
```

Controller 定義：

```java
@PostMapping("/{id}/delete")
public String deleteUser(@PathVariable String id,
                         RedirectAttributes redirectAttributes)
```

普通`<a>`點擊時送出 GET，不符合這個`@PostMapping`。因此刪除操作使用`method="post"`的表單；`th:action`負責產生帶 ID 的提交網址。

`onclick="return confirm(...)"`會先顯示確認視窗：

- 按確定：`confirm()`回傳 true，繼續送出表單。
- 按取消：回傳 false，中止提交。

這個瀏覽器確認只能避免誤按，不是後端安全機制；正式系統仍需驗證身分、權限及 CSRF。

### Flash Attribute 成功訊息

模板預留：

```html
<div th:if="${successMessage}"
     class="alert alert-success"
     th:text="${successMessage}">
    成功訊息
</div>
```

建立、更新或刪除成功時，Controller 使用：

```java
redirectAttributes.addFlashAttribute(
        "successMessage", "使用者建立成功！");
```

Flash Attribute 適合「操作完成後 redirect，再在下一個 Request 顯示一次」的訊息。顯示後通常不會一直保留。這次直接開啟列表時沒有`successMessage`，因此`th:if`為 false，最終 HTML 不會輸出該`div`。

### 驗證方法與預期結果

呼叫`GET /web/users`後，預期結果為：

- 回傳`200 OK`。
- 顯示`共 3 位使用者`。
- 依序輸出 Mary、George、John 三列。
- 每列的詳情、編輯及刪除網址都帶入該列 UUID。
- 瀏覽器收到的最終 HTML 已經沒有`th:each`、`th:text`、`th:href`與`th:action`。

模板底部原先曾使用`@{/web/}`，但該路徑沒有對應端點。後續已修正為：

```html
<a th:href="@{/web/users}">返回首頁</a>
```

現在會回到使用者列表，不再指向不存在的`/web/`。不過文字寫「返回首頁」不太精確，因為目標其實是目前的列表頁；可改成「返回列表」。

### `return "user/form"`與`return "redirect:..."`有何不同？

這幾個`return`雖然都是 String，但 Spring MVC 會依字串是否有`redirect:`前綴，採取完全不同的處理方式。

#### 情況一：回傳 View Name，直接渲染模板

```java
return "user/form";
```

Spring 把`user/form`視為 View Name，交給 Thymeleaf 尋找：

```text
src/main/resources/templates/user/form.html
```

流程：

```text
瀏覽器 Request
    -> Controller
    -> 回傳 View Name：user/form
    -> Thymeleaf 在同一次 Request 內產生 HTML
    -> 回傳 200 與 HTML
```

特性：

- 瀏覽器不會另外發出一次 Request。
- 網址列通常維持原本的 Controller URL。
- `Model`中的資料可以直接交給這次渲染的模板。
- 如果`templates/user/form.html`不存在，會發生找不到模板的錯誤。

後續課堂已建立完整的三個使用者模板：

```text
templates/user/list.html
templates/user/form.html
templates/user/detail.html
```

因此`user/list`、`user/form`與`user/detail`三個 View Name 現在都有對應模板。

#### 情況二：回傳 `redirect:`，通知瀏覽器重新導向

建立成功：

```java
return "redirect:/web/users/" + createdUser.getId();
```

更新成功：

```java
return "redirect:/web/users/" + id;
```

找不到要編輯的使用者：

```java
.orElse("redirect:/web/users");
```

`redirect:`是 Spring MVC 的特殊前綴，不是模板資料夾名稱。Controller 會回傳重新導向回應，瀏覽器再對新網址送出另一個 GET Request。

以新增使用者為例：

```text
1. POST /web/users/create
2. Controller 建立 User
3. 回應 redirect:/web/users/{新ID}
4. 瀏覽器改送 GET /web/users/{新ID}
5. getUserDetail()查詢資料
6. Thymeleaf 渲染 user/detail.html
```

主要差異：

| 比較 | `return "user/form"` | `return "redirect:/web/users/..."` |
|---|---|---|
| 字串意義 | View Name | 重新導向目標網址 |
| 是否找同名模板 | 是 | 否 |
| 是否產生第二次 Request | 否 | 是，瀏覽器通常再送 GET |
| 網址列是否改變 | 通常不變 | 會變成重新導向後的網址 |
| 一般用途 | 顯示頁面 | 資料異動完成後前往其他頁 |
| Model 資料 | 可供目前模板直接使用 | 一般 Model 不會自動跨到下一次 Request |

#### 為什麼 POST 完成後要 Redirect？

這種設計稱為 Post／Redirect／Get（PRG）：

```text
POST 修改資料 -> Redirect -> GET 顯示結果
```

如果 POST 後直接回傳結果模板，使用者在結果頁按重新整理時，瀏覽器可能詢問是否重新提交表單，甚至再次執行新增或更新。Redirect 之後，網址列停在 GET 頁面，重新整理只會再次查詢，較不容易重複寫入。

#### 為什麼成功訊息使用 Flash Attribute？

Redirect 會產生下一次 Request，所以普通 Model attribute 不會自動保留：

```java
model.addAttribute("successMessage", "使用者建立成功！");
```

若要讓重新導向後的頁面讀到一次性訊息，使用：

```java
redirectAttributes.addFlashAttribute(
        "successMessage", "使用者建立成功！");
```

Flash Attribute 會暫時保存到下一次 Request，顯示後通常移除，正適合「建立成功」或「更新成功」訊息。

### 同一份表單同時處理新增與編輯

建立表單時，Controller 傳入空白 User 與`isEdit=false`：

```java
@GetMapping("/create")
public String showCreateForm(Model model) {
    model.addAttribute("user", new User());
    model.addAttribute("isEdit", false);
    return "user/form";
}
```

編輯表單找到使用者時，傳入既有 User 與`isEdit=true`：

```java
model.addAttribute("user", user);
model.addAttribute("isEdit", true);
return "user/form";
```

`form.html`根據`isEdit`切換標題、送出網址及按鈕文字：

```html
<h1 th:text="${isEdit} ? '編輯使用者' : '建立新使用者'">
    使用者表單
</h1>

<form th:action="${isEdit}
        ? @{/web/users/{id}/edit(id=${user.id})}
        : @{/web/users/create}"
      method="post"
      th:object="${user}">

    <input type="text" th:field="*{name}" required>
    <input type="email" th:field="*{email}" required>
    <input type="number" th:field="*{age}" required>

    <button th:text="${isEdit} ? '更新' : '建立'">建立</button>
</form>
```

這樣不必建立兩份幾乎相同的新增、編輯 HTML。

| `isEdit` | 表單用途 | `th:action`結果 | 按鈕文字 |
|---:|---|---|---|
| false | 建立使用者 | `POST /web/users/create` | 建立 |
| true | 更新既有使用者 | `POST /web/users/{id}/edit` | 更新 |

### 拆解第 20 行的巢狀 Thymeleaf 寫法

完整程式：

```html
<form th:action="${isEdit} ?
        @{/web/users/{id}/edit(id=${user.id})} :
        @{/web/users/create}"
      method="post"
      th:object="${user}">
```

這不是一種新的 HTML 語法，而是`th:action`的值裡同時放了數種 Thymeleaf Standard Expression。建議由最外層往內讀。

#### 第一層：三元條件運算

```text
條件 ? 條件成立時的值 : 條件不成立時的值
```

套入本例：

```text
${isEdit}
    ? 編輯用網址
    : 建立用網址
```

它的概念和 Java 三元運算子相似，但這裡是由 Thymeleaf 在伺服器產生 HTML 時解析，不是 Java、JavaScript 或瀏覽器執行。

#### 第二層：判斷 Model 中的 `isEdit`

```html
${isEdit}
```

`${...}`是 Variable Expression，從 Model 取得`isEdit`：

- 建立頁由 Controller 傳入`false`。
- 編輯頁由 Controller 傳入`true`。

#### 第三層：條件成立時建立編輯網址

```html
@{/web/users/{id}/edit(id=${user.id})}
```

`@{...}`是 URL Expression。內部再分成：

```text
/web/users/{id}/edit     URL 樣板
id=${user.id}            用目前 User 的 id 填入 {id}
```

假設`user.id`是`abc123`，結果為：

```text
/web/users/abc123/edit
```

#### 第四層：條件不成立時建立新增網址

```html
@{/web/users/create}
```

這個網址沒有動態參數，結果就是：

```text
/web/users/create
```

#### 把整行翻譯成一般判斷

概念上相當於：

```text
如果 isEdit == true：
    form action = "/web/users/" + user.id + "/edit"
否則：
    form action = "/web/users/create"
```

實際執行已驗證：

| 開啟頁面 | `isEdit` | 最終 HTML |
|---|---:|---|
| `GET /web/users/create` | false | `<form action="/web/users/create" method="post">` |
| `GET /web/users/{Mary的ID}/edit` | true | `<form action="/web/users/{Mary的ID}/edit" method="post">` |

瀏覽器最後只收到普通的`action`，看不到`${isEdit}`、`${user.id}`或`@{...}`。

這行難讀的原因不是單一語法困難，而是它同時巢狀使用：

1. `${...}`Model 變數。
2. `? :`條件運算。
3. `@{...}`網址產生。
4. `{id}`路徑占位符。
5. 另一個`${user.id}`動態值。

閱讀時先找最外面的`? :`分成兩條路，再分別解析兩個`@{...}`，會比從左到右逐字閱讀容易。

#### 每一部分分別屬於什麼語法？

原始碼：

```html
<form th:action="${isEdit} ?
        @{/web/users/{id}/edit(id=${user.id})} :
        @{/web/users/create}"
      method="post"
      th:object="${user}">
```

先區分「正式的 Thymeleaf Expression 類型」和「Expression 內部的 URL 寫法」。前一版把兩者並列，容易誤以為每個名稱都是獨立語法，這裡修正。

#### 這一行真正用到的三種 Expression

| 程式片段 | Thymeleaf 官方分類 | 功能 |
|---|---|---|
| `${isEdit}`、`${user.id}` | Variable Expression | 取得 Context／Model 變數或物件屬性 |
| `${isEdit} ? A : B` | Conditional Expression | 依條件選擇 A 或 B |
| 完整的`@{...}` | Link URL Expression | 建立 URL，並處理 URL parameter、path variable 與 Context Path |

這三種才適合稱為本例的 Expression 語法類型。

#### `${...}`可以叫 EL 語法嗎？

廣義上可以，因為 EL 就是 Expression Language。但必須說清楚是哪一套 EL：

| 使用環境 | `${...}`由誰解析 | 較精確名稱 |
|---|---|---|
| JSP／Jakarta Server Pages | Jakarta Expression Language，舊教材也常稱 Unified EL | JSP EL／Jakarta EL expression |
| 一般 Thymeleaf Standard Dialect | 預設使用 OGNL | Thymeleaf Variable Expression |
| Spring MVC＋Thymeleaf | SpringEL／SpEL | Thymeleaf Variable Expression，內容由 SpEL 求值 |

本專案是 Spring Boot＋Thymeleaf，因此`${isEdit}`的外層分類是 Thymeleaf **Variable Expression**，裡面的`isEdit`由 SpringEL／SpEL 求值。說它是「EL 寫法」並沒有錯，但若直接說成「JSP EL」就不正確；只是兩者剛好都使用`${...}`外觀。

#### `${...}`不是字串，而是「求出一個值」

Expression 的重點是**回傳值**。`${...}`算出的結果可能是：

| Expression | 結果型態例子 | 常見使用位置 |
|---|---|---|
| `${user.name}` | String | `th:text`顯示文字 |
| `${isEdit}` | boolean | `th:if`或條件運算 |
| `${user.age}` | int／Integer | `th:text`、比較運算 |
| `${user}` | User物件 | `th:object`表單綁定 |
| `${users}` | List／其他 Iterable | `th:each`迭代 |

外面的 Thymeleaf attribute processor 決定如何使用這個值：

```html
<!-- 把結果轉成文字，放入元素 body -->
<span th:text="${user.name}"></span>

<!-- 把結果當條件判斷 -->
<div th:if="${isEdit}"></div>

<!-- 把結果當集合迭代 -->
<tr th:each="user : ${users}"></tr>

<!-- 把結果當物件綁定 -->
<form th:object="${user}"></form>
```

因此「需要字串時可以用`${...}`」只是其中一種情況。比較精確的記法是：

```text
${...}：從 Thymeleaf Context 取值或計算一個值；
值要拿來顯示、判斷、迭代或綁定，由外層 th:*決定。
```

如果要把固定文字和變數組成一段文字，不能只把`${...}`隨意塞進一般字串；常用 literal substitution：

```html
<p th:text="|使用者：${user.name}|"></p>
```

或使用 Expression inlining：

```html
<p>使用者：[[${user.name}]]</p>
```

#### Expression 與 Statement 的差別

```text
Expression：計算後得到一個值
Statement：命令程式做一件事
```

Java 例子：

```java
user.getName()       // Expression：得到姓名
age >= 18            // Expression：得到 boolean
int age = 20;        // Statement：宣告並指定變數
user.setAge(20);     // Expression statement：呼叫方法並完成動作
if (age >= 18) { }   // Control-flow statement
```

`${isEdit}`屬於 Expression，因為它會得到 true 或 false。它不能像一段 Java 方法內容那樣獨立寫多個 statements。

#### JSP 的 `<%...%>`是什麼？

`<%...%>`屬於 JSP scripting element，與 Thymeleaf 是不同模板技術：

| JSP寫法 | 名稱 | 用途 |
|---|---|---|
| `<% Java statements %>` | Scriptlet | 執行 Java statements，本身不代表輸出值 |
| `<%= Java expression %>` | JSP Expression | 計算 Java expression 並輸出到 Response |
| `<%! declaration %>` | Declaration | 宣告 JSP 轉成 Servlet 後的欄位或方法 |
| `${...}` | JSP EL Expression | 從 JSP EL Context 取值、運算，常用於輸出或標籤屬性 |

例如舊式 JSP：

```jsp
<%
    int age = 20;
    if (age >= 18) {
%>
    <p>成年人</p>
<%
    }
%>

姓名：<%= user.getName() %>
```

`<%...%>`中的片段合在一起後，必須構成有效的 Java statements。JSP 會被容器轉譯並編譯成 Servlet，所以這些 Java 才能執行。

現代 JSP 通常也避免 Scriptlet，改用 EL＋JSTL，讓 Java 邏輯留在 Controller／Service。Thymeleaf 的`.html`模板則根本不解析 JSP Scriptlet；以下寫在 Thymeleaf 中只會是無效或普通文字，不會執行 Java：

```html
<% int age = 20; %>
```

#### 在 Spring Boot＋Thymeleaf 中如何選？

| 需求 | 應放的位置／語法 |
|---|---|
| 查資料、建立物件、更新資料 | Controller／Service 的 Java |
| 把資料交給頁面 | `model.addAttribute(...)` |
| 頁面讀取 Model 值 | `${...}`Variable Expression |
| 顯示文字 | `th:text`＋`${...}` |
| 顯示或移除區塊 | `th:if`／`th:unless` |
| 重複輸出集合 | `th:each` |
| 建立網址 | Link URL Expression |
| 綁定表單物件與欄位 | `th:object`＋`*{...}` |
| 在模板內寫 Java statements | 不使用；移到 Controller／Service |

本例中的`${isEdit}`不是當字串輸出，而是當 boolean 條件：

```text
${isEdit}得到 boolean
    -> Conditional Expression 選擇其中一個 URL
    -> th:action把該 URL 設為表單 action
```

#### 第 20～21 行各語法的特性與使用時機

分析時不要把整行視為一套語法。它包含三個層次：

```text
HTML負責：表單結構與HTTP提交
Thymeleaf attribute負責：決定要修改哪個HTML屬性、綁定哪個物件
Thymeleaf Expression負責：計算attribute需要的值
```

原始碼：

```html
<form th:action="${isEdit} ?
        @{/web/users/{id}/edit(id=${user.id})} :
        @{/web/users/create}"
      method="post"
      th:object="${user}">
```

##### 1. `<form>`：HTML表單元素

| 項目 | 說明 |
|---|---|
| 類型 | HTML element，不是 Thymeleaf Expression |
| 負責者 | 瀏覽器 |
| 主要特性 | 收集內部 input，提交到 action 指定的網址 |
| 使用時機 | 需要讓使用者送出新增、查詢、更新等資料時 |
| 不負責 | 不會自己決定 action，也不會自己建立 Java物件 |

最終瀏覽器需要的是普通 HTML：

```html
<form action="/web/users/create" method="post">
```

##### 2. `th:action`：動態設定表單提交網址

| 項目 | 說明 |
|---|---|
| 類型 | Thymeleaf attribute processor |
| 輸入 | 一個能算出 URL 的 Expression |
| 輸出 | 最終 HTML 的`action="..."` |
| 使用時機 | action 要依 Model 改變、需要 Context Path、path variable 或 URL encoding 時 |
| 不使用時機 | 完全固定且不需 Thymeleaf 處理時，可直接用普通 HTML`action` |

比較：

```html
<!-- 固定HTML網址 -->
<form action="/web/users/create">

<!-- 由Thymeleaf產生網址 -->
<form th:action="@{/web/users/create}">
```

`th:action`本身不代表 URL 語法；它只是接收右邊算出的值並修改 HTML action。

##### 3. `${...}`：Variable Expression

| 項目 | 說明 |
|---|---|
| 類型 | Thymeleaf Variable Expression；Spring整合環境由 SpEL 求值 |
| 輸入來源 | Model、Thymeleaf Context、Session namespace等 |
| 結果 | 任意值：String、boolean、數字、物件、集合等 |
| 使用時機 | 頁面需要讀取後端傳入的資料、物件屬性或進行簡單計算時 |
| 不適合 | 寫一連串 Java statements、查資料庫、修改複雜業務狀態 |

本例有兩個 Variable Expression：

```html
${isEdit}   <!-- 得到boolean -->
${user.id}  <!-- 得到User的id -->
```

`${user.id}`在 JavaBean property 規則下，概念上會讀取：

```java
user.getId()
```

##### 4. `? :`：Conditional Expression

| 項目 | 說明 |
|---|---|
| 類型 | Thymeleaf Standard Expression 的 conditional operator |
| 輸入 | 一個條件、成立時的 Expression、不成立時的 Expression |
| 結果 | 只選中的那一邊的值 |
| 使用時機 | 同一個屬性只有兩種簡短結果，需要依條件切換時 |
| 不適合 | 分支很長、多層巢狀或包含複雜流程時；應改用`th:if`、拆模板或先在 Controller 計算 |

通式：

```text
condition ? valueWhenTrue : valueWhenFalse
```

本例：

```text
isEdit為true  -> 編輯網址
isEdit為false -> 建立網址
```

它會得到一個值，不是像 Java`if`statement那樣包住多行命令。

##### 5. Link URL Expression：產生網址

Link URL Expression 的外觀是 at-sign 後接大括號。為避免文件或聊天介面把 at-sign 顯示成參照圖示，以下用完整程式區塊表示：

```html
@{/web/users/create}
```

| 項目 | 說明 |
|---|---|
| 類型 | Thymeleaf Link URL Expression |
| 結果 | 可放入 href、src、action等屬性的 URL |
| 主要特性 | 處理 Context Path、URL parameter encoding與URL rewriting |
| 使用時機 | 產生站內連結、圖片路徑、CSS路徑、表單action等 URL |
| 不適合 | 執行 Controller、查資料或直接發出 HTTP Request；它只負責產生網址 |

常見位置：

```html
<a th:href="@{/web/users}">列表</a>
<img th:src="@{/images/mango.png}">
<form th:action="@{/web/users/create}">
```

##### 6. `{id}`與`(id=...)`：Link URL Expression內部的參數機制

它們不是新的頂層 Expression 類型，而是 Link URL Expression 內部的 URL 組成方式。

```html
@{/web/users/{id}/edit(id=${user.id})}
```

| 部分 | 特性 | 使用時機 |
|---|---|---|
| `{id}` | URL path中的 variable template／placeholder | Controller使用`@PathVariable`，ID需要成為路徑的一部分時 |
| `(id=${user.id})` | URL parameter清單中的name-value pair | 為URL提供動態值；Thymeleaf會自動編碼 |

當 parameter名稱和 path中的 placeholder同名：

```text
URL path：/web/users/{id}/edit
parameter：id=abc123
結果：/web/users/abc123/edit
```

Controller通常對應：

```java
@GetMapping("/{id}/edit")
public String edit(@PathVariable String id) {
    // ...
}
```

如果 URL path中沒有同名 placeholder：

```html
@{/web/users(id=${user.id})}
```

結果會變成 Query String：

```text
/web/users?id=abc123
```

Controller通常改用：

```java
public String users(@RequestParam String id) {
    // ...
}
```

##### 7. `method="post"`：HTML提交方法

| 項目 | 說明 |
|---|---|
| 類型 | 原生 HTML attribute |
| 負責者 | 瀏覽器 |
| 結果 | 提交表單時送出 HTTP POST |
| 使用時機 | 新增、更新、刪除等會改變伺服器資料的操作 |
| 對應後端 | Spring`@PostMapping` |

`method="post"`不是 Thymeleaf Expression。Thymeleaf先產生 action，真正送出 POST的是瀏覽器。

##### 8. `th:object`：選定表單綁定物件

```html
th:object="${user}"
```

| 項目 | 說明 |
|---|---|
| 類型 | Thymeleaf Spring表單綁定attribute |
| 輸入 | 一個物件；本例為`${user}` |
| 結果 | 在form範圍內把 User設為selection／form-backing object |
| 使用時機 | 多個欄位都要綁定同一個 Java物件時 |
| 常見搭配 | `th:field="*{name}"`、`*{email}`、`*{age}` |

`th:object`不會建立 User。建立模式的 User 是 Controller先放入 Model：

```java
model.addAttribute("user", new User());
```

##### 9. `*{...}`：Selection Expression

```html
<form th:object="${user}">
    <input th:field="*{name}">
</form>
```

| 項目 | 說明 |
|---|---|
| 類型 | Thymeleaf Selection Variable Expression |
| 查找基準 | 最近的`th:object`所選物件 |
| 結果 | 該物件的屬性值 |
| 使用時機 | 表單欄位要綁定 form-backing object 的屬性時 |

在目前 form內：

```text
*{name}  約等於  ${user.name}
*{email} 約等於  ${user.email}
*{age}   約等於  ${user.age}
```

但`th:field`不只顯示值，也會產生適合資料綁定的`name`、`value`等 HTML屬性。

##### 實際選用速查

| 想做的事 | 使用方式 |
|---|---|
| 從 Model取得值 | Variable Expression |
| 在兩個值中選一個 | Conditional Expression |
| 產生網址 | Link URL Expression |
| 把動態值放入`/{id}` | Link URL Expression內的 path variable＋URL parameter |
| 設定表單提交網址 | `th:action` |
| 指定POST | HTML`method="post"` |
| 指定表單綁定物件 | `th:object` |
| 綁定該物件的單一欄位 | `th:field`＋Selection Expression |
| 執行查詢、儲存與業務邏輯 | Java Controller／Service，不放在模板Expression |

#### `{id}`與Link URL Expression可以寫在哪裡？

以下改用規格式描述，不把功能說明冒充正式語法名稱。

##### Link URL Expression規格

```text
正式名稱：Link URL Expression
一般形式：@{ URL_BASE }
帶參數形式：@{ URL_BASE ( NAME = VALUE [, NAME = VALUE]* ) }
所屬語言：Thymeleaf Standard Expression Syntax
求值結果：由註冊的ILinkBuilder建立的URL值
求值時間：Thymeleaf處理模板時（伺服器端）
```

成立條件：

1. 內容位於Thymeleaf會解析Standard Expression的context。
2. Template由Thymeleaf TemplateEngine處理；原始靜態HTML不會自行解析。
3. URL parameter的VALUE必須是合法Thymeleaf Expression或literal。
4. 若使用context-relative URL（URL_BASE以`/`開頭），需要Web Context才能依部署Context Path建立結果。

合法位置包括：

```html
<a th:href="@{/web/users}">...</a>
<img th:src="@{/images/a.png}">
<form th:action="@{/web/users/create}">
<span th:text="@{/web/users}"></span>
<div th:with="u=@{/web/users}"></div>
<p>[[@{/web/users}]]</p>
```

不成立的位置：

```html
<!-- 普通href不會要求Thymeleaf解析右側 -->
<a href="@{/web/users}">...</a>

<!-- 普通文字沒有[[...]] inline邊界 -->
<p>@{/web/users}</p>
```

限制：

- 它只建立URL值，不發出HTTP Request。
- 瀏覽器、Java編譯器及一般外部CSS解析器不認識此語法。
- 能否使用不由`<>`決定，而由所在位置是否啟動Thymeleaf Expression解析決定。

##### URL parameter與path variable規格

```text
URL_BASE：/web/users/{id}/edit
parameter list：(id=${user.id})
完整Link URL Expression：@{/web/users/{id}/edit(id=${user.id})}
```

`{id}`的正式定位：

- 它不是Thymeleaf Simple Expression類型。
- 它是URL_BASE內的variable template／path variable placeholder。
- 名稱為`id`，名稱比對區分使用的parameter key。
- 它只有在Link URL Expression的URL path解析過程中才具本段所述作用。

`(id=${user.id})`的正式定位：

- 它是Link URL Expression的URL parameter list。
- `id`是parameter name。
- `${user.id}`是parameter value expression。
- parameter name若與URL_BASE中的placeholder相符，值用於path variable展開。
- 沒有對應placeholder的parameter會成為Query Parameter。
- 多個parameter以逗號分隔。
- parameter value會執行必要的URL encoding。

使用條件：

| 需求 | URL形式 | Spring MVC接收方式 |
|---|---|---|
| 值是resource path的一部分 | `/web/users/{id}`＋`(id=...)` | `@PathVariable` |
| 值是查詢條件 | `/web/users`＋`(id=...)` | `@RequestParam` |

範例：

```html
@{/web/users/{id}/edit(id=${user.id})}
```

當`user.id=100`時，求值結果：

```text
/web/users/100/edit
```

```html
@{/web/users(id=${user.id})}
```

當`user.id=100`時，求值結果：

```text
/web/users?id=100
```

##### `{id}`不能單獨當成Thymeleaf Expression

在這段程式裡：

```html
@{/web/users/{id}/edit(id=${user.id})}
```

`{id}`只是 Link URL Expression 的 URL path樣板中的一個位置。它不能脫離URL直接用來讀值：

```html
<!-- 不是取得id的Thymeleaf Variable Expression -->
<span th:text="{id}"></span>
```

要讀 Model中的id，仍要使用Variable Expression：

```html
<span th:text="${id}"></span>
```

但大括號URL樣板不只Thymeleaf有。Spring MVC Controller也會使用相似外觀：

```java
@GetMapping("/{id}/edit")
public String edit(@PathVariable String id) {
    // ...
}
```

兩邊用途不同：

```text
Thymeleaf的{id}：產生出去的URL
Spring Mapping的{id}：比對進來的Request URL
```

它們互相配合，但分別由Thymeleaf與Spring MVC解析，不是可以任意放在任何地方的通用語法。

##### Link URL Expression不只可以放在HTML開始標籤內

最常見的確是在`th:*`attribute中：

```html
<a th:href="@{/web/users}">列表</a>
<img th:src="@{/images/mango.png}">
<form th:action="@{/web/users/create}">
<link th:href="@{/css/style.css}" rel="stylesheet">
```

因為這些HTML attribute本來就需要URL，所以最有實際意義。

但Link URL Expression屬於Thymeleaf Standard Expression，可以出現在其他會解析Expression的位置。

###### 當成文字輸出

```html
<p th:text="@{/web/users}"></p>
```

最終可能顯示：

```text
/web/users
```

這在語法上可行，但通常沒有把URL直接顯示成文字的必要。

###### 使用Expression inlining放在元素內容中

```html
<p>列表網址：[[@{/web/users}]]</p>
```

這表示Link URL Expression不一定要寫在`<...>`開始標籤的attribute裡；它也能在Thymeleaf處理的元素body中求值。

###### 先存成Thymeleaf區域變數

```html
<div th:with="listUrl=@{/web/users}">
    <a th:href="${listUrl}">使用者列表</a>
</div>
```

###### 放進JavaScript模板

```html
<script th:inline="javascript">
    const listUrl = [[@{/web/users}]];
</script>
```

Thymeleaf先在伺服器端產生URL，再把結果放入JavaScript。瀏覽器本身不認識Link URL Expression。

##### 真正限制不是`<>`，而是「有沒有經過Thymeleaf解析」

| 放置位置 | 能否使用Link URL Expression | 原因 |
|---|---:|---|
| Thymeleaf模板的`th:href`、`th:src`、`th:action` | 可以 | attribute processor會解析Expression |
| Thymeleaf模板的`th:text`、`th:with` | 可以 | 這些attribute接受Standard Expression |
| Thymeleaf inline expression | 可以 | `[[...]]`會要求Thymeleaf求值 |
| Java Controller原始碼 | 不可以 | Java編譯器不認識Thymeleaf Expression |
| 瀏覽器收到的普通HTML／JavaScript | 不可以 | 瀏覽器不認識Thymeleaf Expression |
| 未經Thymeleaf處理的靜態HTML | 不會生效 | 沒有模板引擎負責解析 |
| 外部`.css`檔案 | 通常不可以 | CSS由瀏覽器處理，不經Thymeleaf模板引擎 |

因此應記成：

```text
Link URL Expression可以用在Thymeleaf會解析Expression的位置；
最常放在需要URL的HTML attribute，但不受限於<>內。
```

官方參考：

- [Thymeleaf 3.1：Standard Expression Syntax](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html#standard-expression-syntax)
- [Jakarta Server Pages Specification](https://jakarta.ee/specifications/pages/3.1/)

#### `{id}`與`(id=...)`不是獨立的 Expression 類型

整段：

```html
@{/web/users/{id}/edit(id=${user.id})}
```

整體只有一個正式分類：**Link URL Expression**。

它的內部包含：

```text
/web/users/{id}/edit
```

這是帶有 path variable placeholder 的 URL path；`{id}`是占位位置。

```text
(id=${user.id})
```

這是 Link URL Expression 的 URL parameter 寫法。因為 URL path 中剛好存在同名的`{id}`，Thymeleaf 會把該 parameter 值填入 path variable，結果例如：

```text
/web/users/abc123/edit
```

如果 URL path 沒有`{id}`：

```html
@{/web/users(id=${user.id})}
```

同一個`id`就會成為 Query Parameter：

```text
/web/users?id=abc123
```

因此「URL 路徑占位符」「URL 參數替換」比較適合作為功能說明，不應冒充和 Variable／Link URL Expression 同一層級的官方語法名稱。官方文件使用的關鍵用語是 URL parameters、variable templates in URL paths，以及 parameters in the form of path variables。

#### HTML 與 Thymeleaf attribute 也不是 Expression 類型

```html
<form ...>
```

是 HTML element。

```html
th:action="..."
```

是 Thymeleaf Standard Dialect 的 attribute processor；它接收 Expression 的計算結果，最後產生普通 HTML`action`。

```html
method="post"
```

是原生 HTML attribute，由瀏覽器決定使用 POST。

```html
th:object="${user}"
```

是 Thymeleaf 表單綁定用的 attribute processor，屬性值`${user}`仍是 Variable Expression。表單內的`*{name}`則是另一個正式 Expression 類型：Selection Expression。

#### 修正後的層級圖

```text
HTML form element
├─ Thymeleaf th:action attribute processor
│  └─ Conditional Expression：條件 ? A : B
│     ├─ 條件：Variable Expression ${isEdit}
│     ├─ A：一個 Link URL Expression
│     │  └─ 內部使用 URL parameter 與 path variable
│     └─ B：一個 Link URL Expression
├─ HTML method="post"
└─ Thymeleaf th:object attribute processor
   └─ Variable Expression ${user}
```

這個分層的重點是：不要把 HTML、Thymeleaf attribute processor、Expression 類型和 Link URL Expression 內部的 URL 組成方式全部當成同一層語法。

官方參考：

- [Thymeleaf 3.1：Variable Expressions、Conditional Expressions 與 Link URLs](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
- [Thymeleaf：Standard URL Syntax](https://www.thymeleaf.org/doc/articles/standardurlsyntax.html)

### `th:object`與`th:field`

```html
<form th:object="${user}">
    <input th:field="*{name}">
</form>
```

- `th:object="${user}"`指定目前表單綁定的主要物件。
- `*{name}`是 Selection Expression，表示目前 User 的`name`屬性。
- 顯示編輯頁時，欄位會帶入既有資料。
- 提交表單時，Spring MVC 的`@ModelAttribute User user`會接收 name、email、age。

概念流程：

```text
HTML input name／email／age
    -> POST form data
    -> @ModelAttribute User user
    -> UserService.createUser()或updateUser()
```

### 驗證新增第四名使用者

送出下列資料：

```text
姓名：Harry Potter
Email：daniel@demo.com
年齡：77
```

新增後列表應從三筆變成四筆，顯示`共 4 位使用者`，而且新資料取得一組UUID。符合這三個條件才能證明新增流程已串接：

```text
建立表單
    -> POST /web/users/create
    -> @ModelAttribute User
    -> UserService.createUser()
    -> UserRepository.save()
    -> Redirect 到使用者詳情
    -> 返回列表時看到第四筆資料
```

資料仍只存在`CopyOnWriteArrayList`記憶體中；重新啟動應用程式後，第四筆不會永久保存。

### 詳情頁

`detail.html`使用`${user...}`顯示單一使用者：

```html
<span th:text="${user.id}">ID</span>
<span th:text="${user.name}">姓名</span>
<span th:text="${user.email}">Email</span>
<span th:text="${user.age}">年齡</span>
```

它對應：

```java
@GetMapping("/{id}")
public String getUserDetail(@PathVariable String id, Model model)
```

找到資料時回傳`user/detail`；找不到時重新導向`/web/users`。

### 不同Redirect目標的成功訊息差異

三種操作都會建立 Flash Attribute，但重新導向的目標不同：

| 操作 | Redirect 目標 | 目標模板是否顯示`${successMessage}` | 畫面結果 |
|---|---|---:|---|
| 建立 | `/web/users/{新ID}` | `detail.html`目前沒有 | 建立成功訊息不會顯示 |
| 更新 | `/web/users/{id}` | `detail.html`目前沒有 | 更新成功訊息不會顯示 |
| 刪除 | `/web/users` | `list.html`有 | 刪除成功訊息會顯示一次 |

如果希望建立與更新後也顯示訊息，可以在`detail.html`加入：

```html
<div th:if="${successMessage}"
     th:text="${successMessage}"
     class="alert alert-success">
</div>
```

Flash Attribute 有成功傳到 Redirect 後的 Request，不代表一定會自動出現在畫面；模板仍然必須實際讀取並輸出它。

### IDE 補充：CSS 色碼前的小色塊是什麼？

編輯器在`#4CAF50`、`#dc3545`等 CSS 色碼前顯示的小方塊，是 Eclipse Web 編輯工具提供的**顏色預覽**，不是 HTML 或 CSS 原始碼的一部分。

若Eclipse安裝Wild Web Developer與LSP4E，CSS Language Server辨識色碼後，Document Color／Code Mining功能會在編輯器繪製對應顏色的小方塊；點擊時還能開啟選色介面。

因此同一份檔案在兩台電腦上可能顯示不同：

| 情況 | 是否可能顯示色塊 |
|---|---|
| 使用支援 Document Color 的 Wild Web Developer／Generic Editor | 會 |
| 使用不同的 HTML Editor 或一般文字編輯器 | 可能不會 |
| Eclipse／外掛版本較舊 | 可能不會 |
| Language Server 或 Code Mining 沒啟用／尚未啟動 | 可能不會 |

這只是 IDE 輔助顯示：

- 不會被保存成額外字元。
- 不會出現在瀏覽器收到的 HTML。
- 不影響 CSS 顏色與 Spring Boot 執行結果。
- 不能只憑是否出現色塊判斷原始碼是否正確。

若兩邊想顯示一致，先比較 HTML 檔案的`Open With`編輯器，再比較 Eclipse 與 Wild Web Developer 版本，而不是修改 CSS 色碼。

官方專案說明：[Eclipse Wild Web Developer](https://projects.eclipse.org/projects/tools.wildwebdeveloper)

## 本章檢查表

- [ ] 知道`@RestController`直接產生 Response Body
- [ ] 知道 Book 物件由 Jackson 自動轉成 JSON
- [ ] 知道 Lombok 會在編譯時產生 Getter、Setter 與建構子
- [ ] 知道`@Controller`回傳 View Name
- [ ] 知道`return "index"`會尋找`templates/index.html`
- [ ] 知道 Model attribute 名稱要和`${...}`一致
- [ ] 知道`th:text`會跳脫 HTML
- [ ] 知道`th:utext`會把字串當 HTML，並有 XSS 風險
- [ ] 知道`|...${...}...|`是 Thymeleaf 文字模板
- [ ] 知道`${greeting}`是由 Thymeleaf 解析的 Standard Expression
- [ ] 知道`static/images`對外路徑是`/images`
- [ ] 知道`@{...}`是 Thymeleaf URL expression
- [ ] 能用`@{|/images/${fruitImage}|}`組出動態圖片網址
- [ ] 知道瀏覽器最後只收到普通的`src`屬性
- [ ] 知道`th:if`為 false 時元素不會輸出到最終 HTML
- [ ] 能分辨 String`"false"`與 Boolean`false`
- [ ] 知道`th:switch`會選擇符合的`th:case`
- [ ] 知道`th:case="*"`是預設分支
- [ ] 能解釋`role=orange`為什麼顯示「未知角色」
- [ ] 知道 Thymeleaf 3.1使用`param`、`session`、`application`命名空間
- [ ] 能分辨 Model attribute 與 Session attribute 的生命週期
- [ ] 知道只顯示目前頁面時 Model 較直接
- [ ] 知道跨 Request 保存資料時必須使用 Session 或其他持久狀態
- [ ] 知道把整個 HttpSession 再加入 Model 通常可以省略
- [ ] 能用移除 Model attribute 的實驗證明`${session.user}`來自 Session namespace
- [ ] 知道`th:href="@{...}"`用來產生 URL
- [ ] 能用`@{user(id=${userId})}`建立帶查詢參數的網址
- [ ] 能分辨`@{home}`相對網址與`@{/home}`應用程式根路徑
- [ ] 知道連結能產生不代表目標端點一定存在，仍可能得到 404
- [ ] 知道`return "user/list"`對應`templates/user/list.html`
- [ ] 能用`th:each="user : ${users}"`逐筆產生表格列
- [ ] 能分辨 Path Variable 與 Query String 的 URL 產生方式
- [ ] 知道刪除端點使用 POST 時應以表單提交，而不是普通 GET 連結
- [ ] 知道 Flash Attribute 適合 redirect 後顯示一次性訊息
- [ ] 知道目前使用者 Repository 是記憶體資料，重啟後會重新建立
- [ ] 能分辨 View Name 與`redirect:`特殊前綴
- [ ] 能說明 Post／Redirect／Get 如何避免重新整理時重複提交
- [ ] 知道普通 Model 不會自動跨 Redirect，成功訊息應使用 Flash Attribute
- [ ] 知道 CSS 色碼前的小色塊是 IDE 預覽，不屬於程式碼
- [ ] 能用`isEdit`讓同一份表單切換建立與編輯模式
- [ ] 知道`th:object`指定表單物件，`*{...}`讀寫該物件欄位
- [ ] 能說明新增使用者從表單到 Repository 再 Redirect 的流程
- [ ] 知道 Flash Attribute 必須由目標模板輸出才會顯示
- [ ] 能由外往內拆解`th:action`中的三元運算與兩個 URL Expression
- [ ] 知道第 20 行由 Thymeleaf 在伺服器端解析，瀏覽器只收到最後的 action
