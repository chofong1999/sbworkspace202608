# Thymeleaf模板語法

[返回字典首頁](README.md)｜[快速索引](00_快速索引.md)

模板通常放在`src/main/resources/templates/`，由`@Controller`方法回傳模板名稱；直接以檔案方式開啟HTML不會執行Thymeleaf。

<a id="page-syntax"></a>
## 本頁全部語法速查

| 語法 | 一句用途 | 最短寫法 | 詳細 |
|---|---|---|---|
| `${...}` | 取得Model／Context變數 | `${user.name}` | [說明](#variable-expression) |
| `*{...}` | 取得目前`th:object`欄位 | `*{name}` | [說明](#selection-expression) |
| `@{...}` | 產生Context Path安全網址 | `@{/users}` | [說明](#link-expression) |
| `#{...}` | 取得國際化訊息 | `#{home.title}` | [總表](#expressions) |
| `~{...}` | 引用模板Fragment | `~{layout::header}` | [總表](#expressions) |
| `th:text` | escaping後取代元素內容 | `th:text="${name}"` | [說明](#text-utext) |
| `th:utext` | 不escaping地取代元素內容 | `th:utext="${html}"` | [風險](#text-utext) |
| `th:each` | 迭代集合並重複元素 | `th:each="u : ${users}"` | [說明](#flow-control) |
| `th:if` | 條件為真時保留元素 | `th:if="${isLogin}"` | [說明](#flow-control) |
| `th:switch`／`th:case` | 依值選擇分支 | `th:case="'admin'"` | [說明](#flow-control) |
| `th:object` | 選定表單後端物件 | `th:object="${user}"` | [表單](#form-binding) |
| `th:field` | 綁定表單欄位 | `th:field="*{name}"` | [表單](#form-binding) |
| `th:action` | 產生表單送出網址 | `th:action="@{/users}"` | [表單](#form-binding) |
| `th:href` | 產生連結網址 | `th:href="@{/users}"` | [網址](#each-href-src) |
| `th:src` | 產生圖片／資源網址 | `th:src="@{/images/a.png}"` | [網址](#each-href-src) |
| `條件 ? A : B` | 在表達式中二選一 | `${isEdit} ? '更新' : '建立'` | [表單案例](#form-binding) |

<a id="expressions"></a>
## 標準表達式總表

| 語法 | 正式類型 | 取得來源 | 常見位置 |
|---|---|---|---|
| `${...}` | Variable Expression | Model、Context、request/session等可見變數 | 多數`th:*`屬性 |
| `*{...}` | Selection Expression | 最近的`th:object`所選物件 | 表單`th:field`、區塊內欄位 |
| `@{...}` | Link URL Expression | 產生網址並處理Context Path與參數 | `th:href`、`th:src`、`th:action` |
| `#{...}` | Message Expression | `messages.properties`國際化訊息 | `th:text`等 |
| `~{...}` | Fragment Expression | 模板片段 | `th:insert`、`th:replace` |

這些是Thymeleaf表達式，不是Java字串，也不是JSP EL／scriptlet。它們只在Thymeleaf處理模板的語境中成立。

<a id="variable-expression"></a>
## `${...}` Variable Expression

```html
<p th:text="${user.name}">預覽姓名</p>
<p th:text="${isLogin} ? '歡迎' : '請登入'">狀態</p>
```

**使用條件**：變數必須存在於Thymeleaf Context，例如Controller先執行`model.addAttribute("user", user)`。可存取屬性、呼叫允許的方法、做條件與運算；不是只能當字串。

<a id="selection-expression"></a>
## `*{...}` Selection Expression

```html
<form th:object="${user}" method="post">
  <input th:field="*{name}">
  <input th:field="*{email}">
</form>
```

**使用條件**：外層需先有`th:object`。`*{name}`相當於從目前選取的`user`取`name`；沒有選取物件時不應用它代替`${user.name}`。

<a id="link-expression"></a>
## `@{...}` Link URL Expression

```html
<a th:href="@{/web/users}">列表</a>
<a th:href="@{/web/users/{id}(id=${user.id})}">詳情</a>
<img th:src="@{/images/logo.png}">
<form th:action="@{/web/users/create}" method="post">
```

**可使用位置**：只要該Thymeleaf屬性需要URL值即可，最常見是`th:href`、`th:src`、`th:action`；也能先計算後用於其他Thymeleaf表達式，不是HTML尖括號本身的語法。

**URL組成規則**：

- `@{/users}`：以應用程式Context Path為基準的網址。
- `{id}`：URL樣板中的路徑變數位置，只在該URL樣板內有意義。
- `(id=${user.id})`：替換同名`{id}`；若網址沒有`{id}`，通常產生查詢參數。
- 多參數：`@{/search(keyword=${q},page=${page})}`。

<a id="text-utext"></a>
## `th:text`與`th:utext`

```html
<h1 th:text="${greeting}">Greeting：</h1>
<div th:utext="${htmlContent}">HTML內容</div>
```

| 屬性 | 行為 | 使用時機 |
|---|---|---|
| `th:text` | 取代表籤內全部內容，並做HTML escaping | 一般文字，預設選擇 |
| `th:utext` | 取代表籤內全部內容，不escaping | 內容確定可信且確實要輸出HTML |

`Greeting：`是靜態預覽內容，不是`${greeting}`的預設值。只要Thymeleaf執行`th:text`，原內容就會被取代；若表達式是空值，畫面就呈現空白。若要保留前綴：

```html
<h1>Greeting：<span th:text="${greeting}">預覽值</span></h1>
```

<a id="flow-control"></a>
## `th:each`、`th:if`、`th:switch`

```html
<tr th:each="user, stat : ${users}">
  <td th:text="${stat.count}"></td>
  <td th:text="${user.name}"></td>
</tr>

<p th:if="${isLogin}">歡迎回來</p>

<div th:switch="${role}">
  <p th:case="'admin'">管理員</p>
  <p th:case="*">一般使用者</p>
</div>
```

`stat`常用屬性：`index`（從0）、`count`（從1）、`size`、`first`、`last`、`even`、`odd`。

<a id="form-binding"></a>
## `th:object`、`th:field`與`th:action`

```html
<form th:action="${isEdit} ? @{/web/users/{id}/edit(id=${user.id})} : @{/web/users/create}"
      th:object="${user}" method="post">
  <input th:field="*{name}" required>
  <button type="submit" th:text="${isEdit} ? '更新' : '建立'">送出</button>
</form>
```

- `th:object`選定表單後端物件。
- `th:field="*{name}"`會協調`name`、`id`、目前值與綁定狀態。
- `th:action`產生送出網址。
- `條件 ? A : B`是條件運算式；A、B可各自是URL Expression。

<a id="each-href-src"></a>
## `th:href`與`th:src`

```html
<a th:href="@{/web/users/{id}(id=${user.id})}">詳情</a>
<img th:src="@{/images/{file}(file=${imageName})}" alt="圖片">
```

處理後，Thymeleaf會產生真正的HTML `href`／`src`。靜態預覽需要fallback時，可同時保留原生屬性：

```html
<img src="../static/images/sample.png"
     th:src="@{/images/sample.png}" alt="範例">
```

深入說明：[Thymeleaf表達式與安全輸出](延伸閱讀/Thymeleaf表達式與安全輸出.md)。官方參考：[Using Thymeleaf 3.1](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
