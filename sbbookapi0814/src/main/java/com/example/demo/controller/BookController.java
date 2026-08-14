package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.Book;
import com.example.demo.service.BookService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;
import java.util.Map;

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
    
    @GetMapping("/category_count")
    public ResponseEntity<List<Map<String,Integer>>> categoryCount() {
        
        return ResponseEntity.ok(bookService.countBooksByCategory());
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return bookService.findById(id)
                .map(ResponseEntity::ok)                
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/books
//    @PostMapping
//    public ResponseEntity<Book> create(@RequestBody Book book) {
//        Book saved = bookService.create(book);
//        URI location = URI.create("/api/books/" + saved.getId());
//        return ResponseEntity.created(location).body(saved);
//    }
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookCreateRequest req) {
        // ① @Valid 觸發驗證（@NotBlank、@Pattern、@Positive...）
        //    驗證失敗 → 拋 MethodArgumentNotValidException → GlobalExceptionHandler 處理
        // ② 請求 DTO → Entity 轉換
        Book book = new Book(
                req.getTitle(), req.getAuthor(), req.getIsbn(),
                req.getPrice(), req.getStock(), req.getCategory());
        // ③ 委派 Service
        Book saved = bookService.create(book);
        // ④ 建立 Location header 指向新資源
        URI location = URI.create("/api/books/" + saved.getId());
        // ⑤ Entity → 回應 DTO，回傳 201 Created
        return ResponseEntity.created(location).body(BookResponse.from(saved));
    }


    // PUT /api/books/{id}
//    @PutMapping("/{id}")
//    public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book book) {
//        return bookService.update(id, book)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
 // PUT /api/books/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BookUpdateRequest req) {
        Book updatedData = new Book(
                req.getTitle(), req.getAuthor(), req.getIsbn(),
                req.getPrice(), req.getStock(), req.getCategory());
        return bookService.update(id, updatedData)
                .map(BookResponse::from)
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
