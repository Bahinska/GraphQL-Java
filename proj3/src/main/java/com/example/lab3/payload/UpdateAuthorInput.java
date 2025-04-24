package com.example.lab3.payload;

import lombok.Data;

@Data
public class UpdateAuthorInput {
    private Long id;
    private String name;
    private String email;
}