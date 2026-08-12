package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
     
	@Autowired
	ProductService dao;
	
	@GetMapping
	public ResponseEntity<List<Product>> getAll(){
		List<Product> pts=dao.findAll();
		return ResponseEntity.ok(pts);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Product> findByKey(@PathVariable("id")Integer id){
		Product pt=dao.findById(id).orElse(null);
		if(pt!=null)
		   return ResponseEntity.ok(pt);
		else
		   return ResponseEntity.notFound().build();
	}
	
	// POST /api/products → 201 Created + Location header + 新商品資料
    @PostMapping
    public ResponseEntity<Product> create(@ModelAttribute Product product) {
        Product saved = dao.create(product);
        URI location = URI.create("/api/products/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    // PUT /api/products/{id} → 200 OK 或 404 Not Found
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Integer id,
                                          @RequestBody Product updated) {
        return dao.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // DELETE /api/products/{id} → 204 No Content 或 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (dao.delete(id)) {
            return ResponseEntity.noContent().build(); // 204，刪除成功，無 body
        }
        return ResponseEntity.notFound().build();       // 404，商品不存在
    }
}
