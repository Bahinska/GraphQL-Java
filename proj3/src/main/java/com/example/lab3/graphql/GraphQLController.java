package com.example.lab3.graphql;

import com.example.lab3.payload.*;
import com.example.lab3.service.AuthorService;
import com.example.lab3.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.example.lab3.config.WebSecurityConfig.ROLE_USER_ADMIN;
import static com.example.lab3.config.WebSecurityConfig.ROLE_USER;

@Controller
public class GraphQLController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @QueryMapping
    @Secured({ROLE_USER_ADMIN, ROLE_USER})
    public ResponseType<AuthorResponse> getAuthorById(@Argument Long id) {
        try {
            AuthorResponse authorResponse = authorService.getAuthorResponseById(id);
            return new ResponseType<>(true, null, authorResponse);
        } catch (Exception e) {
            return new ResponseType<>(false, List.of(e.getMessage()), null);
        }
    }

    @QueryMapping
    public List<AuthorResponse> getAllAuthors() {
        return authorService.getAllAuthorResponses();
    }

    @MutationMapping
    @Secured({ROLE_USER_ADMIN})
    public ResponseType<AuthorResponse> createAuthor(@Argument CreateAuthorInput input) {
        try {
            AuthorResponse authorResponse = authorService.createAuthorResponse(input.getName(), input.getEmail());
            return new ResponseType<>(true, null, authorResponse);
        } catch (Exception e) {
            return new ResponseType<>(false, List.of(e.getMessage()), null);
        }
    }

    @MutationMapping
    @Secured({ROLE_USER_ADMIN})
    public ResponseType<AuthorResponse> updateAuthor(@Argument UpdateAuthorInput input) {
        try {
            AuthorResponse authorResponse = authorService.updateAuthorResponse(input.getId(), input.getName(), input.getEmail());
            return new ResponseType<>(true, null, authorResponse);
        } catch (Exception e) {
            return new ResponseType<>(false, List.of(e.getMessage()), null);
        }
    }

    @MutationMapping
    @Secured({ROLE_USER_ADMIN})
    public ResponseType<AuthorResponse> deleteAuthor(@Argument Long id) {
        try {
            authorService.deleteAuthor(id);
            return new ResponseType<>(true, null, null);
        } catch (Exception e) {
            return new ResponseType<>(false, List.of(e.getMessage()), null);
        }
    }

    @BatchMapping(typeName = "AuthorResponse")
    public Mono<Map<AuthorResponse, List<BookResponse>>> books(List<AuthorResponse> authors) {
        return bookService.getBooksByUserIds(authors);
    }
}