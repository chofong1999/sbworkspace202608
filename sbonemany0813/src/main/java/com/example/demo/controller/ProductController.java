package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	@Autowired
	ProductService srv;

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(srv.findAll());
    }

    @GetMapping("/{category}")
    public ResponseEntity<Map<String,Integer>> clearStock(@PathVariable("category")String cname)
    {
    	int v=srv.clearStock(cname);
    	Map<String,Integer> m=new HashMap<>();
    	m.put("clear "+cname, v);
    	
    	return ResponseEntity.ok(m);
    }
 // GET /api/products/page?page=0&size=5&sortBy=price
    @GetMapping("/page")
    public Page<Product> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return srv.findPaged(page, size, sortBy);
    }
}
