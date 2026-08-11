package com.example.demo.service;

import org.springframework.stereotype.Service;

//@Service：標記此類別為業務邏輯元件，Spring 會自動掃描並註冊為 Bean
@Service
public class GreetingService {

 /**
  * 根據名稱產生問候訊息
  * @param name 使用者名稱
  * @return 問候字串
  */
 public String greet(String name) {
     // 這裡可以加入複雜的商業邏輯，例如查詢資料庫、呼叫外部 API 等
     return "Hello, " + name + "! 歡迎使用 Spring Boot！";
 }
 public String getWelcomeMessage() {
	 return "welcoome user";
 }
}