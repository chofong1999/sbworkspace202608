
package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
   
    // private String category;    // 允許 null：類別可以不設定
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")    // 資料庫中的外鍵欄位名稱
    @JsonBackReference
    private Category category;
    // ★ JPA 必須有無參數建構子（JPA 反射建立物件時使用）
    public Product() {}

    // 帶參數建構子，方便在測試或 Service 中快速建立物件
    public Product(String name, Double price, Integer stock, Category category) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}