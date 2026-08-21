package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Info;

import java.util.*;
@RestController
@RequestMapping("/api")
@CrossOrigin
public class BasicApi {
	
    @GetMapping("/first")
	public ResponseEntity<String> firstGet(){
		return ResponseEntity.ok("Hello World");
	}
    @PostMapping
    public ResponseEntity<String> nameCity(@ModelAttribute Info data ){
    	String msg="Name:"+data.getName()+" City:"+data.getCity();
    	return ResponseEntity.ok(msg);
    }
}
