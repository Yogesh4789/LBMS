package com.library.database;

import com.library.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;

/**
 * COMPONENT: InMemoryDatabase
 * 
 * Central in-memory repository managing all collections.
 * 
 * STRICT ARCHITECTURAL RESTRICTION:
 * No external database is used. All entities are stored in standard Java Collections:
 * - ArrayList: Primary storage for ordered tables and iterative scans
 * - HashMap: Instant O(1) lookups by primary keys (ISBN, Member ID, Username)
 * - HashSet: Instant collision detection for unique ISBNs, member IDs, and usernames
 * - Comparator: Multiple custom sorting strategies
 */
@Component
public class InMemoryDatabase {

    // =========================================================================
    // JAVA COLLECTIONS: Master Storage (ArrayList)
    // =========================================================================
    private final List<Book> books = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<Librarian> librarians = new ArrayList<>();
    private final List<Administrator> administrators = new ArrayList<>();
    private final List<BorrowTransaction> transactions = new ArrayList<>();

    // =========================================================================
    // JAVA COLLECTIONS: Fast Direct Lookup (HashMap)
    // =========================================================================
    private final Map<String, Book> bookMap = new HashMap<>();             // ISBN -> Book
    private final Map<String, Member> memberMap = new HashMap<>();         // MemberID -> Member
    private final Map<String, User> userMap = new HashMap<>();             // Username -> User
    private final Map<String, BorrowTransaction> transactionMap = new HashMap<>(); // TxnID -> Txn

    // =========================================================================
    // JAVA COLLECTIONS: Unique Value Enforcement (HashSet)
    // =========================================================================
    private final Set<String> isbnSet = new HashSet<>();
    private final Set<String> memberIdSet = new HashSet<>();
    private final Set<String> usernameSet = new HashSet<>();

    // Library institution metadata
    private final Library library = new Library();

    // Sequence Counters
    private int nextBookId = 1;
    private int nextMemberId = 1;
    private int nextLibrarianId = 1;
    private int nextTransactionId = 1;

    @PostConstruct
    public void initSampleData() {
        // Clear any previous state
        books.clear();
        members.clear();
        librarians.clear();
        administrators.clear();
        transactions.clear();
        bookMap.clear();
        memberMap.clear();
        userMap.clear();
        transactionMap.clear();
        isbnSet.clear();
        memberIdSet.clear();
        usernameSet.clear();

        // 1. Seed Administrator
        Administrator admin = new Administrator(
                "ADMIN001", "System Administrator", "admin@centrallibrary.edu", "admin", hashPassword("admin123")
        );
        administrators.add(admin);
        userMap.put(admin.getUsername().toLowerCase(), admin);
        usernameSet.add(admin.getUsername().toLowerCase());

        // 2. Seed Librarians
        Librarian lib1 = new Librarian(
                generateLibrarianId(), "Sarah Connor", "sarah.connor@centrallibrary.edu", "sarah", hashPassword("sarah123")
        );
        Librarian lib2 = new Librarian(
                generateLibrarianId(), "John Doe", "john.doe@centrallibrary.edu", "librarian", hashPassword("lib123")
        );
        addLibrarianInternal(lib1);
        addLibrarianInternal(lib2);

        // 3. Seed Members
        Member mem1 = new Member(
                generateMemberId(), "Alice Johnson", "alice.johnson@university.edu", "alice", hashPassword("alice123"),
                "+1 (555) 234-5678", "42 Maple Street, Sector 4", LocalDate.now().minusMonths(6), "ACTIVE"
        );
        Member mem2 = new Member(
                generateMemberId(), "Bob Smith", "bob.smith@university.edu", "bob", hashPassword("bob123"),
                "+1 (555) 345-6789", "17 Oak Avenue, Suite 2B", LocalDate.now().minusMonths(4), "ACTIVE"
        );
        Member mem3 = new Member(
                generateMemberId(), "Charlie Brown", "charlie.brown@university.edu", "charlie", hashPassword("charlie123"),
                "+1 (555) 456-7890", "88 Pine Road, North Hall", LocalDate.now().minusMonths(3), "ACTIVE"
        );
        Member mem4 = new Member(
                generateMemberId(), "Diana Prince", "diana.prince@university.edu", "diana", hashPassword("diana123"),
                "+1 (555) 567-8901", "101 Cedar Lane, Apt 14", LocalDate.now().minusMonths(2), "ACTIVE"
        );
        Member mem5 = new Member(
                generateMemberId(), "Ethan Hunt", "ethan.hunt@university.edu", "ethan", hashPassword("ethan123"),
                "+1 (555) 678-9012", "55 Elm Street, Block C", LocalDate.now().minusMonths(8), "DEACTIVATED"
        );
        addMemberInternal(mem1);
        addMemberInternal(mem2);
        addMemberInternal(mem3);
        addMemberInternal(mem4);
        addMemberInternal(mem5);

        // 4. Seed Books (Realistic technical catalog)
        addBookInternal(new Book(generateBookId(), "9780134685991", "Effective Java", "Joshua Bloch", "Addison-Wesley", "Programming", 2018, 5, 5));
        addBookInternal(new Book(generateBookId(), "9780132350884", "Clean Code", "Robert C. Martin", "Prentice Hall", "Software Engineering", 2008, 4, 3)); // 1 out
        addBookInternal(new Book(generateBookId(), "9780201616224", "The Pragmatic Programmer", "David Thomas", "Addison-Wesley", "Software Engineering", 2019, 3, 2)); // 1 out
        addBookInternal(new Book(generateBookId(), "9780262033848", "Introduction to Algorithms", "Thomas H. Cormen", "MIT Press", "Algorithms", 2009, 4, 4));
        addBookInternal(new Book(generateBookId(), "9780596007126", "Head First Design Patterns", "Eric Freeman", "O'Reilly Media", "Software Design", 2004, 3, 3));
        addBookInternal(new Book(generateBookId(), "9781260440232", "Java: The Complete Reference", "Herbert Schildt", "McGraw-Hill", "Programming", 2021, 6, 6));
        addBookInternal(new Book(generateBookId(), "9781491950357", "Designing Data-Intensive Applications", "Martin Kleppmann", "O'Reilly Media", "Cloud & Architecture", 2017, 4, 3)); // 1 out
        addBookInternal(new Book(generateBookId(), "9780135957059", "Artificial Intelligence: A Modern Approach", "Stuart Russell", "Pearson", "Artificial Intelligence", 2020, 2, 2));

        // 5. Seed Transactions (Demonstrating returned, active on-time, and active overdue)
        // Txn 1: Returned on time
        BorrowTransaction txn1 = new BorrowTransaction(
                generateTransactionId(), "9780134685991", "Effective Java", mem1.getUserId(), mem1.getName(),
                LocalDate.now().minusDays(25), LocalDate.now().minusDays(11), LocalDate.now().minusDays(12), true, 0.0
        );
        addTransactionInternal(txn1);

        // Txn 2: Currently OVERDUE by 7 days (Clean Code for Alice, fine = 7 * ₹5 = ₹35)
        BorrowTransaction txn2 = new BorrowTransaction(
                generateTransactionId(), "9780132350884", "Clean Code", mem1.getUserId(), mem1.getName(),
                LocalDate.now().minusDays(21), LocalDate.now().minusDays(7), null, false, 0.0
        );
        addTransactionInternal(txn2);

        // Txn 3: Active and ON TIME (The Pragmatic Programmer for Bob)
        BorrowTransaction txn3 = new BorrowTransaction(
                generateTransactionId(), "9780201616224", "The Pragmatic Programmer", mem2.getUserId(), mem2.getName(),
                LocalDate.now().minusDays(4), LocalDate.now().plusDays(10), null, false, 0.0
        );
        addTransactionInternal(txn3);

        // Txn 4: Active and ON TIME (Designing Data-Intensive Applications for Charlie)
        BorrowTransaction txn4 = new BorrowTransaction(
                generateTransactionId(), "9781491950357", "Designing Data-Intensive Applications", mem3.getUserId(), mem3.getName(),
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(12), null, false, 0.0
        );
        addTransactionInternal(txn4);
    }

    // =========================================================================
    // SEQUENCE GENERATORS
    // =========================================================================
    public synchronized String generateBookId() {
        return "BK" + String.format("%03d", nextBookId++);
    }

    public synchronized String generateMemberId() {
        return "MEM" + String.format("%03d", nextMemberId++);
    }

    public synchronized String generateLibrarianId() {
        return "LIB" + String.format("%03d", nextLibrarianId++);
    }

    public synchronized String generateTransactionId() {
        return "TXN" + String.format("%04d", nextTransactionId++);
    }

    // =========================================================================
    // SYNCHRONIZED ENTITY MUTATIONS
    // =========================================================================
    private void addBookInternal(Book book) {
        books.add(book);
        bookMap.put(book.getIsbn(), book);
        isbnSet.add(book.getIsbn());
    }

    public synchronized void addBook(Book book) {
        addBookInternal(book);
    }

    public synchronized void removeBook(String isbn) {
        Book book = bookMap.remove(isbn);
        if (book != null) {
            books.remove(book);
            isbnSet.remove(isbn);
        }
    }

    private void addMemberInternal(Member member) {
        members.add(member);
        memberMap.put(member.getUserId(), member);
        userMap.put(member.getUsername().toLowerCase(), member);
        memberIdSet.add(member.getUserId());
        usernameSet.add(member.getUsername().toLowerCase());
    }

    public synchronized void addMember(Member member) {
        addMemberInternal(member);
    }

    private void addLibrarianInternal(Librarian librarian) {
        librarians.add(librarian);
        userMap.put(librarian.getUsername().toLowerCase(), librarian);
        usernameSet.add(librarian.getUsername().toLowerCase());
    }

    public synchronized void addLibrarian(Librarian librarian) {
        addLibrarianInternal(librarian);
    }

    public synchronized void removeLibrarian(String librarianId) {
        Librarian target = null;
        for (Librarian l : librarians) {
            if (l.getUserId().equalsIgnoreCase(librarianId)) {
                target = l;
                break;
            }
        }
        if (target != null) {
            librarians.remove(target);
            userMap.remove(target.getUsername().toLowerCase());
            usernameSet.remove(target.getUsername().toLowerCase());
        }
    }

    private void addTransactionInternal(BorrowTransaction txn) {
        transactions.add(txn);
        transactionMap.put(txn.getTransactionId(), txn);
    }

    public synchronized void addTransaction(BorrowTransaction txn) {
        addTransactionInternal(txn);
    }

    // =========================================================================
    // DIRECT ACCESSORS & LOOKUPS
    // =========================================================================
    public Book findBookByIsbn(String isbn) {
        return bookMap.get(isbn != null ? isbn.trim() : "");
    }

    public Member findMemberById(String memberId) {
        return memberMap.get(memberId != null ? memberId.trim().toUpperCase() : "");
    }

    public User findUserByUsername(String username) {
        return username != null ? userMap.get(username.trim().toLowerCase()) : null;
    }

    public BorrowTransaction findTransactionById(String transactionId) {
        return transactionMap.get(transactionId != null ? transactionId.trim().toUpperCase() : "");
    }

    public boolean isIsbnTaken(String isbn) {
        return isbn != null && isbnSet.contains(isbn.trim());
    }

    public boolean isUsernameTaken(String username) {
        return username != null && usernameSet.contains(username.trim().toLowerCase());
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<Librarian> getLibrarians() {
        return librarians;
    }

    public List<Administrator> getAdministrators() {
        return administrators;
    }

    public List<BorrowTransaction> getTransactions() {
        return transactions;
    }

    public Library getLibrary() {
        return library;
    }

    // =========================================================================
    // SHA-256 PASSWORD HASHING UTILITY
    // =========================================================================
    public static String hashPassword(String password) {
        if (password == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback
        }
    }
}

