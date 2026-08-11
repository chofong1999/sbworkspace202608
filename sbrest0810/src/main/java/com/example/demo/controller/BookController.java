package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Book;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
   @GetMapping
   public ResponseEntity<Book> getBook() {
	   Book b=new Book(100,"Java Programming",600);
	   return ResponseEntity.ok(b);
   }
}
