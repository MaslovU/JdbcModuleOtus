package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;

import java.util.List;
import java.util.Set;

public interface BookService {
    void getBook();

    void getAllBook();

    Book createBook();

    void updateBook();

    void delBook();

    List<Comment> getComments();
}
