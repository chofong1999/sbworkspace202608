# Spring Boot 圖文版素材（待製作）

這個資料夾目前只保存課堂截圖，不製作圖文版筆記。純文字筆記放在相鄰的`../純文字版/`。

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

## 後續課程素材（章節待整理）

1. `16_HelloWorld_API執行結果.png`：`HelloWorld` REST Controller 與瀏覽器開啟`http://localhost:8080/api/hello`的結果。
2. `17_RunAs執行選項.png`：比較`Java Application`與`Spring Boot App`兩種執行方式。

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

## 第九章：分層式 User CRUD API

1. 共用`25_User表單_JSON與CRUD.png`：畫面左側顯示 User Model、Repository、Service、Controller 分層及 CRUD Controller 程式。

## 後續整理規則

- 新的純文字內容持續更新至`../純文字版/`。
- 使用者提供的新截圖依章節順序重新命名，保存至`images/`。
- 每次加入新截圖，都要同步更新本索引，確保檔名、章節與畫面用途對得上。
- 在使用者要求開始製作前，不建立圖文版筆記。
