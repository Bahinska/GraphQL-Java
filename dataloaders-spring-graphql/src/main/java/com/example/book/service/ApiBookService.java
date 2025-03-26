package com.example.book.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.book.model.api.ApiBook;
import com.example.book.utils.JsonUtils;

import java.util.List;

@Service
public class ApiBookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiBookService.class);

    private final List<ApiBook> apiBooks;

    public ApiBookService() {
        this.apiBooks = JsonUtils.loadListFromJsonFile(
                "/data/api-books.json", ApiBook[].class);
    }

    // /api/books
    public List<ApiBook> getApiBooks() {
        LOGGER.info("REST API: get all books");
        return apiBooks;
    }
}
