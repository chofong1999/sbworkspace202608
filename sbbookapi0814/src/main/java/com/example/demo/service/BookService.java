package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public Book create(Book book) {
        // 業務規則：ISBN 不可重複
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("ISBN 已存在：" + book.getIsbn());
        }
        return bookRepository.save(book);
    }

    public Optional<Book> update(Long id, Book updatedBook) {
        return bookRepository.findById(id).map(existing -> {
            existing.setTitle(updatedBook.getTitle());
            existing.setAuthor(updatedBook.getAuthor());
            existing.setIsbn(updatedBook.getIsbn());
            existing.setPrice(updatedBook.getPrice());
            existing.setStock(updatedBook.getStock());
            existing.setCategory(updatedBook.getCategory());
            return bookRepository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!bookRepository.existsById(id)) {
            return false;
        }
        bookRepository.deleteById(id);
        return true;
    }

    public List<Book> findByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    public List<Book> searchByTitle(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }
    
    public List<Object[]> countBooksByCategory(){
    	return bookRepository.countBooksByCategory();
    }
}
