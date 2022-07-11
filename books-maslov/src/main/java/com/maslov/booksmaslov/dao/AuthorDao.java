package com.maslov.booksmaslov.dao;

import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.repository.AuthorRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthorDao {

    AuthorRepo authorRepo;

    public AuthorDao(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    public List<Author> findAuthorByText(String text) {
        return authorRepo.findByName(text);
    }

    public Author createAuthor(Author author) {
        return authorRepo.save(author);
    }
}