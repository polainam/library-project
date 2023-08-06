package ru.polaina.project1.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1.dao.BookDAO;
import ru.polaina.project1.dao.PersonDAO;
import ru.polaina.project1.models.Book;
import ru.polaina.project1.util.BookValidator;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookDAO bookDAO;
    private final PersonDAO personDAO;
    private final BookValidator bookValidator;

    @Autowired
    public BooksController(BookDAO bookDAO, PersonDAO personDAO, BookValidator bookValidator) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public String listOfBooks(Model model) {
        model.addAttribute("books", bookDAO.showListOfBooks());
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
        bookDAO.addNewBook(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String pageBook(@PathVariable("id") int id, Model model) {
        Book book = bookDAO.showInfoAboutBook(id);
        model.addAttribute("infoAboutBook", book);
        if (book.getPersonId() == null) {
            model.addAttribute("people", personDAO.showListOfPeople());
        } else {
            model.addAttribute("infoAboutPerson", personDAO.showInfoAboutPerson(Integer.parseInt(book.getPersonId())));
        }
        return "books/pageBook";
    }

    @PatchMapping("/{id}/free")
    public String freeBook(@PathVariable("id") int id, Model model) {
        bookDAO.updatePersonId(id);
        model.addAttribute("infoAboutBook", bookDAO.showInfoAboutBook(id));
        model.addAttribute("people", personDAO.showListOfPeople());
        return "books/pageBook";
    }

    @PatchMapping("/{id}/nonfree")
    public String assignBook(@PathVariable("id") int bookId, @RequestParam int personId, Model model) {
        bookDAO.updatePersonId(bookId, personId);
        model.addAttribute("infoAboutBook", bookDAO.showInfoAboutBook(bookId));
        model.addAttribute("infoAboutPerson", personDAO.showInfoAboutPerson(personId));
        return "books/pageBook";
    }

    @GetMapping("/{id}/edit")
    public String bookEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("editBook", bookDAO.showInfoAboutBook(id));
        return "books/editBook";
    }

    @PatchMapping("/{id}")
    public String updateInfoAboutBook(@ModelAttribute("editBook") @Valid Book editBook, BindingResult bindingResult, @PathVariable("id") int id) {
        bookValidator.validate(editBook, bindingResult);
        if (bindingResult.hasErrors()) {
            return "books/editBook";
        }
        bookDAO.updateInfoAboutBook(id, editBook);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookDAO.deleteBook(id);
        return "redirect:/books";
    }
}
