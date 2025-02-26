package org.example.datafetcher;

import org.example.model.Book;
import org.example.provider.DataProvider;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.stream.Collectors;

public class GetBooksByAuthorIdDataFetcher implements DataFetcher<List<Book>> {
    @Override
    public List<Book> get(DataFetchingEnvironment dataFetchingEnvironment) {
        String authorId = dataFetchingEnvironment.getArgument("authorId");
        return DataProvider.getBooks().stream()
                .filter(book -> book.getAuthorId().equals(authorId))
                .collect(Collectors.toList());
    }
}