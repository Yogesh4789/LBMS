package com.library.exception;

/**
 * Thrown when trying to return an already returned book or an invalid transaction ID.
 */
public class InvalidReturnException extends RuntimeException {
    public InvalidReturnException(String message) {
        super(message);
    }
}

