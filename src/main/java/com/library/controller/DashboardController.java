package com.library.controller;

import com.library.database.InMemoryDatabase;
import com.library.model.Book;
import com.library.model.BorrowTransaction;
import com.library.model.User;
import com.library.service.AuthenticationService;
import com.library.service.BorrowingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLLER: DashboardController
 * 
 * Aggregates real-time organizational metrics, recent activities,
 * and quick actions for the main dashboard view.
 */
@Controller
public class DashboardController {

    private final InMemoryDatabase database;
    private final BorrowingService borrowingService;
    private final AuthenticationService authService;

    public DashboardController(InMemoryDatabase database,
                               BorrowingService borrowingService,
                               AuthenticationService authService) {
        this.database = database;
        this.borrowingService = borrowingService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }

        User currentUser = authService.getCurrentUser(session);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("pageTitle", "Executive Dashboard");

        // 1. Dynamic System Metrics (Calculated dynamically from in-memory collections)
        int totalBooks = database.getBooks().size();
        int totalAvailableCopies = database.getBooks().stream().mapToInt(Book::getAvailableCopies).sum();
        int totalIssuedCopies = database.getBooks().stream().mapToInt(Book::getIssuedCopies).sum();
        int totalMembers = database.getMembers().size();
        int totalLibrarians = database.getLibrarians().size();
        int totalOverdue = borrowingService.getOverdueTransactions().size();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalAvailableCopies", totalAvailableCopies);
        model.addAttribute("totalIssuedCopies", totalIssuedCopies);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("totalLibrarians", totalLibrarians);
        model.addAttribute("totalOverdue", totalOverdue);

        // 2. Recent Transactions (Last 5)
        List<BorrowTransaction> allTxns = database.getTransactions();
        List<BorrowTransaction> recentTransactions = allTxns.stream()
                .sorted((a, b) -> b.getTransactionId().compareTo(a.getTransactionId()))
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentTransactions", recentTransactions);

        // 3. Recently Added Books (Last 5)
        List<Book> recentBooks = database.getBooks().stream()
                .sorted((a, b) -> b.getBookId().compareTo(a.getBookId()))
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentBooks", recentBooks);

        // 4. Member-specific data if logged in as Member
        if ("MEMBER".equalsIgnoreCase(currentUser.getRole())) {
            String memberId = currentUser.getUserId();
            List<BorrowTransaction> myActiveLoans = database.getTransactions().stream()
                    .filter(t -> t.getMemberId().equalsIgnoreCase(memberId) && !t.isReturned())
                    .collect(Collectors.toList());
            List<BorrowTransaction> myHistory = database.getTransactions().stream()
                    .filter(t -> t.getMemberId().equalsIgnoreCase(memberId))
                    .collect(Collectors.toList());
            long myOverdueCount = myActiveLoans.stream().filter(BorrowTransaction::isOverdue).count();

            model.addAttribute("myActiveLoans", myActiveLoans);
            model.addAttribute("myHistory", myHistory);
            model.addAttribute("myActiveCount", myActiveLoans.size());
            model.addAttribute("myOverdueCount", myOverdueCount);
        }

        return "dashboard";
    }
}

