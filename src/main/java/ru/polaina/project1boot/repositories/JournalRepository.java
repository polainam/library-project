package ru.polaina.project1boot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Integer> {

    List<Journal> findAllByBookId(int bookId);

    Optional<Journal> findIdByBookIdAndPersonId(int bookId, int personId);

    //void deleteByDateReserveBefore(Date expirationDate);

    void deleteByDateEndReserveBefore(Date currentDate);

    List<Journal> findByPersonIdAndDateReserveNotNull(int personId);

    List<Journal> findByPersonIdAndDateBeginNotNull(int personId);
    List<Journal> findAllByPersonIdAndDateEndBefore(int personId, Date currentDate);

    List<Journal> findAllByPersonId(int personId);

    Integer countAllByPersonId(int personId);
    void deleteByBookId(int id);
    void deleteByPersonId(int id);

}
