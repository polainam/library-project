package ru.polaina.project1boot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.polaina.project1boot.models.Book;
import ru.polaina.project1boot.services.BooksService;

@Component
public class BookValidator implements Validator {

    private final BooksService booksService;

    @Autowired
    public BookValidator(BooksService booksService) {
        this.booksService = booksService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        if (booksService.findByTitleAndWriterAndYearOfPublishing(book).isPresent()) {
            errors.rejectValue("numberOfCopies", "", "This book already exists");
        }
    }


    public void validateNumberOfCopies(Object target, Errors errors) {
        Book book = (Book) target;
        if (book.getNumberOfCopies() < 0) {
            errors.rejectValue("numberOfCopies", "", "The number of copies cannot be less than 0");
        }
    }
}
