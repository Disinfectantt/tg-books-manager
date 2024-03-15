package com.cringe.books.service;

import com.cringe.books.model.Book;
import com.cringe.books.repository.BookRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepo bookRepo;

    @Autowired
    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> findAllByUserId(Long id) {
        return bookRepo.findByUserId(id);
    }

    public Book getOneByUserAndId(Long userId, Long id) {
        return bookRepo.findByUserIdAndId(userId, id);
    }

    public void editByUserAndId(Book book) {
        bookRepo.save(book);
    }

    public void deleteByUserAndId(Long userId, Long id) {
        Book book = getOneByUserAndId(userId, id);
        bookRepo.delete(book);
    }

}
