package com.library.model;

/**
 * CLASS: Library
 * 
 * Represents the institution profile, rules, and policy defaults.
 */
public class Library {

    private String libraryName;
    private String address;
    private String contactNumber;
    private String email;
    private int maxBooksPerMember;
    private int defaultBorrowingDays;
    private double finePerDay;

    public Library() {
        this.libraryName = "Metropolitan Central Library";
        this.address = "100 Academic Boulevard, Knowledge City";
        this.contactNumber = "+1 (555) 019-2834";
        this.email = "contact@centrallibrary.edu";
        this.maxBooksPerMember = BorrowTransaction.MAX_BOOKS_PER_MEMBER;
        this.defaultBorrowingDays = BorrowTransaction.DEFAULT_BORROWING_DAYS;
        this.finePerDay = BorrowTransaction.FINE_PER_DAY;
    }

    public Library(String libraryName, String address, String contactNumber, String email,
                   int maxBooksPerMember, int defaultBorrowingDays, double finePerDay) {
        this.libraryName = libraryName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.maxBooksPerMember = maxBooksPerMember;
        this.defaultBorrowingDays = defaultBorrowingDays;
        this.finePerDay = finePerDay;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMaxBooksPerMember() {
        return maxBooksPerMember;
    }

    public void setMaxBooksPerMember(int maxBooksPerMember) {
        this.maxBooksPerMember = maxBooksPerMember;
    }

    public int getDefaultBorrowingDays() {
        return defaultBorrowingDays;
    }

    public void setDefaultBorrowingDays(int defaultBorrowingDays) {
        this.defaultBorrowingDays = defaultBorrowingDays;
    }

    public double getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(double finePerDay) {
        this.finePerDay = finePerDay;
    }
}

