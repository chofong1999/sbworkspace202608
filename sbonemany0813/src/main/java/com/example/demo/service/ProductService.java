package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Product;
import com.example.demo.repository.*;
import java.util.*;

@Service
public class ProductService {
	@Autowired
	ProductRepository repo;

	// Service 中的正確寫法
	@Transactional // ← 必須加上，否則 @Modifying 會報錯
	public int clearStock(String category) {
		return repo.clearStockByCategory(category);
	}

	public List<Product> findAll() {
		return repo.findAll();
	}

	// 分頁查詢（page 從 0 開始，size = 每頁筆數，sortBy = Entity 屬性名稱）
	public Page<Product> findPaged(int page, int size, String sortBy) {
		return repo.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()));
	}
}
