package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.ProductDAO;
import com.example.demo.model.Product;

import java.util.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {
	@Autowired
	ProductDAO dao;

	@GetMapping
	public ResponseEntity<List<Product>> getProducts() {
		List<Product> data = dao.getAll();
		if (data.size() > 0)
			return ResponseEntity.ok(data);
		else
			return ResponseEntity.noContent().build();
	}

	@PostMapping
	public ResponseEntity<Product> addProduct(@RequestBody Product p) {
		Product px = dao.addProduct(p);
		return ResponseEntity.ok(px);
	}

	@GetMapping("/notfound")
    public ResponseEntity<String> notFound(){
    	return ResponseEntity.notFound().build();
	}
	@GetMapping("/nocontent")
	public ResponseEntity<String> notContent() {
		return ResponseEntity.noContent().build();
	}
	@GetMapping("/error")
	public ResponseEntity<String> error() {
		return ResponseEntity.internalServerError().body("Spring Boot Trigger");
	}
}
