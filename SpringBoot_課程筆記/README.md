# Spring Boot 課程筆記

## 閱讀與重現方式

本筆記以學習者為讀者，章節依編號延續。實作章會列出前置條件、檔案位置、操作順序、測試方式與成功判定；若章節沿用前一章的專案，必須先完成所列前置章節。

筆記中的「範例實際狀態」用來區分已存在的課堂程式與建議改良，不是整理工作的回報。要判定操作成功，必須以該章列出的HTTP結果、Console訊息、檔案產物或畫面條件驗證，不能只以「程式碼已貼上」判定。

本機保存的課堂專案可作為完整原始碼對照；讀者若沒有這些專案，應依各章的前置條件從Spring Initializr建立專案，再依章節順序實作。

目前筆記分成兩個資料夾：

## 1. 純文字版

位置：`純文字版/`

這是目前持續維護的正式筆記：

1. `01_環境設定.md`
2. `02_建立第一個SpringBoot專案.md`
3. `03_Maven打包與執行JAR.md`
4. `04_使用SpringInitializr建立專案.md`
5. `05_Whitelabel404與套件掃描.md`
6. `06_介面多實作與Qualifier依賴注入.md`
7. `07_Component_Configuration與Bean.md`
8. `08_HTML表單與JSON資料綁定.md`
9. `09_分層式User_CRUD_API.md`
10. `10_Swagger_OpenAPI_API文件.md`
11. `11_REST_JSON與Thymeleaf模板.md`
12. `12_REST_CRUD進階_參數DTO與統一回應.md`
13. `13_Spring_Data_JPA與MySQL.md`
14. `14_Spring_Data_JPA與SQLite_Docker_Render部署.md`

新內容會依學習順序新增章節；與既有主題重疊時則修訂原章，避免同一知識分散在多份文件。

## 2. 圖文版素材（待製作）

位置：`圖文版素材_待製作/`

目前只保存使用者提供的課堂截圖，不製作圖文版筆記：

- 圖片：`圖文版素材_待製作/images/`
- 圖片索引：`圖文版素材_待製作/README.md`

等使用者日後明確要求開始製作，再利用已保存的素材建立圖文版。

## 課堂原始碼

目前課堂專案與交接資料夾位於同一層：

`C:\sbworkspace202608\sbfirstapp`

`C:\sbworkspace202608\mysecondapp`

`C:\sbworkspace202608\sbrest0810`

`C:\sbworkspace202608\sbrestcrud0807`

`C:\sbworkspace202608\sbrestjpa0811`

`C:\sbworkspace202608\sbjpacoffee0811`

`C:\sbworkspace202608\sbjpa0812`（SQLite版Spring Data JPA課堂專案）

部署用Git專案另位於：

`C:\git\sbjpa-SQLite-0812`（含Dockerfile與Render部署版本）

整理筆記時，若截圖內容不完整或需要核對實際設定，可以直接查看這些專案的`src/`、`pom.xml`及其他相關原始檔；除非使用者另行要求，不修改專案程式碼。
