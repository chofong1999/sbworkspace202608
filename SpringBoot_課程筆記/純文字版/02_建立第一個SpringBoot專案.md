# Spring Boot 學習筆記 02：建立第一個專案

- 整理日期：2026-08-06
- 範例專案名稱：`sbfirstapp`

## 0. 前置條件與完成結果

- 先完成第1章，確認 Eclipse 已使用JDK 21，而且`Spring Starter Project`精靈可用。
- 本章完成結果是建立可執行的Maven／Jar Spring Boot專案`sbfirstapp`，包含DevTools與Spring Web，並能由主要類別啟動。

## 1. 開啟 Spring Starter Project

進入：

`File → New → Other...`

在建立精靈中展開 `Spring Boot`，選擇：

`Spring Starter Project`

按下 `Next`。

## 2. 填寫專案基本資料

在 `New Spring Starter Project` 畫面設定：

| 欄位 | 設定值 | 說明 |
|---|---|---|
| Service URL | `https://start.spring.io` | Spring Initializr 服務 |
| Name | `sbfirstapp` | Eclipse 中的專案名稱 |
| Location | 使用預設位置 | 勾選 `Use default location` |
| Type | `Maven` | 使用 Maven 管理專案與相依套件 |
| Packaging | `Jar` | 專案封裝格式 |
| Java Version | `21` | 配合先前設定的 JDK 21 |
| Language | `Java` | 開發語言 |
| Group | `com.example` | 組織或專案群組識別 |
| Artifact | `sbfirstapp` | Maven 專案識別名稱 |
| Version | `0.0.1-SNAPSHOT` | 開發中的初始版本 |
| Package | `com.example.demo` | Java 基礎套件名稱 |

填寫完成後按 `Next`。

> 畫面中的 Java Version 實際顯示為 17，但本課程前面指定使用 JDK 21。因此建立新專案時應改選 `21`；如果已經用 17 建立完成，請依本章後面的「Java 版本差異與修正」處理。

## 3. 選擇 Spring Boot 版本與依賴

在`New Spring Starter Project Dependencies`畫面選擇範例使用的Spring Boot版本：

`Spring Boot 4.1.0`

接著加入以下依賴：

### Developer Tools

- `Spring Boot DevTools`

用途：提供開發階段的便利功能，例如程式變更後自動重新啟動應用程式。

### Web

- `Spring Web`

用途：建立 Web 應用程式、REST API，並提供 Spring MVC 與內嵌 Web Server 等功能。

右側 `Selected` 清單應包含：

- `Spring Boot DevTools`
- `Spring Web`

確認後按下 `Finish`，等待 Eclipse 下載依賴並建立專案。

## 4. 確認專案建立完成

建立完成後，`Project Explorer` 會出現 `sbfirstapp`，並可看到常用結構：

```text
sbfirstapp
├─ src/main/java
│  └─ com.example.demo
│     └─ SbfirstappApplication.java
├─ src/main/resources
├─ src/test/java
├─ JRE System Library [JavaSE-21]
├─ Maven Dependencies
├─ pom.xml
├─ mvnw
└─ mvnw.cmd
```

主要啟動類別為：

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SbfirstappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbfirstappApplication.class, args);
    }
}
```

重點：

- `@SpringBootApplication` 表示這是 Spring Boot 應用程式的主要設定與啟動類別。
- `main()` 是 Java 程式進入點。
- `SpringApplication.run(...)` 會啟動 Spring Boot 應用程式。
- `pom.xml` 保存 Maven 專案設定及依賴。

## 5. Java 版本差異與修正

既有範例的兩個地方仍顯示Java 17：

- 建立專案時的 `Java Version` 是 `17`。
- 專案建立後的 `JRE System Library` 是 `JavaSE-17`。

但本課程環境要求使用 Java 21，所以正確目標應為：

- Spring Starter Project 的 `Java Version`：`21`
- Eclipse 的 `JRE System Library`：`JavaSE-21`
- `pom.xml` 的 Java 版本：`21`

### 課堂範例專案的版本差異

課堂範例專案`C:\sbworkspace202608\sbfirstapp`保留了建立當時的設定：

- `pom.xml`：`<java.version>17</java.version>`
- Eclipse 畫面：`JRE System Library [JavaSE-17]`

因此可選擇兩條重現路線：新建專案時直接選Java 21，或先重現Java 17範例再依下列步驟升級。只有完成最後三項檢查後，才能判定Java 21調整成功。

若專案已經以 Java 17 建立，可依序修正：

1. 開啟 `pom.xml`。
2. 找到 Java 版本設定，確認內容為：

   ```xml
   <properties>
       <java.version>21</java.version>
   </properties>
   ```

3. 在專案上按右鍵，選擇 `Maven → Update Project...`。
4. 再到 `Project → Properties → Java Build Path → Libraries`，確認 `JRE System Library` 使用 JDK 21。
5. 若仍是 Java 17，選取 `JRE System Library → Edit`，改選已安裝的 JDK 21。
6. 最後確認 Project Explorer 顯示 `JRE System Library [JavaSE-21]`。

> Java Version、`pom.xml`與JRE System Library是三個需要一起核對的位置；只改其中一處不足以證明專案已完整切換至Java 21。

## 6. 執行方式：Java Application 或 Spring Boot App？

在主要啟動類別上按右鍵，進入`Run As`時會看到：

1. `Java Application`
2. `Spring Boot App`

兩者都會執行`main()`，並由`SpringApplication.run(...)`啟動同一個 Spring Boot 應用程式；不是兩套不同的程式。

| 選項 | 特性 | 適合情況 |
|---|---|---|
| `Java Application` | Eclipse 標準 Java 啟動方式，把類別視為一般 Java 主程式 | 沒安裝 Spring Tools、測試一般 Java `main()`，或需要排除 STS 啟動設定影響時 |
| `Spring Boot App` | Spring Tools 提供的 Spring Boot 專用啟動方式，較方便管理 Boot 啟動設定、Profiles、參數及執行狀態 | 平常開發與執行 Spring Boot 專案 |

本課程的`SbfirstappApplication`建議選：

`Run As → Spring Boot App`

對目前這個簡單專案而言，兩者通常都能啟動並在`localhost:8080`提供服務；主要差異是 Eclipse／Spring Tools 如何建立及管理啟動設定，而不是 Controller 的執行結果。

## 建立專案檢查表

- [ ] 選擇 `Spring Starter Project`
- [ ] 專案名稱及 Artifact 設為 `sbfirstapp`
- [ ] Type 選擇 `Maven`
- [ ] Packaging 選擇 `Jar`
- [ ] Java Version 改為 `21`
- [ ] 加入 `Spring Boot DevTools`
- [ ] 加入 `Spring Web`
- [ ] 按 `Finish` 並等待依賴下載完成
- [ ] Project Explorer 出現 `sbfirstapp`
- [ ] 找到 `SbfirstappApplication.java`
- [ ] JRE System Library 顯示 Java 21
- [ ] `pom.xml` 中的 `<java.version>` 為 `21`
