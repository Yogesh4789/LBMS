package com.library.exception;

/**
 * Thrown when trying to add a book with an ISBN that already exists in the catalog.
 */
public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String message) {
        super(message);
    }
}

