package org.example.datafetcher;

import org.example.model.Book;
import org.example.provider.DataProvider;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;

public class GetBooksDataFetcher implements DataFetcher<List<Book>> {
    @Override
    public List<Book> get(DataFetchingEnvironment dataFetchingEnvironment) {
        List<Book> books = DataProvider.getBooks();
        for (Book book : books) {
            book.setAuthor(DataProvider.getAuthors().stream()
                    .filter(author -> author.getId().equals(book.getAuthorId()))
                    .findFirst().orElse(null));
            book.setReviews(book.getReviewIds().stream()
                    .map(reviewId -> DataProvider.getReviews().stream()
                            .filter(review -> review.getId().equals(reviewId))
                            .findFirst().orElse(null))
                    .collect(Collectors.toList()));
        }
        return books;
    }
}