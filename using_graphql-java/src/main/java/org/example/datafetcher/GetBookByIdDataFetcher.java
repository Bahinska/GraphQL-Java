package org.example.datafetcher;

import org.example.model.Book;
import org.example.provider.DataProvider;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.stream.Collectors;

public class GetBookByIdDataFetcher implements DataFetcher<Book> {
    @Override
    public Book get(DataFetchingEnvironment dataFetchingEnvironment) {
        String id = dataFetchingEnvironment.getArgument("id");
        Book book = DataProvider.getBooks().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst().orElse(null);

        if (book != null) {
            book.setAuthor(DataProvider.getAuthors().stream()
                    .filter(author -> author.getId().equals(book.getAuthorId()))
                    .findFirst().orElse(null));
            book.setReviews(book.getReviewIds().stream()
                    .map(reviewId -> DataProvider.getReviews().stream()
                            .filter(review -> review.getId().equals(reviewId))
                            .findFirst().orElse(null))
                    .collect(Collectors.toList()));
        }
        return book;
    }
}