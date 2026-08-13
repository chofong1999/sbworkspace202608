# Spring Boot 學習筆記 16：JPA `OneToMany`、`ManyToOne`與JSON關聯

- 整理日期：2026-08-13
- 範例專案：`sbonemany0813`
- API前綴：`http://localhost:8080/api/departments`

## 0. 前置條件、實作順序與完成判定

前置條件：

- 已完成第13章的Spring Data JPA基本CRUD；第15章的自訂查詢觀念有助於理解`JOIN FETCH`。
- MySQL已啟動，並準備專用的空資料庫`one2many_db`。
- 建立含Spring Web、Spring Data JPA、MySQL Driver、Lombok與DevTools的專案。
- 範例`pom.xml`的Java目標版本為17；可用JDK 21執行。

建議實作順序：

1. 建立`Department`／`Employee`與`Category`／`Product`兩組Entity。
2. 先在`Employee.department`建立`@ManyToOne`與外鍵。
3. 再在`Department.employees`建立`@OneToMany(mappedBy="department")`。
4. 設定JSON循環處理。
5. 建立`DepartmentRepository`、`CategoryRepository`與各自的`JOIN FETCH`查詢。
6. 建立Department Service、兩個Controller及種子資料。
7. 啟動後測試兩組關聯；讓Category先使用`findAll()`觀察N+1，再切換至`findAllWithProducts()`比較SQL。
8. 建立`ProductRepository`、`ProductService`與`ProductController`，測試依類別名稱批次把庫存歸零。

完成判定：

- 資料庫出現`departments`與`employees`表，`employees.dept_id`為關聯欄位。
- `GET /api/departments`一次取得部門及其員工清單。
- JSON中的Employee不再反向展開Department，沒有無限巢狀或序列化錯誤。
- Console查詢全部部門時可看到`LEFT JOIN`，而不是每個部門各自再查一次員工。
- `PUT /api/departments/{id}`可修改部門名稱，再查詢能看到更新結果。
- `GET /api/categories`取得兩個類別與各自商品，Product不反向輸出Category。
- Category使用`findAll()`時能從Console辨認N+1；切換至`findAllWithProducts()`後改為含`LEFT JOIN`的查詢。
- 依類別執行庫存歸零時，HTTP回傳的更新筆數、Hibernate UPDATE與資料表結果相符。

## 1. 關聯的資料庫結構

需求是「一個部門有多名員工；一名員工屬於一個部門」：

```text
departments
├─ id (PK)
└─ name

employees
├─ id (PK)
├─ name
├─ email
├─ salary
├─ created_at
└─ dept_id (FK → departments.id)
```

外鍵放在多的一方`employees`。因此JPA關聯中：

- `Employee.department`是擁有關聯的Owning Side。
- `Department.employees`是反向查閱的Inverse Side。

Owning Side不是指「物件比較重要」，而是指哪一端實際負責外鍵映射與關聯更新。

## 2. Employee：`@ManyToOne`擁有外鍵

Employee的主鍵、name、email、salary、createdAt與無參數建構子沿用第13、15章；本章真正新增的映射是把原本的String department改成Department關聯：

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "dept_id")
private Department department;
```

### 2.1 `@ManyToOne`

定義：多個`Employee`可參照同一個`Department`。

使用條件：

- 欄位型別必須是關聯目標Entity，而不是只放一個部門名稱字串。
- 這一端通常對應包含外鍵的資料表。
- `fetch=LAZY`表示讀Employee時先保留關聯代理，需要Department資料時才載入。

### 2.2 `@JoinColumn(name="dept_id")`

定義：指定此關聯使用`employees`表中的`dept_id`外鍵欄位。

若省略，JPA會依命名策略推導Join Column；明確指定可讓程式和實際Schema名稱一致。

目前沒有設定`nullable=false`，所以從映射本身看，員工可以沒有部門。若業務規則要求每名員工一定要有部門，可把Join Column設為不可空並同步建立輸入驗證。

## 3. Department：`@OneToMany`反向集合

```java
@Entity
@Table(name = "departments")
@Data
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(
        mappedBy = "department",
        cascade = CascadeType.PERSIST,
        targetEntity = Employee.class,
        fetch = FetchType.LAZY)
    @JsonIgnoreProperties("department")
    private List<Employee> employees = new ArrayList<>();

    public Department() {}
}
```

### 3.1 `mappedBy="department"`

`mappedBy`的值是另一端Java屬性名稱：

```java
Employee.department
```

它不是資料表名，也不是外鍵欄位`dept_id`。這個設定告訴JPA：「外鍵關係已由Employee的department欄位管理，Department這端不要再建立另一套Join Table或外鍵。」

成立條件：

- `Employee`必須真的有名為`department`的關聯屬性。
- 該屬性型別必須能和Department形成對應關係。
- 拼錯通常會在JPA建立EntityManagerFactory時失敗。

### 3.2 `cascade=PERSIST`

當新Department被`persist`時，新的Employee也一起被持久化：

```java
departmentRepository.save(department);
```

此設定只有`PERSIST`，不代表全部操作都連動：

- 不保證刪除Department時自動刪除Employees。
- 不等於`orphanRemoval=true`。
- 不應假設所有merge、remove操作都會級聯。

需要哪種級聯必須依生命週期規則明確選擇，不要為方便直接使用`CascadeType.ALL`。

### 3.3 `fetch=LAZY`

查詢Department時，`employees`集合預設不立即載入。只有在持久化Context仍可用時存取集合，Hibernate才能補查資料。

LAZY能避免每次都載入不需要的員工，但若在Context關閉後才存取，可能發生`LazyInitializationException`。本章查詢全部時用`JOIN FETCH`明確載入所需集合。

## 4. 建立雙向關聯時必須同步兩端

JPA不會因為只設定一端，就自動把Java記憶體中的另一端補好。種子資料同時設定：

```java
Department d1 = new Department("MIS");

List<Employee> employees = List.of(
    new Employee("Andy Chen", "andy@demo.com", d1, 50000.0),
    new Employee("Jason Lee", "jason@demo.com", d1, 52000.0)
);

d1.setEmployees(employees);
departmentRepository.save(d1);
```

這裡完成兩件事：

1. 每個Employee的`department`指向`d1`，讓Owning Side具有外鍵值。
2. `d1.employees`包含兩個Employee，讓Java物件圖雙向一致。

較穩定的寫法是在Department提供helper method：

```java
public void addEmployee(Employee employee) {
    employees.add(employee);
    employee.setDepartment(this);
}
```

之後只呼叫`department.addEmployee(employee)`，避免漏設其中一端。

課堂初始化使用`List.of(...)`，它建立不可修改List，第一次持久化已成功；若後續還要對集合執行`add()`或`remove()`，應改用`new ArrayList<>(...)`或直接使用Entity中已建立的`ArrayList`。

## 5. 雙向關聯為何會造成JSON遞迴

若兩端都完整序列化：

```text
Department
└─ employees[0]
   └─ department
      └─ employees[0]
         └─ department
            └─ ...
```

這不是JPA外鍵循環，而是Jackson沿著雙向Java物件參照持續輸出。常見結果是JSON過度巢狀、Stack Overflow或序列化例外。

本專案在`Department.employees`使用：

```java
@JsonIgnoreProperties("department")
private List<Employee> employees;
```

效果是：當Employee作為這個集合的內容輸出時，忽略它的`department`屬性。最終結構保持為：

```json
{
  "name": "MIS",
  "employees": [
    {
      "name": "Andy Chen",
      "email": "andy@demo.com",
      "salary": 50000.0
    }
  ],
  "id": 1
}
```

員工資料仍存在，只是JSON不再從Employee反向展開Department。

## 6. 另一組映射：`@JsonManagedReference`與`@JsonBackReference`

同一專案以`Category`／`Product`實作另一組雙向關聯：

```java
// Category.java
@OneToMany(mappedBy = "category", fetch = FetchType.LAZY,
           cascade = CascadeType.PERSIST)
@JsonManagedReference
private List<Product> products = new ArrayList<>();

// Product.java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
@JsonBackReference
private Category category;
```

語意：

- `@JsonManagedReference`端正常輸出子集合。
- `@JsonBackReference`端的反向參照不輸出。

這是Jackson的JSON序列化控制，不會決定資料庫外鍵。JPA關聯仍由`@OneToMany`、`@ManyToOne`、`mappedBy`與`@JoinColumn`決定。

這一組已具備`CategoryRepository`、`CategoryController`與種子資料，公開端點為：

```http
GET /api/categories
```

Controller直接使用Repository，沒有Category Service：

```java
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    CategoryRepository repo;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }
}
```

執行結果會輸出Category的`products`陣列；每個Product的`category`因`@JsonBackReference`而省略，避免反向遞迴。

## 7. `JOIN FETCH`與N+1查詢

Repository定義：

```java
@Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

Category也有相同用途的方法：

```java
@Query("SELECT c FROM Category c LEFT JOIN FETCH c.products")
List<Category> findAllWithProducts();
```

### 7.1 `LEFT JOIN FETCH`

- `LEFT JOIN`：沒有員工的部門也會保留。
- `FETCH`：這次查詢直接把`employees`集合載入持久化Context。
- `d.employees`使用Entity關聯屬性，不是SQL資料表名。

Service在查全部時使用這個方法：

```java
public List<Department> findAll() {
    return departmentRepository.findAllWithEmployees();
}
```

### 7.2 N+1問題的實際比較

如果先查N個Department，再於序列化時逐一載入各自的employees，可能形成：

```text
1次：查全部Department
N次：每個Department各查一次Employee
```

這就是常說的N+1查詢。`JOIN FETCH`可在同一次查詢中把父Entity與需要的子集合一起取得。

Department路線已使用：

```java
departmentRepository.findAllWithEmployees();
```

因此畫面可看到`departments LEFT JOIN employees ON ...`。

Category路線目前刻意使用一般`findAll()`：

```java
// return ResponseEntity.ok(repo.findAllWithProducts());
return ResponseEntity.ok(repo.findAll());
```

查到兩個Category後，Jackson輸出各自的`products`集合；目前執行環境能在序列化期間載入LAZY集合，因此Console可看到兩次類似查詢：

```sql
select ...
from products
where category_id = ?
```

兩個Category各補查一次Product，配合前面的Category主查詢，形成`1 + 2`。Category數量增加至N時，最壞情況會變成`1 + N`。

要比較`JOIN FETCH`，把Controller切換為：

```java
return ResponseEntity.ok(repo.findAllWithProducts());
// return ResponseEntity.ok(repo.findAll());
```

重新啟動並再次呼叫`GET /api/categories`。JSON內容應維持相同，但Console應改為概念上相當於：

```sql
select ...
from categories c
left join products p
    on c.id = p.category_id
```

若關閉Open EntityManager in View，或Entity已離開持久化Context，直接序列化尚未載入的LAZY集合可能改成`LazyInitializationException`；所以「目前findAll也能回JSON」不代表它已處理N+1或在所有設定下都安全。

集合Fetch Join可能讓SQL結果中同一Department出現多列；若使用情境或provider產生重複的根Entity，可改寫為：

```java
@Query("SELECT DISTINCT d " +
       "FROM Department d LEFT JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

是否需要`DISTINCT`應以實際JPQL結果與provider行為驗證。

## 8. Controller與目前公開的API

| 功能 | Method與路徑 | 回應 |
|---|---|---|
| 查全部部門及員工 | `GET /api/departments` | `200`＋Department陣列 |
| 依ID查詢 | `GET /api/departments/{id}` | 找到200；否則404 |
| 依名稱查詢 | `GET /api/departments/name/{name}` | 找到200；否則404 |
| 更新部門名稱 | `PUT /api/departments/{id}` | 找到200；否則404 |
| 查全部類別及商品 | `GET /api/categories` | `200`＋Category陣列 |
| 依類別清空商品庫存 | `GET /api/products/{category}` | `200`＋更新筆數Map |

Service雖然已有`create()`與`delete()`，Controller目前沒有`POST`或`DELETE` Mapping，因此HTTP Client不能直接呼叫新增與刪除部門。

更新程式只修改名稱：

```java
Department department = found.get();
department.setName(updated.getName());
departmentRepository.save(department);
```

Request Body即使帶`employees`，目前Service也不會用它覆蓋關聯集合。

## 9. MySQL設定與種子資料

先建立專用資料庫：

```sql
CREATE DATABASE one2many_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

`application.properties`沿用第15章第8節的MySQL Driver、帳密、`ddl-auto=update`與SQL顯示設定，只把資料庫名稱改為`one2many_db`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/one2many_db?useSSL=false&characterEncoding=utf8
```

不能只建立這一行；必須先有第15章的其餘DataSource與JPA設定，帳密也要改成實際環境值。初始化器只有在`departmentRepository.count()==0`時建立：

- MIS：Andy Chen、Jason Lee
- Finance：Mary Wu、Rose Lin

另一段初始化器只在`categoryRepository.count()==0`時建立：

- 3C：iPhone 17、Samsung Phone
- Fruit：Apple、Banana

兩組`count()`分開判斷，所以其中一組表格已有資料，不會阻止另一組空表建立種子資料。

ID由MySQL產生，`createdAt`是執行當下時間，查詢沒有`ORDER BY`，所以不可把固定ID、時間或陣列順序當成通用成功條件。

## 10. 重現測試

### 10.1 查詢全部

```http
GET http://localhost:8080/api/departments
```

成功時應看到兩個Department，每個都包含兩名Employee，而且Employee內沒有再次出現`department`。

### 10.2 查詢單一部門

先由全部查詢取得實際ID，再呼叫：

```http
GET http://localhost:8080/api/departments/1
GET http://localhost:8080/api/departments/name/Finance
```

不要假設MIS一定是ID 1；資料庫已有資料時自動遞增值可能不同。

### 10.3 更新部門名稱

```http
PUT http://localhost:8080/api/departments/1
Content-Type: application/json

{
  "name": "MeMe"
}
```

再執行`GET /api/departments`。若該ID原本是MIS，名稱會變成MeMe，原本兩名員工仍保留。

課堂結果畫面中的`MeMe`不是初始化器預設值，而是部門名稱經PUT更新後的狀態。這可同時驗證更新成功及關聯員工沒有被清除。

### 10.4 SQL成功判定

查全部時，Console應出現概念上相當於：

```sql
select ...
from departments d
left join employees e
    on d.id = e.dept_id
```

實際別名與選取欄位由Hibernate決定，不必逐字相同；重點是只有部門主查詢並含對employees的Left Join。

### 10.5 比較Category的N+1與Join Fetch

先保留Controller目前的`repo.findAll()`：

```http
GET http://localhost:8080/api/categories
```

預期JSON：

- 3C包含iPhone 17與Samsung Phone。
- Fruit包含Apple與Banana。
- Product不包含反向`category`欄位。

此時Console應出現Category主查詢，以及依`category_id`重複查Product的SQL。接著依第7.2節改用`findAllWithProducts()`並重新啟動；JSON應相同，SQL則改為一次`LEFT JOIN`載入Category與Product。

## 11. `@Modifying`、`@Transactional`與批次更新

本段目標是依Category名稱，把所有對應Product的`stock`一次更新為0，而不是先查出每個Product再逐筆呼叫`save()`。

### 11.1 Repository：宣告修改查詢

```java
public interface ProductRepository
        extends JpaRepository<Product, Integer> {

    @Modifying
    @Query("UPDATE Product p " +
           "SET p.stock = 0 " +
           "WHERE p.category.name = :cat")
    int clearStockByCategory(@Param("cat") String cat);
}
```

各部分的確切角色：

| 語法／註解 | 定義與成立條件 |
|---|---|
| `@Query` | 這裡放JPQL；`Product`、`stock`、`category.name`都是Entity與Java屬性路徑，不是SQL表欄位名稱 |
| `p.category.name` | 沿著`Product.category`關聯取得Category的`name`作為條件；Hibernate可據此產生對categories的Join |
| `:cat` | JPQL命名參數，必須和`@Param("cat")`完全對應 |
| `@Modifying` | 告訴Spring Data這不是SELECT，而是UPDATE／DELETE等資料修改查詢 |
| `int`回傳值 | 資料庫實際受影響的資料列數，不是更新後的Product物件 |

只寫`@Query`而沒有`@Modifying`時，Spring Data仍可能依查詢方法的預設SELECT流程處理，修改查詢無法正確執行。

### 11.2 Service：交易邊界

```java
@Service
public class ProductService {
    @Autowired
    ProductRepository repo;

    @Transactional
    public int clearStock(String category) {
        return repo.clearStockByCategory(category);
    }
}
```

`@Transactional`讓方法在可提交的資料庫交易中執行。修改查詢若沒有有效交易，通常會因沒有transaction而失敗；方法正常結束時提交，丟出符合rollback規則的例外時回復。

交易放在Service的原因是Service負責一個完整業務操作的邊界。即使本例只有一次Repository呼叫，日後若加入紀錄、檢查或其他更新，也可以納入同一交易。

### 11.3 Controller與目前回應

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService service;

    @GetMapping("/{category}")
    public ResponseEntity<Map<String, Integer>> clearStock(
            @PathVariable("category") String category) {
        int count = service.clearStock(category);
        Map<String, Integer> result = new HashMap<>();
        result.put("clear " + category, count);
        return ResponseEntity.ok(result);
    }
}
```

課堂呼叫：

```http
GET http://localhost:8080/api/products/fruit
```

畫面回應：

```json
{"clear fruit": 2}
```

這代表該次UPDATE影響兩列。種子資料中的Category名稱是`Fruit`，Request使用小寫`fruit`仍匹配，是目前MySQL字串collation比較不分大小寫所產生的結果；JPQL本身沒有寫`LOWER()`，換成區分大小寫的collation或不同資料庫時不保證仍能匹配。

### 11.4 Hibernate產生的SQL

JPQL條件沿著`p.category.name`導覽關聯，因此Hibernate在目前MySQL環境產生概念上相當於：

```sql
update products p
join categories c
    on c.id = p.category_id
set p.stock = 0
where c.name = ?
```

這是單次Bulk Update。Console看到的Join不是Repository寫的原生SQL，而是Hibernate把JPQL關聯路徑轉換成資料庫SQL的結果。

### 11.5 用影響列數驗證更新範圍

`/api/products/fruit`回應的更新筆數是2，而且WHERE條件只匹配Fruit，因此這個Request只能證明Apple與Banana兩列被更新。

資料庫查詢畫面只顯示目前狀態，不能證明每一列由哪一次Request修改。若四筆資料最後都是0，仍須依每次回傳的affected rows與重設後的測試結果判斷更新範圍。

要做可重複的驗證，先把四筆庫存恢復成種子值，再依序測：

```http
GET /api/products/fruit
```

預期只有Apple、Banana變為0，回傳2。接著測：

```http
GET /api/products/3C
```

預期iPhone 17、Samsung Phone也變為0，回傳2。`3C`含數字，URL可直接使用；若類別名稱含空白或特殊字元，Client必須做URL encoding。

### 11.6 Bulk Update與Persistence Context

JPQL Bulk Update直接修改資料庫，不會逐一同步目前Persistence Context中已載入的Product物件。因此同一個交易裡若先查Product、再做Bulk Update，記憶體中的Entity可能仍保留舊stock。

需要修改後自動清除Persistence Context時，可依流程使用：

```java
@Modifying(clearAutomatically = true,
           flushAutomatically = true)
```

但清除Context會讓尚未flush的其他Entity變更有遺失風險，所以不能機械套用；要先確認同一交易中是否還有其他待保存變更。

### 11.7 HTTP Method的設計問題

目前使用`GET /api/products/{category}`會修改資料庫，不符合GET應為safe method、不得要求伺服器改變資源狀態的HTTP語意。Browser、Cache、Crawler或預先載入機制可能在使用者沒有更新意圖時觸發GET。

較合適的設計例如：

```java
@PatchMapping("/category/{category}/stock/clear")
public ResponseEntity<Map<String, Integer>> clearStock(...)
```

對應Request：

```http
PATCH /api/products/category/Fruit/stock/clear
```

課堂目前的GET端點可用來觀察`@Modifying`，但實務API應改用PATCH、PUT或依資源設計選擇其他非GET方法。

## 12. Lombok與雙向關聯的額外風險

`@Data`會產生`toString()`、`equals()`及`hashCode()`。若雙向關聯兩端都把對方欄位納入這些方法，可能反覆互相呼叫，或在不預期時觸發LAZY載入。

本專案的Department自行覆寫`toString()`且不印employees，但`equals()`／`hashCode()`仍需留意。常見處理方式：

```java
@ToString.Exclude
@EqualsAndHashCode.Exclude
@ManyToOne(fetch = FetchType.LAZY)
private Department department;
```

也可以不用`@Data`，只產生必要Getter／Setter，並以穩定識別策略自行設計Entity的`equals()`與`hashCode()`。

## 13. 常見錯誤

1. **`mappedBy`寫成`dept_id`：**它必須寫Java屬性`department`。
2. **只設定Department集合：**Owning Side沒設，Employee外鍵可能沒有正確值。
3. **JSON無限巢狀：**JPA映射成功不代表Jackson知道哪一端要停止輸出。
4. **LAZY載入例外：**在持久化Context關閉後才存取未載入集合。
5. **以為`PERSIST`會連動刪除：**目前沒有`REMOVE`與`orphanRemoval`。
6. **以為Service方法等於HTTP API：**沒有Controller Mapping就沒有對應端點。
7. **把更新後的MeMe當成初始資料：**初始化原值是MIS。
8. **看到JSON正確便以為沒有N+1：**結果內容正確與查詢次數合理是兩項不同的驗證。
9. **只建立`findAllWithProducts()`卻仍呼叫`findAll()`：**Repository方法不會自動取代內建方法，Controller必須實際呼叫它。
10. **修改查詢漏掉`@Modifying`或交易：**JPQL UPDATE需要被辨識為修改操作，且必須在有效transaction內執行。
11. **把Bulk Update當成逐筆Entity更新：**它直接改資料庫，已載入的Entity可能保留舊值。
12. **用GET改庫存：**程式雖能執行，但違反GET的safe語意，實務API應改用PATCH或PUT。
13. **把四筆stock為0全歸因於fruit呼叫：**該次回應明確是affected rows 2，其他兩筆需要另外的操作證據。

## 14. 本章檢查表

- [ ] 能指出外鍵位於哪張表、哪個欄位
- [ ] 能分辨Owning Side與Inverse Side
- [ ] 知道`mappedBy`填Java屬性名，不是SQL欄位名
- [ ] 建立關聯時會同步設定兩端
- [ ] 能區分JPA關聯註解與Jackson序列化註解
- [ ] 能說明`JOIN FETCH`如何處理本例的N+1問題
- [ ] 能從Category的重複`where category_id=?`查詢辨認`1 + N`
- [ ] 能切換`findAll()`與`findAllWithProducts()`並確認JSON相同、SQL次數不同
- [ ] 能說明`@Modifying`、`@Transactional`與affected rows的不同責任
- [ ] 能從`p.category.name`解釋Hibernate為何產生Join Update
- [ ] 能說明Bulk Update為何可能使Persistence Context保留舊值
- [ ] 知道讀取用GET不應拿來修改庫存
- [ ] 知道`CascadeType.PERSIST`沒有包含刪除級聯
- [ ] 已依第10節查詢、更新並核對JSON與Console SQL
