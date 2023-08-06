package ru.polaina.project1.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.polaina.project1.dao.BookDAO;
import ru.polaina.project1.models.Book;

@Component
public class BookValidator implements Validator {

    private final BookDAO bookDAO;

    @Autowired
    public BookValidator(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        if (bookDAO.showInfoAboutBookByTitle(book.getTitle()).isPresent()) {
            errors.rejectValue("title", "", "This title is already taken");
        }
        if (bookDAO.showInfoAboutBookByWriter(book.getWriter()).isPresent()) {
            errors.rejectValue("writer", "", "This writer is already taken");
        }
    }
}
