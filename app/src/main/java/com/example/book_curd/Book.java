package com.example.book_curd;

public class Book {
    public String id;
    public String name;
    public String author;
    public String description;

    public Book() {
    }

    public Book(String name, String author, String description) {
        this.name = name;
        this.author = author;
        this.description = description;
    }

    public static ValidationError validate(String name, String author, String description) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationError.NAME;
        }
        if (author == null || author.trim().isEmpty()) {
            return ValidationError.AUTHOR;
        }
        if (description == null || description.trim().isEmpty()) {
            return ValidationError.DESCRIPTION;
        }
        return ValidationError.NONE;
    }

    public enum ValidationError {
        NONE,
        NAME,
        AUTHOR,
        DESCRIPTION
    }
}
