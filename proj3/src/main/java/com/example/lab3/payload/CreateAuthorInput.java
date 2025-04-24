package com.example.lab3.payload;

import lombok.Data;

@Data
public class CreateAuthorInput {
    private String name;
    private String email;
}