package com.example.lab3.service;

import com.example.lab3.model.Book;
import com.example.lab3.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BookDbService {
    private final BookRepository bookRepository;

    public Flux<Book> getAllBooks(List<Long> userIds) {
        log.info("REST API: get all books");
        List<Book> books = bookRepository.findAll();
        if (userIds != null && !userIds.isEmpty()) {
            books = books.stream()
                    .filter(book -> userIds.contains(book.getAuthorId()))
                    .toList();
        }
        return Flux.fromIterable(books);
    }
}