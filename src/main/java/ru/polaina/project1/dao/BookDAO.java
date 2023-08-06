package ru.polaina.project1.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.polaina.project1.models.Book;
import ru.polaina.project1.models.Person;

import java.util.List;
import java.util.Optional;

@Component
public class BookDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> showListOfBooks() {
        return jdbcTemplate.query("SELECT * FROM Book", new BeanPropertyRowMapper<>(Book.class));
    }

    public List<Book> showListOfBooksByPersonId(int personId) {
        return jdbcTemplate.query("SELECT * FROM Book WHERE person_id=?", new Object[]{personId}, new BeanPropertyRowMapper<>(Book.class));
    }

    public void addNewBook(Book book) {
        jdbcTemplate.update("INSERT INTO Book(title, writer, year_of_publishing) VALUES(?, ?, ?)", book.getTitle(), book.getWriter(), book.getYearOfPublishing());
    }

    public Book showInfoAboutBook(int id) {
        return jdbcTemplate.query("SELECT * FROM Book WHERE book_id=?", new Object[]{id}, new BeanPropertyRowMapper<>(Book.class))
                .stream().findAny().orElse(null);
    }

    public void updateInfoAboutBook(int id, Book updatedBook) {
        jdbcTemplate.update("UPDATE Book SET title=?, writer=?, year_of_publishing=? WHERE book_id=?", updatedBook.getTitle(), updatedBook.getWriter(), updatedBook.getYearOfPublishing(), id);
    }

    public void updatePersonId(int id) {
        jdbcTemplate.update("UPDATE Book SET person_id=? WHERE book_id=?", null, id);
    }

    public void updatePersonId(int bookId, int personId) {
        jdbcTemplate.update("UPDATE Book SET person_id=? WHERE book_id=?", personId, bookId);
    }

    public Optional<Book> showInfoAboutBookByTitle(String title) {
        return jdbcTemplate.query("SELECT * FROM Book WHERE title=?", new Object[]{title},
                new BeanPropertyRowMapper<>(Book.class)).stream().findAny();
    }

    public Optional<Book> showInfoAboutBookByWriter(String writer) {
        return jdbcTemplate.query("SELECT * FROM Book WHERE writer=?", new Object[]{writer},
                new BeanPropertyRowMapper<>(Book.class)).stream().findAny();
    }

    public void deleteBook(int id) {
        jdbcTemplate.update("DELETE FROM Book WHERE book_id=?", id);
    }
}
