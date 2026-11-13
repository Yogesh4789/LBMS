package com.library.exception;

/**
 * Thrown when a borrowing rule is violated (e.g. member limit exceeded, inactive member, duplicate loan).
 */
public class InvalidBorrowingException extends RuntimeException {
    public InvalidBorrowingException(String message) {
        super(message);
    }
}

