# JPA關聯映射

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

以下以「一個Department有多個Employee；每個Employee屬於一個Department」為例。

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `@ManyToOne` | 多筆Entity指向同一筆Entity | `@ManyToOne(fetch=LAZY)` | [說明](#manytoone-onetomany) |
| `@OneToMany` | 一筆Entity持有多筆集合 | `@OneToMany(mappedBy="department")` | [說明](#manytoone-onetomany) |
| `@JoinColumn` | 指定關聯外鍵欄位 | `@JoinColumn(name="dept_id")` | [參數](#joincolumn) |
| `mappedBy` | 指向另一端負責外鍵的Java屬性 | `mappedBy="department"` | [條件](#mappedby) |
| `FetchType.LAZY`／`EAGER` | 設定關聯載入策略 | `fetch=FetchType.LAZY` | [說明](#manytoone-onetomany) |
| `CascadeType` | 傳遞Entity生命週期操作 | `cascade=CascadeType.PERSIST` | [可選值](#cascade) |
| `orphanRemoval` | 移除孤兒子Entity | `orphanRemoval=true` | [說明](#manytoone-onetomany) |

<a id="manytoone-onetomany"></a>
## `@ManyToOne`與`@OneToMany`

```java
// Employee：外鍵所在的一方，也是關聯擁有端
@ManyToOne(fetch = FetchType.LAZY, optional = false)
@JoinColumn(name = "dept_id", nullable = false)
private Department department;

// Department：反向端
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL,
           orphanRemoval = true)
private List<Employee> employees = new ArrayList<>();
```

| 註解 | 可放位置 | 表達關係 | 預設fetch |
|---|---|---|---|
| `@ManyToOne` | 多方的實體屬性 | 多個Employee指向一個Department | `EAGER` |
| `@OneToMany` | 一方的集合屬性 | 一個Department持有多個Employee | `LAZY` |

實務上常明寫`fetch = FetchType.LAZY`，並在查詢層決定何時載入；不要把`EAGER`當成解決Lazy問題的通用方法。

實際案例：[第16章Employee的`@ManyToOne`](../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#employee-manytoone-example)與[Department的`@OneToMany`](../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#department-onetomany-example)。

### `@ManyToOne`常用參數

| 參數 | 預設 | 定義 |
|---|---|---|
| `fetch` | `EAGER` | 載入策略提示 |
| `optional` | `true` | Java關聯是否可為`null`；常與`@JoinColumn(nullable = false)`一起保持一致 |
| `cascade` | 無 | 對此關聯傳遞哪些實體生命週期操作 |
| `targetEntity` | 由泛型／型別推導 | 無法由屬性型別推導時指定目標實體 |

### `@OneToMany`常用參數

| 參數 | 預設 | 定義 |
|---|---|---|
| `mappedBy` | `""` | 指向擁有端屬性名稱，例如Employee中的`department` |
| `cascade` | 無 | 傳遞`PERSIST`、`MERGE`、`REMOVE`等操作 |
| `fetch` | `LAZY` | 集合載入策略 |
| `orphanRemoval` | `false` | 從集合移除且不再被擁有時，是否刪除子實體 |

<a id="joincolumn"></a>
## `@JoinColumn`

**定義**：指定關聯使用的外鍵欄位。通常放在關聯擁有端。

```java
@JoinColumn(
    name = "dept_id",
    referencedColumnName = "id",
    nullable = false,
    updatable = true
)
```

| 參數 | 用途 |
|---|---|
| `name` | 此資料表中的外鍵欄位名稱 |
| `referencedColumnName` | 對方被參照欄位；預設是對方主鍵，通常可省略 |
| `nullable` | 外鍵是否允許`NULL` |
| `unique` | 外鍵是否唯一；設為`true`可能把多對一限制成一對一效果 |
| `insertable`／`updatable` | JPA產生INSERT／UPDATE時是否包含外鍵欄位 |
| `foreignKey` | 自訂或停用產生的外鍵約束描述 |

實際案例：[第16章`employees.dept_id`](../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#employee-manytoone-example)。

<a id="mappedby"></a>
## `mappedBy`的確切條件

`mappedBy`的值是「另一個實體中的Java屬性名稱」，不是資料表名，也不是外鍵欄位名。

```java
// Employee.java
private Department department;

// Department.java
@OneToMany(mappedBy = "department")
private List<Employee> employees;
```

寫成`mappedBy = "dept_id"`會錯，因為`dept_id`是資料庫欄位名。

實際案例：[第16章`mappedBy="department"`](../純文字版/16_JPA_OneToMany_ManyToOne與JSON關聯.md#department-onetomany-example)。

<a id="cascade"></a>
## `CascadeType`

| 值 | 父實體動作會傳遞什麼 |
|---|---|
| `PERSIST` | 新增 |
| `MERGE` | 合併更新 |
| `REMOVE` | 刪除 |
| `REFRESH` | 重新讀取 |
| `DETACH` | 脫離持久化Context |
| `ALL` | 以上全部 |

級聯不是資料庫`ON DELETE CASCADE`的同義詞；一個是JPA物件操作傳遞，一個是資料庫外鍵行為。

## 雙向關聯同步方法

```java
public void addEmployee(Employee employee) {
    employees.add(employee);
    employee.setDepartment(this);
}

public void removeEmployee(Employee employee) {
    employees.remove(employee);
    employee.setDepartment(null);
}
```

雙向關聯要同步兩側Java物件，否則記憶體狀態與實際外鍵擁有端可能不一致。

## JSON輸出的額外問題

雙向關聯直接序列化可能無限循環；可使用DTO，或依需求使用Jackson關聯註解。DTO通常最能明確控制API格式。詳見[JPA關聯擁有端、級聯與JSON](延伸閱讀/JPA關聯擁有端_級聯與JSON.md)。
