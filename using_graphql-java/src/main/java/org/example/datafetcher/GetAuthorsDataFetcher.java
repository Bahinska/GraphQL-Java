package org.example.datafetcher;

import org.example.model.Author;
import org.example.provider.DataProvider;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;

public class GetAuthorsDataFetcher implements DataFetcher<List<Author>> {
    @Override
    public List<Author> get(DataFetchingEnvironment dataFetchingEnvironment) {
        return DataProvider.getAuthors();
    }
}