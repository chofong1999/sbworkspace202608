package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "products")       // 對應資料庫中的 products 表
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // MySQL AUTO_INCREMENT
    private Integer id;

    @Column(nullable = false)   // NOT NULL：商品名稱必填
    private String name;

    @Column(nullable = false)   // NOT NULL：價格必填
    private Double price;

    private Integer stock;      // 允許 null：庫存可以不設定

    private String category;    // 允許 null：類別可以不設定

    // ★ JPA 必須有無參數建構子（JPA 反射建立物件時使用）
    public Product() {}

    // 帶參數建構子，方便在測試或 Service 中快速建立物件
    public Product(String name, Double price, Integer stock, String category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // Getter / Setter
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public Double getPrice() { return price; }
//    public void setPrice(Double price) { this.price = price; }
//    public Integer getStock() { return stock; }
//    public void setStock(Integer stock) { this.stock = stock; }
//    public String getCategory() { return category; }
//    public void setCategory(String category) { this.category = category; }
}