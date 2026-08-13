package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	@Autowired
	ProductService srv;
    @GetMapping("/{category}")
    public ResponseEntity<Map<String,Integer>> clearStock(@PathVariable("category")String cname)
    {
    	int v=srv.clearStock(cname);
    	Map<String,Integer> m=new HashMap<>();
    	m.put("clear "+cname, v);
    	
    	return ResponseEntity.ok(m);
    }
}
