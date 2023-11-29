package ru.polaina.project1boot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.polaina.project1boot.models.Person;
import ru.polaina.project1boot.services.PeopleService;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (peopleService.findByName(person.getUserName()).isPresent()) {
            errors.rejectValue("userName", "", "This username is already taken");
        }
    }

    public void validateEmail(Object target, Errors errors) {
        Person person = (Person) target;
        if (peopleService.findByEmail(person.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "This email is already taken");
        }
    }

    public void validatePhoneNumber(Object target, Errors errors) {
        Person person = (Person) target;
        if (peopleService.findByPhoneNumber(person.getPhoneNumber()).isPresent()) {
            errors.rejectValue("phoneNumber", "", "This phone number is already taken");
        }
    }
}
