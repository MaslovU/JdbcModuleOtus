package com.maslov.booksmaslov.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Book {
    private final int id;
    private final String name;
    private final String author;
    private final String yearOfPublishing;
    private final String genre;
}