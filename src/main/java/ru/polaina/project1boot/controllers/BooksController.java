package ru.polaina.project1boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.PeopleService;
import ru.polaina.project1boot.util.BookValidator;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService bookService;
    private  final PeopleService peopleService;
    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BooksService bookService, PeopleService peopleService, BookValidator bookValidator) {
        this.bookService = bookService;
        this.peopleService = peopleService;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public String listOfBooks(Model model,
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                              @RequestParam(value = "sort_by_year", required = false) String sortByYear) {
        if (page != null && booksPerPage != null && !Objects.equals(sortByYear, "true")) {
            model.addAttribute("books", bookService.findAll(page, booksPerPage));
        }
        else if (page == null && booksPerPage == null && Objects.equals(sortByYear, "true")) {
            model.addAttribute("books", bookService.findAll("yearOfPublishing"));
        }
        else if (page != null && booksPerPage != null && Objects.equals(sortByYear, "true")) {
            model.addAttribute("books", bookService.findAll(page, booksPerPage, "yearOfPublishing"));
        }
        else {
            model.addAttribute("books", bookService.findAll());
        }
        return "books/listOfBooks";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("newBook") Book book) {
        return "books/newBook";
    }

    @PostMapping()
    public String insertNewBook(@ModelAttribute("newBook") @Valid Book book, BindingResult bindingResult) {
        bookValidator.validate(book, bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/newBook";
        }
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String pageBook(@PathVariable("id") int id, Model model) {
        Book book = bookService.findOne(id);
        model.addAttribute("infoAboutBook", book);
        if (book.getPersonId() == null) {
            model.addAttribute("people", peopleService.findAll());
        } else {
            model.addAttribute("infoAboutPerson", book.getPersonId());
        }
        return "books/pageBook";
    }

    @PatchMapping("/{id}/free")
    public String freeBook(@PathVariable("id") int id, Model model) {
        bookService.update(id);
        model.addAttribute("infoAboutBook", bookService.findOne(id));
        model.addAttribute("people", peopleService.findAll());
        return "books/pageBook";
    }

    @PatchMapping("/{id}/nonfree")
    public String assignBook(@PathVariable("id") int bookId, @RequestParam int personId, Model model) {
        Person person = peopleService.findOne(personId);
        bookService.update(person, bookId);
        model.addAttribute("infoAboutBook", bookService.findOne(bookId));
        model.addAttribute("infoAboutPerson", peopleService.findOne(personId));
        return "books/pageBook";
    }

    @GetMapping("/{id}/edit")
    public String bookEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("editBook", bookService.findOne(id));
        return "books/editBook";
    }

    @PatchMapping("/{id}")
    public String updateInfoAboutBook(@ModelAttribute("editBook") @Valid Book editBook, @PathVariable("id") int id) {
        bookService.update(id, editBook);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam String query, Model model) {
        List<Book> searchBooks = bookService.findByTitleIsStartingWith(query);
        model.addAttribute("books", searchBooks);
        return "books/listOfBooks";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookService.delete(id);
        return "redirect:/books";
    }
}
