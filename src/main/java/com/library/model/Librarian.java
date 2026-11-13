package com.library.model;

/**
 * CLASS: Librarian (extends User)
 * 
 * Represents a library staff member who manages daily book operations.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Inheritance: Subclasses abstract User class.
 * - Constructor Chaining: Uses super(...) for parent initialization.
 * - Method Overriding: Implements getRole() and displayDetails().
 */
public class Librarian extends User {

    public Librarian() {
        super();
    }

    public Librarian(String userId, String name, String email, String username, String password) {
        super(userId, name, email, username, password, "ACTIVE");
    }

    public Librarian(String userId, String name, String email, String username, String password, String status) {
        super(userId, name, email, username, password, status);
    }

    @Override
    public String getRole() {
        return "LIBRARIAN";
    }

    @Override
    public void displayDetails() {
        System.out.println("Librarian ID: " + getUserId() + ", Name: " + getName() + ", Email: " + getEmail());
    }

    @Override
    public String toString() {
        return "Librarian{id='" + getUserId() + "', name='" + getName() + "'}";
    }
}

