package com.library.exception;

/**
 * Thrown when a book with the requested ISBN or ID is not present in the library database.
 * Extends RuntimeException (Unchecked exception).
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
}

