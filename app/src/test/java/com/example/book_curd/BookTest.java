package com.example.book_curd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookTest {

    @Test
    public void validate_returnsNameErrorWhenNameIsNull() {
        assertEquals(Book.ValidationError.NAME, Book.validate(null, "Author", "Description"));
    }

    @Test
    public void validate_returnsNameErrorWhenNameIsBlank() {
        assertEquals(Book.ValidationError.NAME, Book.validate("   ", "Author", "Description"));
    }

    @Test
    public void validate_returnsAuthorErrorWhenAuthorIsBlank() {
        assertEquals(Book.ValidationError.AUTHOR, Book.validate("Book", " \t ", "Description"));
    }

    @Test
    public void validate_returnsDescriptionErrorWhenDescriptionIsBlank() {
        assertEquals(Book.ValidationError.DESCRIPTION, Book.validate("Book", "Author", "\n"));
    }

    @Test
    public void validate_returnsNoneWhenAllFieldsArePresent() {
        assertEquals(Book.ValidationError.NONE,
                Book.validate("Book", "Author", "A short description."));
    }

    @Test
    public void validate_trimsFieldsBeforeChecking() {
        assertEquals(Book.ValidationError.NONE,
                Book.validate("  Book  ", "  Author  ", "  Description  "));
    }

    @Test
    public void constructorStoresBookFieldsForFirebaseSerialization() {
        Book book = new Book("Dune", "Frank Herbert", "A science-fiction novel.");

        assertEquals("Dune", book.name);
        assertEquals("Frank Herbert", book.author);
        assertEquals("A science-fiction novel.", book.description);
        assertEquals(null, book.id);
    }

    @Test
    public void noArgumentConstructorSupportsFirebaseDeserialization() {
        Book book = new Book();
        book.name = "Dune";
        book.author = "Frank Herbert";
        book.description = "A science-fiction novel.";

        assertEquals("Dune", book.name);
        assertEquals("Frank Herbert", book.author);
        assertEquals("A science-fiction novel.", book.description);
    }
}
