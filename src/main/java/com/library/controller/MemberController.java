package com.library.controller;

import com.library.model.BorrowTransaction;
import com.library.model.Member;
import com.library.model.User;
import com.library.service.AuthenticationService;
import com.library.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * CONTROLLER: MemberController
 * 
 * Manages member registrations, profile edits, status toggling, and loan history.
 */
@Controller
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationService authService;

    public MemberController(MemberService memberService, AuthenticationService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @GetMapping("/members")
    public String listMembers(HttpSession session,
                              @RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                              Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            return "redirect:/dashboard";
        }

        User currentUser = authService.getCurrentUser(session);
        List<Member> memberList = memberService.filterAndSearch(query, status);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "members");
        model.addAttribute("pageTitle", "Members Directory");
        model.addAttribute("breadcrumbCategory", "Directory");
        model.addAttribute("members", memberList);
        model.addAttribute("selectedQuery", query != null ? query : "");
        model.addAttribute("selectedStatus", status);

        return "members";
    }

    @PostMapping("/members/add")
    public String registerMember(HttpSession session,
                                 @RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "address", required = false) String address,
                                 @RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Unauthorized role.");
            return "redirect:/members";
        }

        Member member = memberService.registerMember(name, email, phone, address, username, password);
        redirectAttributes.addFlashAttribute("successMessage", "Member '" + member.getName() + "' registered successfully! (ID: " + member.getUserId() + ")");
        return "redirect:/members";
    }

    @PostMapping("/members/edit")
    public String editMember(HttpSession session,
                             @RequestParam("memberId") String memberId,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email,
                             @RequestParam(value = "phone", required = false) String phone,
                             @RequestParam(value = "address", required = false) String address,
                             RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN", "LIBRARIAN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Unauthorized role.");
            return "redirect:/members";
        }

        memberService.updateMember(memberId, name, email, phone, address);
        redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully!");
        return "redirect:/members";
    }

    @PostMapping("/members/toggle-status")
    public String toggleStatus(HttpSession session,
                               @RequestParam("memberId") String memberId,
                               RedirectAttributes redirectAttributes) {
        if (!authService.hasRole(session, "ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: Only Administrators can toggle membership status.");
            return "redirect:/members";
        }

        memberService.toggleStatus(memberId);
        redirectAttributes.addFlashAttribute("successMessage", "Membership status toggled successfully.");
        return "redirect:/members";
    }

    @GetMapping("/members/{id}/history")
    public String viewHistory(@PathVariable("id") String memberId,
                              HttpSession session,
                              Model model) {
        if (!authService.isLoggedIn(session)) {
            return "redirect:/login";
        }

        User currentUser = authService.getCurrentUser(session);
        // Security check: Members can only see their own history!
        if ("MEMBER".equalsIgnoreCase(currentUser.getRole()) && !currentUser.getUserId().equalsIgnoreCase(memberId)) {
            return "redirect:/dashboard";
        }

        Member member = memberService.getMemberById(memberId);
        List<BorrowTransaction> history = memberService.getMemberHistory(memberId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("activeNav", "members");
        model.addAttribute("pageTitle", "Borrowing History");
        model.addAttribute("breadcrumbCategory", member.getName());
        model.addAttribute("member", member);
        model.addAttribute("transactions", history);
        model.addAttribute("selectedStatus", "ALL");
        model.addAttribute("selectedQuery", "");

        return "transactions";
    }

    @GetMapping("/api/members/{id}")
    @ResponseBody
    public Member getMemberApi(@PathVariable("id") String memberId) {
        return memberService.getMemberById(memberId);
    }
}

