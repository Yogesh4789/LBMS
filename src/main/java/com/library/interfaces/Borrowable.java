package com.library.interfaces;

import com.library.model.BorrowTransaction;

/**
 * OOP CONCEPT: Interface
 * 
 * Defines the contract for issuing and returning library materials.
 * Demonstrates abstraction and method overloading across an interface.
 */
public interface Borrowable {

    /**
     * Issues a book with the standard default borrowing duration.
     * OOP CONCEPT: Method Overloading (Two parameter version)
     */
    BorrowTransaction issueBook(String isbn, String memberId);

    /**
     * Issues a book with a custom borrowing duration.
     * OOP CONCEPT: Method Overloading (Three parameter version)
     */
    BorrowTransaction issueBook(String isbn, String memberId, int borrowingDays);

    /**
     * Returns an issued book and calculates any overdue fines.
     */
    BorrowTransaction returnBook(String transactionId);
}

