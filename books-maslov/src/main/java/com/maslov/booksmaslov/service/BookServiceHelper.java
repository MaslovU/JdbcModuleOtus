package com.maslov.booksmaslov.service;

import com.maslov.booksmaslov.dao.AuthorDao;
import com.maslov.booksmaslov.dao.BookDao;
import com.maslov.booksmaslov.dao.GenreDao;
import com.maslov.booksmaslov.dao.YearDao;
import com.maslov.booksmaslov.domain.Author;
import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.domain.Comment;
import com.maslov.booksmaslov.domain.Genre;
import com.maslov.booksmaslov.domain.YearOfPublish;
import com.maslov.booksmaslov.exception.NoAuthorException;
import com.maslov.booksmaslov.exception.NoBookException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class BookServiceHelper {

    private final ScannerHelper helper;
    private final AuthorDao authorDao;
    private final YearDao yearDao;
    private final GenreDao genreDao;
    private final BookDao bookDao;

    public BookServiceHelper(ScannerHelper helper, AuthorDao authorDao, YearDao yearDao, GenreDao genreDao, BookDao bookDao) {
        this.helper = helper;
        this.authorDao = authorDao;
        this.yearDao = yearDao;
        this.genreDao = genreDao;
        this.bookDao = bookDao;
    }

    public Book getBookFromUser(long idOfBook) {
        String name = getNameOfBook(idOfBook);

        List<Author> authors = getListAuthors(idOfBook);

        val year = getYear(name);

        val genre = getGenre(name);

        List<Comment> comments = getComments(idOfBook);

        Book book = new Book();
        book.setAuthor(authors);
        book.setName(name);
        book.setGenre(genre);
        book.setListOfComment(comments);
        book.setYear(year);
        return book;
    }

    private String getNameOfBook(long idOfBook) {
        System.out.println("Enter name of the book");
        String name = helper.getFromUser();
        if (name.isEmpty()) {
            try {
                return bookDao.getBookById(idOfBook).get().getName();
            } catch (Exception e) {
                System.out.println("Has not this book, need enter new name");
                throw new NoBookException("Has not this book, need enter new name");
            }

        } else {
            return name;
        }
    }

    private long getAuthorId(String authorName) {
        if (authorDao.findAuthorByText(authorName).isEmpty()) {
            Author author = authorDao.createAuthor(Author.builder().name(authorName).build());
            return author.getId();
        } else {
            return authorDao.findAuthorByText(authorName).get(0).getId();
        }
    }

    private List<Author> getListAuthors(long idOfBook) {
        System.out.println("Enter new names of the authors");
        List<String> authorNames = List.of(helper.getFromUser().split(","));
        List<Author> authors = new ArrayList<>();
        if (authorNames.get(0).isEmpty() && authorNames.size() == 1) {
            try {
                return bookDao.getBookById(idOfBook).get().getAuthor();
            } catch (NoSuchElementException e) {
                throw new NoAuthorException("Has not authors for this book. Need enter names");
            }
        } else {
            for (String s : authorNames) {
                long authorId = getAuthorId(s);
                Author author = Author.builder().id(authorId).name(s).build();
                authors.add(author);
            }
            return authors;
        }
    }

    private YearOfPublish getYear(String nameOfBook) {
        System.out.println("Enter new years of publish");
        String year = helper.getFromUser();
        if (year.isEmpty()) {
            return yearDao.getYearByDate(bookDao.getBooksByName(nameOfBook).get(0).getYear().getDateOfPublish());
        } else {
            long yearId;
            try {
                yearId = yearDao.getYearByDate(year).getId();
            } catch (NullPointerException e) {
                yearId = yearDao.createYear(YearOfPublish.builder().dateOfPublish(year).build()).getId();
            }
            return YearOfPublish.builder().id(yearId).dateOfPublish(year).build();
        }
    }

    private Genre getGenre(String nameOfBook) {
        System.out.println("Enter new name of the genre");
        String genre = helper.getFromUser();
        if (genre.isEmpty()) {
            return genreDao.getGenreByName(bookDao.getBooksByName(nameOfBook).get(0).getGenre().getName());
        } else {
            long genreId;
            try {
                genreId = genreDao.getGenreByName(genre).getId();
            } catch (NullPointerException e) {
                genreId = genreDao.createGenre(Genre.builder().name(genre).build()).getId();
            }
            return Genre.builder().id(genreId).name(genre).build();
        }
    }

    private List<Comment> getComments(long ibOfBook) {
        System.out.println("You can add comment to this book or press enter");
        String comment = helper.getFromUser();
        if (comment.isEmpty()) {
            try {
                return bookDao.getBookById(ibOfBook).get().getListOfComment();
            } catch (NoSuchElementException | NullPointerException e) {
                return new ArrayList<>();
            }
        } else {
            val comm = Comment.builder().commentForBook(comment).build();
            List<Comment> comments = bookDao.getBookById(ibOfBook).get().getListOfComment();
            comments.add(comm);
            return comments;
        }
    }
}
