package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/submit")
public class SubmitController {
	final UserService userService;
	public SubmitController(UserService userService) {
        this.userService = userService;
    }
	
	@PostMapping("/form")
	@Parameter(name="user",description="用戶",required=true)
    public ResponseEntity<User> receiveModel(@ModelAttribute User user){
    	if(user.getName()!=null) {
    		System.out.println("user:"+user);
    		User u1=userService.createUser(user.getName(),user.getEmail(),user.getAge());
    		return ResponseEntity.ok(u1);
    	}else {
    		return ResponseEntity.badRequest().build();
    	}	
    }
	@PostMapping("/json")
	@Operation(summary = "Json 方式",
    description = "用Json的方式接收User的資料")
//    public ResponseEntity<User> receiveJson(@RequestBody User user){
	   public ResponseEntity<User> receiveJson(@RequestBody User user){
    	if(user.getName()!=null) {
    		System.out.println("user:"+user);
    		User u1=userService.createUser(user.getName(),user.getEmail(),user.getAge());
    		return ResponseEntity.ok(u1);
    	}else {
    		return ResponseEntity.badRequest().build();
    	}
    }
}
