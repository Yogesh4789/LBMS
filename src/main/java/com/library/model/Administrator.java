package com.library.model;

/**
 * CLASS: Administrator (extends User)
 * 
 * Represents an administrator with full system permissions.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Inheritance: Subclasses abstract User class.
 * - Constructor Chaining: Uses super(...) to initialize parent attributes.
 * - Method Overriding: Implements getRole() and displayDetails().
 */
public class Administrator extends User {

    public Administrator() {
        super();
    }

    public Administrator(String userId, String name, String email, String username, String password) {
        super(userId, name, email, username, password, "ACTIVE");
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public void displayDetails() {
        System.out.println("Admin ID: " + getUserId() + ", Name: " + getName() + ", Email: " + getEmail());
    }

    @Override
    public String toString() {
        return "Administrator{id='" + getUserId() + "', name='" + getName() + "'}";
    }
}

