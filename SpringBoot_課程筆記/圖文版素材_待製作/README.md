# Spring Boot 圖文版素材（待製作）

這個資料夾保存課堂原始截圖。純文字筆記位於`../純文字版/`；目前已完成前三章圖文試作，入口位於[`../README.md`](../README.md)。

## 第一章：環境設定

圖片位於`images/`：

1. `01_XML設定.png`
2. `02_JRE調整前.png`
3. `03_JDK21設定完成.png`
4. `04_字體設定.png`
5. `05_Tomcat設定.png`
6. `06_Eclipse商店入口.png`
7. `07_安裝SpringTools.png`
8. `08_安裝進度.png`
9. `09_NewOther入口.png`
10. `10_STS安裝確認.png`

## 第二章：建立第一個 Spring Boot 專案

圖片位於`images/`：

1. `11_選擇StarterProject.png`
2. `12_專案基本資料.png`
3. `13_選擇DevTools.png`
4. `14_選擇SpringWeb.png`
5. `15_專案建立完成.png`
6. `17_RunAs執行選項.png`：比較`Java Application`與`Spring Boot App`兩種執行方式。

## 未納入前三章的後續素材

1. `16_HelloWorld_API執行結果.png`：`HelloWorld` REST Controller 與瀏覽器開啟`http://localhost:8080/api/hello`的結果。

## 第三章：Maven 打包與執行 JAR

1. `18_MavenInstall選單.png`：在 Eclipse 中選擇`Run As → Maven install`。
2. `19_命令列執行JAR與啟動失敗.png`：從`target`執行可執行 JAR，以及啟動失敗畫面。
3. `20_target打包產物.png`：`target`中的可執行 JAR、`.jar.original`及其他 Maven 產物。

## 第四章：使用 Spring Initializr 建立專案

1. `21_SpringInitializr建立mysecondapp.png`：在`start.spring.io`設定 Maven、Java、Spring Boot 4.1.0、`mysecondapp`及 DevTools／Spring Web。

## 第五章：Whitelabel 404 與套件掃描

1. `22_Whitelabel404套件掃描問題.png`：Controller 位於主要套件掃描範圍之外，導致`/api/hello`回傳 404。

## 第六章：介面、多實作與 Qualifier 依賴注入

1. `23_NotificationQualifier依賴注入.png`：Notification Controller 同時注入 Email 與 SMS 兩個`NotificationService`實作。

## 第七章：Component、Configuration 與 Bean

1. `24_UtilController與AppInfoBean.png`：Util Controller 注入`UtilService`與`appInfo`Bean，並顯示`/api/util/info`結果。

## 第八章：HTML 表單與 JSON 資料綁定

1. `25_User表單_JSON與CRUD.png`：瀏覽器表單、Postman JSON POST 與 User 回傳結果。
2. `26_SubmitController_UserService_CRUD.png`：`SubmitController`改用`UserService`保存資料，並以`/api/users/{id}`成功查回表單建立的使用者。

## 第九章：分層式 User CRUD API

1. 共用`25_User表單_JSON與CRUD.png`：畫面左側顯示 User Model、Repository、Service、Controller 分層及 CRUD Controller 程式。
2. 共用`26_SubmitController_UserService_CRUD.png`：驗證 Submit API 與 User CRUD API 共用同一個 Repository。

## 第十章：Swagger／OpenAPI API 文件

1. `27_Swagger_UI_API總覽.png`：Swagger UI 自動列出 User CRUD、Submit、Util 與 Notification Controller 的 API。
2. `28_OpenAPI註解_UserController.png`：UserController 使用`@Tag`與`@Operation`補充 API 分組、摘要與說明。

## 第十一章：REST JSON 與 Thymeleaf 模板

1. `29_Book_REST_API_JSON.png`：瀏覽器呼叫`GET /api/books`，Book Java 物件經 Jackson 轉成 JSON。
2. `30_Thymeleaf_text與utext.png`：比較`th:text`跳脫 HTML、`th:utext`渲染 HTML，以及文字模板中的跳脫結果。
3. `31_Thymeleaf_thtext取代原內容.png`：`th:text`會取代元素原本的全部內容，因此`Greeting :`不是執行時的預設值。
4. `32_Thymeleaf_thsrc靜態與動態圖片.png`：`th:src`載入固定芒果及由 Model 隨機選擇的動態水果圖片，並顯示瀏覽器收到的最終`src`。
5. `33_Thymeleaf_if_switch_case條件判斷.png`：`isLogin="false"`顯示「請先登入」；`role=orange`未符合 admin／user，因此進入`th:case="*"`顯示「未知角色」。
6. `34_Thymeleaf內建物件舊語法參考.png`：舊式`#request`／`#session`與集合、日期、字串、數字工具物件的教材參考。
7. `35_HttpSession加入Model寫法.png`：老師以 HttpSession 保存 user，再把 Session 物件加入 Model 的示範寫法。
8. `36_Session不加入Model仍可讀取.png`：兩個`model.addAttribute(...)`均註解後，`${session['user']}`仍成功顯示 John Lee，證明 Thymeleaf 自動提供 Session namespace。
9. `37_th_href相對網址與查詢參數.png`：`th:href`產生首頁連結及帶`userId`查詢參數的使用者詳情連結；目前兩者都是沒有開頭斜線的相對網址。
10. `38_Thymeleaf使用者列表與動態操作連結.png`：`/web/users`以`th:each`顯示三筆記憶體使用者，並為每列產生詳情、編輯及 POST 刪除操作。
11. `39_SpringMVC_ViewName與Redirect回傳差異.png`：比較`return "user/form"`直接渲染 View，以及新增／更新後以`redirect:`執行 Post／Redirect／Get。
12. `40_Eclipse_CSS色碼預覽色塊.png`：Eclipse Wild Web Developer／LSP4E 對 CSS 十六進位色碼顯示的 IDE 顏色預覽，不是原始碼內容。
13. `41_Thymeleaf新增使用者後列表結果.png`：新增 Harry Potter 後列表由三筆變成四筆，驗證表單、Model binding、Service、記憶體 Repository 與 Redirect 流程。
14. `42_Thymeleaf三元運算動態form_action.png`：第 20 行以`${isEdit} ? A : B`在編輯與建立模式間選擇不同的`th:action`網址。
15. `43_SQLite設定進行中_application_properties.png`：`sbjpa0812`把DataSource改為SQLite時的進度畫面；此圖拍攝當下仍有錯，只是歷史進度，不單獨代表設定成功。

## 第十四章：Spring Data JPA、SQLite與Docker／Render部署

1. `44_Render部署初始_no-server與本機正常.png`：相同`GET /api/products`在本機成功，但Render端只顯示`Not Found`；HTTP回應後續確認為Render路由層`no-server`，請求尚未進入Spring Boot。
2. `45_Git_push非fast-forward遭拒.png`：本機port修正提交因GitHub已有README提交而被`fetch first`拒絕；需先整合遠端歷史，不能直接Force Push。
3. `46_Render與本機_API成功一致.png`：合併並Push後，Render與本機都回傳Apple、Apple iPhone 17、Banana三筆商品JSON，作為SQLite與部署成功證據。

## 第十六章：JPA OneToMany、ManyToOne與JSON關聯

1. `47_OneToMany部門員工JSON與LEFT_JOIN.png`：`GET /api/departments`回傳部門與巢狀員工JSON；Console同時顯示Hibernate以`LEFT JOIN`查詢`departments`與`employees`。畫面中的`MeMe`是以PUT把初始`MIS`更新後的名稱。
2. `48_Category_Product_JSON與N加1查詢.png`：`GET /api/categories`回傳3C、Fruit及各自的Product；Controller目前呼叫`findAll()`，Console可見針對兩個Category分別執行`where category_id=?`的Product查詢，用來辨認N+1。
3. `49_Modifying依類別批次清空庫存.png`：`GET /api/products/fruit`回傳affected rows為2；Console顯示Hibernate依Category名稱產生Join Update，Workbench顯示當下四筆商品庫存皆為0。此圖只能證明fruit呼叫更新兩筆，不能證明另外兩筆也是同一次Request所改。

## 後續整理規則

- 新的純文字內容持續更新至`../純文字版/`。
- 使用者提供的新截圖依章節順序重新命名，原圖完整保存至`images/`；不得直接覆寫、裁切或標記這些原圖。
- 圖片是否納入圖文版以教學價值為準，不要求每張原圖都使用；重複、無關或無法補充說明的截圖可以不放入文章。
- 需要裁切、加框、箭頭、編號或文字標示時，另存為衍生圖，並記錄來源原圖與處理目的，確保日後可追溯及重做。
- 裁切或標記不得遮蔽操作路徑、關鍵程式碼、網址、錯誤訊息或執行結果；每張採用圖片都要有能說明其用途的圖說與替代文字。
- 每次加入新截圖，都要同步更新本索引，確保檔名、章節與畫面用途對得上。
- 第一章版面與GitHub顯示已確認，可依相同原則接續製作其他章節。
