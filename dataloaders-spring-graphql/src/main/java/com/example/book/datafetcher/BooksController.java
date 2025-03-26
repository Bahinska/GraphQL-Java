package com.example.book.datafetcher;

import com.example.book.model.Book;
import org.dataloader.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import com.example.book.model.BookReview;
import com.example.book.model.api.ApiBookReview;
import com.example.book.service.ApiBookReviewService;
import com.example.book.service.ApiBookService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
public class BooksController {

    public static final Logger LOGGER = LoggerFactory.getLogger(BooksController.class);

    private final ApiBookService apiBookService;
    private final ApiBookReviewService apiBookReviewService;
    private final BatchLoaderRegistry batchLoaderRegistry;

    public BooksController(ApiBookService apiBookService,
                           ApiBookReviewService apiBookReviewService,
                           BatchLoaderRegistry batchLoaderRegistry) {
        this.apiBookService = apiBookService;
        this.apiBookReviewService = apiBookReviewService;
        this.batchLoaderRegistry = batchLoaderRegistry;

        batchLoaderRegistry.forTypePair(Book.class, List.class).registerBatchLoader(
                (books, environment ) -> reviews(books));

        batchLoaderRegistry.forTypePair(Book.class, BookReview.class).registerBatchLoader(
                (books, environment) -> mainReviews(books));
    }


    @QueryMapping
    public List<Book> booksWODataLoader() {
        return apiBookService.getApiBooks().stream()
                .map(apiBook -> new Book(
                        apiBook.id(),
                        apiBook.title(),
                        Optional.ofNullable(apiBookReviewService.getMainReviewForBook(apiBook.id()))
                                .map(review -> new BookReview(
                                        review.id(),
                                        review.rating())
                                ).orElse(null),
                        apiBookReviewService.getApiBookReviewsForBook(apiBook.id()).stream()
                                .map(review -> new BookReview(
                                        review.id(),
                                        review.rating())
                                )
                                .toList())
                )
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<Book> books() {
        return apiBookService.getApiBooks().stream()
                .map(apiBook -> new Book(
                        apiBook.id(),
                        apiBook.title(),
                        null,
                        null)
                )
                .collect(Collectors.toList());
    }

    @SchemaMapping
    public CompletableFuture<List<BookReview>> reviews(Book book,
                                                        DataLoader<Book, List<BookReview>> dataLoader) {
        if (book.reviews() != null)
            return CompletableFuture.completedFuture(book.reviews());

        LOGGER.info("Fetching book reviews for book with id=[{}]", book.id());
        return dataLoader.load(book);
    }

    @SchemaMapping
    public CompletableFuture<BookReview> mainReviews(Book book, DataLoader<Book, BookReview> dataLoader) {
        if (book.mainReview() != null) {
            return CompletableFuture.completedFuture(book.mainReview());
        }
        LOGGER.info("Fetching main review for book with id=[{}]", book.id());
        return dataLoader.load(book);
    }


    private Flux<List> reviews(List<Book> books) {
        List<String> bookIds = books.stream()
                .map(Book::id)
                .toList();
        return Flux.fromIterable(getReviewsViaBatchHTTPApi(bookIds));
    }

    private List<List<BookReview>> getReviewsViaBatchHTTPApi(List<String> bookIds) {
        return apiBookReviewService.getApiBookReviewsForBooks(new HashSet<>(bookIds))
                .stream()
                .collect(Collectors.groupingBy(ApiBookReview::bookId))
                .values()
                .stream()
                .map(apiBookReviews -> apiBookReviews.stream()
                        .map(apiBookReview -> new BookReview(
                                apiBookReview.id(),
                                apiBookReview.rating())
                        )
                        .toList()
                )
                .toList();
    }

    private Flux<BookReview> mainReviews(List<Book> books) {
        List<String> authorIds = books.stream().map(Book::id).toList();
        return Flux.fromIterable(getMainReviewsViaBatchHTTPApi(authorIds));
    }

    private List<BookReview> getMainReviewsViaBatchHTTPApi(List<String> bookIds) {
        return apiBookReviewService.getMainReviewForBooks(bookIds).stream()
                .map(review -> new BookReview(
                        review.id(),
                        review.rating()
                ))
                .toList();
    }
}
