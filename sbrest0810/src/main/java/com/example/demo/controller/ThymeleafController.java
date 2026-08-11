package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/thymeleaf")
public class ThymeleafController {
	
	  @GetMapping
      public String firstThymeleaf(Model model) {
          model.addAttribute("greeting", "<h1>Good Morning</h1>");   
          model.addAttribute("htmlContent", "<h1 style='color:blue'>Good Morning</h1>");   
    	  return "index";
      }
}
