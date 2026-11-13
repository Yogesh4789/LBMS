package com.library.exception;

/**
 * Thrown when attempting to issue a book with zero available copies.
 */
public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super(message);
    }
}

