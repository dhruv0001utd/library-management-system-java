package com.library.app;

import com.library.model.*;
import com.library.service.*;
import com.library.util.LibraryException;

import java.util.List;
import java.util.Scanner;

/**
 * Command-line entry point and menu-driven UI for the Library Management System.
 * Delegates all business logic to the service layer (BookService, MemberService,
 * TransactionService) — this class only handles user I/O and input validation.
 *
 * Each submenu loops until the user chooses "0" to go back, for normal
 * menu-driven-app usability.
 */
public class LibraryApp {

    private final Scanner scanner = new Scanner(System.in);
    private final BookService bookService = new BookService();
    private final MemberService memberService = new MemberService();
    private final TransactionService transactionService =
            new TransactionService(bookService, memberService);

    public static void main(String[] args) {
        new LibraryApp().run();
    }

    public void run() {
        System.out.println("=================================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> memberMenu();
                case "2" -> bookMenu();
                case "3" -> transactionMenu();
                case "4" -> reportsMenu();
                case "0" -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Member Management");
        System.out.println("2. Book Inventory Management");
        System.out.println("3. Issue / Return Books");
        System.out.println("4. Reports");
        System.out.println("0. Exit");
        System.out.print("Enter choice: ");
    }

    // ---------------------- MEMBER MODULE ----------------------

    private void memberMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Member Management --");
            System.out.println("1. Add Member  2. Update Member  3. Remove Member");
            System.out.println("4. Search Member  5. List All  6. Pay Fine  0. Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            try {
                switch (c) {
                    case "1" -> {
                        System.out.print("Member ID: "); String id = scanner.nextLine().trim();
                        System.out.print("Name: "); String name = scanner.nextLine().trim();
                        System.out.print("Email: "); String email = scanner.nextLine().trim();
                        System.out.print("Phone: "); String phone = scanner.nextLine().trim();
                        memberService.addMember(new Member(id, name, email, phone));
                        System.out.println("Member added successfully.");
                    }
                    case "2" -> {
                        System.out.print("Member ID to update: "); String id = scanner.nextLine().trim();
                        System.out.print("New Name (blank to skip): "); String name = scanner.nextLine().trim();
                        System.out.print("New Email (blank to skip): "); String email = scanner.nextLine().trim();
                        System.out.print("New Phone (blank to skip): "); String phone = scanner.nextLine().trim();
                        memberService.updateMember(id, name, email, phone);
                        System.out.println("Member updated.");
                    }
                    case "3" -> {
                        System.out.print("Member ID to remove: "); String id = scanner.nextLine().trim();
                        memberService.removeMember(id);
                        System.out.println("Member removed.");
                    }
                    case "4" -> {
                        System.out.print("Name keyword: "); String kw = scanner.nextLine().trim();
                        List<Member> results = memberService.searchByName(kw);
                        results.forEach(System.out::println);
                        if (results.isEmpty()) System.out.println("No matches found.");
                    }
                    case "5" -> memberService.getAllMembers().forEach(System.out::println);
                    case "6" -> {
                        System.out.print("Member ID: "); String id = scanner.nextLine().trim();
                        System.out.print("Amount to pay: "); double amt = Double.parseDouble(scanner.nextLine().trim());
                        memberService.payFine(id, amt);
                        System.out.println("Payment recorded.");
                    }
                    case "0" -> back = true;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (LibraryException e) {
                System.out.println("[ERROR] " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Please enter a valid number.");
            }
        }
    }

    // ---------------------- BOOK MODULE ----------------------

    private void bookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Book Inventory Management --");
            System.out.println("1. Add Book  2. Update Book  3. Remove Book");
            System.out.println("4. Search by Title  5. List All  0. Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            try {
                switch (c) {
                    case "1" -> {
                        System.out.print("ISBN: "); String isbn = scanner.nextLine().trim();
                        System.out.print("Title: "); String title = scanner.nextLine().trim();
                        System.out.print("Author: "); String author = scanner.nextLine().trim();
                        System.out.print("Genre: "); String genre = scanner.nextLine().trim();
                        System.out.print("Total Copies: "); int copies = Integer.parseInt(scanner.nextLine().trim());
                        bookService.addBook(new Book(isbn, title, author, genre, copies));
                        System.out.println("Book added successfully.");
                    }
                    case "2" -> {
                        System.out.print("ISBN to update: "); String isbn = scanner.nextLine().trim();
                        System.out.print("New Title (blank to skip): "); String title = scanner.nextLine().trim();
                        System.out.print("New Author (blank to skip): "); String author = scanner.nextLine().trim();
                        System.out.print("New Genre (blank to skip): "); String genre = scanner.nextLine().trim();
                        System.out.print("New Total Copies (blank to skip): "); String copiesStr = scanner.nextLine().trim();
                        Integer copies = copiesStr.isBlank() ? null : Integer.parseInt(copiesStr);
                        bookService.updateBook(isbn, title, author, genre, copies);
                        System.out.println("Book updated.");
                    }
                    case "3" -> {
                        System.out.print("ISBN to remove: "); String isbn = scanner.nextLine().trim();
                        bookService.removeBook(isbn);
                        System.out.println("Book removed.");
                    }
                    case "4" -> {
                        System.out.print("Title keyword: "); String kw = scanner.nextLine().trim();
                        List<Book> results = bookService.searchByTitle(kw);
                        results.forEach(System.out::println);
                        if (results.isEmpty()) System.out.println("No matches found.");
                    }
                    case "5" -> bookService.getAllBooks().forEach(System.out::println);
                    case "0" -> back = true;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (LibraryException e) {
                System.out.println("[ERROR] " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Please enter a valid number.");
            }
        }
    }

    // ---------------------- TRANSACTION MODULE ----------------------

    private void transactionMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Issue / Return --");
            System.out.println("1. Issue Book  2. Return Book  3. View Member's Active Loans  0. Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            try {
                switch (c) {
                    case "1" -> {
                        System.out.print("ISBN: "); String isbn = scanner.nextLine().trim();
                        System.out.print("Member ID: "); String memberId = scanner.nextLine().trim();
                        Transaction t = transactionService.issueBook(isbn, memberId);
                        System.out.println("Book issued. " + t);
                    }
                    case "2" -> {
                        System.out.print("Transaction ID: "); String txnId = scanner.nextLine().trim();
                        Transaction t = transactionService.returnBook(txnId);
                        System.out.println("Book returned. " + t);
                    }
                    case "3" -> {
                        System.out.print("Member ID: "); String memberId = scanner.nextLine().trim();
                        transactionService.getActiveTransactionsForMember(memberId).forEach(System.out::println);
                    }
                    case "0" -> back = true;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (LibraryException e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    // ---------------------- REPORTS MODULE ----------------------

    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n-- Reports --");
            System.out.println("1. All Transactions  2. Overdue Books  0. Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();
            switch (c) {
                case "1" -> transactionService.getAllTransactions().forEach(System.out::println);
                case "2" -> {
                    List<Transaction> overdue = transactionService.getOverdueTransactions();
                    if (overdue.isEmpty()) System.out.println("No overdue books.");
                    else overdue.forEach(System.out::println);
                }
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
