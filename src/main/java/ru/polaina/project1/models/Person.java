package ru.polaina.project1.models;

import jakarta.validation.constraints.*;

public class Person {

    private int personId;

    @NotEmpty(message = "Full name should not be empty")
    @Pattern(regexp = "[A-Za-z]+ [A-Za-z]+ [A-Za-z]+",
            message = "Full name should be in this format: Surname Name Patronymic")
    private String fullName;

    @Max(value = 2005, message = "Year of birth cannot be more than 2005")
    private int yearOfBirth;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }
}
