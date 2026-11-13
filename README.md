# Library Management System (Web Application) 📚

A complete, beginner-friendly, organizational-grade **Java Web-Based Library Management System** developed with **Spring Boot**, **Thymeleaf**, **HTML5/CSS3/Vanilla JavaScript**, and **Bootstrap 5**. Designed with a professional **Boeing-inspired corporate light theme** and powered by an **in-memory Java Collections Framework** repository.

---

## 🌟 Architecture & Key Features

- **Web-Based Corporate Experience:** Completely replaces traditional console/CLI interfaces with a browser application featuring a persistent left sidebar, sticky header, live metric cards, interactive modals, responsive tables, and toast notifications.
- **Boeing-Inspired Light Theme:** Built with a corporate palette featuring Navy Blue (`#003A70`), Flight Blue (`#0072CE`), Ice Blue (`#EAF3FA`), Crisp Backgrounds (`#F7F9FC`), and Clean White cards (`#FFFFFF`).
- **Strictly In-Memory (Zero Database Dependencies):** Implements an in-memory repository ([`InMemoryDatabase.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/database/InMemoryDatabase.java)) using `ArrayList`, `HashMap`, `HashSet`, and `Comparator`. Absolutely **NO** MySQL, PostgreSQL, SQLite, MongoDB, JPA, or Hibernate is configured.
- **Role-Based Access Control (RBAC):**
  - **Administrator:** Full administrative oversight over inventory, members, staff librarians, loans, and organizational analytics.
  - **Librarian:** Book catalog management, member directory, book issue, return processing, and fine tracking.
  - **Member:** Self-service portal to browse available titles, view personal active loans, track due dates, and review borrowing history.
- **Automated Fine Math & Circulation Guardrails:** Automated ₹5/day overdue fine calculations, 3-book limit enforcement, duplicate borrow blocking, and loan copy availability tracking.
- **Academic OOP Demonstration:** Concrete implementations of inheritance, encapsulation, method overloading, method overriding, runtime polymorphism, abstract classes, and domain interfaces.

---

## 🎨 Boeing-Inspired Theme Palette

| Element | Color Code | Purpose in UI |
| :--- | :--- | :--- |
| **Primary Navy** | `#003A70` | Brand header, primary buttons, card titles, active navigation |
| **Secondary Blue** | `#0072CE` | Accent links, search focus rings, secondary indicators |
| **Light Blue** | `#EAF3FA` | Navigation hover states, metric card backgrounds, subtle pills |
| **Canvas Background** | `#F7F9FC` | Soft, glare-free main application background |
| **Card Background** | `#FFFFFF` | Content containers, modal dialogs, data tables |
| **Border Color** | `#DCE3EA` | Clean, subtle separation borders |
| **Primary Text** | `#172B45` | Headings, high-contrast readable typography |
| **Secondary Text** | `#64748B` | Labels, captions, breadcrumbs, metadata |
| **Success** | `#16834A` | Available status badges, success alerts, return confirmation |
| **Warning** | `#D99016` | Overdue warnings, fine notifications, status changes |
| **Danger** | `#C0392B` | Out-of-stock badges, overdue tags, delete confirmations |

---

## 🏛️ Project Directory Structure

```text
LBMS/
├── pom.xml                                           # Maven build & dependencies (Spring Boot 3, Thymeleaf, Web)
├── mvnw & mvnw.cmd                                   # Standalone Maven Wrappers
├── README.md                                         # Master documentation (this file)
└── src/
    ├── main/
    │   ├── java/com/library/
    │   │   ├── LibraryManagementApplication.java     # Spring Boot application entry point
    │   │   ├── controller/                           # Web Controllers
    │   │   │   ├── AuthController.java               # Login & session routing
    │   │   │   ├── BookController.java               # Catalog listing, add, edit, delete, search, filter
    │   │   │   ├── BorrowingController.java          # Issue, return, transaction audit, reports
    │   │   │   ├── DashboardController.java          # Dynamic metrics aggregation
    │   │   │   ├── LibrarianController.java          # Staff librarian management (Admin-only)
    │   │   │   └── MemberController.java             # Member registration, update, status toggle
    │   │   ├── database/
    │   │   │   └── InMemoryDatabase.java             # In-Memory Collections (ArrayList, HashMap, HashSet)
    │   │   ├── exception/                            # Custom Domain Exceptions & Advice
    │   │   │   ├── BookNotFoundException.java
    │   │   │   ├── BookUnavailableException.java
    │   │   │   ├── DuplicateBookException.java
    │   │   │   ├── GlobalExceptionHandler.java       # Spring @ControllerAdvice with flash alerts
    │   │   │   ├── InvalidBorrowingException.java
    │   │   │   ├── InvalidReturnException.java
    │   │   │   └── MemberNotFoundException.java
    │   │   ├── interfaces/                           # OOP Abstraction Contracts
    │   │   │   ├── Borrowable.java                   # Issue & return method signatures
    │   │   │   └── Searchable.java                   # Search method overloads
    │   │   ├── model/                                # Domain Entities
    │   │   │   ├── Administrator.java                # Subclass of User
    │   │   │   ├── Book.java                         # Book entity with availability tracking
    │   │   │   ├── BorrowTransaction.java            # Loan record with overdue & fine calculations
    │   │   │   ├── Librarian.java                    # Subclass of User
    │   │   │   ├── Library.java                      # Institutional profile model
    │   │   │   ├── Member.java                       # Subclass of User
    │   │   │   └── User.java                         # Abstract parent base class
    │   │   └── service/                              # Business Logic Layer
    │   │       ├── AuthenticationService.java        # Session auth & role verification
    │   │       ├── BookService.java                  # Catalog logic, Comparator sorting
    │   │       ├── BorrowingService.java             # Issue & return business rules
    │   │       ├── LibrarianService.java             # Staff librarian CRUD
    │   │       └── MemberService.java                # Member directory & loan history
    │   └── resources/
    │       ├── application.properties                # Spring Boot port (8080) & Thymeleaf config
    │       ├── static/
    │       │   ├── css/
    │       │   │   ├── style.css                     # Boeing corporate light theme styles
    │       │   │   └── dashboard.css                 # KPI cards, metrics grid, activity layouts
    │       │   └── js/
    │       │       ├── app.js                        # Global toast auto-dismiss, shortcuts
    │       │       ├── books.js                      # Modal population & client-side filtering
    │       │       ├── borrowing.js                  # Dynamic due-date & fine estimator
    │       │       └── members.js                    # Member modal handlers
    │       └── templates/                            # Thymeleaf HTML Templates
    │           ├── books.html                        # Book catalog management
    │           ├── dashboard.html                    # Corporate analytics dashboard
    │           ├── issue-books.html                  # Dedicated book issuing portal
    │           ├── librarians.html                   # Staff librarian administration
    │           ├── login.html                        # Boeing-themed login with demo quick-pills
    │           ├── members.html                      # Member directory & status management
    │           ├── reports.html                      # Operational intelligence & inventory stats
    │           ├── return-books.html                 # Book return processing & fine assessment
    │           ├── transactions.html                 # Full borrowing audit trail
    │           └── fragments/                        # Modular Thymeleaf Components
    │               ├── footer.html                   # Corporate footer
    │               ├── navbar.html                   # Top bar, quick search, user profile menu
    │               └── sidebar.html                  # Left navigation bar with role guards
    └── test/java/com/library/
        └── LibraryManagementApplicationTests.java    # 18-point automated test suite & standalone runner
```

---

## 🚀 How to Run the Web Application

### Prerequisites
- **Java 17** (or Java 21 LTS).
- **Maven 3.6+** (or use the included `mvnw.cmd` / `mvnw`).
- Any modern web browser (Chrome, Edge, Firefox, Safari).

### Method 1: Using the Maven Wrapper (Recommended)
On Windows:
```cmd
.\mvnw.cmd spring-boot:run
```
On Linux / macOS:
```bash
./mvnw spring-boot:run
```

### Method 2: Using Installed Maven or Eclipse IDE
- **Command Line:** `mvn spring-boot:run`
- **Eclipse IDE:** Right-click [`LibraryManagementApplication.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/LibraryManagementApplication.java) &rarr; **`Run As`** &rarr; **`Java Application`**

### Method 3: Accessing the Application
Once started, open your web browser and navigate to:
```text
http://localhost:8080
```

---

## 🔑 Pre-Seeded Demonstration Accounts

The in-memory database initializes with ready-to-test accounts. You can also click the **quick demo pills** directly on the login screen to autofill credentials:

| Role | Username | Password | Purpose & Privileges |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Full access: Books, members, librarians, circulation, reports |
| **Librarian** | `sarah` | `sarah123` | Operations: Catalog, members, issue books, process returns |
| **Librarian** | `librarian` | `lib123` | Alternate staff account |
| **Member** | `alice` | `alice123` | Member view: View inventory, personal loans, due dates, history |
| **Member** | `bob` | `bob123` | Member view: 1 active loan on time |
| **Member** | `ethan` | `ethan123` | Deactivated member account (tests loan restriction) |

---

## 🧪 Comprehensive Testing & Verification

The project includes an 18-point test suite in [`LibraryManagementApplicationTests.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/test/java/com/library/LibraryManagementApplicationTests.java) verifying:

1. Application context & sample data initialization.
2. Adding a book successfully to in-memory catalog.
3. Preventing duplicate ISBN insertions via `HashSet`.
4. Searching books by title (Method Overloading 1).
5. Searching books by title and author (Method Overloading 2).
6. Sorting books by title, author, and year using `Comparator`.
7. Updating book details and copy adjustments.
8. Registering members with validation.
9. Preventing duplicate member usernames.
10. Ensuring deactivated members are prohibited from borrowing.
11. Managing staff librarians (Add, update, remove).
12. Issuing available books and decrementing available copies.
13. Preventing issuance of out-of-stock books (`BookUnavailableException`).
14. Enforcing the strict 3-book loan limit per member.
15. Preventing duplicate borrowing of the same title by the same member.
16. Processing book returns and restoring copy counts.
17. Preventing duplicate returns of the same loan transaction.
18. Accurate overdue fine math (₹5.00 per overdue day via `ChronoUnit.DAYS`).

Run tests via Maven:
```bash
mvn test
```

---

## 🎓 Academic OOP Concepts Mapping

| OOP Concept | File Reference | Concrete Code Example |
| :--- | :--- | :--- |
| **Abstract Class** | [`User.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/model/User.java) | `public abstract class User`<br>`public abstract String getRole();`<br>`public abstract void displayDetails();` |
| **Inheritance** | [`Member.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/model/Member.java)<br>[`Librarian.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/model/Librarian.java)<br>[`Administrator.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/model/Administrator.java) | `public class Member extends User { ... }` |
| **Encapsulation** | [`Book.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/model/Book.java) | Private fields with validated setters (`setAvailableCopies(int)` enforces $0 \le \text{avail} \le \text{total}$) |
| **Constructor Overloading** | [`Book.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/model/Book.java) | `Book()`, full 9-parameter constructor, and quick 5-parameter constructor |
| **Method Overloading** | [`Searchable.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/interfaces/Searchable.java)<br>[`Borrowable.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/interfaces/Borrowable.java) | `searchBook(String title)` vs. `searchBook(String title, String author)`<br>`issueBook(isbn, memberId)` vs. `issueBook(isbn, memberId, days)` |
| **Method Overriding** | Subclasses of `User` | `@Override public String getRole()`<br>`@Override public void displayDetails()`<br>`@Override public String toString()` |
| **Runtime Polymorphism** | [`AuthenticationService.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/service/AuthenticationService.java) | `User user = database.findUserByUsername(username);`<br>`user.getRole()` dispatches dynamically to `Administrator`, `Librarian`, or `Member` |
| **Interfaces** | [`Searchable.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/interfaces/Searchable.java)<br>[`Borrowable.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/interfaces/Borrowable.java) | Implemented by `BookService` and `BorrowingService` respectively |
| **Custom Exceptions** | [`com.library.exception.*`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/exception) | 6 domain exceptions handled centrally by [`GlobalExceptionHandler.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/exception/GlobalExceptionHandler.java) |

---

## 📊 In-Memory Java Collections Mapping

- **`ArrayList<E>`:** Primary ordered storage for `books`, `members`, `librarians`, and `transactions` in [`InMemoryDatabase.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/database/InMemoryDatabase.java). Enables natural table rendering and stream filtering.
- **`HashMap<K, V>`:** High-speed $O(1)$ lookups for `bookMap` (ISBN $\to$ Book), `memberMap` (ID $\to$ Member), and `userMap` (Username $\to$ User).
- **`HashSet<E>`:** Constant-time $O(1)$ collision checks for unique ISBNs (`isbnSet`), member IDs (`memberIdSet`), and usernames (`usernameSet`).
- **`Comparator<T>`:** Implements the Strategy Pattern in [`BookService.java`](file:///c:/Users/DELL/OneDrive/Desktop/My Work/LBMS/src/main/java/com/library/service/BookService.java) to sort titles alphabetically, authors alphabetically, or publication years newest-first without altering `Book.java`.
