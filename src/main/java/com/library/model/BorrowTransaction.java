package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * CLASS: BorrowTransaction
 * 
 * Represents an issuance transaction between a Member and a Book.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Encapsulation: Tracks dates, return state, and overdue fines privately.
 * - Business Logic in Entities: getOverdueDays(), calculateFine().
 * - Constructor Overloading.
 */
public class BorrowTransaction {

    private String transactionId;
    private String isbn;
    private String bookTitle;
    private String memberId;
    private String memberName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;
    private double fineAmount;

    public static final int DEFAULT_BORROWING_DAYS = 14;
    public static final double FINE_PER_DAY = 5.0; // ₹5 per overdue day
    public static final int MAX_BOOKS_PER_MEMBER = 3;

    public BorrowTransaction() {
        this.transactionId = "";
        this.isbn = "";
        this.bookTitle = "";
        this.memberId = "";
        this.memberName = "";
        this.issueDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(DEFAULT_BORROWING_DAYS);
        this.returnDate = null;
        this.isReturned = false;
        this.fineAmount = 0.0;
    }

    public BorrowTransaction(String transactionId, String isbn, String bookTitle,
                             String memberId, String memberName, int borrowingDays) {
        this.transactionId = transactionId;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberName = memberName;
        this.issueDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(borrowingDays > 0 ? borrowingDays : DEFAULT_BORROWING_DAYS);
        this.returnDate = null;
        this.isReturned = false;
        this.fineAmount = 0.0;
    }

    public BorrowTransaction(String transactionId, String isbn, String bookTitle,
                             String memberId, String memberName,
                             LocalDate issueDate, LocalDate dueDate,
                             LocalDate returnDate, boolean isReturned, double fineAmount) {
        this.transactionId = transactionId;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberName = memberName;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
        this.fineAmount = fineAmount;
    }

    public boolean isOverdue() {
        if (isReturned) {
            return returnDate != null && returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public long getOverdueDays() {
        LocalDate checkDate = isReturned && returnDate != null ? returnDate : LocalDate.now();
        long days = ChronoUnit.DAYS.between(dueDate, checkDate);
        return Math.max(0, days);
    }

    public double calculateFine() {
        return getOverdueDays() * FINE_PER_DAY;
    }

    public String getStatus() {
        if (isReturned) return "RETURNED";
        if (isOverdue()) return "OVERDUE";
        return "ISSUED";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public double getFineAmount() {
        if (isReturned) {
            return fineAmount;
        }
        double calculated = calculateFine();
        return calculated > 0 ? calculated : fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public String toString() {
        return "BorrowTransaction{id='" + transactionId + "', book='" + bookTitle + "', member='" + memberName + "', status='" + getStatus() + "'}";
    }
}

