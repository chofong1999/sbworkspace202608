# JPA關聯擁有端、級聯與JSON

[返回JPA關聯字典](../04_JPA關聯映射.md)

## 關聯擁有端決定外鍵更新

在Department／Employee一對多關係中，`employees.dept_id`位於Employee資料表，因此Employee的`department`通常是擁有端。`Department.employees`若寫`mappedBy = "department"`，只是反向檢視。

只把Employee加入`department.getEmployees()`但沒有`employee.setDepartment(department)`，不保證外鍵會更新；應用`addEmployee`方法同步兩側。

## Cascade與orphanRemoval解決不同問題

- `cascade = PERSIST`：儲存父物件時也儲存新子物件。
- `cascade = REMOVE`：刪除父物件時也呼叫刪除子物件。
- `orphanRemoval = true`：子物件從父集合移除且成為孤兒時刪除它。

它們都可能刪除資料，設定前要確認生命週期是否真的由父物件完整擁有。共享或可獨立存在的子物件不應隨意使用`REMOVE`。

## LAZY、交易與N+1

LAZY集合在Persistence Context關閉後才存取，可能出現LazyInitializationException；在迴圈逐筆載入又可能產生N+1。解法應依使用案例選擇：

- 查詢中使用`JOIN FETCH`一次取必要關聯。
- 使用EntityGraph。
- 在交易內轉成DTO。
- 只查需要的投影欄位。

不要把所有關聯改成EAGER，因為會把成本移到每一次查詢，並可能形成更大的JOIN。

## JSON不是Entity關聯的同義詞

JPA關聯描述資料持久化；JSON描述API輸出。雙向Entity若直接序列化，A包含B、B又包含A，會循環。

處理選項：

1. DTO：最明確，推薦用於正式API。
2. `@JsonManagedReference`／`@JsonBackReference`：適合固定父子方向。
3. `@JsonIgnore`／`@JsonIgnoreProperties`：直接略過不需輸出的方向。

測試時應檢查JSON層級、查詢次數與資料量，不可只以「沒有序列化錯誤」判定完成。

