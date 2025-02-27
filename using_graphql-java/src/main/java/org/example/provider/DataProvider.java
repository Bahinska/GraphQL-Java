package org.example.provider;

import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Review;
import org.example.model.Reviewer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.*;

public class DataProvider {
    private static List<Author> authors = new ArrayList<>();
    private static List<Book> books = new ArrayList<>();
    private static List<Review> reviews = new ArrayList<>();
    private static List<Reviewer> reviewers = new ArrayList<>();

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = DataProvider.class.getResourceAsStream("/data.json")) {
            if (inputStream == null) {
                throw new RuntimeException("Data file not found");
            }
            Map<String, List<?>> data = objectMapper.readValue(inputStream, new TypeReference<Map<String, List<?>>>() {});
            authors = objectMapper.convertValue(data.get("authors"), new TypeReference<List<Author>>() {});
            books = objectMapper.convertValue(data.get("books"), new TypeReference<List<Book>>() {});
            reviews = objectMapper.convertValue(data.get("reviews"), new TypeReference<List<Review>>() {});
            reviewers = objectMapper.convertValue(data.get("reviewers"), new TypeReference<List<Reviewer>>() {});
            updateLinks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateLinks() {
        for (Author author : authors) {
            List<Book> authorBooks = new ArrayList<>();
            for (Book book : books) {
                if (book.getAuthorId().equals(author.getId())) {
                    authorBooks.add(book);
                    book.setAuthor(author);
                }
            }
            author.setBooks(authorBooks);
        }

        for (Book book : books) {
            List<Review> bookReviews = new ArrayList<>();
            for (String reviewId : book.getReviewIds()) {
                for (Review review : reviews) {
                    if (review.getId().equals(reviewId)) {
                        bookReviews.add(review);
                    }
                }
            }
            book.setReviews(bookReviews);

            for (Review review : bookReviews) {
                for (Reviewer reviewer : reviewers) {
                    if (review.getReviewerId().equals(reviewer.getId())) {
                        review.setReviewer(reviewer);
                    }
                }
            }
        }

        for (Reviewer reviewer : reviewers) {
            List<Review> reviewerReviews = new ArrayList<>();
            for (Review review : reviews) {
                if (review.getReviewerId().equals(reviewer.getId())) {
                    reviewerReviews.add(review);
                }
            }
            reviewer.setReviews(reviewerReviews);
        }
    }

    public static List<Author> getAuthors() {
        return authors;
    }

    public static List<Book> getBooks() {
        return books;
    }

    public static List<Review> getReviews() {
        return reviews;
    }

    public static List<Reviewer> getReviewers() {
        return reviewers;
    }

    public static void addBook(Book book) {
        books.add(book);

        for (Author author : authors) {
            if (author.getId().equals(book.getAuthorId())) {
                book.setAuthor(author);
                author.getBooks().add(book);
                break;
            }
        }
    }

    public static void addReview(Review review) {
        reviews.add(review);

        for (Book book : books) {
            if (book.getId().equals(review.getBookId())) {
                book.getReviewIds().add(review.getId());
                book.getReviews().add(review);
                break;
            }
        }

        for (Reviewer reviewer : reviewers) {
            if (reviewer.getId().equals(review.getReviewerId())) {
                reviewer.getReviews().add(review);
                review.setReviewer(reviewer);
                break;
            }
        }
    }
}