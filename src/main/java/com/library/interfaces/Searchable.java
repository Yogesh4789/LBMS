package com.library.interfaces;

import com.library.model.Book;
import java.util.List;

/**
 * OOP CONCEPT: Interface
 * 
 * Defines the contract for search functionality across book collections.
 * Demonstrates abstraction and method overloading across an interface.
 */
public interface Searchable {

    /**
     * Searches for books matching title.
     * OOP CONCEPT: Method Overloading (Single parameter version)
     */
    List<Book> searchBook(String title);

    /**
     * Searches for books matching both title and author.
     * OOP CONCEPT: Method Overloading (Two parameter version)
     */
    List<Book> searchBook(String title, String author);
}

