package ru.polaina.project1boot.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Journal;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.JournalService;
import ru.polaina.project1boot.services.PeopleService;

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

    private static final int NUMBER_OF_READING_DAYS = 31;


    @Autowired
    public PeopleController(PeopleService peopleService, JournalService journalService, BooksService booksService) {
        this.peopleService = peopleService;
        this.journalService = journalService;
        this.booksService = booksService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping()
    public String listOfPeople(Model model) {
        model.addAttribute("people", peopleService.findAllUsers("ROLE_USER"));
        return "people/listOfPeople";
    }

/*    @GetMapping("/new")
    public String newPerson(@ModelAttribute("newPerson")Person person) {
        return "people/newPerson";
    }
    @PostMapping()
    public String insertNewPerson(@ModelAttribute("newPerson") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "people/newPerson";
        }
        peopleService.save(person);
        return "redirect:/people";
    }*/

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
        model.addAttribute("bookId", 0); // или любое начальное значение по умолчанию
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

    @GetMapping("/{id}/edit")
    public String personEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("editPerson", peopleService.findOne(id));
        return "people/editPerson";
    }

    @PatchMapping("/{id}")
    public String updateInfoAboutPerson(@ModelAttribute("editPerson") @Valid Person editPerson, @PathVariable("id") int id) {
        peopleService.update(id, editPerson);
        return "redirect:/people";
    }

    //Будет ошибка, тк on delete по умолчанию (Но если у человека нет книг, то удалится)
    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/people";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/assign")
    public String assignBook(@PathVariable("id") int personId, @RequestParam int bookId, Journal journal, Model model) {
        Person person = peopleService.findOne(personId);
        assignBook(journal, bookId, person);
        Integer countOfBooksTakenByPerson = journalService.countAllByPersonId(personId);
        model.addAttribute("countOfBooksTakenByPerson", countOfBooksTakenByPerson);
        model.addAttribute("bookId", 0); // или любое начальное значение по умолчанию

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
        model.addAttribute("bookId", 0); // или любое начальное значение по умолчанию

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
}
