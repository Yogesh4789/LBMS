package com.library.exception;

/**
 * Thrown when a member with the requested Member ID is not found.
 */
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}

