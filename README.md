# Library Management System (LBMS) 📚

A beginner-friendly, web-based **Library Management System** built with **Java 17**, **Spring Boot 3**, **Thymeleaf**, and **Bootstrap 5**. The application runs strictly **in-memory** using standard Java Collections with zero external database dependencies.

---

## 🌟 Key Features

- **Role-Based Access Control:** Dedicated views and capabilities for **Administrator**, **Librarian**, and **Member**.
- **Book Management:** Add, edit, remove, search, filter, and sort catalog inventory.
- **Member Directory:** Register members, update profiles, view borrowing histories, and manage membership status.
- **Circulation Desk:** Issue books with custom loan durations, return books, and calculate automated overdue fines (₹5/day).
- **Executive Dashboard & Reports:** Dynamic real-time metrics, recent loan activity, inventory utilization, and audit logs.
- **In-Memory Storage:** Powered by synchronized Java Collections (`ArrayList`, `HashMap`, `HashSet`).

---

## 🛠️ Technology Stack

- **Backend:** Java 17, Spring Boot 3.2.0, Spring Web
- **Frontend:** HTML5, CSS3, Vanilla JavaScript, Bootstrap 5, Bootstrap Icons
- **Template Engine:** Thymeleaf
- **Build Tool:** Maven (wrapper included)

---

## 🚀 How to Run the Application

### Prerequisites
- **Java 17** (or Java 21 LTS) installed.

### Option 1: Run via Terminal (Recommended)

1. Open a terminal in the project root directory.
2. Run the Maven wrapper:
   - **Windows:**
     ```cmd
     .\mvnw.cmd spring-boot:run
     ```
   - **macOS / Linux:**
     ```bash
     ./mvnw spring-boot:run
     ```
3. Open your browser and navigate to:
   ```text
   http://localhost:8080
   ```

### Option 2: Run in Eclipse IDE

1. Open **Eclipse IDE**.
2. Click **File** &rarr; **Import...** &rarr; **Maven** &rarr; **Existing Maven Projects**.
3. Select this project root folder and click **Finish**.
4. In Project Explorer, locate:
   ```text
   src/main/java  ──>  com.library  ──>  LibraryManagementApplication.java
   ```
5. Right-click `LibraryManagementApplication.java` &rarr; **Run As** &rarr; **Java Application** (or **Spring Boot App**).
6. Open your browser and navigate to:
   ```text
   http://localhost:8080
   ```

---

## 🔑 Demo Accounts

The application initializes with sample data ready to test. You can also click the demo quick-fill pills on the login page:

| Role | Username | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Full access: Books, members, librarians, circulation, reports |
| **Librarian** | `sarah` | `sarah123` | Operations: Books catalog, members, issue & return books |
| **Member** | `alice` | `alice123` | Member portal: Browse catalog, personal loans, due dates |
