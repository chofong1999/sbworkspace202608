package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.GreetingService;

@RestController
@RequestMapping("/api")
public class HelloWorld {
	
	@Autowired
	private  GreetingService greetingService;

//    public HelloWorld(GreetingService greetingService) {
//        this.greetingService = greetingService;
//    }
	
    @GetMapping("/hello")
	public String hello() {
		return "Hello Demo Spring Boot";
	}
    
    @GetMapping("/greeting/{name}")
    public String greeting(@PathVariable("name") String name) {
    	String msg=greetingService.greet(name);
    	return msg;
    }
    @GetMapping("/greeting")
    public String greetingParam(@RequestParam(defaultValue = "NA") String name) {
    	String msg=greetingService.greet(name);
    	return msg;
    }
}
