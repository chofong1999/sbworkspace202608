package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /api/books  或  GET /api/books?category=Programming
    @GetMapping
    public List<Book> getAll(@RequestParam(required = false) String category) {
        if (category != null) {
            return bookService.findByCategory(category);
        }
        return bookService.findAll();
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/books
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book saved = bookService.create(book);
        URI location = URI.create("/api/books/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    // PUT /api/books/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book book) {
        return bookService.update(id, book)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/books/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (bookService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
