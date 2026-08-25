# 第11章延伸閱讀：Thymeleaf表達式與常見疑難

## 本頁快速索引

- [1. `th:text`為何會讓原本的`Greeting：`消失？](#1-thtext為何會讓原本的greeting消失)
- [2. Thymeleaf Standard Expression不是單一種語法](#2-thymeleaf-standard-expression不是單一種語法)
- [3. `@{...}`可以放在哪裡？](#3-可以放在哪裡)
- [4. `*{...}`何時才成立？](#4-何時才成立)
- [5. Model與Session不只是兩種傳值寫法](#5-model與session不只是兩種傳值寫法)
- [6. View Name與Redirect為何都寫在`return`後面？](#6-view-name與redirect為何都寫在return後面)
- [7. 編輯器中的色塊不是程式碼](#7-編輯器中的色塊不是程式碼)
- [8. 快速選擇表](#8-快速選擇表)
- [9. 延伸閱讀完成判定](#9-延伸閱讀完成判定)

## 1. `th:text`為何會讓原本的`Greeting：`消失？

### 定義

`th:text`是Thymeleaf的元素內容處理器。模板經Thymeleaf渲染時，它會：

1. 計算屬性中的表達式。
2. 將結果轉成文字並進行HTML escaping。
3. **以結果取代該元素原有的全部內容。**

因此下面的`Greeting：`只是沒有經過Thymeleaf時可見的靜態原型文字，不是前綴，也不是表達式沒有值時的fallback：

```html
<h1 th:text="${greeting}">
    Greeting：
</h1>
```

### 三種執行情況

| 開啟方式或資料狀態 | Thymeleaf是否執行 | `<h1>`最後內容 |
|---|---:|---|
| Controller提供`greeting="Good Morning"`後回傳模板 | 是 | `Good Morning` |
| Controller回傳模板，但Model沒有`greeting` | 是 | 空內容 |
| 直接把模板當一般HTML開啟 | 否 | `Greeting：` |

### 需要保留固定文字時

把固定文字放在不會被`th:text`取代的外層，動態值放到子元素：

```html
<h1>Greeting：<span th:text="${greeting}"></span></h1>
```

也可以使用文字模板：

```html
<h1 th:text="|Greeting：${greeting}|">Greeting</h1>
```

前者適合固定文字與動態值需要不同標籤或樣式；後者適合合成一段純文字。

## 2. Thymeleaf Standard Expression不是單一種語法

`th:text`、`th:href`、`th:action`等`th:*`屬性是Thymeleaf處理器；屬性值中則可以放不同種類的Standard Expression。兩者是不同層級。

| 寫法 | 正式種類 | 主要輸入 | 求值結果 | 典型使用條件 |
|---|---|---|---|---|
| `${...}` | Variable Expression | Model、Session等Context資料 | 物件、數字、字串、布林值等 | 需要讀取資料或進行條件判斷 |
| `*{...}` | Selection Variable Expression | 最近一層`th:object`選定的物件 | 該物件的屬性值 | 表單欄位綁定或集中存取同一物件 |
| `@{...}` | Link URL Expression | 路徑及參數 | 經Context Path處理的URL字串 | 產生連結、圖片網址或表單提交網址 |
| `#{...}` | Message Expression | 訊息資源檔中的key | 國際化後的文字 | 多語系訊息；本範例專案尚未使用 |
| `|...|` | Literal Substitution | 固定文字與其他表達式 | 合成後的字串 | 一段文字中同時包含固定與動態內容 |

`${...}`的結果不一定是字串。例如`${user.age}`可能得到整數，`${isEdit}`可能得到布林值，`${users}`可能得到List。是否轉成文字，取決於外層處理器；`th:text`會把結果轉成文字，`th:if`則把結果當條件判斷。

Thymeleaf的`${...}`外觀與JSP EL相似，但本章是在Thymeleaf模板引擎中求值，不能把JSP的標籤、作用域規則或Scriptlet直接套用到這裡。

## 3. `@{...}`可以放在哪裡？

### 成立條件

`@{...}`必須出現在**會由Thymeleaf解析的模板表達式位置**。最常見的是`th:href`、`th:src`與`th:action`：

```html
<a th:href="@{/web/users}">使用者列表</a>
<img th:src="@{/images/mango.png}">
<form th:action="@{/web/users/create}" method="post">
```

重點不是「只能寫在HTML的尖括號內」，而是它必須位於Thymeleaf會處理的屬性或表達式環境。寫在一般文字、Java程式碼、純CSS檔或沒有`th:*`處理的普通`href`中，都不會由Thymeleaf求值：

```html
<!-- 不會解析；瀏覽器只會收到字面上的 @{...} -->
<a href="@{/web/users}">錯誤示範</a>
```

### 路徑參數與Query parameter

`{id}`本身不是可到處使用的獨立Expression。它是`@{...}`網址模板中的path placeholder，必須在同一個Link URL Expression內提供替換值：

```html
<a th:href="@{/web/users/{id}(id=${user.id})}">詳情</a>
```

若`user.id`為`abc`，結果為：

```text
/web/users/abc
```

若網址路徑沒有`{id}`，括號中的名稱／值會產生Query parameter：

```html
<a th:href="@{/user(id=${userId})}">使用者</a>
```

結果類似：

```text
/user?id=100
```

因此兩者的正式語法都屬於Link URL Expression；差別在於參數名稱是否對應路徑中的placeholder。

## 4. `*{...}`何時才成立？

`*{...}`會相對於最近的`th:object`求值。沒有選定物件時，就失去預期的相對資料來源。

```html
<form th:object="${user}">
    <input th:field="*{name}">
    <input th:field="*{email}">
</form>
```

在這個範圍內：

```text
*{name}  相當於從已選定的user讀取name
*{email} 相當於從已選定的user讀取email
```

`${user.name}`是從整個Context以名稱找`user`；`*{name}`則從目前選定物件找`name`。表單使用`th:object`配合`th:field`，可以同時處理欄位名稱、目前值及提交時的資料綁定。

## 5. Model與Session不只是兩種傳值寫法

兩者的差別是資料生命週期與可見範圍，不是語法長短。

| 儲存位置 | 寫入方式 | 通常可用多久 | 適合資料 |
|---|---|---|---|
| Model | `model.addAttribute("user", value)` | 目前這一次Request的View渲染 | 查詢結果、表單物件、單頁提示 |
| HttpSession | `session.setAttribute("user", value)` | 同一瀏覽器Session的多次Request | 登入身分、跨頁流程狀態 |

如果只是讓目前頁面顯示`John Lee`，Model比較直接：

```java
model.addAttribute("user", "John Lee");
return "session";
```

如果下一次Request仍要取得同一資料，才使用Session：

```java
session.setAttribute("user", "John Lee");
return "session";
```

模板可以用明確的Session命名空間讀取：

```html
<h1 th:text="${session['user']}">User</h1>
```

把整個`HttpSession`再放入Model通常沒有必要；Thymeleaf本來就能透過`session`內建物件讀取Session attribute。若Model與Session使用同一個attribute名稱，明確寫`${session['user']}`可避免讀者混淆資料來源。

## 6. View Name與Redirect為何都寫在`return`後面？

Controller方法的Java回傳型別都可以是`String`，但字串前綴決定Spring MVC如何解讀：

```java
return "user/form";
```

這是View Name。伺服器在同一次Request內解析`templates/user/form.html`並回傳HTML。

```java
return "redirect:/web/users/" + id;
```

這是Redirect指令。伺服器先回傳重新導向Response，瀏覽器再對新網址發出另一個Request。兩者不能只因為Java型別相同就視為相同用途。

## 7. 編輯器中的色塊不是程式碼

Eclipse可能在`#4CAF50`、`#dc3545`等CSS色碼旁顯示小色塊。這是編輯器的顏色預覽功能，不是HTML或CSS原始碼的一部分，也不會被傳到瀏覽器。

判斷方式：

1. 將游標移動到色塊附近，確認它不能像一般字元一樣選取。
2. 使用純文字檢視或Git diff，確認實際內容只有色碼。
3. 教師端沒有色塊、學習者端有色塊，通常只是Eclipse版本、外掛或偏好設定不同。

## 8. 快速選擇表

| 需求 | 使用方式 |
|---|---|
| 顯示Model中的值 | `th:text="${...}"` |
| 顯示可信任的HTML片段 | `th:utext="${...}"` |
| 保留固定前綴並加入動態值 | 外層固定文字＋子元素`th:text`，或`|...|` |
| 在表單中相對於同一物件取欄位 | `th:object`＋`*{...}` |
| 產生連結、圖片或表單網址 | `@{...}`放入相應`th:*`屬性 |
| 讓資料只服務目前頁面 | Model |
| 讓資料跨多次Request保留 | Session |
| 提交成功後避免重新整理重送POST | `redirect:`＋必要時使用Flash Attribute |

## 9. 延伸閱讀完成判定

- [ ] 能說明`th:text`會取代元素全部內容，而不是把值附加在原文字後面
- [ ] 能區分`th:*`處理器與`${...}`、`*{...}`、`@{...}`等Expression
- [ ] 知道`${...}`結果不一定是字串
- [ ] 知道`@{...}`必須由Thymeleaf解析，普通`href`不會處理它
- [ ] 知道`{id}`只在Link URL Expression的網址模板中充當placeholder
- [ ] 能依資料生命週期選擇Model或Session
- [ ] 能區分View Name與`redirect:`
