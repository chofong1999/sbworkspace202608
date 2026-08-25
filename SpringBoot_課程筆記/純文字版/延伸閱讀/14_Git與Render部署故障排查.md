# 第14章延伸閱讀：Git與Render部署故障排查

## 本頁快速索引

- [0. 使用條件](#0-使用條件)
- [1. Git Push為何被拒絕？](#1-git-push為何被拒絕)
- [2. Render 404如何判斷是哪一層？](#2-render-404如何判斷是哪一層)
- [3. 為何本機修正沒有進入部署版本？](#3-為何本機修正沒有進入部署版本)
- [4. 最短排查順序](#4-最短排查順序)
- [5. 延伸閱讀檢查表](#5-延伸閱讀檢查表)

## 0. 使用條件

- 本機API已完成基本驗證。
- 專案使用GitHub作為Render部署來源。
- 已保留Push輸出、Render Events／Logs、公開網址HTTP回應與部署commit。

## 1. Git Push為何被拒絕？

SourceTree顯示：

```text
[rejected] main -> main (fetch first)
Updates were rejected because the remote contains work
that you do not have locally.
```

當時歷史是：

```text
                70ee83a  本機：fix server port
               /
f92ab33  create
               \
                3f61c97  GitHub：更新README部署網址
```

兩邊都從`f92ab33`開始各自新增提交，因此本機不能用fast-forward直接覆蓋遠端。

### 1.1 安全處理

1. 先Pull／Fetch取得GitHub提交。
2. Merge或Rebase雙方歷史。
3. 有衝突時逐檔確認。
4. 再Push整合後的歷史。

這次兩邊分別修改README與port設定，合併後得到：

```text
bb814c0 Merge remote-tracking branch 'origin/main'
```

接著Push成功，本機`main`與`origin/main`已同步。

不要直接Force Push；Force Push可能把GitHub上別人或網頁編輯產生的提交刪掉。


## 2. Render 404如何判斷是哪一層？

### 2.1 案例：Render路由層沒有可用Server

部署網址只顯示：

```text
Not Found
```

HTTP回應還有：

```text
x-render-routing: no-server
```

這表示Render路由層當時找不到可接收請求的Server。請求還沒有到Spring Boot，因此不是`ProductController`缺少`@GetMapping`，也不是SQLite查詢回傳404。

### 2.2 依回應特徵判斷錯誤層級

| 現象 | 已到哪一層 | 優先檢查 |
|---|---|---|
| Render純文字`Not Found`＋`no-server` | Render邊緣路由 | Deploy狀態、Logs、服務網址、是否有執行中的Instance |
| Spring Whitelabel Error Page／Spring格式404 | 已到Spring Boot | Controller mapping、package掃描、URL |
| 啟動Log出現DataSource／Hibernate例外 | Spring啟動資料庫階段 | Driver、Dialect、JDBC URL、Schema |
| API成功回`[]` | Controller與Repository已工作 | 資料表是否有資料、初始化是否執行 |
| 本機正常、部署仍是舊結果 | 部署版本未更新 | Git Push、Render使用的branch、最新Deploy commit |


## 3. 為何本機修正沒有進入部署版本？

完整因果鏈：

```text
本機修改server.port與Dockerfile
    ↓
本機完成commit
    ↓
GitHub已另外修改README
    ↓
Push被non-fast-forward拒絕
    ↓
修正沒有進入GitHub main
    ↓
Render自然無法部署這筆本機修正
```

因此要分開理解：

- Render的`no-server`是線上請求無法進入應用程式的現象。
- Git Push被拒絕是修正無法送到部署來源的原因。
- 它們不是同一種404，也不是Product程式碼錯誤。


## 4. 最短排查順序

1. 確認本機修改已commit。
2. 確認commit已Push至Render監看的GitHub branch。
3. 確認Render最新Deploy使用同一個commit。
4. 從Logs確認Spring Boot是否啟動並監聽port。
5. 依HTTP回應特徵區分Render路由、Spring Mapping與DataSource錯誤。
6. 修正後重新Deploy，再比較本機與公開API。

## 5. 延伸閱讀檢查表

- [ ] 遇到`fetch first`時不直接Force Push
- [ ] 能區分`x-render-routing: no-server`與Spring Boot 404
- [ ] 能確認GitHub branch與Render Deploy使用的commit
- [ ] 不把路由錯誤誤判成Controller或SQLite錯誤
- [ ] 修正後已重新驗證公開API
