package com.example.demo.service;

import com.example.demo.exception.BookNotFoundException;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    	Book b=  bookRepository.findById(id)
    			  .orElseThrow(() -> new BookNotFoundException(id));
    	return Optional.of(b);
    	              
    }
    @Transactional
    public Book create(Book book) {
        // 業務規則：ISBN 不可重複
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("ISBN 已存在：" + book.getIsbn());
        }
        
        Book saved= bookRepository.save(book);
        
        if (saved.getId()>0) {
            throw new RuntimeException("測試是否儲存BOOK" );
        }
        return saved;
    }
    @Transactional
    public Optional<Book> update(Long id, Book updatedBook) {
    	Book bk1=bookRepository.findById(id).map(existing -> {
            existing.setTitle(updatedBook.getTitle());
            existing.setAuthor(updatedBook.getAuthor());
            existing.setIsbn(updatedBook.getIsbn());
            existing.setPrice(updatedBook.getPrice());
            existing.setStock(updatedBook.getStock());
            existing.setCategory(updatedBook.getCategory());
            return existing;
        }).get();
    	try {
    	    bookRepository.save(bk1);
    	    throw new RuntimeException("測試修改");
    	    //return Optional.of(bk1);
    	}catch(Exception ex) {    		
    		System.out.println("update error "+ex.getMessage());
    		return Optional.empty();
    	}
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
    
    public List<Map<String,Integer>> countBooksByCategory(){
    	// List<Map<String,Integer>>
    	List<Object[]> data=bookRepository.countBooksByCategory();
    	List<Map<String,Integer>> objs=data.stream().
    			map(obj-> Map.of(obj[0].toString(),Integer.parseInt(obj[1].toString()))).toList();
    	return objs;
    }
}
