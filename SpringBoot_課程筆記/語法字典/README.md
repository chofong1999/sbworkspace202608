# Spring Boot 語法字典

這份字典供「正在寫程式、需要立刻查寫法」時使用；課程主章仍負責依序教學與完整實作。

## 使用方式

1. 不知道語法名稱：先開[快速索引](00_快速索引.md)，依「想完成的事情」查找。
2. 已知道語法名稱：由下方分類進入，再利用頁面目錄或瀏覽器搜尋。
3. 只需完成程式：看條目的「基本寫法、使用條件、常用參數」。
4. 要理解設計原因、效能或資料庫差異：再開條目附的「延伸閱讀」。

## 主要入口：先看正在寫哪一種檔案

### Spring Boot Java程式

| 分類入口 | 負責什麼 | 常見檔案 | 何時進來查 |
|---|---|---|---|
| [Controller／API](02_Spring_MVC與REST.md) | HTTP路徑、輸入來源、輸出與狀態碼 | `controller/*.java` | 建立端點、接收Path／Query／JSON、回傳404或201 |
| [Service／依賴注入](01_Spring核心與依賴注入.md#page-syntax) | 業務流程、Bean建立與依賴選擇 | `service/*.java`、`config/*.java` | 注入Repository、多實作選擇、建立第三方Bean |
| [Model／Entity／DTO](Spring_Boot/Model與Entity.md) | Java資料結構、資料庫映射、關聯、驗證與JSON格式 | `model/*.java`、`entity/*.java`、`dto/*.java` | 設定欄位、主鍵、關聯、日期格式或輸入限制 |
| [Repository／查詢](05_Spring_Data查詢交易與分頁.md) | CRUD、資料庫查詢、批次修改、分頁排序 | `repository/*.java` | 撰寫`findBy...`、JPQL、更新查詢或Pageable |
| [Config／啟動類別](01_Spring核心與依賴注入.md#springbootapplication) | 啟動應用程式、元件掃描與Java設定 | `*Application.java`、`config/*.java` | 調整掃描範圍、註冊Bean或排除自動設定 |
| [Swagger／OpenAPI](08_Swagger_OpenAPI.md) | 為實際Controller補上API文件 | `controller/*.java`、DTO | 加摘要、參數說明、Schema與回應狀態 |

> 分類以「通常在哪裡使用」為主。`@Column`實際屬於Jakarta Persistence、`@JsonFormat`屬於Jackson，但都集中在Model／Entity入口，並在條目中標示真正來源。

### Thymeleaf模板

- [HTML模板語法](06_Thymeleaf模板語法.md)：`${...}`、`*{...}`、`@{...}`、`th:text`、表單、條件與迴圈。

### 設定檔與建置檔

- [`application.properties`、`pom.xml`與Maven](09_設定檔與建置.md)：連線設定、JPA設定、SQL初始化、依賴與打包。

### Web前端

| 分類入口 | 負責什麼 | 常見檔案 | 何時進來查 |
|---|---|---|---|
| [HTML／CSS／瀏覽器載入](10_HTML_CSS與瀏覽器載入.md) | 頁面骨架、樣式、選取器與外部資源 | `*.html`、`*.css` | 頁面載入不到CSS／Script／圖片，或要確認選取器 |
| [JavaScript核心](11_JavaScript核心_陣列與物件.md) | 變數、函式、運算式、Array、Object與JSON | `*.js`、HTML內`script` | 查`map`、`filter`、`??`、`?.`、Spread等語法 |
| [DOM／BOM／表單／Fetch](12_DOM_BOM表單與Fetch.md) | 操作頁面、事件、表單、對話框及HTTP請求 | 瀏覽器端`*.js` | 需要改畫面、處理事件、阻止送出或呼叫API |
| [jQuery／AJAX](13_jQuery與AJAX.md) | jQuery DOM操作與AJAX | 載入jQuery的HTML／JS | 查`$()`、`.wrapAll()`、`$.ajax()`及狀態處理 |
| [React／Vite／JSX／Hooks](14_React_Vite_JSX與Hooks.md) | 建立React專案、元件、State、表單與Effect | `*.jsx`、`vite.config.js` | 寫React元件、不可變更新或從API取得資料 |

### 輔助入口

- [依「想完成的事情」快速查找](00_快速索引.md)：不知道語法名稱或所在層級時使用。
- [延伸閱讀](延伸閱讀/)：資料庫限制、關聯風險、MVC綁定與Thymeleaf安全輸出。

## 各分類的定位

### Controller／API是「HTTP邊界」

Controller決定哪個網址與HTTP Method會進入哪個Java方法，也負責把Path、Query、Header、表單或JSON轉成方法參數，再把結果轉成HTTP回應。業務規則通常不要全部塞在Controller，應交給Service。

### Service／依賴注入是「流程與協調」

Service安排一個使用案例需要呼叫哪些Repository或其他服務。這裡也最常遇到建構子注入、同介面多實作、`@Qualifier`與交易邊界。`@Configuration`／`@Bean`雖不一定放在Service資料夾，但同屬Spring Bean建立與組裝問題。

### Model／Entity／DTO是「資料長什麼樣子」

Model入口集中Java欄位、JPA資料庫映射、Entity關聯、Bean Validation、Jackson JSON格式與Lombok。`@Column`屬於Jakarta Persistence、`@JsonFormat`屬於Jackson；分類依實際編輯位置安排，條目中仍會標出真正來源。

### Repository／查詢是「資料怎麼取得或修改」

Repository處理`JpaRepository`內建CRUD、方法名稱查詢、JPQL、原生SQL、批次更新、交易一致性與分頁排序。這裡使用的是Entity的Java屬性名還是資料庫欄位名，要依Derived Query、JPQL或native SQL分辨。

### Config／啟動類別是「Spring怎麼啟動與建立物件」

這一類處理`@SpringBootApplication`、元件掃描、`@Configuration`與`@Bean`。遇到Bean找不到、掃描不到Controller或需要建立第三方物件時，通常從此分類查起。

### Thymeleaf是「伺服器端產生HTML」

Thymeleaf模板位於`templates/`，使用`${...}`、`@{...}`與`th:*`把Controller提供的Model轉成HTML。它和Java註解、JSP scriptlet以及`application.properties`是不同語境。

### 設定檔與建置檔是「不用Java程式碼調整執行環境」

`application.properties`控制port、DataSource、JPA與SQL初始化；`pom.xml`宣告依賴與建置資訊；Maven命令負責測試、打包與執行。相似的`${...}`在設定檔與Thymeleaf中代表不同機制。

### HTML／CSS是「頁面結構與視覺」

此分類負責瀏覽器如何解析HTML、套用CSS，以及如何找到外部Script、樣式與圖片。它不負責JavaScript執行流程，也不負責React元件狀態。

### JavaScript與DOM／Fetch是「語言」與「瀏覽器能力」

JavaScript核心頁負責語言本身、Array與Object；DOM／BOM／Fetch頁負責瀏覽器提供的頁面、事件、對話框與網路API。遇到`map()`先查核心，遇到`querySelector()`或`fetch()`則查瀏覽器API。

### jQuery是「瀏覽器操作的函式庫包裝」

jQuery以`$()`、`.on()`、`$.ajax()`包裝常見DOM與HTTP操作。相同工作也可使用原生DOM與Fetch；字典分頁是為了辨識兩套API，避免混用jQuery集合與DOM Element。

### React是「用State描述介面」

React頁負責元件、Props、State、受控表單與Effect；它會使用JavaScript核心語法，也會透過Fetch存取後端，但不重複定義那些底層語法。

## 字典的範圍

- 內容以本課程使用的Java 21、Spring Boot、Spring MVC、Spring Data JPA、Thymeleaf、JavaScript、jQuery、React與Vite為主。
- 同名註解可能來自不同套件；複製前要核對`import`。
- 「預設值」會受框架版本影響的項目，條目會明確標示版本或要求以專案依賴為準。
- 範例專案名稱只是對照來源。單獨複製整個`SpringBoot_課程筆記`資料夾，不影響本字典、課程文件或圖片連結。

## 官方參考

- [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/)
- [Jakarta Persistence API](https://jakarta.ee/specifications/persistence/)
- [Thymeleaf 3.1 Tutorial](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
- [springdoc-openapi Documentation](https://springdoc.org/)
