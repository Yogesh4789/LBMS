package com.library.model;

/**
 * CLASS: Book
 * 
 * Represents a library book entity with copy inventory tracking.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Encapsulation: Private attributes with validated getters and setters.
 * - Constructor Overloading: Default, parameterized, and partial constructors.
 * - Method Overriding: toString() overridden.
 */
public class Book {

    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;

    public Book() {
        this.bookId = "";
        this.isbn = "";
        this.title = "";
        this.author = "";
        this.publisher = "";
        this.category = "";
        this.publicationYear = 0;
        this.totalCopies = 0;
        this.availableCopies = 0;
    }

    public Book(String bookId, String isbn, String title, String author,
                String publisher, String category, int publicationYear,
                int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        setTitle(title);
        setAuthor(author);
        this.publisher = publisher != null ? publisher.trim() : "Unknown";
        this.category = category != null ? category.trim() : "General";
        this.publicationYear = publicationYear;
        setTotalCopies(totalCopies);
        setAvailableCopies(availableCopies);
    }

    // Overloaded Constructor for quick creation
    public Book(String bookId, String isbn, String title, String author, int totalCopies) {
        this(bookId, isbn, title, author, "Unknown", "General", 0, totalCopies, totalCopies);
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public int getIssuedCopies() {
        return Math.max(0, totalCopies - availableCopies);
    }

    public String getStatus() {
        return isAvailable() ? "AVAILABLE" : "OUT_OF_STOCK";
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn != null ? isbn.trim() : "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty.");
        }
        this.author = author.trim();
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher != null ? publisher.trim() : "Unknown";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category != null ? category.trim() : "General";
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative.");
        }
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative.");
        }
        if (availableCopies > this.totalCopies) {
            throw new IllegalArgumentException("Available copies (" + availableCopies
                    + ") cannot exceed total copies (" + this.totalCopies + ").");
        }
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "Book{id='" + bookId + "', isbn='" + isbn + "', title='" + title + "', avail=" + availableCopies + "/" + totalCopies + "}";
    }
}

