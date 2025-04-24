package com.example.lab3.service;

import com.example.lab3.payload.BookResponse;
import com.example.lab3.payload.AuthorResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookService {

    private final WebClient webClient;

    public Mono<Map<AuthorResponse, List<BookResponse>>> getBooksByUserIds(List<AuthorResponse> users) {
        // Extract user IDs from UserResponse
        String userIdsParam = String.join(",", users.stream().map(user -> String.valueOf(user.getId())).toList());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/book")
                        .queryParam("user_ids", userIdsParam)
                        .build())
                .retrieve()
                .bodyToFlux(BookResponse.class)
                .collectList()
                .map(productResponses -> {
                    // Group products by userId
                    Map<Long, List<BookResponse>> productsByUserId = productResponses.stream()
                            .collect(Collectors.groupingBy(BookResponse::getAuthorId));

                    // Map userId to UserResponse
                    return users.stream()
                            .collect(Collectors.toMap(
                                    user -> user,
                                    user -> productsByUserId.getOrDefault(user.getId(), List.of())
                            ));
                });
    }
}