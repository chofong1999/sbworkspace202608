# Spring Boot 學習筆記 03：Maven 打包與執行 JAR

- 範例專案：`sbfirstapp`
- 範例專案名稱：`sbfirstapp`

> 語法速查：[Maven與建置](../語法字典/09_設定檔與建置.md#maven-goals)

## 本章快速索引

- [0. 前置條件與完成結果](#0-前置條件與完成結果)
- [1. 使用 Eclipse 執行 Maven install](#1-使用-eclipse-執行-maven-install)
- [2. package 與 install 的差異](#2-package-與-install-的差異)
- [3. target 資料夾的打包產物](#3-target-資料夾的打包產物)
- [4. 從命令列執行 JAR](#4-從命令列執行-jar)
- [5. 案例：第二份程式啟動失敗](#5-案例第二份程式啟動失敗)
- [6. 解決 8080 連接埠衝突](#6-解決-8080-連接埠衝突)
- [7. Java 版本補充](#7-java-版本補充)
- [檢查表](#檢查表)

## 0. 前置條件與完成結果

- 先完成第2章，並確認專案可在Eclipse中啟動。
- 開啟命令提示字元；本章指令使用Windows CMD語法。
- 完成結果是`target`中產生Spring Boot可執行JAR，而且能用`java -jar`啟動。

## 1. 使用 Eclipse 執行 Maven install

在 `Project Explorer` 對專案按右鍵：

`Run As → Maven install`

`Maven install`會依序執行 Maven 生命週期直到`install`階段，主要包含：

1. 編譯程式
2. 執行測試
3. 將專案打包成 JAR
4. 把成品安裝到本機 Maven Repository

若只需要產生 JAR，不需要安裝到本機 Repository，也可以使用`Maven package`。

## 2. package 與 install 的差異

| 指令／階段 | 作用 |
|---|---|
| `mvn clean package` | 清除舊產物、編譯、測試並在`target`產生 JAR |
| `mvn clean install` | 完成`package`的工作後，再把成品放入本機 Maven Repository |
| Eclipse 的`Maven install` | 相當於從 Eclipse 執行 Maven 的`install`階段 |

本專案已附 Maven Wrapper，在 Windows 命令提示字元中可使用：

```bat
cd /d <專案資料夾>
mvnw.cmd clean package
```

使用 Wrapper 的好處是不用另外依賴系統是否已安裝`mvn`。

## 3. target 資料夾的打包產物

打包完成後，本專案的`target`中包含：

```text
target
├─ classes
├─ generated-sources
├─ generated-test-sources
├─ maven-archiver
├─ maven-status
├─ surefire-reports
├─ test-classes
├─ sbfirstapp-0.0.1-SNAPSHOT.jar
└─ sbfirstapp-0.0.1-SNAPSHOT.jar.original
```

兩個 JAR 的差異：

- `sbfirstapp-0.0.1-SNAPSHOT.jar`：Spring Boot Maven Plugin 重新封裝後的可執行 JAR，包含啟動所需結構與相依套件，執行時使用這個。
- `sbfirstapp-0.0.1-SNAPSHOT.jar.original`：重新封裝前的原始 JAR，通常不直接使用`java -jar`啟動 Spring Boot。

## 4. 從命令列執行 JAR

### 從專案根目錄執行

```bat
cd /d <專案資料夾>
java -jar target\sbfirstapp-0.0.1-SNAPSHOT.jar
```

### 已經位於 target 資料夾

```bat
cd /d <專案資料夾>\target
java -jar sbfirstapp-0.0.1-SNAPSHOT.jar
```

`cd /d`可以同時切換磁碟機與資料夾。Windows CMD 中不能只輸入`..`切換上層，必須使用：

```bat
cd ..
```

## 5. 案例：第二份程式啟動失敗

命令列畫面顯示：

- Spring Boot 已開始載入
- 使用 Java 21.0.10 執行
- Tomcat 嘗試在`8080`啟動
- 最後出現`APPLICATION FAILED TO START`

`application.properties`目前內容為：

```properties
spring.application.name=sbfirstapp
#server.port=8000
```

`#server.port=8000`前面有`#`，所以這一行是註解，不會生效；程式仍使用預設連接埠`8080`。

若 Eclipse 已經執行一份 Spring Boot 應用程式並占用8080，接著又從命令列啟動第二份JAR，就會發生連接埠衝突。這個判斷必須再以錯誤最下方的Description或連接埠查詢結果確認。

## 6. 解決 8080 連接埠衝突

可選擇其中一種方式：

### 方法一：停止原本的程式

在 Eclipse Console 按紅色停止按鈕，確認原本的 Spring Boot 程式已停止，再重新執行 JAR。

### 方法二：改用 8000

將`application.properties`改成：

```properties
spring.application.name=sbfirstapp
server.port=8000
```

注意要移除`#`，然後重新打包並執行。

### 方法三：啟動時暫時指定連接埠

不修改設定檔，直接執行：

```bat
java -jar target\sbfirstapp-0.0.1-SNAPSHOT.jar --server.port=8000
```

### 查詢 8080 是否被占用

```bat
netstat -ano | findstr :8080
```

> 只有`APPLICATION FAILED TO START`不足以斷定是port衝突。若Description含`Port 8080 was already in use`，或`netstat`顯示其他程序正在監聽8080，才能確認這個原因。

## 7. Java 版本補充

範例`pom.xml`的編譯目標是Java 17，而啟動Log中的執行環境是Java 21.0.10。Java 21可以執行以Java 17為目標編譯的程式，因此這項版本組合本身不是啟動失敗的原因。

若要讓專案設定也統一為 Java 21，仍需依第二章修改`pom.xml`與 Eclipse 的 JRE System Library。

## 檢查表

- [ ] 使用`Maven package`或`Maven install`成功完成打包
- [ ] `target`中出現`sbfirstapp-0.0.1-SNAPSHOT.jar`
- [ ] 執行的是一般`.jar`，不是`.jar.original`
- [ ] 命令列位於正確資料夾，或使用正確的 JAR 相對路徑
- [ ] 啟動前確認 8080 沒有被其他程式占用
- [ ] 若改用 8000，確認`server.port=8000`前面沒有`#`
