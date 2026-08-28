# 練習資料來源字典

這份字典整理課程中可拿來練習資料擷取與畫面呈現的資料來源。內容分成兩種，避免把補充資料誤認成老師範例：

- **課堂實際使用**：能在 `sbworkspace202608`、`workspace202607` 或 `jscode` 的課程程式中找到直接證據。
- **額外練習補充**：同一服務提供、可繼續練習的資料，但目前三個資料夾中沒有老師使用它的證據。

JavaScript、Fetch、jQuery AJAX 與 React 語法由[語法字典](../語法字典/README.md)負責；本區只回答「去哪裡取得練習資料」。

## 課堂實際使用的資料

| 資料內容 | 來源 | 課堂實際出現的網址或路徑 | 原始範例 |
|---|---|---|---|
| 單一使用者 | [JSONPlaceholder](01_JSONPlaceholder.md#課堂實際使用) | `https://jsonplaceholder.typicode.com/users/{id}` | `jscode/day4/fetch.html` |
| 使用者清單、單一使用者 | [JSONPlaceholder](01_JSONPlaceholder.md#課堂實際使用) | `/users`、`/users/{id}` | `jscode/react-day1/src/components/UserList.jsx` |
| 單篇文章 | [JSONPlaceholder](01_JSONPlaceholder.md#課堂實際使用) | `/posts/{postId}` | `jscode/react-day1/src/components/PostDetail.jsx` |
| 商品清單 | [Fake Store API](02_Fake_Store_API.md#課堂實際使用) | `https://fakestoreapi.com/products` | `jscode/day4/jquery_fakestore.html` |
| 商品詳情 | [Fake Store API](02_Fake_Store_API.md#課堂實際使用) | `/products/{id}` | `jscode/react-routing-app/src/components/FakeProductDetail.jsx` |
| 空氣品質陣列 | [課堂本機 JSON](03_課堂本機JSON資料.md) | `air.json` | `jscode/day4/jquery_ajax.html` |

表格中的 `{id}`、`{postId}` 是要換成實際數字的位置，不是網址中的固定文字。

## 額外練習補充

以下資料可以使用，但**目前沒有證據顯示老師曾在三個指定資料夾的範例中使用**：

| 額外資料 | 所屬服務 | 可練習內容 |
|---|---|---|
| 留言、待辦、相簿、照片 | [JSONPlaceholder](01_JSONPlaceholder.md#額外練習補充非老師範例) | 關聯查詢、布林篩選、圖片列表 |
| 文章清單與 Query 篩選 | [JSONPlaceholder](01_JSONPlaceholder.md#額外練習補充非老師範例) | 列表、作者篩選、留言關聯 |
| 購物車、商店使用者、登入 | [Fake Store API](02_Fake_Store_API.md#額外練習補充非老師範例) | 關聯資料、巢狀物件、登入 Request |
| 商品分類、排序、限制筆數 | [Fake Store API](02_Fake_Store_API.md#額外練習補充非老師範例) | Query、分類選單、排序 |

## 使用前必讀

1. 這些都是練習用假資料，不可當成正式會員、商品或營運資料。
2. 公開 API 需要網路，也可能暫時維護、限流或改版；失敗時先檢查 HTTP 狀態碼與瀏覽器 Network。
3. 公開練習 API 的寫入操作不一定永久保存；不能只因收到成功 Response 就認定資料已真正寫入。
4. API 回傳欄位應以當次實際 JSON 為準。
5. 不同資料來源的 ID 沒有共同意義，不能把兩個服務的 `id=1` 當成同一筆資料。

## 搭配的語法入口

- [Fetch、async／await 與 HTTP 請求](../語法字典/12_DOM_BOM表單與Fetch.md)
- [jQuery `$.ajax()`](../語法字典/13_jQuery與AJAX.md)
- [React `useEffect` 載入 API](../語法字典/14_React_Vite_JSX與Hooks.md)
- [JavaScript Array 與 Object](../語法字典/11_JavaScript核心_陣列與物件.md)
