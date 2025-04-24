package com.example.lab3.payload;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseType<T> {
    private boolean success;
    private List<String> errors;
    private T data;
}