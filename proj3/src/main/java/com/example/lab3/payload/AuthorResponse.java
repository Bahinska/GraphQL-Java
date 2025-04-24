package com.example.lab3.payload;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthorResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private List<BookResponse> products; // Список продуктів, які належать автору
}