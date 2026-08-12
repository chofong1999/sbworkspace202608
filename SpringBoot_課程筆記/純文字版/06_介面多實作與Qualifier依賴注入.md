# Spring Boot 學習筆記 06：介面、多實作與 Qualifier 依賴注入

- 整理日期：2026-08-06
- 範例專案：`sbfirstapp`
- 原始碼位置：`C:\sbworkspace202608\sbfirstapp\src\main\java\com\example\demo`

## 0. 前置條件與重現步驟

1. 先完成第2章，並確認`sbfirstapp`已加入Spring Web。
2. 在`com.example.demo`底下建立`service`與`controller`package。
3. 依本章第3～5節建立介面與兩個Service實作。
4. 依第8節建立Controller。
5. 啟動主要類別後，依第9節測試兩個網址。

兩個網址分別顯示Email與簡訊文字，才代表兩個Bean與`@Qualifier`對應成功。

## 1. 本章範例的目的

同一種通知功能可能有不同傳送方式，例如：

- 電子郵件通知
- 簡訊通知

兩者都屬於「通知服務」，所以先定義共同介面，再建立不同實作。Controller 不直接建立物件，而是讓 Spring 注入需要的 Service。

本章涉及：

- Java 介面與多型
- `@Service`與 Spring Bean
- 建構子注入
- 同一介面的多個 Bean
- `@Qualifier`指定注入對象

## 2. 專案結構

需要建立的檔案為：

```text
com.example.demo
├─ controller
│  └─ NotificationController.java
└─ service
   ├─ NotificationService.java
   ├─ EmailNotificationService.java
   └─ SmsNotificationService.java
```

## 3. 定義共用介面

`NotificationService.java`：

```java
package com.example.demo.service;

public interface NotificationService {
    String sendNotification(String message);
}
```

介面只定義「通知服務必須具備什麼功能」，不決定 Email 或 SMS 要如何處理。

好處：

- Controller 依賴抽象介面，不綁死特定實作。
- 未來可新增 LINE、Push Notification 等實作。
- 不同實作可以透過相同方法呼叫。
- 較容易測試與替換實作。

## 4. Email 實作

`EmailNotificationService.java`：

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    @Override
    public String sendNotification(String message) {
        return "Email 已發送: " + message;
    }
}
```

重點：

- `implements NotificationService`表示實作通知介面。
- `@Service`讓 Spring 掃描並建立 Bean。
- 預設 Bean 名稱是類別名稱首字母小寫：`emailNotificationService`。

## 5. SMS 實作

`SmsNotificationService.java`：

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService implements NotificationService {

    @Override
    public String sendNotification(String message) {
        return "簡訊已發送: " + message;
    }
}
```

這個類別也是`NotificationService`型別的 Bean，預設名稱為：

`smsNotificationService`

此時 Spring 容器中共有兩個`NotificationService`：

```text
NotificationService
├─ emailNotificationService
└─ smsNotificationService
```

## 6. 為什麼需要 Qualifier？

若只有這樣注入：

```java
public NotificationController(NotificationService service) {
    // ...
}
```

Spring 會發現`NotificationService`有兩個候選 Bean，無法判斷應注入 Email 還是 SMS，通常會產生`NoUniqueBeanDefinitionException`。

因此使用`@Qualifier`指定 Bean 名稱：

```java
public NotificationController(
        @Qualifier("emailNotificationService") NotificationService emailService,
        @Qualifier("smsNotificationService") NotificationService smsService) {
    this.emailService = emailService;
    this.smsService = smsService;
}
```

對應關係：

| Qualifier | 注入實作 | 儲存欄位 |
|---|---|---|
| `emailNotificationService` | `EmailNotificationService` | `emailService` |
| `smsNotificationService` | `SmsNotificationService` | `smsService` |

## 7. 建構子注入

Controller 使用：

```java
private final NotificationService emailService;
private final NotificationService smsService;
```

再透過建構子接收依賴。這稱為建構子注入。

優點：

- `final`欄位建立後不能被任意更換。
- 必要依賴會明確出現在建構子中。
- 容易撰寫單元測試，可自行傳入替代實作。
- 單一建構子在 Spring 中不需要額外加`@Autowired`。

## 8. 完整 Controller

`NotificationController.java`：

```java
package com.example.demo.controller;

import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService emailService;
    private final NotificationService smsService;

    public NotificationController(
            @Qualifier("emailNotificationService") NotificationService emailService,
            @Qualifier("smsNotificationService") NotificationService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    @GetMapping("/email")
    public String sendEmail() {
        return emailService.sendNotification("這是一封電子郵件");
    }

    @GetMapping("/sms")
    public String sendSms() {
        return smsService.sendNotification("這是一條簡訊");
    }
}
```

## 9. API 路徑與預期結果

Controller 類別層級路徑：

```java
@RequestMapping("/api/notification")
```

### Email API

網址：

`http://localhost:8080/api/notification/email`

呼叫流程：

```text
NotificationController.sendEmail()
→ emailService.sendNotification(...)
→ EmailNotificationService
```

預期回傳：

```text
Email 已發送: 這是一封電子郵件
```

### SMS API

網址：

`http://localhost:8080/api/notification/sms`

呼叫流程：

```text
NotificationController.sendSms()
→ smsService.sendNotification(...)
→ SmsNotificationService
```

預期回傳：

```text
簡訊已發送: 這是一條簡訊
```

## 10. Qualifier 與 Primary 的差異

- `@Qualifier`：注入時明確指定某個 Bean。這個範例同時需要 Email 與 SMS，所以適合使用。
- `@Primary`：把某個實作設成預設候選者，適合大部分地方使用同一個實作時。

如果 Controller 同時要使用兩個不同實作，即使其中一個加了`@Primary`，仍建議用`@Qualifier`清楚標示兩個參數的用途。

## 檢查表

- [ ] 建立`NotificationService`介面
- [ ] Email 與 SMS Service 都實作介面
- [ ] 兩個實作類別都有`@Service`
- [ ] Bean 名稱與`@Qualifier`字串完全一致
- [ ] Controller 使用建構子注入
- [ ] 依賴欄位使用`private final`
- [ ] 測試`/api/notification/email`
- [ ] 測試`/api/notification/sms`
