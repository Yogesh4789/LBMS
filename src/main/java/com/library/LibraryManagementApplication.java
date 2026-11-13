package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MAIN ENTRY POINT: Spring Boot Web Application
 * 
 * Library Management System - Boeing-Inspired Light Theme Web Application
 * 
 * Features:
 * - Pure In-Memory Java Collections architecture (ArrayList, HashMap, HashSet, Comparator)
 * - Zero external databases (No MySQL, Postgres, SQLite, JPA, Hibernate)
 * - Complete role-based access for Administrator, Librarian, and Member
 * - Full Book, Member, Librarian, Issuing, and Returning workflows
 */
@SpringBootApplication
public class LibraryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("  Library Management System Web Application Started!      ");
        System.out.println("  Access URL: http://localhost:8080                       ");
        System.out.println("  Default Admin: admin / admin123                         ");
        System.out.println("  Default Librarian: sarah / sarah123                     ");
        System.out.println("  Default Member: alice / alice123                        ");
        System.out.println("==========================================================");
    }
}

