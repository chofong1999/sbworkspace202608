package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Coffee;

public interface CoffeeRepository extends JpaRepository<Coffee,String> {

}
