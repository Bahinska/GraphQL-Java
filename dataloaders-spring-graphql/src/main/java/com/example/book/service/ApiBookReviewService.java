package com.example.book.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.book.model.api.ApiBookReview;
import com.example.book.utils.JsonUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiBookReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiBookReviewService.class);

    private final List<ApiBookReview> apiBookReviews;

    public ApiBookReviewService() {
        this.apiBookReviews = JsonUtils.loadListFromJsonFile(
                "/data/api-book-review.json", ApiBookReview[].class);
    }

    // /api/books/{bookId}/reviews
    public List<ApiBookReview> getApiBookReviewsForBook(String bookId) {
        LOGGER.info("REST API: get all reviews for book with id=[{}]", bookId);
        return apiBookReviews.stream()
                .filter(apiBookReview -> bookId.equals(apiBookReview.bookId()))
                .toList();
    }

    // /api/reviews?bookIds=1,2,3
    public List<ApiBookReview> getApiBookReviewsForBooks(Set<String> bookIds) {
        LOGGER.info("REST API: get all reviews for books with ids={}", bookIds);
        return apiBookReviews.stream()
                .filter(apiBookReview -> bookIds.contains(apiBookReview.bookId()))
                .toList();
    }

    public List<ApiBookReview> getMainReviewForBooks(List<String> bookIds) {
        LOGGER.info("REST API: get main review for multiple bookIds {}", bookIds);
        List<ApiBookReview> allBooks = apiBookReviews.stream().filter(review -> bookIds.contains(review.bookId())).toList();

        return allBooks.stream()
                .collect(Collectors.groupingBy(ApiBookReview::bookId))
                .values()
                .stream()
                .map(books -> books.stream().findFirst().orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public ApiBookReview getMainReviewForBook(String bookId) {
        LOGGER.info("REST API: get main review for bookId {}", bookId);
        return apiBookReviews.stream().filter(review -> bookId.equals(review.bookId())).findFirst().orElse(null);
    }




    // get Books for multiple authorIds
    public List<ApiBookReview> getBooksForAuthors(List<String> bookIds) {
        LOGGER.info("REST API: get all reviews for multiple bookIds {}", bookIds);
        return apiBookReviews.stream().filter(review -> bookIds.contains(review.bookId())).toList();
    }
}
