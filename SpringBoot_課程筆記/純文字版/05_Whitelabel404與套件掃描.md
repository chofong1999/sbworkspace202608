# Spring Boot 學習筆記 05：Whitelabel 404 與套件掃描

- 整理日期：2026-08-06
- 範例專案：`mysecondapp`
- 測試網址：`http://localhost:8080/api/hello`

## 1. 畫面中的錯誤

瀏覽器顯示：

```text
Whitelabel Error Page
There was an unexpected error (type=Not Found, status=404).
No static resource api/hello.
```

這代表：

- Spring Boot 應用程式有正常啟動，瀏覽器才能收到 Spring 產生的錯誤頁。
- HTTP 狀態是`404 Not Found`。
- Spring 找不到處理`/api/hello`的 Controller 映射。
- 找不到 Controller 後，請求又被當成靜態資源尋找，因此出現`No static resource api/hello`。

所以這不是「伺服器沒有啟動」，也不是單純的瀏覽器問題。

## 2. 實際原始碼結構

已直接檢查`mysecondapp`原始碼，目前結構為：

```text
src/main/java
└─ com/example
   ├─ mysecondapp
   │  └─ MysecondappApplication.java
   └─ demo/controller
      └─ HelloWorld.java
```

主要啟動類別的套件：

```java
package com.example.mysecondapp;
```

Controller 的套件：

```java
package com.example.demo.controller;
```

## 3. 根本原因：Component Scan 範圍

`@SpringBootApplication`預設會從主要啟動類別所在套件開始，掃描該套件及其子套件。

本專案的掃描範圍是：

```text
com.example.mysecondapp
└─ 其所有子套件
```

但`HelloWorld`位於：

```text
com.example.demo.controller
```

它不是`com.example.mysecondapp`的子套件，所以 Spring 沒有掃描到`@RestController`，也就沒有建立：

```text
GET /api/hello
```

## 4. 建議修正方式

將`HelloWorld.java`移到主要套件底下：

```text
src/main/java/com/example/mysecondapp/controller/HelloWorld.java
```

並把第一行改成：

```java
package com.example.mysecondapp.controller;
```

修正後完整程式可以整理為：

```java
package com.example.mysecondapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorld {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Demo Spring Boot";
    }
}
```

在 Eclipse 中建議使用重構移動：

1. 對`HelloWorld.java`按右鍵。
2. 選擇`Refactor → Move...`。
3. 目標 Package 選擇或建立`com.example.mysecondapp.controller`。
4. 完成後確認檔案的`package`宣告已同步修改。
5. 停止並重新啟動 Spring Boot 應用程式。
6. 再次開啟`http://localhost:8080/api/hello`。

預期結果：

```text
Hello Demo Spring Boot
```

## 5. 黃色警告：未使用的 import

目前`HelloWorld.java`還匯入了未使用的類別：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
```

這些不會造成目前的 404，但 Eclipse 會顯示黃色警告。因為目前程式沒有使用它們，可以移除，保留實際需要的：

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
```

可使用 Eclipse 的`Source → Organize Imports`自動整理匯入。

## 6. 其他做法與取捨

也可以在主要類別指定較大的掃描範圍：

```java
@SpringBootApplication(scanBasePackages = "com.example")
```

這樣可以掃描`com.example.demo.controller`，但對目前專案不建議優先採用。把`mysecondapp`的 Controller 放回`com.example.mysecondapp`底下，專案結構更清楚，也比較不容易意外掃描其他專案的元件。

## 404 排查順序

遇到類似問題時依序確認：

1. 應用程式是否真的啟動成功。
2. 網址與 HTTP Method 是否正確。
3. `@RequestMapping`與`@GetMapping`組合後的完整路徑。
4. Controller 是否有`@RestController`或`@Controller`。
5. Controller 是否位於主要啟動類別的套件或子套件。
6. 修改後是否已重新啟動應用程式。

## 檢查表

- [ ] 主類別位於`com.example.mysecondapp`
- [ ] Controller 移至`com.example.mysecondapp.controller`
- [ ] `package`宣告與資料夾結構一致
- [ ] 保留`@RestController`
- [ ] 路徑為`@RequestMapping("/api")`加`@GetMapping("/hello")`
- [ ] 移除未使用的 import
- [ ] 重新啟動後測試`http://localhost:8080/api/hello`
