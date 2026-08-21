package com.example.demo.dao;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Product;

import java.util.*;

@Repository
public class ProductDAO implements CommandLineRunner {
    List<Product> data=new ArrayList<>();  
    
    public List<Product> getAll(){
    	return data;
    }
    public Product addProduct(Product pt) {
    	int maxId=data.stream().max((x,y)->x.getId()-y.getId()).get().getId()+1;
    	pt.setId(maxId);
    	data.add(pt);
    	return pt;
    }
    
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if(data.size()==0) {
			data.add(new Product(1,"Apple Mac Mini",19000.0));
			data.add(new Product(2,"Google Pixel Phone",29000.0));
			data.add(new Product(3,"Samsung Galaxy Phone",23900.0));
		}
	}
   
}
