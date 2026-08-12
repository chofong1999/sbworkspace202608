package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service  // (1) 標記為 Spring 元件
public class ProductService implements CommandLineRunner {

//    private final ProductRepository productRepository;
//
//    // (2) 建構子注入：比 @Autowired 更推薦，便於單元測試
//    public ProductService(ProductRepository productRepository) {
//        this.productRepository = productRepository;
//    }
	@Autowired
	ProductRepository productRepository;
    
	public List<Product> findAll() {
        return productRepository.findAll();  // (3)
    }

    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);  // (4)
    }

    public Product create(Product product) {
        return productRepository.save(product);  // (5) id 為 null → INSERT
    }

    public Optional<Product> update(Integer id, Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setPrice(updated.getPrice());
            existing.setStock(updated.getStock());
            existing.setCategory(updated.getCategory());
            return productRepository.save(existing);  // (6) id 有值 → UPDATE
        });
    }

    public boolean delete(Integer id) {
        if (productRepository.existsById(id)) {  // (7) 確認是否存在
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if(productRepository.count()==0) {
			productRepository.save(new Product("Apple",50.0,500,"Fruits"));
			productRepository.save(new Product("Apple iPhone 17",39900.0,100,"3C"));
			productRepository.save(new Product("Banana",30.0,1000,"Fruits"));
		}
	}
}
