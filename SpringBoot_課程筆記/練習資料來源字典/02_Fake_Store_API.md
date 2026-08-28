# Fake Store API：商品假資料

- 網站：<https://fakestoreapi.com/>
- Base URL：`https://fakestoreapi.com`
- 特性：免 API Key、回傳 JSON，適合商品清單與詳情頁。
- 本頁標記原則：先列課堂實際使用，再列額外可用但未在課堂範例中出現的端點。

## 課堂實際使用

### 快速網址

| 用途 | 寫法 | 課堂程式證據 |
|---|---|---|
| 全部商品 | `https://fakestoreapi.com/products` | `jscode/day4/jquery_fakestore.html` |
| 指定商品 | `https://fakestoreapi.com/products/{id}` | `jscode/react-routing-app/src/components/FakeProductDetail.jsx` |

商品清單範例會使用 `id`、`title`、`price`、`image`；商品詳情範例依 React Router 的網址參數取得 `id`：

```javascript
fetch(`https://fakestoreapi.com/products/${id}`)
```

課程對照：

- [第27章：jQuery AJAX、JSON 資料來源與 HTTP 狀態處理](../純文字版/27_jQuery_AJAX_JSON資料來源與HTTP狀態處理.md)
- [第38章：React Router 導覽、動態路由與 404](../純文字版/38_React_Router導覽_動態路由與404.md)

### 課堂另有使用商品圖片網址

`jscode/day2/createobj.html` 的物件範例使用過 Fake Store API 網域下的一張商品圖片。它是圖片資源，不是資料 API：

```text
https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_t.png
```

## 額外練習補充（非老師範例）

下列端點屬於 Fake Store API，但在這次查核的三個資料夾中，**沒有找到老師範例直接使用的證據**。

### 商品延伸操作

```text
https://fakestoreapi.com/products?limit=5
https://fakestoreapi.com/products?sort=desc
https://fakestoreapi.com/products/categories
https://fakestoreapi.com/products/category/jewelery
```

### 購物車

```text
https://fakestoreapi.com/carts
https://fakestoreapi.com/carts/1
https://fakestoreapi.com/carts/user/1
```

### 商店使用者與登入

```text
https://fakestoreapi.com/users
https://fakestoreapi.com/users/1
POST https://fakestoreapi.com/auth/login
```

購物車的商品通常只含商品 ID 與數量；若要顯示名稱、價格和圖片，還要再取得商品資料。使用者資料中的帳號與密碼都是假資料，不要把真實密碼放進測試程式或 Git。

## 寫入操作限制

這類公開假 API 適合練習 HTTP Method、Request Body 與狀態處理；不能用來證明重新整理後資料仍存在，也不代表正式會員、訂單或 Token 安全設計已完成。
