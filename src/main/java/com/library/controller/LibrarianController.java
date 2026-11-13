package com.library.controller;

import com.library.model.Librarian;
import com.library.model.User;
import com.library.service.AuthenticationService;
import com.library.service.LibrarianService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * CONTROLLER: LibrarianController
 * 
 * Manages staff librarian records. Access is restricted to Administrator role.
 */
@Controller
public class LibrarianController {

    private final LibrarianService librarianService;
    private final AuthenticationService authService;

    public LibrarianController(LibrarianService librarianService, AuthenticationService authService) {
        this.librarianService = librarianService;
        this.authService = authService;
    }

    @GetMapping("/librarians")
    public String listLibrarians(HttpSession session,
                                 @RequestParam(value = "query", required = false) String query,
                                 Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!authService.hasRole(session, "ADMIN")) {
            return "redirect:/dashboard";
        }

        User currentUser = authService.getCurrentUser(session);
        List<Librarian> librarianList = librarianService.filterAndSearch(query);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "librarians");
        model.addAttribute("pageTitle", "Staff Librarians");
        model.addAttribute("breadcrumbCategory", "Administration");
        model.addAttribute("librarians", librarianList);
        model.addAttribute("selectedQuery", query != null ? query : "");

        return "librarians";
    }

    @PostMapping("/librarians/add")
    public String addLibrarian(HttpSession session,
                               @RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password,
                               RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Only Administrators can add staff.");
            return "redirect:/librarians";
        }

        Librarian lib = librarianService.addLibrarian(name, email, username, password);
        redirectAttributes.addFlashAttribute("successMessage", "Librarian '" + lib.getName() + "' created! (ID: " + lib.getUserId() + ")");
        return "redirect:/librarians";
    }

    @PostMapping("/librarians/edit")
    public String editLibrarian(HttpSession session,
                                @RequestParam("librarianId") String librarianId,
                                @RequestParam("name") String name,
                                @RequestParam("email") String email,
                                RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Only Administrators can update staff.");
            return "redirect:/librarians";
        }

        librarianService.updateLibrarian(librarianId, name, email);
        redirectAttributes.addFlashAttribute("successMessage", "Librarian updated successfully!");
        return "redirect:/librarians";
    }

    @PostMapping("/librarians/delete")
    public String deleteLibrarian(HttpSession session,
                                  @RequestParam("librarianId") String librarianId,
                                  RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Only Administrators can remove staff.");
            return "redirect:/librarians";
        }

        librarianService.removeLibrarian(librarianId);
        redirectAttributes.addFlashAttribute("successMessage", "Librarian account removed.");
        return "redirect:/librarians";
    }
}

