package com.library.service;

import com.library.database.InMemoryDatabase;
import com.library.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 * SERVICE: AuthenticationService
 * 
 * Manages user authentication, session binding, and role-based access control.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Polymorphism: Returns abstract User type; child role is resolved dynamically at runtime.
 */
@Service
public class AuthenticationService {

    private final InMemoryDatabase database;

    public static final String SESSION_USER = "currentUser";
    public static final String SESSION_ROLE = "userRole";

    public AuthenticationService(InMemoryDatabase database) {
        this.database = database;
    }

    /**
     * Authenticates a user against in-memory records.
     * OOP CONCEPT: Polymorphism - returns User reference which could be
     * an Administrator, Librarian, or Member at runtime.
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;

        User user = database.findUserByUsername(username);
        if (user == null) return null;

        String hashedInput = InMemoryDatabase.hashPassword(password);
        if (user.getPassword().equals(hashedInput) || user.getPassword().equals(password)) {
            if (user.isActive()) {
                return user;
            }
        }
        return null;
    }

    public void login(HttpSession session, User user) {
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_ROLE, user.getRole());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        Object obj = session.getAttribute(SESSION_USER);
        return obj instanceof User ? (User) obj : null;
    }

    public boolean isLoggedIn(HttpSession session) {
        return getCurrentUser(session) != null;
    }

    public boolean hasRole(HttpSession session, String... allowedRoles) {
        User user = getCurrentUser(session);
        if (user == null) return false;

        String userRole = user.getRole();
        for (String role : allowedRoles) {
            if (role.equalsIgnoreCase(userRole)) {
                return true;
            }
        }
        return false;
    }
}

