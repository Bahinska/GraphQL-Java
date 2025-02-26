package org.example.datafetcher;

import org.example.model.Review;
import org.example.model.Reviewer;
import org.example.provider.DataProvider;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Map;
import java.util.UUID;

public class AddReviewDataFetcher implements DataFetcher<Review> {
    @Override
    public Review get(DataFetchingEnvironment dataFetchingEnvironment) {
        Map<String, Object> reviewInput = dataFetchingEnvironment.getArgument("reviewInput");
        Review review = new Review();
        review.setId(UUID.randomUUID().toString());
        review.setContent((String) reviewInput.get("content"));
        review.setRating((Integer) reviewInput.get("rating"));
        review.setReviewerId((String) reviewInput.get("reviewerId"));
        review.setBookId((String) reviewInput.get("bookId"));

        Reviewer reviewer = DataProvider.getReviewers().stream()
                .filter(r -> r.getId().equals(review.getReviewerId()))
                .findFirst()
                .orElse(null);

        if (reviewer != null) {
            review.setReviewer(reviewer);
        }

        DataProvider.addReview(review);
        return review;
    }
}