package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.*;
import com.example.demo.repository.CategoryRepository;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
	@Autowired
	CategoryRepository repo;
	  @GetMapping
	  public ResponseEntity<List<Category>> getAll() {
//	        return ResponseEntity.ok(repo.findAllWithProducts());
	        return ResponseEntity.ok(repo.findAll());
	  }
}
