package com.library.model;

/**
 * ABSTRACT CLASS: User
 * 
 * Base class for all user types in the Library Management System.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Abstract Class: Defines common fields and abstract methods; cannot be instantiated directly.
 * - Inheritance: Extended by Member, Librarian, and Administrator.
 * - Encapsulation: All attributes are private with public getters and setters.
 * - Polymorphism: Parent-type references (User user = new Member(...)) are used across services.
 */
public abstract class User {

    // =========================================================================
    // OOP: Encapsulation - Private Attributes
    // =========================================================================
    private String userId;
    private String name;
    private String email;
    private String username;
    private String password; // SHA-256 hash or plaintext in memory
    private String status;   // "ACTIVE" or "INACTIVE" / "DEACTIVATED"

    // =========================================================================
    // OOP: Default Constructor
    // =========================================================================
    public User() {
        this.userId = "";
        this.name = "";
        this.email = "";
        this.username = "";
        this.password = "";
        this.status = "ACTIVE";
    }

    // =========================================================================
    // OOP: Parameterized Constructor
    // =========================================================================
    public User(String userId, String name, String email, String username, String password) {
        this.userId = userId;
        this.name = name != null ? name.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.username = username != null ? username.trim() : "";
        this.password = password;
        this.status = "ACTIVE";
    }

    // Overloaded Constructor with Status
    public User(String userId, String name, String email, String username, String password, String status) {
        this(userId, name, email, username, password);
        this.status = status != null ? status : "ACTIVE";
    }

    // =========================================================================
    // OOP: Abstract Methods (Must be implemented by subclasses)
    // =========================================================================
    public abstract String getRole();
    public abstract void displayDetails();

    // =========================================================================
    // OOP: Encapsulation - Getters & Setters with Validation
    // =========================================================================
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status : "ACTIVE";
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "User{id='" + userId + "', username='" + username + "', role='" + getRole() + "'}";
    }
}

