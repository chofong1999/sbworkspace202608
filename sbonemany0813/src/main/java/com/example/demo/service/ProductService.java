package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {
	@Autowired
	ProductRepository repo;

    // Service 中的正確寫法
	@Transactional // ← 必須加上，否則 @Modifying 會報錯
	public int clearStock(String category) {
		return repo.clearStockByCategory(category);
	}
}
