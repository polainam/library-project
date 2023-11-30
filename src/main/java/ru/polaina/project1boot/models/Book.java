package ru.polaina.project1boot.models;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Book")
public class Book {

    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookId;

    @OneToMany(mappedBy = "book")
    private List<Journal> journalEntries;

    @NotEmpty(message = "Title should not be empty")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Writer should not be empty")
    @Column(name = "writer")
    private String writer;

    @NotEmpty(message = "Year of publishing should not be empty")
    @Column(name = "year_of_publishing")
    private String yearOfPublishing;

    @Column(name = "number_of_copies")
    private Integer numberOfCopies;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
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

    public String getYearOfPublishing() {
        return yearOfPublishing;
    }

    public void setYearOfPublishing(String yearOfPublishing) {
        this.yearOfPublishing = yearOfPublishing;
    }

    public List<Journal> getJournalEntries() {
        return journalEntries;
    }

    public void setJournalEntries(List<Journal> journalEntries) {
        this.journalEntries = journalEntries;
    }

    public Integer getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(Integer numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public void reduceNumberOfCopies() {
        setNumberOfCopies(getNumberOfCopies() - 1);
    }

    public void increaseNumberOfCopies() {
        setNumberOfCopies(getNumberOfCopies() + 1);
    }
}
