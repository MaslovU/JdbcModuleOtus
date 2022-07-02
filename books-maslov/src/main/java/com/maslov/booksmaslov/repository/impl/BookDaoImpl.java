package com.maslov.booksmaslov.repository.impl;

import com.maslov.booksmaslov.domain.Book;
import com.maslov.booksmaslov.repository.AuthorDao;
import com.maslov.booksmaslov.repository.BookDao;
import com.maslov.booksmaslov.repository.GenreDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
public class BookDaoImpl implements BookDao {
    private final JdbcOperations jdbc;

    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookDaoImpl(JdbcOperations jdbc, AuthorDao authorDao, GenreDao genreDao) {
        this.jdbc = jdbc;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    @Override
    public List<Book> getAllBook() {
        return jdbc.query("select * from book", new BookMapper(authorDao, genreDao));
    }

    @Override
    public Book getBookById(int id) {
        try {
            return jdbc.queryForObject("select * from public.book where id =?", new BookMapper(authorDao, genreDao), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Book with this id is not exist");
        }
        return null;
    }

    @Override
    public List<Book> getBooksByName(String name) {
        return jdbc.query("select * from book where name =?", new BookMapper(authorDao, genreDao), name);
    }

    @Override
    public void createBook(String name, String author, String year, String genre) {
        int id = getAllBook().size() + 1;
        String authorId = authorDao.getAuthorId(author);
        String genreId = genreDao.getAuthorId(genre);
        jdbc.update("insert into book (id, name, author_id, year_of_publishing, genre_id) " +
                "values (?, ?, ?, ?, ?)", id, name, authorId, year, genreId);
    }

    @Override
    public Book updateBook(int id, String name, String author, String year, String genre) {
        jdbc.update("update book set id=?, name=?, author=?, year=?, genre=? " +
                "where id=?", id, name, author, year, genre);
        return getBookById(id);
    }

    @Override
    public void deleteBook(int id) {
        jdbc.update("delete from book where id=?", id);
    }

    private static class BookMapper implements RowMapper<Book> {

        private final AuthorDao authorDao;
        private final GenreDao genreDao;

        private BookMapper(AuthorDao authorDao, GenreDao genreDao) {
            this.authorDao = authorDao;
            this.genreDao = genreDao;
        }

        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {

            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int authorId = Integer.parseInt(resultSet.getString("author_id"));
            String author = authorDao.getNameById(authorId);
            String year = resultSet.getString("year_of_publishing");
            int genreId = Integer.parseInt(resultSet.getString("genre_id"));
            String genre = genreDao.getNameById(genreId);
            return new Book(id, name, author, year, genre);
        }
    }
}
