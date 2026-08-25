# 第13章延伸閱讀：JPA對應既有資料表的Coffee案例

## 本頁快速索引

- [0. 前置條件與完成判定](#0-前置條件與完成判定)
- [1. Coffee範例：對應既有表格](#1-coffee範例對應既有表格)
- [2. 啟動前檢查](#2-啟動前檢查)
- [3. 常見錯誤](#3-常見錯誤)
- [4. 延伸閱讀檢查表](#4-延伸閱讀檢查表)

## 0. 前置條件與完成判定

- MySQL服務已啟動，`classicmodels`資料庫可連線。
- `coffees`表與必要欄位已存在；`validate`不會替讀者建立缺少的Schema。
- 專案包含Spring Web、Spring Data JPA與MySQL Connector。
- 完成時ApplicationContext應正常啟動，`GET /api/coffees`回傳既有資料。

## 1. Coffee範例：對應既有表格

Coffee專案比較精簡：

```text
CoffeeController
    ↓ 直接注入
CoffeeRepository extends JpaRepository<Coffee, String>
    ↓
Coffee Entity → coffees table
```

### 1.1 Entity映射

```java
@Entity
@Table(name = "coffees")
@Data
public class Coffee {
    @Id
    @Column(name = "COF_NAME")
    String cofName;

    @Column(name = "SUP_ID", nullable = false)
    int supId;

    @Column(nullable = false)
    BigDecimal price;
    // sales、total...
}
```

和Employee不同：

| 項目 | Employee | Coffee |
|---|---|---|
| 主鍵型別 | `Long` | `String` |
| 主鍵產生 | `IDENTITY` | 應用程式／既有資料提供 |
| 表格 | `employees` | `coffees` |
| DDL設定 | `create-drop` | `validate` |
| API | 完整CRUD | 目前只有GET全部 |
| 分層 | Controller→Service→Repository | Controller直接→Repository |

### 1.2 Repository泛型必須對應主鍵

```java
public interface CoffeeRepository
        extends JpaRepository<Coffee, String> {
}
```

第二個泛型是`String`，因為`@Id`欄位`cofName`是String。它不是資料表名稱，也不是任意指定的型別。

### 1.3 `validate`的成立條件

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Hibernate只驗證既有Schema是否符合Entity映射，不會建立缺少的表格或欄位。因此啟動成功的必要條件包括：

- MySQL的`classicmodels`資料庫存在。
- `coffees`表存在。
- 必要欄位名稱與型別可和Entity對應。

不符合時ApplicationContext可能啟動失敗。

因此Coffee路線不能只建立一個空的`classicmodels`資料庫。必須先匯入包含`coffees`表的既有Schema與資料；若手上的classicmodels版本沒有這張表，應先取得課程使用的SQL腳本或自行建立與`Coffee`Entity完全相容的表格，再使用`validate`啟動。

### 1.4 目前只提供查詢全部

```java
@GetMapping
public ResponseEntity<List<Coffee>> getAll() {
    List<Coffee> cofs = dao.findAll();
    return ResponseEntity.ok(cofs);
}
```

目前端點：

```http
GET /api/coffees
```

原始碼沒有POST、PUT、DELETE，因此不能把Coffee範例寫成完整CRUD。


## 2. 啟動前檢查

- [ ] `classicmodels`可連線
- [ ] `coffees`表及欄位已存在
- [ ] Entity欄位名稱、型別與nullable條件相容
- [ ] Repository的ID泛型與`@Id`欄位同為String
- [ ] 啟動後再測`GET /api/coffees`

## 3. 常見錯誤

1. **Schema-validation failed：**核對表名、欄位名稱、型別與nullable限制。
2. **Unknown table：**`validate`不會建表，必須先匯入或建立`coffees`。
3. **Repository ID型別錯誤：**第二個泛型必須與`cofName`的String型別一致。
4. **把案例寫成完整CRUD：**目前Controller只有查詢全部，沒有POST、PUT或DELETE。

## 4. 延伸閱讀檢查表

- [ ] 能說明`@Table`與`@Column`如何對應既有Schema
- [ ] 能說明Coffee為何使用String主鍵
- [ ] 能區分`create-drop`與`validate`
- [ ] 知道`validate`不會建立缺少的表格或欄位
- [ ] 已以`GET /api/coffees`驗證映射結果
