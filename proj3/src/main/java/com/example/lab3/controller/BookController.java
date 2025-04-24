package com.example.lab3.controller;

import com.example.lab3.model.Book;
import com.example.lab3.service.BookDbService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/book")
@AllArgsConstructor
public class BookController {

    private final BookDbService bookDBService;

    @GetMapping
    public Flux<Book> getAllBooks(@RequestParam(value = "user_ids", required = false) List<Long> userIds) {
        return bookDBService.getAllBooks(userIds);
    }
}