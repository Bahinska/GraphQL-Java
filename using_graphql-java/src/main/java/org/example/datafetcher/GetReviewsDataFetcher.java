package org.example.datafetcher;

import org.example.model.Review;
import org.example.provider.DataProvider;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class GetReviewsDataFetcher implements DataFetcher<List<Review>> {
    @Override
    public List<Review> get(DataFetchingEnvironment dataFetchingEnvironment) {
        return DataProvider.getReviews();
    }
}