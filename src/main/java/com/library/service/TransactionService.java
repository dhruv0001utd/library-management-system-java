package com.library.service;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.storage.FileStorage;
import com.library.util.LibraryException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Module 3: Issue / Return workflow and fine calculation.
 * Coordinates BookService and MemberService to enforce business rules:
 *   - a book can only be issued if a copy is available
 *   - loan period is fixed (LOAN_DAYS)
 *   - returning late accrues a fine (FINE_PER_DAY) added to the member's account
 */
public class TransactionService {

    private static final int LOAN_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0; // in rupees

    private final FileStorage storage = new FileStorage("transactions.csv");
    private final List<Transaction> transactions = new ArrayList<>();
    private final BookService bookService;
    private final MemberService memberService;
    private int nextId;

    public TransactionService(BookService bookService, MemberService memberService) {
        this.bookService = bookService;
        this.memberService = memberService;
        transactions.addAll(storage.readAll(Transaction::fromCsv));
        this.nextId = transactions.stream()
                .mapToInt(t -> Integer.parseInt(t.getTransactionId().replace("T", "")))
                .max().orElse(0) + 1;
    }

    public Transaction issueBook(String isbn, String memberId) throws LibraryException {
        Book book = bookService.getBookOrThrow(isbn);
        Member member = memberService.getMemberOrThrow(memberId);

        if (member.getOutstandingFine() > 0) {
            throw new LibraryException("Member has an outstanding fine of Rs." +
                    member.getOutstandingFine() + ". Please clear dues before issuing.");
        }
        if (!book.borrowCopy()) {
            throw new LibraryException("No available copies for ISBN: " + isbn);
        }

        LocalDate today = LocalDate.now();
        Transaction txn = new Transaction(
                "T" + (nextId++), isbn, memberId, today, today.plusDays(LOAN_DAYS));
        transactions.add(txn);
        bookService.persist();
        persist();
        return txn;
    }

    public Transaction returnBook(String transactionId) throws LibraryException {
        Transaction txn = transactions.stream()
                .filter(t -> t.getTransactionId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new LibraryException("No transaction found with ID: " + transactionId));

        if (txn.getStatus() == Transaction.Status.RETURNED) {
            throw new LibraryException("Transaction " + transactionId + " is already marked as returned.");
        }

        LocalDate today = LocalDate.now();
        txn.markReturned(today);

        Book book = bookService.getBookOrThrow(txn.getIsbn());
        book.returnCopy();

        long lateDays = ChronoUnit.DAYS.between(txn.getDueDate(), today);
        if (lateDays > 0) {
            double fine = lateDays * FINE_PER_DAY;
            Member member = memberService.getMemberOrThrow(txn.getMemberId());
            member.addFine(fine);
            memberService.persist();
        }

        bookService.persist();
        persist();
        return txn;
    }

    public List<Transaction> getActiveTransactionsForMember(String memberId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getMemberId().equals(memberId) && t.getStatus() == Transaction.Status.ISSUED) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    /** Reporting: books currently overdue. */
    public List<Transaction> getOverdueTransactions() {
        LocalDate today = LocalDate.now();
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getStatus() == Transaction.Status.ISSUED && t.getDueDate().isBefore(today)) {
                result.add(t);
            }
        }
        return result;
    }

    public void persist() {
        storage.writeAll(transactions, Transaction::toCsv);
    }
}
