package ru.polaina.project1boot.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public List<Book> findAll(Integer page, Integer booksPerPage) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    public List<Book> findAll(String yearOfPublishing) {
        return booksRepository.findAll(Sort.by(yearOfPublishing));
    }

    public List<Book> findAll(Integer page, Integer booksPerPage, String yearOfPublishing) {
        return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by(yearOfPublishing))).getContent();
    }

/*    public List<Book> findByPersonId(Person personId) {
        //return journalRepository.findByPersonId(personId);
        return booksRepository.findByPersonId(personId);
    }*/

    @Transactional
    public void save(Book book) {
        booksRepository.save(book);
    }

    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElse(null);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setBookId(id);
        booksRepository.save(updatedBook);
    }

    public Optional<Book> findByTitleAndWriterAndYearOfPublishing(Book book) {
        return booksRepository.findByTitleAndWriterAndYearOfPublishing(book.getTitle(), book.getWriter(), book.getYearOfPublishing());
    }

    public List<Book> findByWriter(String writer) {
        return booksRepository.findByWriter(writer);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }


    public List<Book> findByTitleIsStartingWith(String query) {
        return booksRepository.findByTitleIsStartingWith(query);
    }

    public int countAll() {
        return findAll().size();
    }

    public boolean isNewBookTheSame(int bookId, Book editBook) {
        Book oldBook = findOne(bookId);
        return oldBook.getTitle().equals(editBook.getTitle()) &&
                oldBook.getWriter().equals(editBook.getWriter()) &&
                oldBook.getYearOfPublishing().equals(editBook.getYearOfPublishing());
    }
}
