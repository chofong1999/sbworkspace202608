# Spring Boot 學習筆記 13：Spring Data JPA與MySQL

- 整理日期：2026-08-12
- 主要範例：`sbrestjpa0811`（Employee完整CRUD分層）
- 對照範例：`sbjpacoffee0811`（既有Coffee表格的唯讀查詢）

## 0. 前置條件、主線與完成判定

前置條件：

- 已安裝並啟動MySQL，知道可用的host、port、帳號與密碼。
- 建立含Spring Web、Spring Data JPA、MySQL Connector與Lombok的專案。
- 先理解第9、12章的Controller／Service／Repository分層。

本章主線是Employee CRUD：建立`employee_db`，依第3～10節建立Entity、Repository、Service、Controller與連線設定，再依第7節測試全部CRUD。

若要練習「Entity對應已存在的資料表」與`ddl-auto=validate`，另讀[Coffee既有表格案例](延伸閱讀/13_JPA對應既有資料表_Coffee案例.md)。它不是完成Employee CRUD的必要步驟。

既有編譯產物不等於資料庫連線成功。必須看到ApplicationContext正常啟動、Hibernate沒有連線／Schema錯誤，且HTTP端點取得預期資料，才能判定主線完成。

## 1. 從記憶體Repository進入資料庫Repository

前面的User與Product範例把資料放在`List`或`Map`：

```text
Controller → Service → 自製Repository → JVM記憶體
```

JPA範例改成：

```text
HTTP Request
    ↓
EmployeeController
    ↓
EmployeeService
    ↓
EmployeeRepository（Spring Data產生實作）
    ↓
JPA／Hibernate
    ↓
JDBC Driver
    ↓
MySQL employee_db
```

主要差異：

| 比較 | 記憶體Store | Spring Data JPA |
|---|---|---|
| 儲存位置 | JVM記憶體 | 資料庫表格 |
| Repository | 自己寫CRUD方法 | 介面繼承`JpaRepository` |
| Model | 一般Java物件 | `@Entity`實體 |
| 重啟後資料 | 通常消失 | 依資料庫與DDL設定決定 |
| 查詢執行者 | Java集合 | JPA provider產生SQL |

## 2. Maven依賴的角色

兩個JPA專案都包含：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

| 依賴 | 責任 |
|---|---|
| `spring-boot-starter-data-jpa` | Spring Data Repository、JPA整合、Hibernate等 |
| `mysql-connector-j` | 讓JDBC能和MySQL通訊 |
| `spring-boot-starter-webmvc` | REST Controller與Spring MVC |
| `lombok` | 編譯期產生Getter／Setter等程式碼 |

JPA是持久化規格與API；Hibernate是本專案實際使用的JPA provider；MySQL Connector是資料庫Driver。三者不是同一個元件。

<a id="employee-entity-example"></a>
## 3. Employee Entity實作

`Employee`：

```java
@Entity
@Table(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String department;
    private Double salary;

    public Employee() {}
}
```

本章只需掌握Employee實際使用的設定：

| 本例語法 | 在Employee中的作用 |
|---|---|
| `@Entity` | 讓JPA管理這個類別；類別需有主鍵及`public`／`protected`無參數建構子 |
| `@Table(name = "employees")` | 明確對應`employees`資料表 |
| `@Id` | 把`id`設為Entity識別值；Repository的ID型別因此是`Long` |
| `@GeneratedValue(strategy = IDENTITY)` | 由MySQL自動遞增機制產生ID；新增Request通常不要自行指定ID |
| `@Column(nullable = false)` | 產生／驗證Schema時要求欄位不可為NULL |
| `@Column(unique = true)` | 對email建立單欄唯一限制 |

要查`@Table`、主鍵策略、`@Column`完整參數、預設值與其他Model註解，使用[Model／Entity語法入口](../語法字典/Spring_Boot/Model與Entity.md)。

`@Column`屬於映射與Schema限制，不等於API輸入驗證。例如Request中的email為空時，可能直到寫入資料庫才失敗；若要提早回傳可讀的400錯誤，還要加入Bean Validation與例外處理。完整差異見[`@Column`參數與資料庫限制](../語法字典/延伸閱讀/JPA_Column參數與資料庫限制.md)。

## 4. JpaRepository的定義與成立條件

```java
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {
}
```

泛型參數：

```text
JpaRepository<管理的Entity型別, Entity主鍵型別>
```

成立條件：

1. 專案有Spring Data JPA依賴。
2. `Employee`是可被JPA管理的Entity。
3. 主鍵型別和Repository的第二個泛型一致。
4. Repository位於Spring Boot可掃描的package範圍內，或有額外設定掃描範圍。
5. DataSource與JPA provider能正確初始化。

Spring Data會在執行時替這個介面建立代理實作，因此不用自己寫實作類別。

### 4.1 繼承後可直接使用的方法

| 方法 | 回傳／作用 |
|---|---|
| `save(entity)` | 儲存新Entity或合併既有Entity |
| `findById(id)` | `Optional<Employee>` |
| `findAll()` | 全部Entity清單 |
| `existsById(id)` | 是否存在 |
| `deleteById(id)` | 依ID刪除 |
| `count()` | 總筆數 |

`@Repository`寫在Spring Data Repository介面上通常可以省略，因為Repository掃描會建立並註冊代理；保留它可表達分層意圖，但不是這個介面能取得CRUD實作的原因。

## 5. `save()`不是固定等於INSERT

Spring Data JPA的`save()`會先判斷Entity是否為新資料：

```text
新Entity → EntityManager.persist(...)
既有Entity → EntityManager.merge(...)
```

在這個Employee範例中可簡化理解為：

- 新增時`id == null`，通常走新增。
- 從Repository查出的Employee已經有ID，修改後再`save()`，通常走更新／合併。

但「只要ID有值就一定UPDATE」只是課堂簡化，不是完整JPA規格；Spring Data還會使用version property、ID與`Persistable`等策略判斷Entity狀態。

## 6. Employee Service的完整CRUD流程

```java
@Service
public class EmployeeService implements CommandLineRunner {
    @Autowired
    EmployeeRepository employeeRepository;
}
```

### 6.1 查詢

```java
public List<Employee> findAll() {
    return employeeRepository.findAll();
}

public Optional<Employee> findById(Long id) {
    return employeeRepository.findById(id);
}
```

單筆使用`Optional<Employee>`，讓Controller明確處理「有資料／無資料」兩種結果。

### 6.2 新增

```java
public Employee create(Employee employee) {
    return employeeRepository.save(employee);
}
```

Request Body傳入的Employee通常不應帶ID；成功儲存後回傳物件才會有資料庫產生的ID。

### 6.3 更新

```java
public Optional<Employee> update(Long id, Employee updatedEmployee) {
    return employeeRepository.findById(id).map(existing -> {
        existing.setName(updatedEmployee.getName());
        existing.setEmail(updatedEmployee.getEmail());
        existing.setDepartment(updatedEmployee.getDepartment());
        existing.setSalary(updatedEmployee.getSalary());
        return employeeRepository.save(existing);
    });
}
```

流程：

1. 先用URL中的ID查詢。
2. 找不到就回empty Optional。
3. 找到後更新「查出的既有Entity」。
4. 再交給`save()`儲存。

這樣不會直接相信Request Body裡可能出現的ID。

### 6.4 刪除

```java
if (employeeRepository.existsById(id)) {
    employeeRepository.deleteById(id);
    return true;
}
return false;
```

回傳boolean是Service自行設計的契約：Controller可用它決定回`204`或`404`。

## 7. Employee Controller與HTTP語意

| 功能 | Method | 路徑 | 成功回應 | 失敗回應 |
|---|---|---|---|---|
| 查全部 | `GET` | `/api/employees` | `200`＋JSON陣列 | — |
| 查單筆 | `GET` | `/api/employees/{id}` | `200`＋Employee | `404` |
| 新增 | `POST` | `/api/employees` | `201`＋Employee＋Location | 未自訂 |
| 更新 | `PUT` | `/api/employees/{id}` | `200`＋更新後Employee | `404` |
| 刪除 | `DELETE` | `/api/employees/{id}` | `204` | `404` |

### 7.1 `ResponseEntity.created(location)`

```java
URI location = URI.create("/api/employees/" + saved.getId());
return ResponseEntity.created(location).body(saved);
```

結果包含：

- Status：`201 Created`
- Header：`Location: /api/employees/{新ID}`
- Body：儲存後的Employee

`Location`告訴Client新資源的網址；它不會讓瀏覽器自動跳轉。

## 8. CommandLineRunner初始化資料

```java
public class EmployeeService implements CommandLineRunner {
    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            employeeRepository.save(new Employee(...));
            // 共三筆
        }
    }
}
```

`CommandLineRunner.run()`會在Spring Boot ApplicationContext啟動完成後執行。

這個範例先判斷`count()==0`，避免資料表已有資料時重複新增種子資料。

但目前`ddl-auto=create-drop`會在ApplicationContext建立時重建Schema，正常關閉時再移除，因此每次完整啟動通常又會面對空表，接著重新加入三筆。

## 9. MySQL連線設定

先在MySQL建立空資料庫；`create-drop`會在這個資料庫內建立與移除Entity對應的表格，但不會替Application建立`employee_db`本身：

```sql
CREATE DATABASE employee_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

若資料庫已存在，不要重複建立。接著把帳號與密碼改成實際可連線的MySQL帳密。

`sbrestjpa0811/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db?useSSL=false&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=1234
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 9.1 DataSource四個核心值

| 設定 | 定義 |
|---|---|
| `url` | JDBC連線位置、資料庫與參數 |
| `username` | 資料庫帳號 |
| `password` | 資料庫密碼 |
| `driver-class-name` | JDBC Driver類別 |

使用條件：MySQL服務必須已啟動、`employee_db`可用、帳密正確，而且MySQL Connector已在runtime classpath。

### 9.2 `ddl-auto`

| 值 | 啟動時的主要行為 | 適合情境／風險 |
|---|---|---|
| `none` | 不由Hibernate處理Schema | Schema由外部管理 |
| `validate` | 只檢查Entity與既有Schema是否相容 | 既有正式表格；不代替migration |
| `update` | 嘗試依Entity調整Schema | 開發方便，但不適合作為正式migration策略 |
| `create` | 啟動時重建Schema | 原資料可能被刪除 |
| `create-drop` | 啟動時建立、正常關閉時刪除 | 測試／練習；不可當成保留資料的設定 |

此專案實際是`create-drop`，因此不能把它描述成「資料永遠保留」。

### 9.3 顯示SQL

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

- `show-sql`：把Hibernate執行的SQL輸出到Console。
- `format_sql`：改善SQL排版，方便閱讀。

它們是觀察與除錯設定，不會改變CRUD本身的業務邏輯。

## 10. 欄位注入與建構子注入

目前程式實際使用：

```java
@Autowired
EmployeeRepository employeeRepository;
```

註解中的建構子注入版本被註解掉。兩者都能注入，但建構子注入通常較容易：

- 宣告依賴為`final`。
- 在建立物件時保證必要依賴存在。
- 單元測試時直接傳入替身Repository。
- 避免依賴被藏在可變欄位中。

閱讀範例時應區分「註解中的建構子注入建議」和「實際執行的欄位注入」；被註解的程式不會參與執行。

## 11. 延伸案例：對應既有Coffee表格

Employee主線使用JPA建立及操作自己的資料表；若資料表已經存在，Entity映射、主鍵型別與Schema驗證會有不同限制。完整案例見[第13章延伸閱讀：JPA對應既有資料表](延伸閱讀/13_JPA對應既有資料表_Coffee案例.md)。

## 12. Employee專案啟動前檢查

- [ ] MySQL服務已啟動
- [ ] `employee_db`可連線
- [ ] 帳密正確
- [ ] 接受`create-drop`在啟動／關閉時重建與刪除Schema的結果
- [ ] 觀察Console是否出現建表與INSERT SQL

## 13. 常見錯誤定位順序

1. **Driver找不到：**檢查`mysql-connector-j`與Maven依賴下載。
2. **Connection refused：**確認MySQL服務與port。
3. **Unknown database：**確認資料庫名稱。
4. **Access denied：**確認帳號、密碼與權限。
5. **Duplicate entry：**核對`unique=true`欄位，例如Employee email。
6. **404：**確認Controller package掃描與Request path；這和資料庫連線錯誤不是同一層。

`Schema-validation failed`與既有資料表映射的排查方式放在Coffee延伸案例。

## 14. 下一章銜接：由MySQL改成SQLite

本章使用需要獨立Server的MySQL；第14章改用SQLite檔案資料庫，並加入Docker與Render部署。切換資料庫時不能只改JDBC URL，還要同步調整Driver、Dialect、初始化策略與資料保存位置。

## 15. 官方資料入口

- Spring Data JPA Repository核心概念：<https://docs.spring.io/spring-data/jpa/reference/repositories/core-concepts.html>
- Spring Data JPA儲存Entity：<https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html>
- Jakarta Persistence `@Entity`：<https://jakarta.ee/specifications/persistence/4.0/apidocs/jakarta.persistence/jakarta/persistence/entity>
- Jakarta Persistence `@GeneratedValue`：<https://jakarta.ee/specifications/persistence/4.0/apidocs/jakarta.persistence/jakarta/persistence/generatedvalue>

## 16. 本章檢查表

- [ ] 能區分JPA、Hibernate、JDBC Driver與MySQL
- [ ] 能說出Entity成立條件與每個Mapping註解的角色
- [ ] 能正確填寫`JpaRepository<Entity, ID>`兩個型別
- [ ] 知道`save()`可能使用persist或merge，不是固定等於INSERT
- [ ] 能說明Employee新增、更新與刪除的完整分層資料流
- [ ] 能說明`create-drop`的資料風險
- [ ] 能說出切換至SQLite時必須同步調整哪些DataSource與Hibernate設定
