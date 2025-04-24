package com.example.lab3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class GraphQLIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String GRAPHQL_ENDPOINT = "/graphql";

    @Test
    @WithMockUser(username = "admin", roles = {"USER-ADMIN", "USER-READER"})
    void testGetAuthorById_Success() {
        // Define the GraphQL query in the correct JSON format
        String query = """
        {
            "query": "query { getAuthorById(id: 1231231) { success errors data { id name email createdAt books { id title price } } } }",
            "variables": null
        }
        """;

        // Create HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity with the query
        HttpEntity<String> entity = new HttpEntity<>(query, headers);

        // Send POST request to GraphQL endpoint
        ResponseEntity<String> response = restTemplate.exchange(GRAPHQL_ENDPOINT, HttpMethod.POST, entity, String.class);

        // Assert the response status code
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        // Assert the response body
        assertThat(response.getBody()).contains("\"success\":true");
        assertThat(response.getBody()).contains("\"name\":\"John Doe\"");
        assertThat(response.getBody()).contains("\"email\":\"john.doe@example.com\"");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER-ADMIN", "USER-READER"})
    void testGetAuthorById_NotFound() {
        // Define the GraphQL query
        String query = """
        {
            "query": "query { getAuthorById(id: 1) { success errors data { id name email createdAt books { id title price } } } }",
            "variables": null
        }
        """;

        // Create HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity with the query
        HttpEntity<String> entity = new HttpEntity<>(query, headers);

        // Send POST request to GraphQL endpoint
        ResponseEntity<String> response = restTemplate.exchange(GRAPHQL_ENDPOINT, HttpMethod.POST, entity, String.class);

        // Assert the response status code
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        // Assert the response body
        assertThat(response.getBody()).contains("\"success\":false");
        assertThat(response.getBody()).contains("\"errors\"");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER-ADMIN", "USER-READER"})
    void testCreateAuthor_Success() {
        // Define the GraphQL mutation
        String mutation = """
        {
            "query": "mutation { createAuthor(input: { name: \\"Alice\\", email: \\"alice@example.com\\" }) { success errors data { id name email createdAt } } }",
            "variables": null
        }
        """;

        // Create HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity with the mutation
        HttpEntity<String> entity = new HttpEntity<>(mutation, headers);

        // Send POST request to GraphQL endpoint
        ResponseEntity<String> response = restTemplate.exchange(GRAPHQL_ENDPOINT, HttpMethod.POST, entity, String.class);

        // Assert the response status code
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        // Assert the response body
        assertThat(response.getBody()).contains("\"success\":true");
        assertThat(response.getBody()).contains("\"name\":\"Alice\"");
        assertThat(response.getBody()).contains("\"email\":\"alice@example.com\"");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER-ADMIN", "USER-READER"})
    void testDeleteAuthor_Success() {
        // Define the GraphQL mutation
        String mutation = """
        {
            "query": "mutation { deleteAuthor(id: 1231231) { success errors } }",
            "variables": null
        }
        """;

        // Create HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity with the mutation
        HttpEntity<String> entity = new HttpEntity<>(mutation, headers);

        // Send POST request to GraphQL endpoint
        ResponseEntity<String> response = restTemplate.exchange(GRAPHQL_ENDPOINT, HttpMethod.POST, entity, String.class);

        // Assert the response status code
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        // Assert the response body
        assertThat(response.getBody()).contains("\"success\":true");
    }
}