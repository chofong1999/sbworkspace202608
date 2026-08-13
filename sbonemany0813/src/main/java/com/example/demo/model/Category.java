package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // ← 避免遞迴：此端正常序列化
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)  // 類別名稱不可重複
    private String name;

    // @OneToMany：一個 Category 對應多個 Product
    // mappedBy = "category" → 指向 Product.java 中的屬性名稱（不是欄位名）
    // fetch = LAZY → 需要時才查詢商品（預設 LAZY，但明確標示更清楚）
    // @JsonManagedReference → 「管理端」，序列化時正常輸出 products 陣列
    //   搭配 Product 端的 @JsonBackReference，共同切斷 JSON 無限遞迴
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY ,
    		cascade = CascadeType.PERSIST , targetEntity=Product.class)
    @JsonManagedReference
    private List<Product> products = new ArrayList<>();

    public Category() {}
    public Category(String name) { this.name = name; }

//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public List<Product> getProducts() { return products; }
//    public void setProducts(List<Product> products) { this.products = products; }

    // ⚠️ 若有 toString()，切勿直接印出 products（會觸發 LAZY 載入並可能遞迴）
    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
