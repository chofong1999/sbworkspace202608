# Spring MVC參數綁定選擇

## 本頁快速索引

- [先依HTTP位置選語法](#先依http位置選語法)
- [一個Request Body的限制](#一個request-body的限制)
- [Entity與DTO的選擇](#entity與dto的選擇)
- [綁定失敗的判斷順序](#綁定失敗的判斷順序)

## 先依HTTP位置選語法

同一個值能被放在不同位置，但API契約不同：

- `/users/42`：`42`識別特定資源，適合`@PathVariable`。
- `/users?role=admin`：`role`是篩選或選項，適合`@RequestParam`。
- JSON物件：結構化資料，適合`@RequestBody DTO`。
- 傳統瀏覽器表單：適合`@ModelAttribute`。
- 認證、追蹤資訊：通常來自`@RequestHeader`。

不要因為某個註解比較熟就任意改資料位置，否則前端、Swagger與測試都會不一致。

## 一個Request Body的限制

HTTP只有一個request body，因此Controller通常只放一個`@RequestBody`。需要多組欄位時建立包裝DTO：

```java
public record CreateOrderRequest(
    Long userId,
    List<OrderItemRequest> items
) {}
```

檔案上傳則使用`multipart/form-data`及`@RequestPart`，不是把二進位檔硬塞進一般JSON。

## Entity與DTO的選擇

練習可直接把JSON綁到Entity，但正式API使用DTO可避免：

- 客戶端修改不應公開的欄位。
- Entity關聯被意外載入／序列化。
- 資料庫欄位調整直接破壞API。
- 驗證規則無法依不同操作區分。

## 綁定失敗的判斷順序

1. 路由和HTTP method是否正確。
2. `Content-Type`是否與送出格式一致。
3. 參數實際位於path、query、header、form或body哪裡。
4. 名稱是否一致。
5. 字串能否轉成目標Java型別。
6. JSON能否建立DTO、欄位型別是否相容。
7. 是否由`@Valid`拒絕，而不是綁定本身失敗。

