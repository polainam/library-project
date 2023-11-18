package ru.polaina.project1boot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.repositories.PeopleRepository;
import ru.polaina.project1boot.security.PersonDetails;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    @Transactional
    public void save(Person person) {
        String encodedPassword = passwordEncoder.encode(person.getPassword());
        person.setPassword(encodedPassword);
        person.setRole("ROLE_USER");
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setPersonId(id);
        peopleRepository.save(updatedPerson);
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElse(null);
    }

    public Optional<Person> findByName(String name) {
        return peopleRepository.findByFullName(name);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByFullName(s);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new PersonDetails(person.get());
    }

    public List<Person> findAllUsers(String role) {
        return peopleRepository.findAllByRole(role);
    }

/*    public List<Person> findAll(Integer page, Integer peoplePerPage) {
        return peopleRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }*/

    public Page<Person> findUsersWithRole(Integer page, Integer peoplePerPage) {
        String roleName = "ROLE_USER";
        Pageable pageable = PageRequest.of(page, peoplePerPage);

        return peopleRepository.findByRole(roleName, pageable);
    }
}
