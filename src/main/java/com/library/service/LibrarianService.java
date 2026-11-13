package com.library.service;

import com.library.database.InMemoryDatabase;
import com.library.model.Librarian;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE: LibrarianService
 * 
 * Manages staff librarian records (restricted to Administrator role).
 */
@Service
public class LibrarianService {

    private final InMemoryDatabase database;

    public LibrarianService(InMemoryDatabase database) {
        this.database = database;
    }

    public List<Librarian> getAllLibrarians() {
        return new ArrayList<>(database.getLibrarians());
    }

    public Librarian getLibrarianById(String librarianId) {
        for (Librarian lib : database.getLibrarians()) {
            if (lib.getUserId().equalsIgnoreCase(librarianId)) {
                return lib;
            }
        }
        throw new IllegalArgumentException("No librarian found with ID: " + librarianId);
    }

    public Librarian addLibrarian(String name, String email, String username, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Librarian name cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (database.isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }

        String id = database.generateLibrarianId();
        String hashedPassword = InMemoryDatabase.hashPassword(password != null && !password.isEmpty() ? password : "lib123");

        Librarian librarian = new Librarian(id, name.trim(), email != null ? email.trim() : "", username.trim(), hashedPassword);
        database.addLibrarian(librarian);
        return librarian;
    }

    public Librarian updateLibrarian(String librarianId, String name, String email) {
        Librarian lib = getLibrarianById(librarianId);
        if (name != null && !name.trim().isEmpty()) lib.setName(name.trim());
        if (email != null && !email.trim().isEmpty()) lib.setEmail(email.trim());
        return lib;
    }

    public void removeLibrarian(String librarianId) {
        getLibrarianById(librarianId); // ensure existence
        database.removeLibrarian(librarianId);
    }

    public List<Librarian> filterAndSearch(String query) {
        if (query == null || query.trim().isEmpty()) return getAllLibrarians();
        String q = query.trim().toLowerCase();
        return database.getLibrarians().stream()
                .filter(l -> l.getName().toLowerCase().contains(q)
                        || l.getUserId().toLowerCase().contains(q)
                        || l.getEmail().toLowerCase().contains(q)
                        || l.getUsername().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }
}

