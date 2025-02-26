package org.example.datafetcher;

import org.example.model.Author;
import org.example.model.Book;
import org.example.provider.DataProvider;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.Map;
import java.util.UUID;

public class AddBookDataFetcher implements DataFetcher<Book> {
    @Override
    public Book get(DataFetchingEnvironment dataFetchingEnvironment) {
        Map<String, Object> bookInput = dataFetchingEnvironment.getArgument("bookInput");
        Book book = new Book();
        book.setId(UUID.randomUUID().toString());
        book.setTitle((String) bookInput.get("title"));
        book.setAuthorId((String) bookInput.get("authorId"));

        Author author = DataProvider.getAuthors().stream()
                .filter(a -> a.getId().equals(book.getAuthorId()))
                .findFirst()
                .orElse(null);

        if (author != null) {
            book.setAuthor(author);
            author.getBooks().add(book);
        }

        DataProvider.addBook(book);
        return book;
    }
}