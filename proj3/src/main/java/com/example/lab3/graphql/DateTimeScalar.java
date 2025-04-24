package com.example.lab3.graphql;

import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeScalar {

    public static final GraphQLScalarType DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("A custom scalar to handle DateTime")
            .coercing(new Coercing<LocalDateTime, String>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

                @Override
                public String serialize(Object dataFetcherResult) {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return ((LocalDateTime) dataFetcherResult).format(formatter);
                    }
                    throw new IllegalArgumentException("Expected a LocalDateTime object.");
                }

                @Override
                public LocalDateTime parseValue(Object input) {
                    if (input instanceof String) {
                        return LocalDateTime.parse((String) input, formatter);
                    }
                    throw new IllegalArgumentException("Expected a String in ISO_DATE_TIME format.");
                }

                @Override
                public LocalDateTime parseLiteral(Object input) {
                    if (input instanceof String) {
                        return LocalDateTime.parse((String) input, formatter);
                    }
                    throw new IllegalArgumentException("Expected a String in ISO_DATE_TIME format.");
                }
            })
            .build();
}