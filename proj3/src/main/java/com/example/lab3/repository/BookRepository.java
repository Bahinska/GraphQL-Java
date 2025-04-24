package com.example.lab3.repository;

import com.example.lab3.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    // You can add custom query methods here if needed
}