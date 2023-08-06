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
import ru.polaina.project1.models.Person;
import ru.polaina.project1.util.PersonValidator;

import java.util.List;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonValidator personValidator;
    private final PersonDAO personDAO;
    private final BookDAO bookDAO;

    @Autowired
    public PeopleController(PersonValidator personValidator, PersonDAO personDAO, BookDAO bookDAO) {
        this.personValidator = personValidator;
        this.personDAO = personDAO;
        this.bookDAO = bookDAO;
    }

    @GetMapping()
    public String listOfPeople(Model model) {
        model.addAttribute("people", personDAO.showListOfPeople());
        return "people/listOfPeople";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("newPerson")Person person) {
        return "people/newPerson";
    }

    @PostMapping()
    public String insertNewPerson(@ModelAttribute("newPerson") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "people/newPerson";
        }
        personDAO.addNewPerson(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}")
    public String pagePerson(@PathVariable("id") int id, Model model) {
        Person person = personDAO.showInfoAboutPerson(id);
        List<Book> books = bookDAO.showListOfBooksByPersonId(person.getPersonId());
        model.addAttribute("books", books);
        model.addAttribute("infoAboutPerson", person);
        return "people/pagePerson";
    }

    @GetMapping("/{id}/edit")
    public String personEditPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("editPerson", personDAO.showInfoAboutPerson(id));
        return "people/editPerson";
    }

    @PatchMapping("/{id}")
    public String updateInfoAboutPerson(@ModelAttribute("editPerson") @Valid Person editPerson, BindingResult bindingResult, @PathVariable("id") int id) {
        personValidator.validate(editPerson, bindingResult);
        if (bindingResult.hasErrors()) {
            return "people/editPerson";
        }
        personDAO.updateInfoAboutPerson(id, editPerson);
        return "redirect:/people";
    }

    //Будет ошибка, тк on delete по умолчанию (Но если у человека нет книг, то удалится)
    @DeleteMapping("/{id}")
    public String deletePerson(@PathVariable("id") int id) {
        personDAO.deletePerson(id);
        return "redirect:/people";
    }
}
