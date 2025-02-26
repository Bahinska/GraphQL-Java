package org.example;

import graphql.schema.PropertyDataFetcher;
import org.example.datafetcher.*;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.File;
import java.io.IOException;

public class GraphQLServer {
    private static GraphQL graphQL;

    public static void startServer() throws IOException {
        File schemaFile = new File("src/main/resources/schema.graphqls");
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildWiring();

        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private static RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder
                        .dataFetcher("getBooks", new GetBooksDataFetcher())
                        .dataFetcher("getBookById", new GetBookByIdDataFetcher())
                        .dataFetcher("getBooksByAuthorId", new GetBooksByAuthorIdDataFetcher())
                        .dataFetcher("getAuthors", new GetAuthorsDataFetcher()))
                .type("Mutation", builder -> builder
                        .dataFetcher("addBook", new AddBookDataFetcher())
                        .dataFetcher("addReview", new AddReviewDataFetcher()))
                .type("Book", builder -> builder
                        .dataFetcher("author", PropertyDataFetcher.fetching("author"))
                        .dataFetcher("reviews", PropertyDataFetcher.fetching("reviews")))
                .build();
    }

    public static void main(String[] args) throws IOException {
        startServer();

        // Example query execution
        String query = "{ getBooks { id title author { name } reviews { content rating reviewer { name } } } }";
        System.out.println(graphQL.execute(query).toSpecification());

        String queryBookById = "{ getBookById(id: \"2\") { id title author { name } reviews { content rating reviewer { name } } } }";
        System.out.println(graphQL.execute(queryBookById).toSpecification());

        String queryBooksByAuthorId = "{ getBooksByAuthorId(authorId: \"1\") { id title author { name } reviews { content rating reviewer { name } } } }";
        System.out.println(graphQL.execute(queryBooksByAuthorId).toSpecification());

        // Example mutation execution
        String mutationBook = "mutation { addBook(bookInput: { title: \"New Book\", authorId: \"1\" }) { id title author { name } } }";
        System.out.println(graphQL.execute(mutationBook).toSpecification());

        String mutationReview = "mutation { addReview(reviewInput: { content: \"Great book\", rating: 5, reviewerId: \"1\", bookId: \"1\" }) { id content rating reviewer { name } } }";
        System.out.println(graphQL.execute(mutationReview).toSpecification());

        // Test query
        String testChangesQuery = "{ getBooks { id reviews { content rating reviewer { name } }} }";
        System.out.println(graphQL.execute(testChangesQuery).toSpecification()); // first book has 2 reviews and there are 3 books in total
    }
}