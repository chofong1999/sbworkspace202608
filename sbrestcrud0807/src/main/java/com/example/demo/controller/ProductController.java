package com.example.demo.controller;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Product;
import com.example.demo.model.ProductDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Entry API", description = "ProductController CRUD 示範操作")
public class ProductController {
    static AtomicInteger number=new AtomicInteger(4);
    private final static Map<String, Product> productStore = new ConcurrentHashMap<>();
//    static {
//    	Product p1=new Product("Apple","USA Fruit",new BigDecimal(30.50),100);
//    	productStore.put(p1.getId(),p1); 
//    	p1=new Product("Banana","Taiwan Fruit",new BigDecimal(10.0),500);
//    	productStore.put(p1.getId(),p1); 
//    	p1=new Product("Cherry","Canada Fruit",new BigDecimal(300.0),1000);
//    	productStore.put(p1.getId(),p1); 
//    }    
    // GET /api/products - 取得所有產品
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = new ArrayList<>(productStore.values());
        return ResponseEntity.ok(products);
    }
 // POST /api/products - 建立新產品
    @PostMapping
    @Operation(summary = "Product單筆資料新增", description = "單筆Product資料")
    @Parameter(name = "product", description = "ProductDTO裁減欄位後類別", required = true)
    @ApiResponse(responseCode = "200", description = "產品新增成功")
    @ApiResponse(responseCode = "400", description = "產品新增失敗")
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Product newProduct = new Product(
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock()
        );
        int value=number.getAndIncrement();
        //productStore.put(newProduct.getId(), newProduct);
        productStore.put(""+value, newProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }
    
 // PUT /api/products/{id} - 更新產品
    @PutMapping("/{id}")    
    public ResponseEntity<Product> updateProduct(@PathVariable String id, 
    		                                   @RequestBody ProductDTO product) {
        if (!productStore.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Product existingProduct = productStore.get(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.updateTimestamp();
        
        return ResponseEntity.ok(existingProduct);
    }
    @GetMapping("/{id}")
    @Operation(summary = "Product單筆資料查詢", description = "依Product查詢單筆資料")
    @Parameter(name = "id", description = "HashMap Key ID", required = true)
    @ApiResponse(responseCode = "200", description = "查詢 Key ID成功")
    @ApiResponse(responseCode = "404", description = "Key ID不存在")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return Optional.ofNullable(productStore.get(id))
               // .map(ResponseEntity::ok)
        		.map(p-> ResponseEntity.ok().body(p))
                .orElse(ResponseEntity.notFound().build());
    }
 // DELETE /api/products/{id} - 刪除產品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (!productStore.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Product p=productStore.remove(id);
        System.out.println("remove product:"+p);
        return ResponseEntity.noContent().build();
    }
    @PostConstruct
    public void initTestData() {
        productStore.put("1", new Product("iPhone 15", "Apple 最新手機", new BigDecimal("29999"), 100));
        productStore.put("2", new Product("MacBook Pro", "Apple 筆記型電腦", new BigDecimal("59999"), 50));
        productStore.put("3", new Product("AirPods Pro", "無線耳機", new BigDecimal("7999"), 200));
    }
}
