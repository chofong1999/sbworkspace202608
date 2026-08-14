# Spring Boot 課程筆記

## 閱讀與重現方式

本筆記以學習者為讀者，章節依編號延續。實作章會列出前置條件、檔案位置、操作順序、測試方式與成功判定；若章節沿用前一章的專案，必須先完成所列前置章節。

筆記中的「範例實際狀態」用來區分已存在的課堂程式與建議改良，不是整理工作的回報。要判定操作成功，必須以該章列出的HTTP結果、Console訊息、檔案產物或畫面條件驗證，不能只以「程式碼已貼上」判定。

本機保存的課堂專案可作為完整原始碼對照；讀者若沒有這些專案，應依各章的前置條件從Spring Initializr建立專案，再依章節順序實作。

目前筆記包含純文字版、完整圖文版與原始截圖素材：

## 1. 純文字版

位置：`純文字版/`

這是目前持續維護的正式筆記：

1. [環境設定](純文字版/01_環境設定.md)
2. [建立第一個Spring Boot專案](純文字版/02_建立第一個SpringBoot專案.md)
3. [Maven打包與執行JAR](純文字版/03_Maven打包與執行JAR.md)
4. [使用Spring Initializr建立專案](純文字版/04_使用SpringInitializr建立專案.md)
5. [Whitelabel 404與套件掃描](純文字版/05_Whitelabel404與套件掃描.md)
6. [介面多實作與Qualifier依賴注入](純文字版/06_介面多實作與Qualifier依賴注入.md)
7. [Component、Configuration與Bean](純文字版/07_Component_Configuration與Bean.md)
8. [HTML表單與JSON資料綁定](純文字版/08_HTML表單與JSON資料綁定.md)
9. [分層式User CRUD API](純文字版/09_分層式User_CRUD_API.md)
10. [Swagger／OpenAPI API文件](純文字版/10_Swagger_OpenAPI_API文件.md)
11. [REST JSON與Thymeleaf模板](純文字版/11_REST_JSON與Thymeleaf模板.md)
12. [REST CRUD進階、參數DTO與統一回應](純文字版/12_REST_CRUD進階_參數DTO與統一回應.md)
13. [Spring Data JPA與MySQL](純文字版/13_Spring_Data_JPA與MySQL.md)
14. [Spring Data JPA、SQLite、Docker與Render部署](純文字版/14_Spring_Data_JPA與SQLite_Docker_Render部署.md)
15. [Spring Data JPA查詢方法、Query與分頁](純文字版/15_Spring_Data_JPA查詢方法_Query與分頁.md)
16. [JPA OneToMany、ManyToOne與JSON關聯](純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md)

### 延伸閱讀

延伸閱讀用來保存值得保留、但不是首次完成實作所必需的第二套案例、語法邊界、效能分析與故障診斷。主章保持可依序操作；需要深入研究或遇到相應問題時再查閱補充文件。

- [第11章：Thymeleaf表達式與常見疑難](純文字版/延伸閱讀/11_Thymeleaf表達式與常見疑難.md)
- [第13章：JPA對應既有資料表的Coffee案例](純文字版/延伸閱讀/13_JPA對應既有資料表_Coffee案例.md)
- [第14章：Git與Render部署故障排查](純文字版/延伸閱讀/14_Git與Render部署故障排查.md)
- [第16章：JPA查詢效能、批次更新與關聯風險](純文字版/延伸閱讀/16_JPA查詢效能_批次更新與關聯風險.md)

新內容會依學習順序新增章節；與既有主題重疊時則修訂原章，避免同一知識分散在多份文件。

### 重複主題應讀哪一章？

後面的實作會自然沿用前面的概念。若同一名詞出現在多章，以下列「主章」為完整定義來源；後續章只說該專案的新差異：

| 主題 | 主章 | 後續章的用途 |
|---|---|---|
| Eclipse、JDK 21與Spring Tools | 第1章 | 第2～4章只處理建立方式與專案版本差異 |
| IDE建立Spring Boot專案 | 第2章 | 第4章是改用Spring Initializr網站的替代入口 |
| 表單與JSON資料綁定 | 第8章 | 第9章把綁定後的User接入共用Service／Repository |
| 記憶體分層CRUD | 第9章 | 第11章只新增Thymeleaf網頁層；第12章加入DTO、參數來源與統一回應 |
| Swagger／OpenAPI | 第10章 | 第12章只比較文件註解與實際HTTP結果 |
| Thymeleaf語法與MVC View | 第11章 | 其他REST章不重複說明模板語法 |
| Entity、JpaRepository與MySQL | 第13章 | 第15章新增查詢；第16章新增Entity關聯與批次更新 |
| SQLite、Docker與Render | 第14章 | 與MySQL JPA基礎分開，集中處理資料庫切換及部署 |
| Derived Query、JPQL與分頁 | 第15章 | 第16章直接套用JPQL處理Fetch Join與Bulk Update |
| OneToMany、N+1與關聯JSON | 第16章 | 目前最新關聯實作主章 |

閱讀某一個後續章時，不必重新背誦主章全部內容；只要確認該章列出的前置條件，再集中理解「相對前章新增什麼」。

## 2. 圖文版

位置：`圖文版/`

圖文版保留純文字主章的完整操作內容，再依教學價值加入課堂截圖、圖說與流程圖：

1. [環境設定](圖文版/01_環境設定.md)
2. [建立第一個Spring Boot專案](圖文版/02_建立第一個SpringBoot專案.md)
3. [Maven打包與執行JAR](圖文版/03_Maven打包與執行JAR.md)
4. [使用Spring Initializr建立專案](圖文版/04_使用SpringInitializr建立專案.md)
5. [Whitelabel 404與套件掃描](圖文版/05_Whitelabel404與套件掃描.md)
6. [介面多實作與Qualifier依賴注入](圖文版/06_介面多實作與Qualifier依賴注入.md)
7. [Component、Configuration與Bean](圖文版/07_Component_Configuration與Bean.md)
8. [HTML表單與JSON資料綁定](圖文版/08_HTML表單與JSON資料綁定.md)
9. [分層式User CRUD API](圖文版/09_分層式User_CRUD_API.md)
10. [Swagger／OpenAPI API文件](圖文版/10_Swagger_OpenAPI_API文件.md)
11. [REST JSON與Thymeleaf模板](圖文版/11_REST_JSON與Thymeleaf模板.md)
12. [REST CRUD進階、參數DTO與統一回應](圖文版/12_REST_CRUD進階_參數DTO與統一回應.md)
13. [Spring Data JPA與MySQL](圖文版/13_Spring_Data_JPA與MySQL.md)
14. [Spring Data JPA、SQLite、Docker與Render部署](圖文版/14_Spring_Data_JPA與SQLite_Docker_Render部署.md)
15. [Spring Data JPA查詢方法、Query與分頁](圖文版/15_Spring_Data_JPA查詢方法_Query與分頁.md)
16. [JPA OneToMany、ManyToOne與JSON關聯](圖文版/16_JPA_OneToMany_ManyToOne與JSON關聯.md)

### 圖文延伸閱讀

- [第11章：Thymeleaf表達式與常見疑難](圖文版/延伸閱讀/11_Thymeleaf表達式與常見疑難.md)
- [第13章：JPA對應既有資料表的Coffee案例](圖文版/延伸閱讀/13_JPA對應既有資料表_Coffee案例.md)
- [第14章：Git與Render部署故障排查](圖文版/延伸閱讀/14_Git與Render部署故障排查.md)
- [第16章：JPA查詢效能、批次更新與關聯風險](圖文版/延伸閱讀/16_JPA查詢效能_批次更新與關聯風險.md)

## 3. 圖文版素材

位置：`圖文版素材_待製作/`

此處保存使用者提供的49張課堂原始截圖，供圖文版引用及日後重製衍生圖：

- 圖片：`圖文版素材_待製作/images/`
- 圖片索引：`圖文版素材_待製作/README.md`

原圖保持不變；若後續需要裁切或標記，使用另存的衍生圖並保留來源對照。

## 課堂原始碼

目前課堂專案與交接資料夾位於同一層：

`C:\sbworkspace202608\sbfirstapp`

`C:\sbworkspace202608\mysecondapp`

`C:\sbworkspace202608\sbrest0810`

`C:\sbworkspace202608\sbrestcrud0807`

`C:\sbworkspace202608\sbrest0722`（早期記憶體User CRUD、Request參數與統一回應對照）

`C:\sbworkspace202608\sbrestjpa0811`

`C:\sbworkspace202608\sbjpacoffee0811`

`C:\sbworkspace202608\sbjpa0812`（SQLite版Spring Data JPA課堂專案）

`C:\sbworkspace202608\sbemployee0812`（JPA衍生查詢、`@Query`與分頁）

`C:\sbworkspace202608\sbonemany0813`（Department／Employee一對多關聯）

部署用Git專案另位於：

`C:\git\sbjpa-SQLite-0812`（含Dockerfile與Render部署版本）

整理筆記時，若截圖內容不完整或需要核對實際設定，可以直接查看這些專案的`src/`、`pom.xml`及其他相關原始檔；除非使用者另行要求，不修改專案程式碼。
