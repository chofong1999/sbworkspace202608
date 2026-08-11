package com.example.demo.model;

//泛型類別 ApiResponse<T>：T 為 data 欄位的型別
public class ApiResponse<T> {
 private boolean success;   // 成功/失敗
 private String message;    // 訊息
 private T data;            // 實際資料

 public ApiResponse(boolean success, String message, T data) {
     this.success = success;
     this.message = message;
     this.data = data;
 }

 // 靜態工廠方法：快速建立成功/失敗回應
 public static <T> ApiResponse<T> ok(T data) {
     return new ApiResponse<>(true, "成功", data);
 }

 public static <T> ApiResponse<T> error(String message) {
     return new ApiResponse<>(false, message, null);
 }

 // Getters（Jackson 序列化時需要）
 public boolean isSuccess() { return success; }
 public String getMessage() { return message; }
 public T getData() { return data; }
}
