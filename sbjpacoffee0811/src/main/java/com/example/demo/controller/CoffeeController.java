package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Coffee;
import com.example.demo.repository.CoffeeRepository;
import java.util.*;

@RestController
@RequestMapping("/api/coffees")
public class CoffeeController {
	
   @Autowired
   CoffeeRepository dao;
   
   @GetMapping
   public ResponseEntity<List<Coffee>> getAll(){
	   List<Coffee> cofs= dao.findAll();
	   return ResponseEntity.ok(cofs);
   }
}
