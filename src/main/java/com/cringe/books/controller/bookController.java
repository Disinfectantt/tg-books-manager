package com.cringe.books.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class bookController {
    @GetMapping("/")
    public String home() {
        return "books";
    }
}
