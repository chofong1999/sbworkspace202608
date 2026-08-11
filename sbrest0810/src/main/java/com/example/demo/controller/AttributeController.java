package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Book;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@Controller
@RequestMapping("/attribute")
public class AttributeController {
    
	@GetMapping("/img")
	public String imageAttr(Model model) {
		String[] imgs= {"banana.png","grape.png","guava.png","orange.png"};
		int index=(int)(Math.random()*imgs.length);
		model.addAttribute("fruitImage", imgs[index]);
		return "showimage";
	}
	@GetMapping("/status")
	public String statusAttr(Model model) {
		model.addAttribute("isLogin", "false");
		return "status";
	}
	@GetMapping("/role")
	public String roleAttr(Model model) {
		String[] roles= {"user","grape","admin","orange"};
		int index=(int)(Math.random()*roles.length);
		model.addAttribute("role", roles[index]);
		return "role";
	}
	@GetMapping("/iterate")
	public String loopAttr(Model model) {
		List<Book> data=List.of(new Book(10,"Plants",750),new Book(11,"Fruits",600),
				new Book(12,"Sweets",550));
		model.addAttribute("books",data );
		return "book";
	}
	@GetMapping("/session")
	public String sessionAttr(Model model,HttpSession session) {
		session.setAttribute("user", "John Lee");
//		model.addAttribute("session", session);
//		model.addAttribute("user", "John Lee");
		model.addAttribute("price", 19.9565);
		model.addAttribute("today", new java.util.Date());
		return "session";
	}
	@GetMapping("/href")
	public String hrefAttr(Model model) {
		
		model.addAttribute("userId", 100);
		return "hyperlink";
	}
}
