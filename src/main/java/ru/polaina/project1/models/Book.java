package ru.polaina.project1.models;

import jakarta.validation.constraints.NotEmpty;

public class Book {

    private int bookId;
    private String personId;

    @NotEmpty(message = "Title should not be empty")
    private String title;

    @NotEmpty(message = "Writer should not be empty")
    private String writer;

    private int yearOfPublishing;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public int getYearOfPublishing() {
        return yearOfPublishing;
    }

    public void setYearOfPublishing(int yearOfPublishing) {
        this.yearOfPublishing = yearOfPublishing;
    }

}
