package com.example.book.model;

import java.util.List;

public record Book(String id,
                   String title,
                   BookReview mainReview,
                   List<BookReview> reviews) {
}
