# Spring Boot 課程筆記

## 閱讀與重現方式

本筆記以學習者為讀者，章節依編號延續。實作章會列出前置條件、檔案位置、操作順序、測試方式與成功判定；若章節沿用前一章的專案，必須先完成所列前置章節。

筆記中的「範例實際狀態」用來區分已存在的課堂程式與建議改良，不是整理工作的回報。要判定操作成功，必須以該章列出的HTTP結果、Console訊息、檔案產物或畫面條件驗證，不能只以「程式碼已貼上」判定。

本機保存的課堂專案可作為完整原始碼對照；讀者若沒有這些專案，應依各章的前置條件從Spring Initializr建立專案，再依章節順序實作。

## 資料夾可獨立使用

只要完整保留`SpringBoot_課程筆記/`資料夾，就能單獨閱讀與上傳GitHub：

- 課程文件、語法字典、延伸閱讀與圖片都在本資料夾內。
- 文件間連結與圖片引用使用資料夾內的相對路徑，不依賴交接資料或外部圖片位置。
- `sbfirstapp`、`sbemployee0812`等課堂專案名稱與本機位置是原始碼對照資訊；沒有這些專案仍可依筆記步驟自行建立。
- 複製時要複製整個資料夾，不要只複製單一Markdown檔，否則該頁引用的圖片與相關章節可能缺失。

目前筆記包含語法字典、純文字版、完整圖文版、原始截圖素材與老師教材封存：

## 1. 語法字典

位置：`語法字典/`

寫程式時需要快速確認註解、參數、成立條件或最小寫法，可由下列入口查找：

- [語法字典首頁與分類目錄](語法字典/README.md)
- [依「想完成的事情」查語法](語法字典/00_快速索引.md)
- [JPA `@Column`完整參數表](語法字典/03_JPA實體與欄位映射.md#column)

字典主頁保持精簡；設計原因、效能、資料庫差異與容易混淆的情境放在`語法字典/延伸閱讀/`。

## 2. 純文字版

位置：`純文字版/`

這是目前持續維護的正式筆記：

- [JavaScript、jQuery與React課程功能快速索引](純文字版/00_JavaScript功能快速索引.md)：不知道語法名稱、只知道想完成什麼功能時，先從這裡查找第19～32章。

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
17. [Book API的DTO、Bean Validation與例外處理](純文字版/17_Book_API的DTO_Bean_Validation與例外處理.md)
18. [VS Code首次設定、終端機與Live Server](純文字版/18_VSCode首次設定_終端機與LiveServer.md)
19. [HTML、CSS與JavaScript基礎](純文字版/19_HTML_CSS與JavaScript基礎.md)
20. [JavaScript變數、型別、條件與迴圈](純文字版/20_JavaScript變數型別條件與迴圈.md)
21. [JavaScript陣列、物件與JSON](純文字版/21_JavaScript陣列_物件與JSON.md)
22. [JavaScript BOM：視窗、頁面導向與定時器](純文字版/22_JavaScript_BOM視窗導向與定時器.md)
23. [JavaScript DOM：元素選取、節點與動態修改](純文字版/23_JavaScript_DOM元素選取_節點與動態修改.md)
24. [JavaScript表單事件與送出驗證](純文字版/24_JavaScript表單事件與送出驗證.md)
25. [jQuery：DOM操作、事件與樣式](純文字版/25_jQuery_DOM操作_事件與樣式.md)
26. [jQuery AJAX與Spring Boot前後端分離](純文字版/26_jQuery_AJAX與SpringBoot前後端分離.md)
27. [jQuery AJAX：JSON資料來源與HTTP狀態處理](純文字版/27_jQuery_AJAX_JSON資料來源與HTTP狀態處理.md)
28. [JavaScript現代語法與Fetch API](純文字版/28_JavaScript現代語法與Fetch_API.md)
29. [jQuery員工CRUD與Spring Boot JPA串接](純文字版/29_jQuery員工CRUD與SpringBoot_JPA串接.md)
30. [React、Vite、JSX、元件與Props](純文字版/30_React_Vite_JSX元件與Props.md)
31. [React列表、useState與受控表單](純文字版/31_React列表_useState與受控表單.md)
32. [React TodoList、LoginForm與useEffect API資料擷取](純文字版/32_React_TodoList_LoginForm與useEffect_API資料擷取.md)

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
| Request／Response DTO、Bean Validation與統一例外 | 第17章 | 延續第12章的簡單DTO，加入Create／Update／Response拆分與欄位驗證 |
| VS Code、Java擴充套件、PowerShell與Live Server | 第18章 | 獨立於Eclipse環境設定，整理VS Code首次使用與終端機／HTML預覽排錯 |
| HTML結構、CSS Cascade與JavaScript迴圈 | 第19章 | 使用Live Server與DevTools重現外部CSS、inline style及1到10加總 |
| JavaScript表單送出與前端驗證 | 第24章 | 接續第23章DOM與Event，加入`submit`、欄位值與`preventDefault()` |
| jQuery DOM與Event | 第25章 | 以jQuery簡化Selector、內容、樣式、節點與事件操作 |
| 前後端分離與AJAX | 第26章 | Live Server前端透過GET／POST呼叫Spring Boot REST API |
| AJAX資料來源與狀態處理 | 第27章 | 讀取本機JSON、外部API與自己的商品API，處理JSON格式及HTTP錯誤 |
| JavaScript現代語法與Fetch API | 第28章 | Arrow Function、Object解構、Spread／Rest與`async`／`await`原生Request流程 |
| jQuery員工CRUD與Spring Boot JPA | 第29章 | 整合前面jQuery AJAX、REST API、Entity與JpaRepository，完成可操作的員工CRUD |
| React、Vite、JSX、元件與Props | 第30章 | React環境、入口渲染、JSX規則、元件拆分與父子資料傳遞 |
| React列表、State與受控表單 | 第31章 | `map()`清單、`useState`更新、事件處理、Object State與表單同步 |
| React TodoList、useEffect與Fetch | 第32章 | Array State CRUD、表單送出、Effect依賴與API載入／錯誤狀態 |

閱讀某一個後續章時，不必重新背誦主章全部內容；只要確認該章列出的前置條件，再集中理解「相對前章新增什麼」。

## 3. 圖文版

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

## 4. 圖文版素材

位置：`圖文版素材_待製作/`

此處保存使用者提供的61張課堂原始截圖，供圖文版引用及日後重製衍生圖：

- 圖片：`圖文版素材_待製作/images/`
- 圖片索引：`圖文版素材_待製作/README.md`

原圖保持不變；若後續需要裁切或標記，使用另存的衍生圖並保留來源對照。

## 5. 老師教材

位置：`老師教材/`

- [依週次與主題查找老師講義](老師教材/README.md)
- `老師教材/原檔/`保存教室暫存區 `week1`～`week5` 根目錄的35份Markdown／PDF講義。
- 原檔副本維持原檔名與原內容；分類說明只寫在外層索引。
- `老師教材/原檔_SHA256.txt`可驗證封存副本是否遭到修改。

## 課堂原始碼

以下課堂專案是本機原始碼對照來源，不是閱讀筆記的必要依賴：

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

`C:\sbworkspace202608\sbemployeetest`（jQuery員工CRUD對接的Spring Boot JPA後端）

部署用Git專案另位於：

`C:\git\sbjpa-SQLite-0812`（含Dockerfile與Render部署版本）

老師的JavaScript、jQuery與React課堂原檔位於：

`\\Fs2\306_教室暫存區\職訓202607\week6\day1`～`day4`

`\\Fs2\306_教室暫存區\職訓202607\week7\day5`（jQuery員工CRUD）

`\\Fs2\306_教室暫存區\職訓202607\week7\my-react-app`（React入門實作）

`\\Fs2\306_教室暫存區\職訓202607\week7\react-day1`（React TodoList、LoginForm與useEffect API實作）

整理第19～32章時以老師原檔為主，來源只讀、不修改。

可執行的本機工作副本位於`C:\jscode`，React專案包含`C:\jscode\my-react-app`與`C:\jscode\react-day1`。工作副本含部分個人修改與練習，內容不一定與老師原檔完全相同；筆記若採用個人延伸，必須明確標示。

主題對照：

- `day1`：第19章HTML／CSS／初始JavaScript，以及第20章JavaScript語言基礎。
- `day2`：第21章Array／Object／JSON、第22章BOM，以及第23章DOM。
- `day3`：第23章DOM Attribute、動態Style、節點與事件；第24章表單送出驗證；第25章jQuery DOM操作；第26章jQuery AJAX與Spring Boot前後端分離。
- `day4`：第27章jQuery AJAX讀取本機JSON、外部API及自己的Spring Boot商品API，並處理HTTP狀態；第28章整理Arrow Function、解構、Spread／Rest與Fetch API。
- `week7/day5`與`sbemployeetest`：第29章以jQuery呼叫Spring Boot JPA員工CRUD。
- `week7/my-react-app`：第30章React／Vite／JSX／元件／Props；第31章清單、State、事件與受控表單。
- `week7/react-day1`：第32章TodoList、LoginForm、`useEffect`與Fetch API資料擷取。

整理筆記時，若截圖內容不完整或需要核對實際設定，可以直接查看這些專案的`src/`、`pom.xml`及其他相關原始檔；除非使用者另行要求，不修改專案程式碼。
