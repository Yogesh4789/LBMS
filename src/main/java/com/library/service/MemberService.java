package com.library.service;

import com.library.database.InMemoryDatabase;
import com.library.exception.MemberNotFoundException;
import com.library.model.BorrowTransaction;
import com.library.model.Member;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SERVICE: MemberService
 * 
 * Manages member registrations, profile updates, and borrowing history queries.
 */
@Service
public class MemberService {

    private final InMemoryDatabase database;

    public MemberService(InMemoryDatabase database) {
        this.database = database;
    }

    public List<Member> getAllMembers() {
        return new ArrayList<>(database.getMembers());
    }

    public Member getMemberById(String memberId) {
        Member member = database.findMemberById(memberId);
        if (member == null) {
            throw new MemberNotFoundException("No member found with ID: " + memberId);
        }
        return member;
    }

    public Member registerMember(String name, String email, String phone,
                                 String address, String username, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (database.isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already registered.");
        }

        String memberId = database.generateMemberId();
        String hashedPassword = InMemoryDatabase.hashPassword(password != null && !password.isEmpty() ? password : "password123");

        Member member = new Member(
                memberId,
                name.trim(),
                email != null ? email.trim() : "",
                username.trim(),
                hashedPassword,
                phone != null ? phone.trim() : "",
                address != null ? address.trim() : "",
                LocalDate.now(),
                "ACTIVE"
        );

        database.addMember(member);
        return member;
    }

    public Member updateMember(String memberId, String name, String email, String phone, String address) {
        Member member = getMemberById(memberId);

        if (name != null && !name.trim().isEmpty()) member.setName(name.trim());
        if (email != null && !email.trim().isEmpty()) member.setEmail(email.trim());
        if (phone != null) member.setPhone(phone.trim());
        if (address != null) member.setAddress(address.trim());

        return member;
    }

    public void toggleStatus(String memberId) {
        Member member = getMemberById(memberId);
        if ("ACTIVE".equalsIgnoreCase(member.getStatus())) {
            member.setStatus("DEACTIVATED");
        } else {
            member.setStatus("ACTIVE");
        }
    }

    public List<Member> filterAndSearch(String query, String status) {
        List<Member> result = new ArrayList<>(database.getMembers());

        if (query != null && !query.trim().isEmpty()) {
            String q = query.trim().toLowerCase();
            result = result.stream()
                    .filter(m -> m.getName().toLowerCase().contains(q)
                            || m.getUserId().toLowerCase().contains(q)
                            || m.getEmail().toLowerCase().contains(q)
                            || m.getUsername().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            result = result.stream()
                    .filter(m -> m.getStatus().equalsIgnoreCase(status.trim()))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public List<BorrowTransaction> getMemberHistory(String memberId) {
        return database.getTransactions().stream()
                .filter(t -> t.getMemberId().equalsIgnoreCase(memberId))
                .collect(Collectors.toList());
    }

    public long getActiveLoanCount(String memberId) {
        return database.getTransactions().stream()
                .filter(t -> t.getMemberId().equalsIgnoreCase(memberId) && !t.isReturned())
                .count();
    }
}

