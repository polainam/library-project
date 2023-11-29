package ru.polaina.project1boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.security.PersonDetails;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.JournalService;
import ru.polaina.project1boot.services.PeopleService;
import ru.polaina.project1boot.util.PersonValidator;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final JournalService journalService;
    private final BooksService booksService;

    private final PersonValidator personValidator;

    private static final int NUMBER_OF_READING_DAYS = 31;


    @Autowired
    public PeopleController(PeopleService peopleService, JournalService journalService, BooksService booksService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.journalService = journalService;
        this.booksService = booksService;
        this.personValidator = personValidator;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping()
    public String listOfPeople(Model model,
                               @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                               @RequestParam(value = "people_per_page", required = false, defaultValue = "10") Integer peoplePerPage) {
        List<Person> users = peopleService.findAllUsers("ROLE_USER");
        int totalPeople = users.size();
        int totalPages = (int) Math.ceil((double) totalPeople / peoplePerPage) - 1;
        Page<Person> people = peopleService.findUsersWithRole(page, peoplePerPage);
        model.addAttribute("people", people);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("peoplePerPage", peoplePerPage);

        return "people/listOfPeople";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/{id}")
    public String pagePersonForAdmin(@PathVariable("id") int id, Model model) {
        Person person = peopleService.findOne(id);
        model.addAttribute("infoAboutPerson", person);
        List<Journal> reservedBooks = journalService.findByPersonIdAndDateReserveNotNull(person.getPersonId());
        model.addAttribute("reservedBooks", reservedBooks);
        List<Journal> borrowedBooks = journalService.findByPersonIdAndDateBeginNotNull(person.getPersonId());
        model.addAttribute("borrowedBooks", borrowedBooks);

        List<Journal> journalList = journalService.findAllByPersonId(id);
        List<Book> books = booksService.findAll();
        for (Journal bookFromJournalList : journalList) {
            if (bookFromJournalList.getDateBegin() != null) {
                Book book = bookFromJournalList.getBook();
                books.remove(book);
            }
        }
        books.removeIf(book -> book.getNumberOfCopies() == 0);
        model.addAttribute("books", books);
        Integer countOfBooksTakenByPerson = journalService.countAllByPersonId(id);
        model.addAttribute("countOfBooksTakenByPerson", countOfBooksTakenByPerson);
        model.addAttribute("bookId", 0);
        Date currentDate = Calendar.getInstance().getTime();
        model.addAttribute("currentDate", currentDate);

        return "people/admin/pagePerson";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/user/{id}")
    public String pagePersonForUser(@PathVariable("id") int id, Model model) {
        Person person = peopleService.findOne(id);
        model.addAttribute("infoAboutPerson", person);
        List<Journal> reservedBooks = journalService.findByPersonIdAndDateReserveNotNull(person.getPersonId());
        model.addAttribute("reservedBooks", reservedBooks);
        List<Journal> borrowedBooks = journalService.findByPersonIdAndDateBeginNotNull(person.getPersonId());
        model.addAttribute("borrowedBooks", borrowedBooks);
        Date currentDate = Calendar.getInstance().getTime();
        model.addAttribute("currentDate", currentDate);

        return "people/user/pagePerson";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}/edit")
    public String personEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("editPerson", peopleService.findOne(id));
        return "people/editPerson";
    }

    @PatchMapping("/{id}")
    public String updateInfoAboutPerson(@ModelAttribute("editPerson") @Valid Person editPerson, BindingResult bindingResult, @PathVariable("id") int id, Model model) {
        if (!peopleService.isNewUsernameTheSame(id, editPerson)) {
            personValidator.validate(editPerson, bindingResult);
        }
        if (!peopleService.isNewEmailTheSame(id, editPerson)) {
            personValidator.validateEmail(editPerson, bindingResult);
        }
        if (!peopleService.isNewPhoneNumberTheSame(id, editPerson)) {
            personValidator.validatePhoneNumber(editPerson, bindingResult);
        }
        if(bindingResult.hasErrors()) {
            editPerson.setPersonId(id);
            model.addAttribute("editPerson", editPerson);
            return "people/editPerson";
        }
        peopleService.update(id, editPerson);
        return "redirect:/people/user/" + id;
    }

    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id, Authentication authentication) {
        peopleService.delete(id);
        Person person = ((PersonDetails) authentication.getPrincipal()).getPerson();
        if (person.getRole().equals("ROLE_USER")) {
            return "redirect:/auth/registration";
        }
        return "redirect:/people";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/assign")
    public String assignBook(@PathVariable("id") int personId, @RequestParam int bookId, Journal journal, Model model) {
        Person person = peopleService.findOne(personId);
        assignBook(journal, bookId, person);
        Integer countOfBooksTakenByPerson = journalService.countAllByPersonId(personId);
        model.addAttribute("countOfBooksTakenByPerson", countOfBooksTakenByPerson);
        model.addAttribute("bookId", 0);

        return "redirect:/people/admin/" + personId;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/assignAllReserved")
    public String assignAllReservedBooks(@PathVariable("id") int personId, Journal journal, Model model) {
        Person person = peopleService.findOne(personId);
        List<Journal> reservedBooks = journalService.findByPersonIdAndDateReserveNotNull(person.getPersonId());
        for (Journal reservedBook : reservedBooks) {
            assignBook(journal, reservedBook.getBookId(), person);
        }
        Integer countOfBooksTakenByPerson = journalService.countAllByPersonId(personId);
        model.addAttribute("countOfBooksTakenByPerson", countOfBooksTakenByPerson);
        model.addAttribute("bookId", 0);

        return "redirect:/people/admin/" + personId;
    }

    private void assignBook(Journal journal, int bookId, Person person) {
        Journal journalEntry = journalService.getJournalEntry(bookId, person.getPersonId());
        if (journalEntry != null) {
            journal = journalEntry;
            journal.setDateReserve(null);
            journal.setDateEndReserve(null);
        }

        Date dateBegin = new Timestamp(System.currentTimeMillis());
        journal.setDateBegin(dateBegin);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateBegin);
        calendar.add(Calendar.DAY_OF_MONTH, NUMBER_OF_READING_DAYS);
        Date dateEnd = calendar.getTime();
        journal.setDateEnd(dateEnd);

        Book book = booksService.findOne(bookId);
        if (journalEntry == null) {
            book.reduceNumberOfCopies();
            booksService.save(book);
            journal.setBook(book);
        }

        journal.setPerson(person);

        journalService.save(journal);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/return/{id}")
    public String returnBorrowedBooks(@PathVariable("id") Person person, Model model) {
        model.addAttribute("person", person);
        List<Journal> borrowedBooks = journalService.findByPersonIdAndDateBeginNotNull(person.getPersonId());
        model.addAttribute("borrowedBooks", borrowedBooks);

        return "people/admin/return/returnBorrowedBooks";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/return/{id}")
    public String returnBooks(@PathVariable("id") int id, @RequestParam("returnedBooksId") List<Integer> returnedBooksId) {
        journalService.returnBooks(returnedBooksId);
        return "redirect:/people/admin/" + id;
    }

    @GetMapping("/search")
    public String searchPeople(@RequestParam String query, Model model) {
        List<Person> searchPeople = peopleService.findByTitleIsStartingWith(query);
        model.addAttribute("people", searchPeople);
        return "people/listOfPeople";
    }
}
