package com.cringe.books.repository;

import com.cringe.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    List<Book> findByUserId(Long id);

    Book findByUserIdAndId(Long id, Long userId);
}
