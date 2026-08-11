package com.example.demo.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {	
	    private String name;
	    private String description;
	    private BigDecimal price;
	    private int stock;
	    
}
