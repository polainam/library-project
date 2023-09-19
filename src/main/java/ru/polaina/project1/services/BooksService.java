package ru.polaina.project1.services;

import org.hibernate.Hibernate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polaina.project1.models.Book;
import ru.polaina.project1.models.Person;
import ru.polaina.project1.repositories.BooksRepository;

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

/*    public List<Book> findByPersonId(int personId) {
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

    public Optional<Book> findByTitle(String title) {
        return booksRepository.findByTitle(title);
    }

    public List<Book> findByWriter(String writer) {
        return booksRepository.findByWriter(writer);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void update(int bookId) {
        Book updatedBook = findOne(bookId);
        updatedBook.setPersonId(null);
        booksRepository.save(updatedBook);
    }

    @Transactional
    public void update(Person personId, int bookId) { //personId был int
        Book updatedBook = findOne(bookId);
        updatedBook.setPersonId(personId);
        booksRepository.save(updatedBook);
    }
}
