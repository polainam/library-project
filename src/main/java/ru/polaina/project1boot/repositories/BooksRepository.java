package ru.polaina.project1boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    Optional<Book> findByTitle(String title);

    List<Book> findByWriter(String writer);

    List<Book> findByTitleIsStartingWith(String query);

}
