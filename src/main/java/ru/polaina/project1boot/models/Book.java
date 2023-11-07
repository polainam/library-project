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

/*    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person personId;*/

    @OneToMany(mappedBy = "book")
    private List<Journal> journalEntries;

    @NotEmpty(message = "Title should not be empty")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Writer should not be empty")
    @Column(name = "writer")
    private String writer;

    @Column(name = "year_of_publishing")
    private int yearOfPublishing;

    @Column(name = "number_of_copies")
    private int numberOfCopies;

/*    @Column(name = "date_of_receiving")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateOfReceiving;*/

/*    @Transient
    private boolean isReturnTimeOverdue;*/

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

/*    public Person getPersonId() {
        return personId;
    }

    public void setPersonId(Person personId) {
        this.personId = personId;
    }*/

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

    public List<Journal> getJournalEntries() {
        return journalEntries;
    }

    public void setJournalEntries(List<Journal> journalEntries) {
        this.journalEntries = journalEntries;
    }

    /*    public Date getDateOfReceiving() {
        return dateOfReceiving;
    }

    public void setDateOfReceiving(Date dateOfReceiving) {
        this.dateOfReceiving = dateOfReceiving;
    }

    public boolean isReturnTimeOverdue() {
        Date currentTime = new Timestamp(System.currentTimeMillis());
        long differenceInMillis = currentTime.getTime() - dateOfReceiving.getTime();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

        return differenceInDays > 10;
    }*/

/*    public void setReturnTimeOverdue(boolean returnTimeOverdue) {
        isReturnTimeOverdue = returnTimeOverdue;
    }*/

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public void reduceNumberOfCopies() {
        setNumberOfCopies(getNumberOfCopies() - 1);
    }

    public void increaseNumberOfCopies() {
        setNumberOfCopies(getNumberOfCopies() + 1);
    }
}
