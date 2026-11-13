package com.library.service;

import com.library.database.InMemoryDatabase;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateBookException;
import com.library.exception.InvalidBorrowingException;
import com.library.interfaces.Searchable;
import com.library.model.Book;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SERVICE: BookService
 * 
 * Manages all book inventory operations.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Interface Implementation: Implements Searchable interface.
 * - Method Overloading: Implements multiple searchBook(...) variants.
 * - Strategy Pattern with Comparator: Flexible sorting strategies.
 */
@Service
public class BookService implements Searchable {

    private final InMemoryDatabase database;

    public BookService(InMemoryDatabase database) {
        this.database = database;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(database.getBooks());
    }

    public Book getBookByIsbn(String isbn) {
        Book book = database.findBookByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException("No book found with ISBN: " + isbn);
        }
        return book;
    }

    public Book addBook(String isbn, String title, String author, String publisher,
                        String category, int publicationYear, int totalCopies) {
        if (database.isIsbnTaken(isbn)) {
            throw new DuplicateBookException("A book with ISBN '" + isbn + "' already exists in catalog.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative.");
        }

        String bookId = database.generateBookId();
        Book book = new Book(
                bookId,
                isbn.trim(),
                title.trim(),
                author.trim(),
                publisher != null && !publisher.trim().isEmpty() ? publisher.trim() : "Unknown",
                category != null && !category.trim().isEmpty() ? category.trim() : "General",
                publicationYear > 0 ? publicationYear : 0,
                totalCopies,
                totalCopies
        );

        database.addBook(book);
        return book;
    }

    public Book updateBook(String isbn, String title, String author, String publisher,
                           String category, int publicationYear, int totalCopies) {
        Book book = getBookByIsbn(isbn);

        int issuedCopies = book.getIssuedCopies();
        if (totalCopies >= 0 && totalCopies < issuedCopies) {
            throw new IllegalArgumentException(
                    "Total copies (" + totalCopies + ") cannot be less than currently issued copies (" + issuedCopies + ")."
            );
        }

        if (title != null && !title.trim().isEmpty()) book.setTitle(title.trim());
        if (author != null && !author.trim().isEmpty()) book.setAuthor(author.trim());
        if (publisher != null && !publisher.trim().isEmpty()) book.setPublisher(publisher.trim());
        if (category != null && !category.trim().isEmpty()) book.setCategory(category.trim());
        if (publicationYear > 0) book.setPublicationYear(publicationYear);
        if (totalCopies >= 0) {
            book.setTotalCopies(totalCopies);
            book.setAvailableCopies(totalCopies - issuedCopies);
        }

        return book;
    }

    public void deleteBook(String isbn) {
        Book book = getBookByIsbn(isbn);

        // Validation: Cannot delete a book with active loans
        boolean hasActiveLoans = database.getTransactions().stream()
                .anyMatch(t -> t.getIsbn().equals(isbn) && !t.isReturned());

        if (hasActiveLoans) {
            throw new InvalidBorrowingException(
                    "Cannot delete book '" + book.getTitle() + "'. Active borrowing records exist."
            );
        }

        database.removeBook(isbn);
    }

    // =========================================================================
    // SEARCHABLE INTERFACE IMPLEMENTATION (Method Overloading)
    // =========================================================================

    @Override
    public List<Book> searchBook(String title) {
        if (title == null || title.trim().isEmpty()) return getAllBooks();
        String term = title.trim().toLowerCase();
        return database.getBooks().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchBook(String title, String author) {
        String titleTerm = title != null ? title.trim().toLowerCase() : "";
        String authorTerm = author != null ? author.trim().toLowerCase() : "";

        return database.getBooks().stream()
                .filter(b -> (titleTerm.isEmpty() || b.getTitle().toLowerCase().contains(titleTerm))
                        && (authorTerm.isEmpty() || b.getAuthor().toLowerCase().contains(authorTerm)))
                .collect(Collectors.toList());
    }

    // =========================================================================
    // ADVANCED FILTERING & SORTING (Comparator)
    // =========================================================================

    public List<Book> filterAndSort(String query, String category, String availability, String sortBy) {
        List<Book> result = new ArrayList<>(database.getBooks());

        // 1. Text Search Query (Title, Author, or ISBN)
        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            result = result.stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(q)
                            || b.getAuthor().toLowerCase().contains(q)
                            || b.getIsbn().toLowerCase().contains(q)
                            || b.getBookId().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        // 2. Category Filter
        if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
            result = result.stream()
                    .filter(b -> b.getCategory().equalsIgnoreCase(category.trim()))
                    .collect(Collectors.toList());
        }

        // 3. Availability Filter
        if (availability != null && !availability.trim().isEmpty() && !"ALL".equalsIgnoreCase(availability)) {
            if ("AVAILABLE".equalsIgnoreCase(availability)) {
                result = result.stream().filter(Book::isAvailable).collect(Collectors.toList());
            } else if ("OUT_OF_STOCK".equalsIgnoreCase(availability)) {
                result = result.stream().filter(b -> !b.isAvailable()).collect(Collectors.toList());
            }
        }

        // 4. Sorting via Comparator
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "author":
                    result.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "year":
                    result.sort(Comparator.comparingInt(Book::getPublicationYear).reversed());
                    break;
                case "title":
                default:
                    result.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
                    break;
            }
        }

        return result;
    }

    public List<String> getAllCategories() {
        return database.getBooks().stream()
                .map(Book::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}

