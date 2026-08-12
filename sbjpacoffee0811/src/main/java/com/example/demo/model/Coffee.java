package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.*;
@Entity
@Table(name="coffees")
@Data
public class Coffee {
   @Id
   @Column(name="COF_NAME")
   String cofName;   
   @Column(name="SUP_ID" , nullable=false)
   int supId;
   @Column(nullable=false)
   BigDecimal price;   
   @Column(nullable=false)
   int sales;
   @Column(nullable=false)
   int total;
   
}
