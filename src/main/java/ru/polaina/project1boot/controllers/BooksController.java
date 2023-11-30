package ru.polaina.project1boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.polaina.project1boot.util.BookValidator;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService bookService;
    private  final JournalService journalService;
    private final BookValidator bookValidator;
    private final PeopleService peopleService;

    private static final int NUMBER_OF_DAYS_OF_RESERVE = 3;

    @Autowired
    public BooksController(BooksService bookService, JournalService journalService, BookValidator bookValidator, PeopleService peopleService) {
        this.bookService = bookService;
        this.journalService = journalService;
        this.bookValidator = bookValidator;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String listOfBooks(Model model,
                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                              @RequestParam(value = "books_per_page", required = false, defaultValue = "10") Integer booksPerPage,
                              @RequestParam(value = "sort_by_year", required = false) String sortByYear,
                              Authentication authentication) {
        int totalBooks = bookService.countAll();

        int totalPages = (int) Math.ceil((double) totalBooks / booksPerPage) - 1;

        List<Book> books = bookService.findAll(page, booksPerPage);

        model.addAttribute("booksPerPage", booksPerPage);
        model.addAttribute("books", books);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        Person person = ((PersonDetails) authentication.getPrincipal()).getPerson();
        model.addAttribute("personId", person.getPersonId());

        return "books/listOfBooks";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("newBook") Book book) {
        return "books/admin/newBook";
    }

    @PostMapping()
    public String insertNewBook(@ModelAttribute("newBook") @Valid Book book, BindingResult bindingResult) {
        if (book.getNumberOfCopies() == null) {
            book.setNumberOfCopies(0);
        }
        bookValidator.validateNumberOfCopies(book, bindingResult);
        bookValidator.validate(book, bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/admin/newBook";
        }
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/user/{id}")
    public String pageBookForUser(@PathVariable("id") int id, Authentication authentication, Model model) {
        Person person = ((PersonDetails) authentication.getPrincipal()).getPerson();
        model.addAttribute("infoAboutPerson", person);
        Integer countOfBooksTakenByPerson = journalService.countAllByPersonId(person.getPersonId());
        model.addAttribute("countOfBooksTakenByPerson", countOfBooksTakenByPerson);
        getInfoAboutBook(person.getPersonId(), id, model);
        LocalDate localDate = LocalDate.now();
        Date currentDate = java.sql.Date.valueOf(localDate);
        model.addAttribute("currentDate", currentDate);

        return "books/user/pageBook";
    }

    private void getInfoAboutBook(int personId, int bookId, Model model) {
        Book book = bookService.findOne(bookId);

        boolean isBookReserved = journalService.isBookReserved(bookId, personId);
        if (isBookReserved) {
            Date dateEndReserve = journalService.getJournalEntry(bookId, personId).getDateEndReserve();
            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(dateEndReserve);*/
            model.addAttribute("dateEndReserve", dateEndReserve);
        }

        boolean isBookBorrowed = journalService.isBookBorrowed(bookId, personId);
        if (isBookBorrowed) {
            Date dateEnd = journalService.getJournalEntry(bookId, personId).getDateEnd();
           /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(dateEnd);*/
            model.addAttribute("dateEnd", dateEnd);
        }

        model.addAttribute("isBookReserved", isBookReserved);
        model.addAttribute("isBookBorrowed", isBookBorrowed);
        model.addAttribute("infoAboutBook", book);
    }

    @GetMapping("/admin/{id}")
    public String pageBookForAdmin(@PathVariable("id") int id, Model model) {
        Book book = bookService.findOne(id);
        int countBooks = book.getNumberOfCopies();
        if (countBooks == 1) {
            model.addAttribute("isOne", true);
        }
        else {
            model.addAttribute("isOne", false);
        }
        model.addAttribute("infoAboutBook", book);
        model.addAttribute("countBooks", countBooks);

        return "books/admin/pageBook";
    }

    @PatchMapping ("/{book_id}/{person_id}/reserve")
    public String reserve(@PathVariable("book_id") int bookId, @PathVariable("person_id") int personId, Journal journal, Model model) {
        Date dateReserve = new Timestamp(System.currentTimeMillis());
        journal.setDateReserve(dateReserve);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateReserve);
        calendar.add(Calendar.DAY_OF_MONTH, NUMBER_OF_DAYS_OF_RESERVE);
        Date dateEndReserve = calendar.getTime();
        journal.setDateEndReserve(dateEndReserve);

        Book book = bookService.findOne(bookId);
        book.reduceNumberOfCopies();
        bookService.save(book);
        journal.setBook(book);

        Person person = peopleService.findOne(personId);
        journal.setPerson(person);

        journalService.save(journal);

        return "redirect:/books/user/" + bookId;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/{id}/edit")
    public String bookEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("editBook", bookService.findOne(id));
        return "books/admin/editBook";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public String updateBook(@ModelAttribute("editBook") @Valid Book editBook, BindingResult bindingResult, @PathVariable("id") int bookId, Model model) {
        if (editBook.getNumberOfCopies() == null) {
            editBook.setNumberOfCopies(0);
        }
        bookValidator.validateNumberOfCopies(editBook, bindingResult);
        if (!bookService.isNewBookTheSame(bookId, editBook)) {
            bookValidator.validate(editBook, bindingResult);
        }
        if (bindingResult.hasErrors()) {
            editBook.setBookId(bookId);
            model.addAttribute("editBook", editBook);
            return "books/admin/editBook";
        }
        bookService.update(bookId, editBook);
        return "redirect:/books/admin/" + bookId;
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam String query, Model model) {
        if (query.isEmpty()) {
            return "redirect:/books";
        }
        List<Book> searchBooks = bookService.findByTitleIsStartingWith(query);
        model.addAttribute("books", searchBooks);
        return "books/listOfBooks";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/admin/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }

    @DeleteMapping("/{book_id}/{person_id}/reserve")
    public String deleteReservation(@PathVariable("book_id") int bookId, @PathVariable("person_id") int personId, Model model) {
        Journal journalEntry = journalService.getJournalEntry(bookId, personId);
        journalService.delete(journalEntry);
        Book book = bookService.findOne(bookId);
        book.increaseNumberOfCopies();
        bookService.save(book);

        return "redirect:/books/user/" + bookId;
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/pageBookForPerson/{person_id}/{book_id}")
    public String pageBookForPerson(@PathVariable("person_id") int personId, @PathVariable("book_id") int bookId, Model model) {
        getInfoAboutBook(personId, bookId, model);
        LocalDate localDate = LocalDate.now();
        Date currentDate = java.sql.Date.valueOf(localDate);
        model.addAttribute("currentDate", currentDate);

        return "/books/admin/pageBookForPerson";
    }

}
