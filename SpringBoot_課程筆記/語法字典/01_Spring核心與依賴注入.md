# Spring核心與依賴注入

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

<a id="springbootapplication"></a>
## `@SpringBootApplication`

**定義**：標記Spring Boot啟動與主要設定類別。它組合了自動設定、元件掃描與設定類別能力。

**可放位置**：類別。通常整個專案只需放在主啟動類別。

**成立條件**：主類別應位在希望掃描之套件樹的最上層；預設只掃描主類別所在套件及其子套件。

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

常用參數：

| 參數 | 用途 | 範例 |
|---|---|---|
| `scanBasePackages` | 額外指定掃描套件 | `@SpringBootApplication(scanBasePackages = "com.example")` |
| `exclude` | 排除指定自動設定類別 | `exclude = DataSourceAutoConfiguration.class` |

<a id="stereotype"></a>
## `@Component`、`@Service`、`@Repository`、`@Controller`

**共同定義**：把類別標記為Spring管理的元件，元件掃描找到後會建立Bean。

| 註解 | 適用層 | 主要差異 |
|---|---|---|
| `@Component` | 一般元件 | 最通用的元件標記 |
| `@Service` | Service | 表達業務邏輯角色 |
| `@Repository` | Repository／DAO | 表達資料存取角色，並支援資料存取例外轉換 |
| `@Controller` | MVC Controller | 方法通常回傳View名稱 |
| `@RestController` | REST Controller | 等同`@Controller`加上類別層級的`@ResponseBody`；詳見[MVC頁](02_Spring_MVC與REST.md#restcontroller) |

```java
@Service
public class UserService {
}
```

這些註解都可填Bean名稱，例如`@Service("userService")`；只有在需要明確命名或搭配`@Qualifier`時才必須填。

<a id="configuration-bean"></a>
## `@Configuration`與`@Bean`

**使用時機**：要把第三方類別、不可修改的類別，或需要自訂建立流程的物件交給Spring管理。

```java
@Configuration
public class AppConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
```

| 語法 | 可放位置 | 定義 |
|---|---|---|
| `@Configuration` | 類別 | 宣告這是Java設定類別 |
| `@Bean` | 設定類別的方法 | 以方法回傳值註冊Bean；預設Bean名稱是方法名 |

`@Bean(name = "appClock")`可改名；`@Bean(initMethod = "start", destroyMethod = "close")`可指定生命週期方法。

<a id="autowired"></a>
## 建構子注入與`@Autowired`

**定義**：要求Spring把符合型別的Bean提供給物件。只有一個建構子時，Spring可自動使用它，不必寫`@Autowired`。

```java
@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }
}
```

**推薦條件**：必要依賴優先使用建構子注入，欄位可設為`final`，測試時也容易自行傳入替身物件。

`@Autowired(required = false)`代表找不到Bean時可不注入，但必要依賴不應這樣隱藏錯誤。可放在建構子、方法或欄位；欄位注入雖短，依賴不明確且較難測試。

<a id="qualifier-primary"></a>
## `@Qualifier`與`@Primary`

**成立條件**：同一個介面型別存在多個Bean，Spring無法只靠型別決定要注入哪一個。

```java
public NotificationController(
        @Qualifier("emailNotificationService") NotificationService service) {
    this.service = service;
}
```

| 語法 | 選擇方式 | 適用時機 |
|---|---|---|
| `@Qualifier("beanName")` | 注入點明確指定候選Bean | 不同注入點需要不同實作 |
| `@Primary` | 多個候選中設定預設優先者 | 大多數地方使用同一個預設實作 |

兩者同時存在時，注入點的`@Qualifier`比`@Primary`更明確。字串必須和Bean名稱或Qualifier值一致。

<a id="postconstruct"></a>
## `@PostConstruct`

**定義**：Spring完成依賴注入後，呼叫一次的初始化方法。

```java
@PostConstruct
void loadSampleData() {
    // 初始化資料
}
```

**可放位置**：無參數、通常回傳`void`的方法。Spring Boot 3以上使用`jakarta.annotation.PostConstruct`。

**不適合**：耗時工作、需要精確交易邊界的資料匯入，或每次啟動都不應重做的不可重複操作。

## 常見錯誤

- Bean不在主啟動類別的套件樹下：元件掃描不到。
- 同型別有兩個Bean但沒有`@Qualifier`或`@Primary`：啟動時出現候選不唯一。
- 手動`new Service()`：該物件不經Spring建立，注入與生命週期功能不會生效。

