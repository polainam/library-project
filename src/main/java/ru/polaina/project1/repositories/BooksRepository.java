package ru.polaina.project1.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.polaina.project1.models.Book;
import ru.polaina.project1.models.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findByTitle(String title);
    List<Book> findByWriter(String writer);

    List<Book> findByPersonId(Person personId);

    List<Book> findByTitleIsStartingWith(String query);
}
