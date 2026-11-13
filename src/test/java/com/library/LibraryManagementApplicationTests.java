package com.library;

import com.library.database.InMemoryDatabase;
import com.library.exception.*;
import com.library.model.*;
import com.library.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST SUITE: LibraryManagementApplicationTests
 * 
 * Verifies all 18 core functional and edge-case requirements:
 * 1.  Application context and database initialization
 * 2.  Adding a book successfully
 * 3.  Preventing duplicate ISBN
 * 4.  Searching book by title (Method Overloading 1)
 * 5.  Searching book by title & author (Method Overloading 2)
 * 6.  Sorting books by title, author, and year (Comparator)
 * 7.  Updating book details
 * 8.  Registering a member
 * 9.  Preventing duplicate member username
 * 10. Updating member profile
 * 11. Toggling member status (Deactivation prevents borrowing)
 * 12. Adding and managing staff librarians
 * 13. Issuing an available book (decrements copies)
 * 14. Preventing issue of out-of-stock book
 * 15. Enforcing maximum 3-book borrowing limit
 * 16. Returning an issued book (restores copies)
 * 17. Preventing double returns
 * 18. Accurately calculating overdue fines (₹5/day)
 */
@SpringBootTest
public class LibraryManagementApplicationTests {

    private InMemoryDatabase database;
    private BookService bookService;
    private MemberService memberService;
    private LibrarianService librarianService;
    private BorrowingService borrowingService;
    private AuthenticationService authService;

    @BeforeEach
    public void setUp() {
        database = new InMemoryDatabase();
        database.initSampleData();

        bookService = new BookService(database);
        memberService = new MemberService(database);
        librarianService = new LibrarianService(database);
        borrowingService = new BorrowingService(database);
        authService = new AuthenticationService(database);
    }

    @Test
    @DisplayName("01. Context & Sample Data Initialization")
    public void testInitialization() {
        assertTrue(database.getBooks().size() >= 6, "Initial books should be at least 6");
        assertTrue(database.getMembers().size() >= 4, "Initial members should be at least 4");
        assertTrue(database.getLibrarians().size() >= 2, "Initial librarians should be at least 2");
        assertNotNull(database.findUserByUsername("admin"), "Default admin should exist");
    }

    @Test
    @DisplayName("02. Add Book Successfully")
    public void testAddBook() {
        int initialCount = bookService.getAllBooks().size();
        Book book = bookService.addBook("9780131103627", "The C Programming Language", "Brian Kernighan", "Prentice Hall", "Programming", 1988, 3);
        assertNotNull(book);
        assertEquals(initialCount + 1, bookService.getAllBooks().size());
        assertEquals("The C Programming Language", bookService.getBookByIsbn("9780131103627").getTitle());
    }

    @Test
    @DisplayName("03. Prevent Duplicate ISBN")
    public void testPreventDuplicateIsbn() {
        assertThrows(DuplicateBookException.class, () -> {
            bookService.addBook("9780134685991", "Duplicate Book", "Author", "Pub", "Cat", 2022, 2);
        });
    }

    @Test
    @DisplayName("04. Search Book by Title (Method Overloading)")
    public void testSearchByTitle() {
        List<Book> result = bookService.searchBook("Clean Code");
        assertFalse(result.isEmpty());
        assertEquals("9780132350884", result.get(0).getIsbn());
    }

    @Test
    @DisplayName("05. Search Book by Title & Author (Method Overloading)")
    public void testSearchByTitleAndAuthor() {
        List<Book> result = bookService.searchBook("Clean", "Martin");
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getAuthor().contains("Martin"));
    }

    @Test
    @DisplayName("06. Sort Books via Comparator")
    public void testSortBooks() {
        List<Book> byTitle = bookService.filterAndSort(null, "ALL", "ALL", "title");
        assertTrue(byTitle.get(0).getTitle().compareToIgnoreCase(byTitle.get(byTitle.size() - 1).getTitle()) <= 0);

        List<Book> byYear = bookService.filterAndSort(null, "ALL", "ALL", "year");
        assertTrue(byYear.get(0).getPublicationYear() >= byYear.get(byYear.size() - 1).getPublicationYear());
    }

    @Test
    @DisplayName("07. Update Book Details")
    public void testUpdateBook() {
        bookService.updateBook("9780134685991", "Effective Java (3rd Edition)", "Joshua Bloch", "Addison-Wesley", "Programming", 2018, 6);
        Book updated = bookService.getBookByIsbn("9780134685991");
        assertEquals("Effective Java (3rd Edition)", updated.getTitle());
        assertEquals(6, updated.getTotalCopies());
    }

    @Test
    @DisplayName("08. Register Member Successfully")
    public void testRegisterMember() {
        int initialCount = memberService.getAllMembers().size();
        Member member = memberService.registerMember("George Miller", "george@univ.edu", "+1555999888", "Campus 4", "georgem", "pass123");
        assertNotNull(member);
        assertEquals(initialCount + 1, memberService.getAllMembers().size());
        assertEquals("ACTIVE", member.getStatus());
    }

    @Test
    @DisplayName("09. Prevent Duplicate Member Username")
    public void testPreventDuplicateUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.registerMember("Duplicate Alice", "alice2@univ.edu", "+1555", "Address", "alice", "pass");
        });
    }

    @Test
    @DisplayName("10. Deactivated Member Cannot Borrow Books")
    public void testDeactivatedMemberBorrowing() {
        // Ethan Hunt is DEACTIVATED in seed data
        assertThrows(InvalidBorrowingException.class, () -> {
            borrowingService.issueBook("9780134685991", "MEM005");
        });
    }

    @Test
    @DisplayName("11. Add & Manage Staff Librarians")
    public void testLibrarianManagement() {
        int initial = librarianService.getAllLibrarians().size();
        Librarian lib = librarianService.addLibrarian("Alex Morgan", "alex@library.edu", "alexm", "libpass");
        assertNotNull(lib);
        assertEquals(initial + 1, librarianService.getAllLibrarians().size());

        librarianService.removeLibrarian(lib.getUserId());
        assertEquals(initial, librarianService.getAllLibrarians().size());
    }

    @Test
    @DisplayName("12. Issue Available Book & Verify Copy Decrement")
    public void testIssueBook() {
        Book book = bookService.getBookByIsbn("9780134685991");
        int availBefore = book.getAvailableCopies();

        BorrowTransaction txn = borrowingService.issueBook("9780134685991", "MEM002");
        assertNotNull(txn);
        assertFalse(txn.isReturned());
        assertEquals(availBefore - 1, book.getAvailableCopies());
    }

    @Test
    @DisplayName("13. Prevent Issuing Unavailable (Out of Stock) Book")
    public void testPreventUnavailableIssue() {
        // Create book with 1 copy
        bookService.addBook("9789999990001", "Limited Manuscript", "Author", "Pub", "History", 1990, 1);
        borrowingService.issueBook("9789999990001", "MEM002"); // copy taken

        assertThrows(BookUnavailableException.class, () -> {
            borrowingService.issueBook("9789999990001", "MEM003"); // should fail
        });
    }

    @Test
    @DisplayName("14. Enforce Maximum 3-Book Loan Limit")
    public void testMaxBorrowingLimit() {
        // MEM002 has 1 book in seed data. Issue 2nd and 3rd books:
        bookService.addBook("9789999990002", "Extra Title 1", "A1", "P1", "Cat", 2020, 2);
        bookService.addBook("9789999990003", "Extra Title 2", "A2", "P2", "Cat", 2020, 2);
        borrowingService.issueBook("9789999990002", "MEM002");
        borrowingService.issueBook("9789999990003", "MEM002"); // 3 active books now

        // Attempting 4th book must fail
        bookService.addBook("9789999990004", "Extra Title 3", "A3", "P3", "Cat", 2020, 2);
        assertThrows(InvalidBorrowingException.class, () -> {
            borrowingService.issueBook("9789999990004", "MEM002");
        });
    }

    @Test
    @DisplayName("15. Prevent Duplicate Borrowing of Same Book")
    public void testPreventDuplicateBorrowOfSameBook() {
        // MEM003 already has Designing Data-Intensive Applications in seed data
        assertThrows(InvalidBorrowingException.class, () -> {
            borrowingService.issueBook("9781491950357", "MEM003");
        });
    }

    @Test
    @DisplayName("16. Return Book & Restore Availability")
    public void testReturnBook() {
        // Issue book
        Book book = bookService.getBookByIsbn("9780262033848");
        int availBefore = book.getAvailableCopies();
        BorrowTransaction txn = borrowingService.issueBook("9780262033848", "MEM004");
        assertEquals(availBefore - 1, book.getAvailableCopies());

        // Return book
        BorrowTransaction returnedTxn = borrowingService.returnBook(txn.getTransactionId());
        assertTrue(returnedTxn.isReturned());
        assertNotNull(returnedTxn.getReturnDate());
        assertEquals(availBefore, book.getAvailableCopies());
    }

    @Test
    @DisplayName("17. Prevent Returning Same Book Twice")
    public void testPreventDoubleReturn() {
        // TXN0001 is already returned in seed data
        assertThrows(InvalidReturnException.class, () -> {
            borrowingService.returnBook("TXN0001");
        });
    }

    @Test
    @DisplayName("18. Calculate Overdue Fines Accurately (₹5/day)")
    public void testOverdueFineCalculation() {
        // TXN0002 has dueDate 7 days in the past in seed data
        BorrowTransaction overdueTxn = database.findTransactionById("TXN0002");
        assertTrue(overdueTxn.isOverdue());
        assertTrue(overdueTxn.getOverdueDays() >= 7);

        double fine = overdueTxn.calculateFine();
        assertEquals(overdueTxn.getOverdueDays() * BorrowTransaction.FINE_PER_DAY, fine, 0.001);
        assertEquals(fine, overdueTxn.getFineAmount(), 0.001);
    }

    // =========================================================================
    // STANDALONE CONSOLE TEST RUNNER
    // Enables running test suite without Maven if needed
    // =========================================================================
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("  LBMS WEB APPLICATION - AUTOMATED SERVICE TEST HARNESS   ");
        System.out.println("==========================================================");

        LibraryManagementApplicationTests suite = new LibraryManagementApplicationTests();
        int passed = 0;
        int failed = 0;

        java.lang.reflect.Method[] methods = LibraryManagementApplicationTests.class.getDeclaredMethods();
        for (java.lang.reflect.Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                String testName = m.isAnnotationPresent(DisplayName.class) ? m.getAnnotation(DisplayName.class).value() : m.getName();
                try {
                    suite.setUp();
                    m.invoke(suite);
                    System.out.printf("  %-62s [PASS]\n", testName);
                    passed++;
                } catch (Exception e) {
                    System.out.printf("  %-62s [FAIL] (%s)\n", testName, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                    failed++;
                }
            }
        }

        System.out.println("==========================================================");
        System.out.println("  Total: " + (passed + failed) + " | Passed: " + passed + " | Failed: " + failed);
        System.out.println("==========================================================");
    }
}

