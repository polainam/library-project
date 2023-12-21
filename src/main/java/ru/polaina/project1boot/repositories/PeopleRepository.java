package ru.polaina.project1boot.repositories;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.polaina.project1boot.models.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUserName(String name);
    List<Person> findAllByRole(String role);
    Page<Person> findByRole(@Param("roleName") String roleName, Pageable pageable);
    List<Person> findByUserNameIsStartingWithAndRole(String query, String roleName);
    Optional<Person> findByEmail(String email);
    Optional<Person> findByPhoneNumber(String phoneNumber);
}
