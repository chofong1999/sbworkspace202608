# Spring Boot 圖文學習筆記 15：Spring Data JPA查詢方法、`@Query`與分頁

- 範例專案：`sbemployee0812`
- API前綴：`http://localhost:8080/api/employees`

> 語法速查：[Repository查詢與分頁](../語法字典/05_Spring_Data查詢交易與分頁.md)

## 本章快速索引

- [0. 前置條件、實作順序與完成判定](#0-前置條件實作順序與完成判定)
- [1. 本章相對基本CRUD新增的能力](#1-本章相對基本crud新增的能力)
- [2. Employee欄位與種子資料](#2-employee欄位與種子資料)
- [3. Derived Query Method：由方法名稱產生查詢](#3-derived-query-method由方法名稱產生查詢)
- [4. `@Query`：查詢內容由程式明確提供](#4-query查詢內容由程式明確提供)
- [5. 聚合查詢與回傳型別](#5-聚合查詢與回傳型別)
- [6. 分頁與排序](#6-分頁與排序)
- [7. Controller端點與實際行為](#7-controller端點與實際行為)
- [8. MySQL設定](#8-mysql設定)
- [9. 重現測試](#9-重現測試)
- [10. 常見錯誤](#10-常見錯誤)
- [11. 本章檢查表](#11-本章檢查表)

## 0. 前置條件、實作順序與完成判定

前置條件：

- 已完成第13章，理解Entity、`JpaRepository`、MySQL連線與基本CRUD。
- MySQL已啟動，而且已有可使用的`employee_db`資料庫。
- 建立含Spring Web、Spring Data JPA、MySQL Driver、Lombok與DevTools的專案。
- 範例的`pom.xml`將Java版本設為17；若Eclipse使用JDK 21執行，JDK 21可以執行Java 17目標版本的程式。

建議實作順序：

1. 建立`Employee` Entity。
2. 建立`EmployeeRepository`並逐步加入衍生查詢與`@Query`。
3. 建立`EmployeeService`，加入分頁查詢。
4. 建立`EmployeeController`公開HTTP端點。
5. 建立`DataInitConfig`加入五筆種子資料。
6. 設定MySQL連線後啟動，依第9節逐支測試。

完成判定不是「方法沒有紅線」，而是：

- ApplicationContext與DataSource正常啟動。
- 第9節的HTTP測試取得預期資料或狀態碼。
- Console可看到Hibernate產生的查詢SQL。
- 能分辨哪些Repository方法由名稱推導、哪些使用JPQL、哪些使用原生SQL。

## 1. 本章相對基本CRUD新增的能力

第13章只需要`JpaRepository`內建的`findAll()`、`findById()`、`save()`等方法。本章在Repository介面宣告額外方法，讓Spring Data依方法名稱或查詢字串產生實作：

```text
EmployeeController
    ├─ 直接使用 EmployeeRepository：範例查詢端點
    └─ 使用 EmployeeService：分頁端點
            ↓
EmployeeRepository
    ├─ Derived Query Method
    ├─ JPQL @Query
    └─ native SQL @Query
            ↓
MySQL employee_db.employees
```

範例程式混用Controller直連Repository與Controller經Service兩條路徑。兩者都能執行，但正式分層若已採Service，通常應把查詢規則集中在Service，避免Controller同時承擔資料存取責任。

## 2. Employee欄位與種子資料

本章沿用第13章的Employee Entity、主鍵、name、email、department與salary，不重複貼出整個類別。`sbemployee0812`另外加入建立時間：

```java
@CreationTimestamp
@Column(updatable = false)
private LocalDateTime createdAt;
```

`@CreationTimestamp`由Hibernate在第一次保存時填入時間；`updatable=false`表示後續更新不應修改這個欄位。完整類別仍必須保留JPA需要的無參數建構子，詳細成立條件見第13章第3節。

`DataInitConfig`實作`CommandLineRunner`，只在資料表為空時加入五筆資料：

```java
if (employeeRepository.count() == 0) {
    employeeRepository.save(
        new Employee("王小明", "wang@example.com", "Engineering", 80000.0));
    // 其餘四筆略
}
```

要重現相同的五筆初始資料，必須使用空的`employees`表。已有資料時`count()`不為0，初始化程式不會補入種子資料。不要為了練習清空含正式資料的資料庫；應建立專用的練習資料庫。

## 3. Derived Query Method：由方法名稱產生查詢

Repository只宣告方法，不撰寫實作類別：

```java
public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartment(String department);
    List<Employee> findByNameContaining(String keyword);
    List<Employee> findBySalaryGreaterThan(Double minSalary);
    long countByDepartment(String dept);
    List<Employee> findBySalaryBetween(Double min, Double max);
}
```

### 3.1 確切定義

Derived Query Method是Spring Data依「方法名稱中的主詞、條件關鍵字與Entity屬性名稱」解析出的查詢。成立條件包括：

- 方法位於Spring Data Repository介面。
- `By`後面的屬性名稱必須能對應Entity property，例如`Department`對應`department`。
- 參數數量與運算子必須相符，例如`Between`需要上下界兩個值。
- 回傳型別必須能接收查詢結果，例如`List<Employee>`、`Optional<Employee>`或計數用的數值型別。

### 3.2 名稱片段與用途

| 方法名稱片段 | 推導條件 | 使用時機 |
|---|---|---|
| `findByDepartment` | `department = ?` | 欄位完全相等查詢 |
| `findByNameContaining` | `name like %?%` | 文字包含關鍵字 |
| `findBySalaryGreaterThan` | `salary > ?` | 大於某個門檻 |
| `countByDepartment` | 對部門條件計數 | 只需要筆數，不需整批Entity |
| `findBySalaryBetween` | `salary between ? and ?` | 封閉區間查詢 |

方法名稱不是任意英文句子；Spring Data只認得規定的關鍵字與Entity屬性路徑。屬性拼錯通常會在ApplicationContext建立Repository時失敗，而不是等到HTTP呼叫才發現。

## 4. `@Query`：查詢內容由程式明確提供

當方法名稱會過長、需要聚合、排序、JOIN或資料庫特有語法時，可使用`@Query`。

### 4.1 JPQL

```java
@Query("SELECT AVG(e.salary) " +
       "FROM Employee e WHERE e.department = :dept")
Double averageSalaryByDepartment(@Param("dept") String dept);
```

JPQL操作的是Entity與Java property：

- `Employee`是Entity類別名稱。
- `e.salary`與`e.department`是Java屬性。
- `:dept`是命名參數，透過`@Param("dept")`綁定方法參數。

若資料庫表名改變但Entity映射保持不變，JPQL通常不必跟著改表名。

### 4.2 原生SQL

```java
@Query(
    value = "SELECT * FROM employees WHERE department = :dept",
    nativeQuery = true)
List<Employee> findByDepartmentIgnoreCase(
        @Param("dept") String dept);
```

`nativeQuery=true`表示內容直接交給資料庫執行，因此使用的是實際表名、欄位名與資料庫SQL語法。

這個方法雖命名為`IgnoreCase`，SQL本身只有`department = :dept`，沒有`LOWER(...)`或不分大小寫的明確條件。是否忽略大小寫會受到MySQL欄位collation影響，不能只由Java方法名稱保證。

若要讓查詢意圖由JPQL明確表達，可寫成：

```java
@Query("SELECT e FROM Employee e " +
       "WHERE LOWER(e.department) = LOWER(:dept)")
List<Employee> findByDepartmentIgnoreCase(
        @Param("dept") String dept);
```

也可使用Spring Data支援的衍生方法名稱`findByDepartmentIgnoreCase(...)`，但不要同時又用一段語意不同的原生SQL覆蓋它。

### 4.3 JPQL與原生SQL選擇

| 條件 | 優先考慮 |
|---|---|
| 查詢以Entity與關聯為中心 | JPQL |
| 需要資料庫特有函式或既有複雜SQL | 原生SQL |
| 簡單等值、範圍、包含條件 | Derived Query Method |

原生SQL能直接利用資料庫功能，但和特定Schema及資料庫產品綁得較緊；JPQL較貼近Entity模型。

## 5. 聚合查詢與回傳型別

平均薪資查詢回傳`Double`：

```java
Double averageSalaryByDepartment(String department);
```

若部門沒有任何員工，SQL的`AVG`可能得到`null`。範例Controller仍會把這個值放入Map：

```java
Double avg = repo.averageSalaryByDepartment(department);
Map<String, Double> data = new HashMap<>();
data.put(department, avg);
return ResponseEntity.ok(data);
```

因此空部門可能得到`200 OK`與`{"部門":null}`，而不是404。`data.size()>0`在`put()`後必定成立，不能用來判定平均值是否存在。若API規格要求不存在時回404，應檢查`avg == null`。

`countByDepartment`則會得到0而非「沒有回傳值」；同樣不應用Map是否為空判斷查詢是否找到資料。

## 6. 分頁與排序

Service建立`PageRequest`：

```java
public Page<Employee> findPaged(
        int page, int size, String sortBy) {
    return employeeRepository.findAll(
        PageRequest.of(page, size,
            Sort.by(sortBy).ascending())
    );
}
```

使用條件：

- `page`從0開始；`page=0`才是第一頁。
- `size`是每頁最多筆數，必須大於0。
- `sortBy`必須是可解析的Entity property，例如`id`、`name`或`salary`。

Controller目前只回傳當頁內容：

```java
List<Employee> data =
        service.findPaged(page, size, sortBy).getContent();
return data;
```

這會丟掉`totalElements`、`totalPages`、目前頁碼等分頁metadata。若Client需要完整分頁資訊，可直接回傳`Page<Employee>`，或另建分頁DTO。

不要直接把任意外部字串當`sortBy`而不檢查；不存在的屬性會造成查詢錯誤。正式API可建立允許欄位白名單。

## 7. Controller端點與實際行為

| 功能 | Method與路徑 | 成功結果 | 查無資料時 |
|---|---|---|---|
| 依部門 | `GET /department/{department}` | Employee陣列 | `404` |
| 姓名包含 | `GET /name/{name}` | Employee陣列 | `404` |
| 部門人數 | `GET /count/{department}` | `{"部門":數量}` | 目前仍回200與0 |
| 範例中的大小寫查詢 | `GET /ignore/{department}` | Employee陣列 | `404` |
| 平均薪資 | `GET /average/{department}` | `{"部門":平均值}` | 目前可能回200與null |
| 分頁 | `GET /page?page=0&size=5&sortBy=id` | 當頁Employee陣列 | 空陣列 |

Repository另有`findBySalaryGreaterThan`、`findBySalaryBetween`與`findRecentEmployees`，Service也包裝了部分方法；但是Controller沒有替它們建立端點，所以不能只憑Repository方法宣告便從瀏覽器呼叫。

## 8. MySQL設定

本章沿用第13章第9節的MySQL Driver、帳密、Dialect與SQL顯示設定。`sbemployee0812`仍連到`employee_db`，主要差異是把Schema策略改為`update`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db?useSSL=false&characterEncoding=utf8
spring.jpa.hibernate.ddl-auto=update
```

建立專案時先複製第13章完整設定，再覆蓋上面兩項；帳號與密碼仍必須改成學習環境實際值。`ddl-auto=update`會嘗試調整Schema，但不等於安全、可追蹤的正式migration；正式環境應使用明確的Schema遷移工具與版本紀錄。

## 9. 重現測試

啟動專案後依序測試：

```http
GET http://localhost:8080/api/employees/department/Engineering
GET http://localhost:8080/api/employees/name/王
GET http://localhost:8080/api/employees/count/Engineering
GET http://localhost:8080/api/employees/average/Engineering
GET http://localhost:8080/api/employees/page?page=0&size=2&sortBy=salary
```

使用空表與本章種子資料時，關鍵預期如下：

- Engineering有王小明與張大偉兩筆。
- 姓名包含「王」會取得王小明。
- Engineering計數為2。
- Engineering平均薪資為86000.0。
- 第一頁大小2時只回兩筆；排序為薪資遞增。

資料表已有其他資料時，筆數、平均值、ID與排序結果會不同；這不一定是程式錯誤。應先確認資料庫內容和`DataInitConfig`是否真的執行。

## 10. 常見錯誤

1. **Repository建立失敗：**先檢查衍生方法中的屬性拼字是否和Entity一致。
2. **`@Query`命名參數錯誤：**`:dept`與`@Param("dept")`必須一致。
3. **JPQL寫成資料表名：**JPQL使用Entity與property；原生SQL才直接使用表格與欄位。
4. **分頁第一頁沒資料：**確認是否把第一頁誤寫為`page=1`。
5. **排序欄位錯誤：**`sortBy`應是Entity property，不是任意顯示名稱。
6. **以為方法存在就有API：**Repository／Service方法還必須被Controller端點呼叫。

## 11. 本章檢查表

- [ ] 能從方法名稱辨認Derived Query的條件與Entity屬性
- [ ] 能區分JPQL和原生SQL使用的名稱空間
- [ ] 知道`@Param`如何綁定命名參數
- [ ] 知道聚合結果可能為`null`
- [ ] 知道`page`從0開始，且`getContent()`會丟掉分頁metadata
- [ ] 已實際呼叫第9節端點並核對資料庫內容與Console SQL
