# Spring Boot 語法字典

這份字典供「正在寫程式、需要立刻查寫法」時使用；課程主章仍負責依序教學與完整實作。

## 使用方式

1. 不知道語法名稱：先開[快速索引](00_快速索引.md)，依「想完成的事情」查找。
2. 已知道語法名稱：由下方分類進入，再利用頁面目錄或瀏覽器搜尋。
3. 只需完成程式：看條目的「基本寫法、使用條件、常用參數」。
4. 要理解設計原因、效能或資料庫差異：再開條目附的「延伸閱讀」。

## 分類目錄

| 分類 | 適合查什麼 |
|---|---|
| [Spring核心與依賴注入](01_Spring核心與依賴注入.md) | 元件註冊、Bean、建構子注入、多實作選擇 |
| [Spring MVC與REST](02_Spring_MVC與REST.md) | Controller、網址映射、路徑／查詢／Body參數、HTTP回應 |
| [JPA實體與欄位映射](03_JPA實體與欄位映射.md) | Entity、資料表、主鍵、欄位限制、非持久化欄位 |
| [JPA關聯映射](04_JPA關聯映射.md) | `ManyToOne`、`OneToMany`、外鍵、級聯、擁有端 |
| [Spring Data查詢、交易與分頁](05_Spring_Data查詢交易與分頁.md) | Repository、方法名稱查詢、JPQL、批次更新、分頁排序 |
| [Thymeleaf模板](06_Thymeleaf模板語法.md) | `${...}`、`*{...}`、`@{...}`、輸出、迴圈、表單與連結 |
| [驗證、Jackson與Lombok](07_驗證_Jackson_Lombok.md) | 輸入驗證、JSON輸出控制、樣板程式碼產生 |
| [Swagger／OpenAPI](08_Swagger_OpenAPI.md) | API分組、摘要、參數與回應文件 |
| [設定檔與建置](09_設定檔與建置.md) | `application.properties`、Maven、JAR、連線與初始化設定 |

## 字典的範圍

- 內容以本課程使用的Java 21、Spring Boot、Spring MVC、Spring Data JPA、Thymeleaf與springdoc-openapi為主。
- 同名註解可能來自不同套件；複製前要核對`import`。
- 「預設值」會受框架版本影響的項目，條目會明確標示版本或要求以專案依賴為準。
- 範例專案名稱只是對照來源。單獨複製整個`SpringBoot_課程筆記`資料夾，不影響本字典、課程文件或圖片連結。

## 官方參考

- [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/)
- [Jakarta Persistence API](https://jakarta.ee/specifications/persistence/)
- [Thymeleaf 3.1 Tutorial](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
- [springdoc-openapi Documentation](https://springdoc.org/)

