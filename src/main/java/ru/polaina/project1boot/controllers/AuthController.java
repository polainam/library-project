package ru.polaina.project1boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.PeopleService;
import ru.polaina.project1boot.util.PersonValidator;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final PersonValidator personValidator;
    private final PeopleService peopleService;

    @Autowired
    public AuthController(PersonValidator personValidator, PeopleService peopleService) {
        this.personValidator = personValidator;
        this.peopleService = peopleService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("newPerson") Person person) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("newPerson") @Valid Person person,
                                      BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        personValidator.validateEmail(person, bindingResult);
        personValidator.validatePhoneNumber(person, bindingResult);
        if(bindingResult.hasErrors()) {
            return "/auth/registration";
        }
        peopleService.save(person);
        return "redirect:/auth/login";
    }
}
