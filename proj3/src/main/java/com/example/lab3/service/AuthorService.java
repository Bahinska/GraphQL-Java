package com.example.lab3.service;

import com.example.lab3.model.Author;
import com.example.lab3.payload.AuthorResponse;
import com.example.lab3.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public AuthorResponse getAuthorResponseById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        return mapToAuthorResponse(author);
    }

    public List<AuthorResponse> getAllAuthorResponses() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(this::mapToAuthorResponse)
                .collect(Collectors.toList());
    }

    public AuthorResponse createAuthorResponse(String name, String email) {
        Author author = new Author();
        author.setName(name);
        author.setEmail(email);
        author.setCreatedAt(LocalDateTime.now());
        Author savedAuthor = authorRepository.save(author);
        return mapToAuthorResponse(savedAuthor);
    }

    public AuthorResponse updateAuthorResponse(Long id, String name, String email) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if (name != null) author.setName(name);
        if (email != null) author.setEmail(email);

        Author updatedAuthor = authorRepository.save(author);
        return mapToAuthorResponse(updatedAuthor);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorResponse mapToAuthorResponse(Author author) {
        AuthorResponse response = new AuthorResponse();
        response.setId(author.getId());
        response.setName(author.getName());
        response.setEmail(author.getEmail());
        response.setCreatedAt(author.getCreatedAt());
        return response;
    }
}