# Spring Boot 課程筆記

## 閱讀與重現方式

本筆記以學習者為讀者，章節依編號延續。實作章會列出前置條件、檔案位置、操作順序、測試方式與成功判定；若章節沿用前一章的專案，必須先完成所列前置章節。

筆記中的「範例實際狀態」用來區分可直接重現的程式與可選改善，不是整理工作的回報。要判定操作成功，必須以該章列出的HTTP結果、Console訊息、檔案產物或畫面條件驗證，不能只以「程式碼已貼上」判定。

若同時保留範例專案，可用來核對完整原始碼；讀者即使沒有這些專案，也能依各章前置條件建立專案並依序實作。

## 資料夾可獨立使用

只要完整保留`SpringBoot_課程筆記/`資料夾，就能單獨閱讀與上傳GitHub：

- 課程文件、語法字典、延伸閱讀與圖片都在本資料夾內。
- 文件間連結與圖片引用使用資料夾內的相對路徑，不依賴交接資料或外部圖片位置。
- `sbfirstapp`、`sbemployee0812`等名稱只是範例原始碼的辨識資訊；沒有這些專案仍可依筆記步驟自行建立。
- 複製時要複製整個資料夾，不要只複製單一Markdown檔，否則該頁引用的圖片與相關章節可能缺失。

目前筆記包含語法字典、練習資料來源字典、純文字版、正式圖文版、原始截圖素材與教材封存附錄：

## 1. 語法字典

位置：`語法字典/`

寫程式時需要快速確認註解、參數、成立條件或最小寫法，可由下列入口查找：

- [語法字典首頁與分類目錄](語法字典/README.md)
- [依「想完成的事情」查語法](語法字典/00_快速索引.md)
- [JPA `@Column`完整參數表](語法字典/03_JPA實體與欄位映射.md#column)

字典主頁保持精簡；設計原因、效能、資料庫差異與容易混淆的情境放在`語法字典/延伸閱讀/`。

## 2. 練習資料來源字典

位置：`練習資料來源字典/`

需要使用者、文章、留言、待辦、商品、購物車或本機JSON練習資料時，可由以下入口查找：

- [依資料內容與練習目標快速選擇](練習資料來源字典/README.md)
- [JSONPlaceholder：使用者、文章與關聯資料](練習資料來源字典/01_JSONPlaceholder.md)
- [Fake Store API：商品、購物車與商店使用者](練習資料來源字典/02_Fake_Store_API.md)
- [課堂本機JSON資料](練習資料來源字典/03_課堂本機JSON資料.md)

這個字典負責資料從哪裡取得、有哪些端點與欄位；實際的Fetch、AJAX與React寫法由語法字典及課程主章負責。

## 3. 純文字版

位置：`純文字版/`

這是目前持續維護的正式筆記：

- [JavaScript、jQuery與React課程功能快速索引](純文字版/00_JavaScript功能快速索引.md)：不知道語法名稱、只知道想完成什麼功能時，先從這裡查找第19～38章。

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
33. [React Timer、Effect Cleanup與URL參數查詢](純文字版/33_React_Timer_cleanup與URL參數查詢.md)
34. [Spring Boot WebSocket即時聊天室](純文字版/34_SpringBoot_WebSocket即時聊天室.md)
35. [HTTP Session與React跨來源Cookie](純文字版/35_HTTP_Session與React跨來源Cookie.md)
36. [庫存監控模組前後端整合](純文字版/36_庫存監控模組_前後端整合.md)
37. [JWT登入、Token驗證與受保護API](純文字版/37_JWT登入_Token驗證與受保護API.md)
38. [React Router導覽、動態路由與404](純文字版/38_React_Router導覽_動態路由與404.md)

### 延伸閱讀

延伸閱讀用來保存值得保留、但不是首次完成實作所必需的第二套案例、語法邊界、效能分析與故障診斷。主章保持可依序操作；需要深入研究或遇到相應問題時再查閱補充文件。

- [第11章：Thymeleaf表達式與常見疑難](純文字版/延伸閱讀/11_Thymeleaf表達式與常見疑難.md)
- [第13章：JPA對應既有資料表的Coffee案例](純文字版/延伸閱讀/13_JPA對應既有資料表_Coffee案例.md)
- [第14章：Git與Render部署故障排查](純文字版/延伸閱讀/14_Git與Render部署故障排查.md)
- [第16章：JPA查詢效能、批次更新與關聯風險](純文字版/延伸閱讀/16_JPA查詢效能_批次更新與關聯風險.md)
- [第32章：React useEffect與Fetch穩定性](純文字版/延伸閱讀/32_React_useEffect與Fetch穩定性.md)

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
| React Timer、Cleanup與URL參數 | 第33章 | Timer清理、網址路徑拆分、依動態ID查詢單筆文章 |
| WebSocket即時通訊 | 第34章 | Spring WebSocket Handler、瀏覽器連線事件與多人廣播 |
| HTTP Session與跨來源Cookie | 第35章 | 同一Session辨識、Fetch Credentials與CORS憑證設定 |
| 庫存監控完整模組 | 第36章 | JPA統計、交易邊界、統一回應、Axios Service與React儀表板 |
| JWT登入與受保護API | 第37章 | Token簽發、驗證、Bearer Header與前端儲存 |
| React Router | 第38章 | BrowserRouter、Link／NavLink、動態參數、Navigate與404 |

閱讀某一個後續章時，不必重新背誦主章全部內容；只要確認該章列出的前置條件，再集中理解「相對前章新增什麼」。

## 4. 圖文版

位置：`圖文版/`

圖文版以已驗證的純文字主章為基礎，只在圖片能增加操作辨識、結果證據或觀念理解時加入；沒有必要圖片的章節仍保留完整文字與程式碼，不以圖片數量判定完成度。

- [JavaScript、jQuery與React課程功能快速索引](圖文版/00_JavaScript功能快速索引.md)

1. [環境設定](圖文版/01_環境設定.md)
2. [建立第一個專案](圖文版/02_建立第一個SpringBoot專案.md)
3. [Maven 打包與執行 JAR](圖文版/03_Maven打包與執行JAR.md)
4. [使用 Spring Initializr 建立專案](圖文版/04_使用SpringInitializr建立專案.md)
5. [Whitelabel 404 與套件掃描](圖文版/05_Whitelabel404與套件掃描.md)
6. [介面、多實作與 Qualifier 依賴注入](圖文版/06_介面多實作與Qualifier依賴注入.md)
7. [Component、Configuration 與 Bean](圖文版/07_Component_Configuration與Bean.md)
8. [HTML 表單與 JSON 資料綁定](圖文版/08_HTML表單與JSON資料綁定.md)
9. [分層式 User CRUD API](圖文版/09_分層式User_CRUD_API.md)
10. [Swagger／OpenAPI API 文件](圖文版/10_Swagger_OpenAPI_API文件.md)
11. [REST JSON與Thymeleaf模板](圖文版/11_REST_JSON與Thymeleaf模板.md)
12. [REST CRUD進階、參數、DTO與統一回應](圖文版/12_REST_CRUD進階_參數DTO與統一回應.md)
13. [Spring Data JPA與MySQL](圖文版/13_Spring_Data_JPA與MySQL.md)
14. [Spring Data JPA、SQLite與Docker／Render部署](圖文版/14_Spring_Data_JPA與SQLite_Docker_Render部署.md)
15. [Spring Data JPA查詢方法、`@Query`與分頁](圖文版/15_Spring_Data_JPA查詢方法_Query與分頁.md)
16. [JPA `OneToMany`、`ManyToOne`與JSON關聯](圖文版/16_JPA_OneToMany_ManyToOne與JSON關聯.md)
17. [Book API的DTO、Bean Validation與例外處理](圖文版/17_Book_API的DTO_Bean_Validation與例外處理.md)
18. [VS Code首次設定、終端機與Live Server](圖文版/18_VSCode首次設定_終端機與LiveServer.md)
19. [HTML、CSS與JavaScript基礎](圖文版/19_HTML_CSS與JavaScript基礎.md)
20. [JavaScript變數、型別、條件與迴圈](圖文版/20_JavaScript變數型別條件與迴圈.md)
21. [JavaScript陣列、物件與JSON](圖文版/21_JavaScript陣列_物件與JSON.md)
22. [JavaScript BOM：視窗、頁面導向與定時器](圖文版/22_JavaScript_BOM視窗導向與定時器.md)
23. [JavaScript DOM：元素選取、節點與動態修改](圖文版/23_JavaScript_DOM元素選取_節點與動態修改.md)
24. [JavaScript表單事件與送出驗證](圖文版/24_JavaScript表單事件與送出驗證.md)
25. [jQuery：DOM操作、事件與樣式](圖文版/25_jQuery_DOM操作_事件與樣式.md)
26. [jQuery AJAX與Spring Boot前後端分離](圖文版/26_jQuery_AJAX與SpringBoot前後端分離.md)
27. [jQuery AJAX：JSON資料來源與HTTP狀態處理](圖文版/27_jQuery_AJAX_JSON資料來源與HTTP狀態處理.md)
28. [JavaScript現代語法與Fetch API](圖文版/28_JavaScript現代語法與Fetch_API.md)
29. [jQuery員工CRUD與Spring Boot JPA串接](圖文版/29_jQuery員工CRUD與SpringBoot_JPA串接.md)
30. [React與Vite：JSX、元件與Props](圖文版/30_React_Vite_JSX元件與Props.md)
31. [React列表、useState與受控表單](圖文版/31_React列表_useState與受控表單.md)
32. [React TodoList、LoginForm與useEffect API資料擷取](圖文版/32_React_TodoList_LoginForm與useEffect_API資料擷取.md)

### 圖文延伸閱讀

- [第11章延伸閱讀：Thymeleaf表達式與常見疑難](圖文版/延伸閱讀/11_Thymeleaf表達式與常見疑難.md)
- [第13章延伸閱讀：JPA對應既有資料表的Coffee案例](圖文版/延伸閱讀/13_JPA對應既有資料表_Coffee案例.md)
- [第14章延伸閱讀：Git與Render部署故障排查](圖文版/延伸閱讀/14_Git與Render部署故障排查.md)
- [第16章延伸閱讀：JPA查詢效能、批次更新與關聯風險](圖文版/延伸閱讀/16_JPA查詢效能_批次更新與關聯風險.md)
- [第32章延伸閱讀：React useEffect與Fetch穩定性](圖文版/延伸閱讀/32_React_useEffect與Fetch穩定性.md)

## 5. 圖文版素材

位置：`圖文版素材_待製作/`

此處保存61張原始截圖，供圖文版引用及日後重製衍生圖：

- 圖片：`圖文版素材_待製作/images/`
- 圖片索引：`圖文版素材_待製作/README.md`

原圖保持不變；若後續需要裁切或標記，使用另存的衍生圖並保留來源對照。

## 6. 附錄：教材封存

位置：`老師教材/`

- [依週次與主題查找教材](老師教材/README.md)
- `老師教材/原檔/`保存35份Markdown／PDF參考講義。
- 原檔副本維持原檔名與原內容；分類說明只寫在外層索引。
- `老師教材/原檔_SHA256.txt`可驗證封存副本是否遭到修改。
- 此附錄不是完成課程實作的必要條件，主章不得依賴教室網路、暫存區或特定電腦路徑。

## 範例專案名稱

部分章節會提到下列範例專案名稱，方便辨認案例來源；讀者不需要特定電腦、網路磁碟或固定的絕對路徑，也能依各章步驟自行建立相同成果：

- `sbfirstapp`：第一個Spring Boot與依賴注入範例。
- `mysecondapp`：Spring Initializr與Thymeleaf範例。
- `sbrest0810`、`sbrestcrud0807`、`sbrest0722`：REST、記憶體CRUD與參數處理範例。
- `sbrestjpa0811`、`sbjpacoffee0811`、`sbjpa0812`：Spring Data JPA、MySQL與SQLite範例。
- `sbemployee0812`、`sbonemany0813`：查詢方法與Entity關聯範例。
- `sbemployeetest`：jQuery員工CRUD對接Spring Boot JPA後端。
- `sbjpa-SQLite-0812`：Docker與Render部署範例。
- `my-react-app`、`react-day1`：React入門、State、表單與API資料擷取範例。

這些名稱只是參考，不是筆記的外部依賴。所有必要操作、程式碼與驗證條件應直接寫在相應章節內。
