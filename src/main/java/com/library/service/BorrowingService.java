package com.library.service;

import com.library.database.InMemoryDatabase;
import com.library.exception.*;
import com.library.interfaces.Borrowable;
import com.library.model.Book;
import com.library.model.BorrowTransaction;
import com.library.model.Member;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE: BorrowingService
 * 
 * Coordinates book loaning, returning, availability synchronization,
 * and automated overdue fine calculations.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Interface Implementation: Implements Borrowable interface.
 * - Method Overloading: Provides issueBook(...) variants.
 */
@Service
public class BorrowingService implements Borrowable {

    private final InMemoryDatabase database;

    public BorrowingService(InMemoryDatabase database) {
        this.database = database;
    }

    // =========================================================================
    // BORROWABLE INTERFACE IMPLEMENTATION (Method Overloading)
    // =========================================================================

    @Override
    public BorrowTransaction issueBook(String isbn, String memberId) {
        return issueBook(isbn, memberId, BorrowTransaction.DEFAULT_BORROWING_DAYS);
    }

    @Override
    public BorrowTransaction issueBook(String isbn, String memberId, int borrowingDays) {
        // Step 1: Validate member
        Member member = database.findMemberById(memberId);
        if (member == null) {
            throw new MemberNotFoundException("No member found with ID: " + memberId);
        }
        if (!member.isActive()) {
            throw new InvalidBorrowingException("Member '" + member.getName() + "' is deactivated and cannot borrow books.");
        }

        // Step 2: Validate book
        Book book = database.findBookByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException("No book found with ISBN: " + isbn);
        }

        // Step 3: Check availability
        if (!book.isAvailable()) {
            throw new BookUnavailableException("Book '" + book.getTitle() + "' has 0 copies available.");
        }

        // Step 4: Check active loan count (Max 3 limit)
        long currentLoans = database.getTransactions().stream()
                .filter(t -> t.getMemberId().equalsIgnoreCase(memberId) && !t.isReturned())
                .count();

        if (currentLoans >= BorrowTransaction.MAX_BOOKS_PER_MEMBER) {
            throw new InvalidBorrowingException(
                    "Member '" + member.getName() + "' has reached the borrowing limit of "
                            + BorrowTransaction.MAX_BOOKS_PER_MEMBER + " books."
            );
        }

        // Step 5: Check duplicate borrow
        boolean alreadyHasCopy = database.getTransactions().stream()
                .anyMatch(t -> t.getMemberId().equalsIgnoreCase(memberId)
                        && t.getIsbn().equalsIgnoreCase(isbn)
                        && !t.isReturned());

        if (alreadyHasCopy) {
            throw new InvalidBorrowingException("Member already holds an active copy of '" + book.getTitle() + "'.");
        }

        // Step 6: Create transaction
        int days = borrowingDays > 0 ? borrowingDays : BorrowTransaction.DEFAULT_BORROWING_DAYS;
        String txnId = database.generateTransactionId();
        BorrowTransaction transaction = new BorrowTransaction(
                txnId, book.getIsbn(), book.getTitle(), member.getUserId(), member.getName(), days
        );

        // Step 7: Decrement available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        // Step 8: Save to in-memory database
        database.addTransaction(transaction);

        return transaction;
    }

    @Override
    public BorrowTransaction returnBook(String transactionId) {
        BorrowTransaction transaction = database.findTransactionById(transactionId);
        if (transaction == null) {
            throw new InvalidReturnException("No transaction found with ID: " + transactionId);
        }

        if (transaction.isReturned()) {
            throw new InvalidReturnException(
                    "This book was already returned on " + transaction.getReturnDate() + "."
            );
        }

        // Record return date and calculate fine
        transaction.setReturnDate(LocalDate.now());
        double fine = transaction.calculateFine();
        transaction.setFineAmount(fine);
        transaction.setReturned(true);

        // Restore book copy availability
        Book book = database.findBookByIsbn(transaction.getIsbn());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }

        return transaction;
    }

    // =========================================================================
    // QUERY & FILTER OPERATIONS
    // =========================================================================

    public List<BorrowTransaction> getAllTransactions() {
        return new ArrayList<>(database.getTransactions());
    }

    public List<BorrowTransaction> getActiveTransactions() {
        return database.getTransactions().stream()
                .filter(t -> !t.isReturned())
                .collect(Collectors.toList());
    }

    public List<BorrowTransaction> getOverdueTransactions() {
        return database.getTransactions().stream()
                .filter(BorrowTransaction::isOverdue)
                .collect(Collectors.toList());
    }

    public List<BorrowTransaction> getReturnedTransactions() {
        return database.getTransactions().stream()
                .filter(BorrowTransaction::isReturned)
                .collect(Collectors.toList());
    }

    public BorrowTransaction getTransactionById(String id) {
        BorrowTransaction txn = database.findTransactionById(id);
        if (txn == null) {
            throw new InvalidReturnException("No transaction found with ID: " + id);
        }
        return txn;
    }

    public List<BorrowTransaction> filterTransactions(String status, String query) {
        List<BorrowTransaction> list = new ArrayList<>(database.getTransactions());

        // Status Filter
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            switch (status.toUpperCase()) {
                case "ISSUED":
                case "ACTIVE":
                    list = list.stream().filter(t -> !t.isReturned()).collect(Collectors.toList());
                    break;
                case "OVERDUE":
                    list = list.stream().filter(BorrowTransaction::isOverdue).collect(Collectors.toList());
                    break;
                case "RETURNED":
                    list = list.stream().filter(BorrowTransaction::isReturned).collect(Collectors.toList());
                    break;
            }
        }

        // Search Query (Transaction ID, Book Title, ISBN, Member Name, Member ID)
        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            list = list.stream()
                    .filter(t -> t.getTransactionId().toLowerCase().contains(q)
                            || t.getBookTitle().toLowerCase().contains(q)
                            || t.getIsbn().toLowerCase().contains(q)
                            || t.getMemberName().toLowerCase().contains(q)
                            || t.getMemberId().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        // Default sort by most recent issue date
        list.sort((a, b) -> b.getTransactionId().compareTo(a.getTransactionId()));
        return list;
    }
}

