package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import java.util.*;

@Component
public class DataInitConfig implements CommandLineRunner{
	
    @Autowired
	DepartmentRepository repo;
    
    @Autowired
    CategoryRepository  repo2;
    
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		if(repo.count()==0) {
			Department d1=new Department("MIS");
			List<Employee> e1=List.of(new Employee("Andy Chen","andy@demo.com",d1,50000.0),
					new Employee("Jason Lee","jason@demo.com",d1,52000.0));
			d1.setEmployees(e1);
			Department d2=new Department("Finance");
			List<Employee> e2=List.of(new Employee("Mary Wu","mary@demo.com",d2,45000.0),
					new Employee("Rose Lin","rose@demo.com",d2,53000.0));
			d2.setEmployees(e2);
			repo.save(d1);
			repo.save(d2);
		}
		
		if(repo2.count()==0) {
			Category c1=new Category("3C");
			List<Product> p1=List.of(new Product("iPhone 17",35900.0,5,c1),
					new Product("Samsung Phone",25000.0,20,c1));
			c1.setProducts(p1);
			Category c2=new Category("Fruit");
			List<Product> p2=List.of(new Product("Apple",35.0,150,c2),
					new Product("Banana",25.0,200,c2));
			c2.setProducts(p2);
			repo2.save(c1);
			repo2.save(c2);
		}
	}

}
