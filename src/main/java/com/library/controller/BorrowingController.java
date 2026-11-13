package com.library.controller;

import com.library.database.InMemoryDatabase;
import com.library.model.Book;
import com.library.model.BorrowTransaction;
import com.library.model.Member;
import com.library.model.User;
import com.library.service.AuthenticationService;
import com.library.service.BorrowingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER: BorrowingController
 * 
 * Handles dedicated workflows for Issuing Books, Returning Books,
 * Transaction Audits, and Operational Reports.
 */
@Controller
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final InMemoryDatabase database;
    private final AuthenticationService authService;

    public BorrowingController(BorrowingService borrowingService,
                               InMemoryDatabase database,
                               AuthenticationService authService) {
        this.borrowingService = borrowingService;
        this.database = database;
        this.authService = authService;
    }

    // =========================================================================
    // ISSUE BOOKS PAGE
    // =========================================================================

    @GetMapping("/issue-books")
    public String issueBooksPage(HttpSession session, Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            return "redirect:/dashboard";
        }

        User currentUser = authService.getCurrentUser(session);
        List<Book> availableBooks = database.getBooks().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
        List<Member> activeMembers = database.getMembers().stream()
                .filter(Member::isActive)
                .collect(Collectors.toList());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "issue-books");
        model.addAttribute("pageTitle", "Issue Books");
        model.addAttribute("breadcrumbCategory", "Circulation");
        model.addAttribute("availableBooks", availableBooks);
        model.addAttribute("activeMembers", activeMembers);
        model.addAttribute("todayDate", LocalDate.now());
        model.addAttribute("defaultDueDate", LocalDate.now().plusDays(BorrowTransaction.DEFAULT_BORROWING_DAYS));
        model.addAttribute("defaultDays", BorrowTransaction.DEFAULT_BORROWING_DAYS);
        model.addAttribute("maxLimit", BorrowTransaction.MAX_BOOKS_PER_MEMBER);

        return "issue-books";
    }

    @PostMapping("/issue-books")
    public String processIssue(HttpSession session,
                               @RequestParam("isbn") String isbn,
                               @RequestParam("memberId") String memberId,
                               @RequestParam(value = "borrowingDays", defaultValue = "14") int borrowingDays,
                               RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Unauthorized role.");
            return "redirect:/dashboard";
        }

        BorrowTransaction txn = borrowingService.issueBook(isbn, memberId, borrowingDays);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Book '" + txn.getBookTitle() + "' issued to " + txn.getMemberName() + " successfully! (Transaction ID: " + txn.getTransactionId() + ")"
        );
        return "redirect:/issue-books";
    }

    // =========================================================================
    // RETURN BOOKS PAGE
    // =========================================================================

    @GetMapping("/return-books")
    public String returnBooksPage(HttpSession session,
                                  @RequestParam(value = "query", required = false) String query,
                                  Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            return "redirect:/dashboard";
        }

        User currentUser = authService.getCurrentUser(session);
        List<BorrowTransaction> activeLoans = borrowingService.getActiveTransactions();

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            activeLoans = activeLoans.stream()
                    .filter(t -> t.getTransactionId().toLowerCase().contains(q)
                            || t.getBookTitle().toLowerCase().contains(q)
                            || t.getMemberName().toLowerCase().contains(q)
                            || t.getMemberId().toLowerCase().contains(q)
                            || t.getIsbn().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "return-books");
        model.addAttribute("pageTitle", "Return Processing");
        model.addAttribute("breadcrumbCategory", "Circulation");
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("selectedQuery", query != null ? query : "");
        model.addAttribute("todayDate", LocalDate.now());
        model.addAttribute("fineRate", BorrowTransaction.FINE_PER_DAY);

        return "return-books";
    }

    @PostMapping("/return-books")
    public String processReturn(HttpSession session,
                                @RequestParam("transactionId") String transactionId,
                                RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Unauthorized role.");
            return "redirect:/dashboard";
        }

        BorrowTransaction txn = borrowingService.returnBook(transactionId);
        String msg = "Book '" + txn.getBookTitle() + "' returned successfully!";
        if (txn.getFineAmount() > 0) {
            msg += " Overdue by " + txn.getOverdueDays() + " day(s). Fine assessed: Rs. " + String.format("%.2f", txn.getFineAmount());
            redirectAttributes.addFlashAttribute("warningMessage", msg);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", msg);
        }

        return "redirect:/return-books";
    }

    // =========================================================================
    // TRANSACTIONS PAGE
    // =========================================================================

    @GetMapping("/transactions")
    public String listTransactions(HttpSession session,
                                   @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                                   @RequestParam(value = "query", required = false) String query,
                                   Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }

        User currentUser = authService.getCurrentUser(session);
        List<BorrowTransaction> list;

        // If member, restrict strictly to their own transactions
        if ("MEMBER".equalsIgnoreCase(currentUser.getRole())) {
            String memberId = currentUser.getUserId();
            list = database.getTransactions().stream()
                    .filter(t -> t.getMemberId().equalsIgnoreCase(memberId))
                    .collect(Collectors.toList());

            if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
                if ("ACTIVE".equalsIgnoreCase(status) || "ISSUED".equalsIgnoreCase(status)) {
                    list = list.stream().filter(t -> !t.isReturned()).collect(Collectors.toList());
                } else if ("OVERDUE".equalsIgnoreCase(status)) {
                    list = list.stream().filter(BorrowTransaction::isOverdue).collect(Collectors.toList());
                } else if ("RETURNED".equalsIgnoreCase(status)) {
                    list = list.stream().filter(BorrowTransaction::isReturned).collect(Collectors.toList());
                }
            }
        } else {
            list = borrowingService.filterTransactions(status, query);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "transactions");
        model.addAttribute("pageTitle", "Circulation Audit Logs");
        model.addAttribute("breadcrumbCategory", "Audit & Records");
        model.addAttribute("transactions", list);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedQuery", query != null ? query : "");

        return "transactions";
    }

    // =========================================================================
    // REPORTS PAGE
    // =========================================================================

    @GetMapping("/reports")
    public String reportsPage(HttpSession session, Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            return "redirect:/dashboard";
        }

        User currentUser = authService.getCurrentUser(session);

        // Calculate report aggregates
        int totalBooks = database.getBooks().size();
        int totalCopies = database.getBooks().stream().mapToInt(Book::getTotalCopies).sum();
        int availableCopies = database.getBooks().stream().mapToInt(Book::getAvailableCopies).sum();
        int issuedCopies = database.getBooks().stream().mapToInt(Book::getIssuedCopies).sum();

        List<BorrowTransaction> allTxns = database.getTransactions();
        int totalLoans = allTxns.size();
        long returnedLoans = allTxns.stream().filter(BorrowTransaction::isReturned).count();
        long activeLoans = allTxns.stream().filter(t -> !t.isReturned()).count();
        long overdueLoans = allTxns.stream().filter(BorrowTransaction::isOverdue).count();
        double totalFinesCollected = allTxns.stream().mapToDouble(BorrowTransaction::getFineAmount).sum();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "reports");
        model.addAttribute("pageTitle", "Operational Reports");
        model.addAttribute("breadcrumbCategory", "Analytics");
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalCopies", totalCopies);
        model.addAttribute("availableCopies", availableCopies);
        model.addAttribute("issuedCopies", issuedCopies);
        model.addAttribute("totalLoans", totalLoans);
        model.addAttribute("returnedLoans", returnedLoans);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("overdueLoans", overdueLoans);
        model.addAttribute("totalFinesCollected", totalFinesCollected);
        model.addAttribute("categories", database.getBooks().stream().map(Book::getCategory).distinct().collect(Collectors.toList()));

        return "reports";
    }
}

