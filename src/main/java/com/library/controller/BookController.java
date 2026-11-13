package com.library.controller;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.AuthenticationService;
import com.library.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * CONTROLLER: BookController
 * 
 * Handles all catalog routes, filtering, sorting, adding, updating, and removing books.
 */
@Controller
public class BookController {

    private final BookService bookService;
    private final AuthenticationService authService;

    public BookController(BookService bookService, AuthenticationService authService) {
        this.bookService = bookService;
        this.authService = authService;
    }

    @GetMapping("/books")
    public String listBooks(HttpSession session,
                            @RequestParam(value = "query", required = false) String query,
                            @RequestParam(value = "category", required = false, defaultValue = "ALL") String category,
                            @RequestParam(value = "availability", required = false, defaultValue = "ALL") String availability,
                            @RequestParam(value = "sortBy", required = false, defaultValue = "title") String sortBy,
                            Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }

        User currentUser = authService.getCurrentUser(session);
        List<Book> bookList = bookService.filterAndSort(query, category, availability, sortBy);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "books");
        model.addAttribute("pageTitle", "Books Catalog");
        model.addAttribute("breadcrumbCategory", "Inventory");
        model.addAttribute("books", bookList);
        model.addAttribute("categories", bookService.getAllCategories());
        model.addAttribute("selectedQuery", query != null ? query : "");
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedAvailability", availability);
        model.addAttribute("selectedSort", sortBy);

        return "books";
    }

    @PostMapping("/books/add")
    public String addBook(HttpSession session,
                          @RequestParam("isbn") String isbn,
                          @RequestParam("title") String title,
                          @RequestParam("author") String author,
                          @RequestParam(value = "publisher", required = false, defaultValue = "Unknown") String publisher,
                          @RequestParam(value = "category", required = false, defaultValue = "General") String category,
                          @RequestParam(value = "publicationYear", required = false, defaultValue = "0") int publicationYear,
                          @RequestParam(value = "totalCopies", defaultValue = "1") int totalCopies,
                          RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Unauthorized role.");
            return "redirect:/books";
        }

        bookService.addBook(isbn, title, author, publisher, category, publicationYear, totalCopies);
        redirectAttributes.addFlashAttribute("successMessage", "Book '" + title + "' added successfully!");
        return "redirect:/books";
    }

    @PostMapping("/books/edit")
    public String editBook(HttpSession session,
                           @RequestParam("isbn") String isbn,
                           @RequestParam("title") String title,
                           @RequestParam("author") String author,
                           @RequestParam("publisher") String publisher,
                           @RequestParam("category") String category,
                           @RequestParam("publicationYear") int publicationYear,
                           @RequestParam("totalCopies") int totalCopies,
                           RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Unauthorized role.");
            return "redirect:/books";
        }

        bookService.updateBook(isbn, title, author, publisher, category, publicationYear, totalCopies);
        redirectAttributes.addFlashAttribute("successMessage", "Book '" + title + "' updated successfully!");
        return "redirect:/books";
    }

    @PostMapping("/books/delete")
    public String deleteBook(HttpSession session,
                             @RequestParam("isbn") String isbn,
                             RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Only Administrators can remove books.");
            return "redirect:/books";
        }

        bookService.deleteBook(isbn);
        redirectAttributes.addFlashAttribute("successMessage", "Book has been removed from catalog.");
        return "redirect:/books";
    }

    @GetMapping("/api/books/{isbn}")
    @ResponseBody
    public Book getBookDetailsApi(@PathVariable("isbn") String isbn) {
        return bookService.getBookByIsbn(isbn);
    }
}

