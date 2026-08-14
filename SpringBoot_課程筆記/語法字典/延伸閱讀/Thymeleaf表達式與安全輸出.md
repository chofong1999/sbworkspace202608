# Thymeleaf表達式與安全輸出

[返回Thymeleaf字典](../06_Thymeleaf模板語法.md)

## 語法只在模板處理時成立

`${...}`、`*{...}`、`@{...}`等由Thymeleaf Template Engine解讀。它們可以出現在HTML的`th:*`屬性值或Thymeleaf支援的inline expression中，但不是瀏覽器原生HTML語法。

因此：

- 直接以檔案方式開啟模板：瀏覽器忽略`th:*`，只顯示靜態fallback內容。
- 經Controller回傳模板：Thymeleaf處理後，瀏覽器收到已完成替換的普通HTML。
- 放在`src/main/resources/static/`：通常當靜態檔直接送出，不會走模板引擎。

## `th:text`為何讓原內容消失

`th:text`的職責是設定元素body，不是把值附加到原文字後面：

```html
<h1 th:text="${greeting}">Greeting：</h1>
```

有值時整個`Greeting：`被值取代；無值時body被設成空。要保留固定文字，應把動態範圍縮到子元素：

```html
<h1>Greeting：<span th:text="${greeting}">預覽值</span></h1>
```

## `th:text`與XSS

`th:text`會escaping，使用者輸入的`<script>`會顯示成文字，不會當標籤執行。`th:utext`不escaping，只有在內容已可信或經可靠HTML sanitizer處理時使用。

「內容來自自己的資料庫」不代表可信；只要最初可能由使用者輸入，仍可能含惡意HTML。

## URL Expression的邊界

```html
<a th:href="@{/users/{id}(id=${user.id},tab='profile')}">詳情</a>
```

- `@{...}`負責產生URL。
- `${user.id}`負責取得變數值。
- `{id}`只在目前URL樣板中標示替換位置。
- 沒有對應路徑變數的`tab`會成為query parameter。

不同表達式可以巢狀組合，因為它們各自負責不同階段，不應把整段只稱為「EL語法」。

## Inline Expression

```html
<p>歡迎，[[${user.name}]]！</p>
```

`[[...]]`相當於escaped文字輸出；`[(...)]`為unescaped輸出。能用`th:text`時優先使用明確的HTML元素範圍，inline適合句子中只插入一小段值。

官方參考：[Using Thymeleaf 3.1](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)

