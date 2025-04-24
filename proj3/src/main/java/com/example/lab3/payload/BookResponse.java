package com.example.lab3.payload;

import lombok.Data;

@Data
public class BookResponse {
    private Long id;
    private String title;
    private Double price;
    private Long authorId;
}