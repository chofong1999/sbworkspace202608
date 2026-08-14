# Spring Data查詢、交易與分頁

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

<a id="jparepository"></a>
## `JpaRepository<T, ID>`

```java
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
```

- `T`：實體型別。
- `ID`：該實體`@Id`欄位的Java型別。
- 介面通常不必手動加`@Repository`；Spring Data會建立實作。

常用方法：

| 方法 | 回傳 | 用途 |
|---|---|---|
| `save(entity)` | 儲存後實體 | 新增或更新 |
| `findById(id)` | `Optional<T>` | 依主鍵查詢 |
| `findAll()` | `List<T>` | 查全部 |
| `existsById(id)` | `boolean` | 是否存在 |
| `count()` | `long` | 筆數 |
| `deleteById(id)` | `void` | 依主鍵刪除 |

<a id="derived-query"></a>
## 方法名稱查詢（Derived Query）

**定義**：Spring Data解析Repository方法名稱，依實體的Java屬性建立查詢。

```java
Optional<Product> findByName(String name);
List<Product> findByCategoryAndPriceLessThan(String category, BigDecimal price);
List<Product> findByNameContainingIgnoreCaseOrderByPriceAsc(String keyword);
long countByCategory(String category);
boolean existsByEmail(String email);
```

| 關鍵字 | 功能 | 範例片段 |
|---|---|---|
| `And`／`Or` | 組合條件 | `findByNameAndAge` |
| `LessThan`／`GreaterThan`／`Between` | 範圍 | `findByPriceBetween` |
| `Containing`／`StartingWith`／`EndingWith` | 字串比對 | `findByNameContaining` |
| `IgnoreCase` | 忽略大小寫 | `findByEmailIgnoreCase` |
| `In`／`NotIn` | 集合條件 | `findByIdIn` |
| `IsNull`／`IsNotNull` | NULL判斷 | `findByDeletedAtIsNull` |
| `OrderBy...Asc/Desc` | 固定排序 | `findByCategoryOrderByPriceDesc` |
| `Top`／`First` | 限制筆數 | `findTop5ByOrderByPriceDesc` |

方法中的名稱是Java屬性名，不是資料庫欄位名。名稱過長或條件複雜時改用`@Query`或Specification。

<a id="query-param"></a>
## `@Query`與`@Param`

```java
@Query("SELECT p FROM Product p WHERE p.category.name = :category")
List<Product> findByCategoryName(@Param("category") String category);
```

**預設是JPQL**：查詢實體與Java屬性，不是直接查資料表與欄位。

```java
@Query(value = "SELECT * FROM products WHERE stock < :limit", nativeQuery = true)
List<Product> findLowStock(@Param("limit") int limit);
```

`nativeQuery = true`才是資料庫原生SQL。原生SQL較容易使用特定資料庫功能，但可攜性與自動分頁計數要額外確認。

`@Query`常用參數：

| 參數 | 用途 |
|---|---|
| `value` | JPQL／SQL內容 |
| `nativeQuery` | 是否為原生SQL，預設`false` |
| `countQuery` | 複雜分頁查詢另指定計數查詢 |

<a id="modifying-transactional"></a>
## `@Modifying`與`@Transactional`

**使用條件**：`@Query`執行UPDATE、DELETE或DDL時，要加`@Modifying`；修改操作也必須在交易中。

```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Transactional
@Query("UPDATE Product p SET p.stock = 0 WHERE p.category.name = :category")
int clearStock(@Param("category") String category);
```

| `@Modifying`參數 | 預設 | 用途 |
|---|---|---|
| `clearAutomatically` | `false` | 執行後清除Persistence Context，避免仍讀到舊實體狀態 |
| `flushAutomatically` | `false` | 執行前先flush尚未同步的變更 |

回傳`int`通常代表受影響筆數。Bulk UPDATE／DELETE繞過逐筆實體生命週期與已載入物件，後續若仍使用那些物件要特別處理快取一致性。

`@Transactional(readOnly = true)`適合查詢服務；修改交易不要設`readOnly = true`。`rollbackFor`可明確加入預設不回滾的checked exception。

<a id="paging-sort"></a>
## `Pageable`、`Page<T>`與`Sort`

```java
Page<Product> findByCategory(String category, Pageable pageable);

Pageable pageable = PageRequest.of(
    0, 10, Sort.by(Sort.Direction.DESC, "price")
);
Page<Product> page = repository.findByCategory("Fruit", pageable);
```

- 頁碼從`0`開始。
- `Page<T>`含內容、總筆數與總頁數，通常會多做count查詢。
- 只需知道還有沒有下一頁時，可考慮`Slice<T>`減少總筆數查詢需求。
- `Sort`中的欄位是實體Java屬性名。

Controller常見寫法：

```java
@GetMapping
public Page<Product> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    return repository.findAll(PageRequest.of(page, size));
}
```

官方參考：[Spring Data JPA Query Methods](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)

