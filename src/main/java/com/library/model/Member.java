package com.library.model;

import java.time.LocalDate;

/**
 * CLASS: Member (extends User)
 * 
 * Represents a library member/student who borrows books.
 * 
 * OOP CONCEPTS DEMONSTRATED:
 * - Inheritance: Subclasses abstract User class.
 * - Encapsulation: Member-specific attributes (phone, address, membershipDate).
 * - Constructor Chaining & Overloading: Uses super(...) and multiple constructors.
 * - Method Overriding: Implements getRole() and displayDetails().
 */
public class Member extends User {

    private String phone;
    private String address;
    private LocalDate membershipDate;

    public Member() {
        super();
        this.phone = "";
        this.address = "";
        this.membershipDate = LocalDate.now();
    }

    public Member(String userId, String name, String email, String username,
                  String password, String phone, String address) {
        super(userId, name, email, username, password, "ACTIVE");
        this.phone = phone != null ? phone.trim() : "";
        this.address = address != null ? address.trim() : "";
        this.membershipDate = LocalDate.now();
    }

    public Member(String userId, String name, String email, String username,
                  String password, String phone, String address, LocalDate membershipDate, String status) {
        super(userId, name, email, username, password, status);
        this.phone = phone != null ? phone.trim() : "";
        this.address = address != null ? address.trim() : "";
        this.membershipDate = membershipDate != null ? membershipDate : LocalDate.now();
    }

    @Override
    public String getRole() {
        return "MEMBER";
    }

    @Override
    public void displayDetails() {
        System.out.println("Member ID: " + getUserId() + ", Name: " + getName() + ", Phone: " + phone);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : "";
    }

    public LocalDate getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
    }

    @Override
    public String toString() {
        return "Member{id='" + getUserId() + "', name='" + getName() + "', status='" + getStatus() + "'}";
    }
}

