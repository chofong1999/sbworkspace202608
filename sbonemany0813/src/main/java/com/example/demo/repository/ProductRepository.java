package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	// (3) @Modifying 批次更新：庫存歸零
    // ★ 呼叫此方法的 Service 方法上必須加 @Transactional
    // ⚠️ 練習 2-3 完成後，需改為：WHERE p.category.name = :cat
    @Modifying
    @Query("UPDATE Product p SET p.stock = 0 WHERE p.category.name = :cat")
    int clearStockByCategory(@Param("cat") String cat);
}
