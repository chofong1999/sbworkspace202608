# 老師教材索引

這裡保存課堂提供的Markdown講義，以及明確屬於講義的PDF。用途有兩個：

1. 保留老師提供的原始版本，方便核對課程內容。
2. 透過本索引依週次與主題快速找到適合的講義。

## 原檔保存規則

- `原檔/`內的35份講義是直接複製的封存副本，不改檔名、不改內容，也不在檔案內補註解。
- `原檔/`中的封存副本視為唯讀資料，不直接修改。
- 分類、說明與連結只寫在這份索引，不寫入原始教材。
- [`原檔_SHA256.txt`](原檔_SHA256.txt)記錄每份副本的SHA-256與位元組數，可用來檢查檔案是否被改動。
- 本次只保存每週根目錄中的Markdown講義與一份講義PDF；SQL、JAR、Dockerfile、資料庫模型、設定片段及Eclipse範例專案都沒有收進來。

## 目前進度應先看什麼

目前的 `sbbookapi0814` 是Book管理API，主線教材是：

1. [Day 3 Part 1：Book CRUD與分層架構](原檔/week5/springboot-jpabeginner-day3-part1.md)
2. [Day 3 Part 2：DTO、輸入驗證與例外處理](原檔/week5/springboot-jpabeginner-day3-part2.md)
3. [Day 3 Part 3：交易管理與測試](原檔/week5/springboot-jpabeginner-day3-part3.md)

## 依主題查找

| 主題 | 建議教材 |
|---|---|
| Eclipse、Maven、Tomcat | [Eclipse Maven專案安裝](原檔/week1/EclipseMaven專案安裝.md)、[Tomcat 10安裝](原檔/week1/JavaEE_Tomcat10_Installation_Guide.md) |
| Servlet、JSP、EL、JavaBean、MVC | [Servlet](原檔/week1/WebServlet教學文件.md)、[JSP](原檔/week1/JSP教學文件.md)、[EL](原檔/week1/JavaEE7_Expression_Language_教學文件.md)、[JavaBean](原檔/week1/JSP_JavaBean_初學者指南.md)、[MVC](原檔/week1/JSP_Servlet_MVC_初學者完全指南.md) |
| Session、ServletContext、Filter、Listener | [Session與ServletContext](原檔/week2/Maven_Tomcat10_Session_ServletContext.md)、[Filter](原檔/week2/Java_Filter_學習文件.md)、[Listener](原檔/week2/Java_Listener_學習文件.md) |
| JAX-RS與REST CRUD | [JAX-RS入門](原檔/week2/Day1_JAX-RS入門與環境設置.md)、[CRUD](原檔/week3/Day2_CRUD.md)、[統一API回應](原檔/week3/Day2_ApiResponse.md)、[Jackson](原檔/week3/Day2_Jackson.md) |
| 傳統JPA、EclipseLink | [JPA基礎](原檔/week3/day1-jpa-basics.md)、[EclipseLink JPA](原檔/week3/eclipselink_jpa.md)、[JPA REST基礎](原檔/week3/JpaRestBasic.md) |
| Spring Boot、Maven、IoC、REST | [Spring Boot Day 1](原檔/week4/springboot-day01-maven-ioc.md)、[Day 1練習](原檔/week4/springboot-day01-practice.md)、[MVC REST練習](原檔/week4/springboot-day02-mvc-rest-practice.md) |
| Swagger／OpenAPI | [Spring Boot Swagger](原檔/week4/springboot-swagger-學習文件.md)、[Swagger學習文件](原檔/week4/Swagger學習文件.md) |
| Thymeleaf Web MVC | [Spring Boot＋Thymeleaf](原檔/week5/SpringBoot_Thymeleaf_學習文件.md) |
| Spring Data JPA CRUD | [Day 1講義](原檔/week5/springboot-jpabeginner-day1.md)、[Day 1練習](原檔/week5/springboot-jpabeginner-practice-day1.md) |
| Derived Query、`@Query`、分頁與關聯 | [Day 2講義](原檔/week5/springboot-jpabeginner-day2.md)、[Day 2練習](原檔/week5/springboot-jpabeginner-practice-day2.md) |
| Book CRUD、DTO、Validation、交易與測試 | [Day 3 Part 1](原檔/week5/springboot-jpabeginner-day3-part1.md)、[Part 2](原檔/week5/springboot-jpabeginner-day3-part2.md)、[Part 3](原檔/week5/springboot-jpabeginner-day3-part3.md) |

## Week 1：Java Web基礎

### 講義

- [EclipseMaven專案安裝.md](原檔/week1/EclipseMaven專案安裝.md)
- [JavaEE_Tomcat10_Installation_Guide.md](原檔/week1/JavaEE_Tomcat10_Installation_Guide.md)
- [WebServlet教學文件.md](原檔/week1/WebServlet教學文件.md)
- [JSP教學文件.md](原檔/week1/JSP教學文件.md)
- [JavaEE7_Expression_Language_教學文件.md](原檔/week1/JavaEE7_Expression_Language_教學文件.md)
- [JSP_JavaBean_初學者指南.md](原檔/week1/JSP_JavaBean_初學者指南.md)
- [JSP_Servlet_MVC_初學者完全指南.md](原檔/week1/JSP_Servlet_MVC_初學者完全指南.md)

## Week 2：Servlet進階與JAX-RS

### 講義

- [Day1_JAX-RS入門與環境設置.md](原檔/week2/Day1_JAX-RS入門與環境設置.md)
- [GuessJSPBean.md](原檔/week2/GuessJSPBean.md)
- [Java_Filter_學習文件.md](原檔/week2/Java_Filter_學習文件.md)
- [Java_Listener_學習文件.md](原檔/week2/Java_Listener_學習文件.md)
- [Maven_Tomcat10_Session_ServletContext.md](原檔/week2/Maven_Tomcat10_Session_ServletContext.md)
- [Maven_Session_ServletContext.pdf](原檔/week2/Maven_Session_ServletContext.pdf)
- [商品管理系統_學習文件.md](原檔/week2/商品管理系統_學習文件.md)

## Week 3：REST與JPA

### 講義

- [day1-jpa-basics.md](原檔/week3/day1-jpa-basics.md)
- [Day2_ApiResponse.md](原檔/week3/Day2_ApiResponse.md)
- [Day2_CRUD.md](原檔/week3/Day2_CRUD.md)
- [Day2_Jackson.md](原檔/week3/Day2_Jackson.md)
- [eclipselink_jpa.md](原檔/week3/eclipselink_jpa.md)
- [JpaRestBasic.md](原檔/week3/JpaRestBasic.md)

## Week 4：Spring Boot入門與Swagger

### 講義

- [BookStore學習文件.md](原檔/week4/BookStore學習文件.md)
- [jpasuppliercoffee.md](原檔/week4/jpasuppliercoffee.md)
- [springboot-day01-maven-ioc.md](原檔/week4/springboot-day01-maven-ioc.md)
- [springboot-day01-practice.md](原檔/week4/springboot-day01-practice.md)
- [springboot-day02-mvc-rest-practice.md](原檔/week4/springboot-day02-mvc-rest-practice.md)
- [springboot-swagger-學習文件.md](原檔/week4/springboot-swagger-學習文件.md)
- [Swagger學習文件.md](原檔/week4/Swagger學習文件.md)

## Week 5：Spring Data JPA與Thymeleaf

### 講義

- [springboot-jpabeginner-day1.md](原檔/week5/springboot-jpabeginner-day1.md)
- [springboot-jpabeginner-practice-day1.md](原檔/week5/springboot-jpabeginner-practice-day1.md)
- [springboot-jpabeginner-day2.md](原檔/week5/springboot-jpabeginner-day2.md)
- [springboot-jpabeginner-practice-day2.md](原檔/week5/springboot-jpabeginner-practice-day2.md)
- [springboot-jpabeginner-day3-part1.md](原檔/week5/springboot-jpabeginner-day3-part1.md)
- [springboot-jpabeginner-day3-part2.md](原檔/week5/springboot-jpabeginner-day3-part2.md)
- [springboot-jpabeginner-day3-part3.md](原檔/week5/springboot-jpabeginner-day3-part3.md)
- [SpringBoot_Thymeleaf_學習文件.md](原檔/week5/SpringBoot_Thymeleaf_學習文件.md)

## 原教材中已存在的缺少連結

下列連結在取得的原始教材中沒有對應檔案。為了維持原檔不變，本封存不修改連結，也不製作同名假檔：

- Week 2 `Day1_JAX-RS入門與環境設置.md`：`Day2_HTTP方法與資源設計.md`
- Week 3 `Day2_ApiResponse.md`、`Day2_CRUD.md`：上一層的Day 2文件及 `examples/day2` 範例路徑
- Week 3 `eclipselink_jpa.md`：上一層 `project/` 範例路徑
- Week 4 `springboot-day01-maven-ioc.md`：`springboot-day01-optimization-suggestions.md`
- Week 5兩份practice講義：practice day 3、pagination、relationship query等延伸文件

這些是來源教材本身的現況，不代表封存時遺漏了同層檔案。
