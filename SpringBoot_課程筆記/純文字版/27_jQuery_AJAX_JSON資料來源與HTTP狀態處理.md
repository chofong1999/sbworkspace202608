# jQuery AJAX：JSON資料來源與HTTP狀態處理

[返回JavaScript課程功能快速索引](00_JavaScript功能快速索引.md)

本章接續第26章的前後端分離，練習以`$.ajax()`讀取本機JSON、外部公開API及自己的Spring Boot API，最後依HTTP狀態碼處理錯誤。課堂前端來源位於`week6\day4`，後端專案為`sbstatus0821`；可執行副本分別位於`C:\jscode\day4`與`C:\sbworkspace202608\sbstatus0821`。

## 1. 本章功能快速索引

| 想完成的事情 | 主要寫法 |
|---|---|
| 載入jQuery | `<script src="...jquery.min.js"></script>` |
| 發送可完整設定的AJAX Request | `$.ajax({ ... })` |
| 指定HTTP Method與URL | `method`、`url` |
| 以共用基礎網址組合不同端點 | `const dataUrl = "..."`、JavaScript模板字串 |
| 指定預期Response格式 | `dataType: "json"` |
| 成功後處理資料 | `success: onSuccess` |
| 逐筆顯示Array資料 | `$.each(data, function () { ... })` |
| 傳送JSON Request Body | `JSON.stringify(...)`、`contentType: "application/json"` |
| 依URL中的ID查詢單筆商品 | `findById(...)`、`Optional<Product>`、`@PathVariable` |
| 依狀態碼處理結果 | `statusCode: { 404: ..., 500: ... }` |
| 處理一般AJAX錯誤 | `error: function (...) { ... }` |

課堂檔案：

| 檔案 | 資料來源與重點 |
|---|---|
| `air.json` | 本機JSON測試資料 |
| `jquery_ajax.html` | 讀取同資料夾的`air.json` |
| `jquery_fakestore.html` | 呼叫外部Fake Store API |
| `jquery_myproduct.html` | GET／POST自己的Spring Boot商品API |
| `jquery_status.html` | 204、404、500與一般錯誤處理 |

## 2. `air.json`放在哪裡

`air.json`不是Spring Boot專案資源。本例將它與前端HTML放在同一個資料夾：

```text
C:\jscode\day4\
├─ jquery_ajax.html
└─ air.json
```

HTML中的設定為：

```javascript
const dataUrl = "air.json";
```

`air.json`是相對URL。若Live Server開啟：

```text
http://127.0.0.1:5500/day4/jquery_ajax.html
```

瀏覽器就會請求：

```text
http://127.0.0.1:5500/day4/air.json
```

因此兩個檔案必須保持同層；若把JSON移到`data`子資料夾，URL也要改成`data/air.json`。檔案不存在或路徑錯誤時，Network通常會看到404。

## 3. 使用`$.ajax()`讀取本機JSON

頁面至少需要按鈕與結果表格。`id`必須和後續jQuery Selector一致：

```html
<button id="b1">jQuery Ajax</button>

<table id="airQ" border="1" style="display: none;" width="90%">
    <tr>
        <th>地區</th>
        <th>預報內容</th>
    </tr>
</table>
```

基本設定：

```javascript
const dataUrl = "air.json";

function onSuccess(data) {
    $("#airQ").empty().show();
    $("#airQ").append("<tr><th>地區</th><th>預報內容</th></tr>");

    $.each(data, function () {
        const row = $("<tr></tr>");
        $("<td></td>").text(this.Area).appendTo(row);
        $("<td></td>").text(this.Content).appendTo(row);
        $("#airQ").append(row);
    });
}

$("#b1").click(function () {
    $.ajax({
        method: "GET",
        url: dataUrl,
        dataType: "json",
        success: onSuccess
    });
});
```

各設定的責任：

| 設定 | 作用 |
|---|---|
| `method: "GET"` | 使用GET取得資料 |
| `url: dataUrl` | 指定Request目標 |
| `dataType: "json"` | 告訴jQuery預期Response為JSON，成功後轉成JavaScript資料 |
| `success: onSuccess` | Request成功時呼叫函式，並把解析後資料傳入 |

`dataType`描述的是「預期收到的Response格式」，不要和POST的`contentType`混淆。

## 4. 改成外部公開API

`jquery_fakestore.html`保留相同流程，只將URL改為外部API：

```javascript
const dataUrl = "https://fakestoreapi.com/products";
```

頁面結果區可以使用：

```html
<button id="b1">Fakestore Products</button>

<table id="productTable" border="1" style="display: none;" width="90%">
    <tr>
        <th>Product ID</th>
        <th>Title</th>
        <th>Price</th>
        <th>Image</th>
    </tr>
</table>
```

每一項商品可讀取`id`、`title`、`price`與`image`：

```javascript
$.each(data, function () {
    const row = $("<tr></tr>");
    $("<td></td>").text(this.id).appendTo(row);
    $("<td></td>").text(this.title).appendTo(row);
    $("<td></td>").text(this.price).appendTo(row);
    $("<td></td>")
        .append($("<img>").attr("src", this.image).css("width", "50px"))
        .appendTo(row);
    $("#productTable").append(row);
});
```

此例需要網路，而且外部服務必須允許瀏覽器跨Origin讀取Response；若服務停止或CORS不允許，前端程式本身正確也可能失敗。

## 5. 串接自己的Spring Boot商品API

### 5.1 建立專案與套件結構

使用Spring Starter Project建立後端：

| 設定 | 值 |
|---|---|
| Name／Artifact | `sbstatus0821` |
| Type | Maven |
| Packaging | Jar |
| Java | 17 |
| Dependencies | Spring Web MVC、Spring Boot DevTools、Lombok |

專案建立方式與第26章相同，只有專案名稱不同。核心結構：

```text
src/main/java/com/example/demo/
├─ controller/ProductController.java
├─ dao/ProductDAO.java
└─ model/Product.java
```

### 5.2 建立`Product`

```java
package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    Integer id;
    String name;
    double price;
}
```

三個Lombok註解分別產生常用方法、無參數建構子及包含全部欄位的建構子。DAO初始化資料時會使用全欄位建構子；Spring將JSON轉成Java物件時需要能建立物件並設定欄位。

### 5.3 建立記憶體版`ProductDAO`

```java
package com.example.demo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Product;

@Repository
public class ProductDAO implements CommandLineRunner {
    private final List<Product> data = new ArrayList<>();

    public List<Product> getAll() {
        return data;
    }

    public Product addProduct(Product product) {
        int nextId;
        if (data.isEmpty()) {
            nextId = 1;
        } else {
            nextId = data.stream()
                    .mapToInt(Product::getId)
                    .max()
                    .orElse(0) + 1;
        }

        product.setId(nextId);
        data.add(product);
        return product;
    }

    public Optional<Product> findById(int id) {
        return data.stream()
                .filter(product -> product.getId() == id)
                .findAny();
    }

    @Override
    public void run(String... args) {
        if (data.isEmpty()) {
            data.add(new Product(1, "Apple Mac Mini", 19000.0));
            data.add(new Product(2, "Google Pixel Phone", 29000.0));
            data.add(new Product(3, "Samsung Galaxy Phone", 23900.0));
        }
    }
}
```

`@Repository`把DAO登記成Spring管理的Bean。`CommandLineRunner.run()`會在Spring Boot啟動完成後執行，本例利用它加入三筆初始商品。

`findById(int id)`依序檢查記憶體List中的商品ID，找到時回傳包含商品的`Optional<Product>`，找不到時回傳空的`Optional`。Controller可依`Optional`是否有值決定回200或404，避免以`null`表示查無資料。

老師原檔使用：

```java
data.stream().max((x, y) -> x.getId() - y.getId()).get().getId() + 1;
```

這段在目前有三筆預設資料時可以執行，但若List為空，`max()`得到空的`Optional`，直接`.get()`會拋出`NoSuchElementException`。上面的完整版本先處理空List，並用`mapToInt(...).max().orElse(0)`避免這個風險。

本例沒有資料庫；資料只存在記憶體。重新啟動程式後，執行期間新增的商品會消失並回到三筆初始資料。

### 5.4 建立`ProductController`

```java
package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
    private final ProductDAO dao;

    public ProductController(ProductDAO dao) {
        this.dao = dao;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        List<Product> products = dao.getAll();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProduct(@PathVariable("id") int id) {
        return dao.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(dao.addProduct(product));
    }

    @GetMapping("/notfound")
    public ResponseEntity<String> notFound() {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/nocontent")
    public ResponseEntity<String> notContent() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/error")
    public ResponseEntity<String> internalError() {
        return ResponseEntity.internalServerError()
                .body("Spring Boot Triggered");
    }
}
```

老師原檔在欄位上使用`@Autowired`注入DAO；上例改用等效的建構子注入，使必要依賴不可省略，也較容易測試。兩者在本課程專案都能運作：

```java
@Autowired
ProductDAO dao;
```

Controller提供：

| Method | URL | 功能 |
|---|---|---|
| GET | `http://localhost:8080/api/products` | 有資料回200與JSON；空List回204 No Content |
| GET | `http://localhost:8080/api/products/{id}` | 依ID查單筆；找到回200與商品，找不到回404 |
| POST | `http://localhost:8080/api/products` | 接收JSON並新增商品 |
| GET | `http://localhost:8080/api/products/notfound` | 刻意回傳404供前端測試 |
| GET | `http://localhost:8080/api/products/nocontent` | 刻意回傳204且不帶Response Body |
| GET | `http://localhost:8080/api/products/error` | 刻意回傳500及文字內容供前端測試 |

Controller上的`@CrossOrigin`允許Live Server頁面讀取API Response。`@RequestBody`表示POST的JSON Request Body要轉成`Product`。

`@GetMapping("/{id}")`中的`{id}`是URL路徑變數位置，`@PathVariable("id") int id`負責取得該段文字並轉成`int`。例如開啟：

```text
http://localhost:8080/api/products/2
```

實際傳入的`id`是`2`。若第2號商品存在，Response為200與該商品JSON；若不存在，Response為404且沒有商品Body。

### 5.5 建立商品前端HTML

```html
<button id="b1">My Products</button>
<button id="b2">Add Product</button>

<table id="productTable" border="1" style="display: none;" width="40%">
    <tr>
        <th>Product ID</th>
        <th>Name</th>
        <th>Price</th>
    </tr>
</table>
```

### 5.6 GET商品

```javascript
$.ajax({
    method: "GET",
    url: "http://localhost:8080/api/products",
    dataType: "json",
    success: onSuccess
});
```

### 5.7 POST商品JSON

```javascript
$.ajax({
    method: "POST",
    url: "http://localhost:8080/api/products",
    data: JSON.stringify({
        id: -1,
        name: "New Product",
        price: 100
    }),
    contentType: "application/json",
    dataType: "json",
    success: function (data) {
        alert("Product added: " + JSON.stringify(data));
    }
});
```

這裡的兩種格式設定不同：

| 設定 | 描述哪一邊 |
|---|---|
| `contentType: "application/json"` | 送給後端的Request Body是JSON |
| `dataType: "json"` | 預期後端回傳JSON Response |

後端以`@RequestBody Product p`接收JSON。前端送出的`id: -1`只是暫時值，DAO會計算新ID後覆蓋。

## 6. 組合URL並處理204、404、500

頁面建立三個觸發按鈕：

```html
<button id="b1">404 Not Found</button>
<button id="b2">204 No Content</button>
<button id="b3">500 Internal Server Error</button>
```

三個Request的共同路徑先存成變數：

```javascript
const dataUrl = "http://localhost:8080/api/products";
```

各按鈕只需要在共同路徑後接上自己的端點：

```javascript
url: `${dataUrl}/notfound`
url: `${dataUrl}/nocontent`
url: `${dataUrl}/error`
```

反引號包住的內容是JavaScript模板字串（Template Literal），`${運算式}`會把運算結果插入字串。因此：

```javascript
`${dataUrl}/error`
```

產生的實際URL為：

```text
http://localhost:8080/api/products/error
```

這種寫法等同字串串接：

```javascript
dataUrl + "/error"
```

模板字串的`${...}`必須寫在反引號內；使用單引號或雙引號時只會成為普通文字。這是JavaScript字串插值，不是Thymeleaf的Expression。

404測試：

```javascript
$.ajax({
    method: "GET",
    url: `${dataUrl}/notfound`,
    dataType: "text",
    statusCode: {
        404: function () {
            alert("404 Not Found!");
        }
    },
    error: function (jqXHR, textStatus, errorThrown) {
        console.log("Error: " + textStatus + ", " + errorThrown);
    }
});
```

204測試：

```javascript
$.ajax({
    method: "GET",
    url: `${dataUrl}/nocontent`,
    dataType: "text",
    statusCode: {
        204: function () {
            alert("204 No Content!");
        }
    },
    error: function (jqXHR, textStatus, errorThrown) {
        console.log("Error: " + textStatus + ", " + errorThrown);
    }
});
```

500測試：

```javascript
$.ajax({
    method: "GET",
    url: `${dataUrl}/error`,
    dataType: "text",
    statusCode: {
        500: function (xhr) {
            alert(xhr.responseText + ", status: " + xhr.status);
        }
    },
    error: function (jqXHR, textStatus, errorThrown) {
        console.log(
            "jQuery error output: "
            + jqXHR.status + ", "
            + textStatus + ", "
            + errorThrown
        );
    }
});
```

- `statusCode`適合替特定HTTP狀態指定處理方式。
- `error`處理所有未成功的AJAX結果，適合記錄共通錯誤資訊。
- 204是成功回應，只是沒有Response Body；會執行204處理函式，不會當成一般AJAX錯誤。
- 404與500是錯誤回應，可能同時執行對應的`statusCode`函式和`error`函式。
- `xhr.status`可取得HTTP狀態碼；`xhr.responseText`可取得後端傳回的原始文字。
- 無法連到後端、CORS失敗或URL錯誤，不一定有可讀的HTTP Response Body。

`noContent()`表示204，因此使用不帶內容的`build()`：

```java
return ResponseEntity.noContent().build();
```

500若要同時回傳文字，應使用`body(...)`：

```java
return ResponseEntity.internalServerError()
        .body("Spring Boot Triggered");
```

`build("Spring Boot Triggered")`不是合法寫法；`build()`不接收Response Body。

## 7. 完整執行順序

1. 先以Spring Boot App啟動`C:\sbworkspace202608\sbstatus0821`。
2. 確認Console顯示應用程式已在8080啟動。
3. 在VS Code開啟`C:\jscode`資料夾。
4. 以Live Server開啟要測試的`day4` HTML。
5. 按下頁面按鈕並觀察表格、alert、Console與Network。
6. 另以瀏覽器或API工具開啟`http://localhost:8080/api/products/2`，確認能取得單筆商品；再使用不存在的ID確認回404。

成功判定：

| 範例 | 預期結果 |
|---|---|
| `jquery_ajax.html` | 顯示空氣品質地區與預報內容 |
| `jquery_fakestore.html` | 顯示外部商品與縮圖 |
| `jquery_myproduct.html` GET | 顯示後端預設的三筆商品 |
| `jquery_myproduct.html` POST | alert顯示新增商品，重新GET後可看見它 |
| `GET /api/products/2` | 回200及第2號商品；不存在的ID回404 |
| `jquery_status.html` | 三個按鈕分別顯示204、404與500測試結果；500可讀到後端文字 |

## 8. 常見錯誤

| 現象 | 優先檢查 |
|---|---|
| `$ is not defined` | jQuery CDN網路、URL及`script`先後順序 |
| `air.json`為404 | JSON是否與HTML同層、相對URL是否正確 |
| 外部API沒有資料 | 網路、第三方服務狀態、CORS、Network Response |
| `localhost:8080`連線失敗 | Spring Boot是否已啟動、Port是否正確 |
| POST回400或415 | `JSON.stringify`、`contentType`及後端`@RequestBody`是否一致 |
| 前端被CORS阻擋 | Controller是否有`@CrossOrigin`或全域CORS設定 |

## 9. 本章檢查表

- [ ] 知道`<script src>`是下載並執行外部JavaScript，不等同Module `import`。
- [ ] `air.json`與`jquery_ajax.html`位於同一資料夾。
- [ ] 能區分`dataType`與`contentType`。
- [ ] 能以`$.ajax()`讀取本機、外部及自己的API。
- [ ] 能以GET取得資料、以POST傳送JSON。
- [ ] 能用`statusCode`及`error`處理失敗結果。
- [ ] 能用模板字串`${...}`將共用基礎網址與不同API端點組合成完整URL。
- [ ] 能區分204無內容、404找不到資源及500伺服器錯誤。
- [ ] 能用`xhr.status`與`xhr.responseText`讀取狀態碼及後端錯誤文字。
- [ ] 能建立`Product`、`ProductDAO`與`ProductController`並取得三筆初始資料。
- [ ] 能以`findById()`、`Optional<Product>`與`@PathVariable`查詢單筆商品並區分200／404。
- [ ] 能說明空商品List為何回204，以及老師ID計算寫法在空List時的風險。
- [ ] 測試自己的API前已先啟動Spring Boot後端。
