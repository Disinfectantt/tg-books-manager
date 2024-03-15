package com.cringe.books.controller;

import com.cringe.books.model.Book;
import com.cringe.books.service.BookService;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Controller
@Validated
public class bookController {

    private final BookService bookService;

    public bookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/getBooks")
    public String allBooks(Model model) {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Book> books = bookService.findAllByUserId(Long.parseLong(id));
        if (books.isEmpty()) {
            return "empty";
        }
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/editBook")
    @ExceptionHandler({MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            NumberFormatException.class})
    public String editBook(@RequestParam(name = "id") Long bookId,
                           @Size(max = 100) @RequestParam(name = "author") String author,
                           @Size(max = 100) @RequestParam(name = "name") String name,
                           Model model) {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Book book = bookService.getOneByUserAndId(Long.parseLong(id), bookId);
        book.setAuthor(author);
        book.setName(name);
        bookService.editByUserAndId(book);
        model.addAttribute("book", book);
        return "editBook";
    }

    @GetMapping("/delBook/{bookId}")
    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            NumberFormatException.class})
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookService.deleteByUserAndId(Long.parseLong(id), bookId);
        return ResponseEntity.ok().build();
    }

}
