package ru.polaina.project1boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.BooksService;
import ru.polaina.project1boot.services.PeopleService;
import java.util.List;

@Controller
@RequestMapping("/people")
public class PeopleController {
    private final PeopleService peopleService;
    private final BooksService booksService;

    @Autowired
    public PeopleController(PeopleService peopleService, BooksService bookService, BooksService booksService) {
        this.peopleService = peopleService;
        this.booksService = booksService;
    }

    @GetMapping()
    public String listOfPeople(Model model) {
        model.addAttribute("people", peopleService.findAll());
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

/*    @GetMapping("/{id}")
    public String pagePerson(@PathVariable("id") int id, Model model) {
        Person person = peopleService.findOne(id);
        List<Book> books = booksService.findByPersonId(person);
        model.addAttribute("books", books);
        model.addAttribute("infoAboutPerson", person);
        return "people/pagePerson";
    }*/

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
}
