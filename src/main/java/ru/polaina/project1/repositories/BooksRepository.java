package ru.polaina.project1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.polaina.project1.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {

    //List<Book> findByPersonId(int personId);
    Optional<Book> findByTitle(String title);
    List<Book> findByWriter(String writer);
}
