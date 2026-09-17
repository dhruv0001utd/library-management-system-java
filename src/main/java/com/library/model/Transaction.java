package com.library.model;

import java.time.LocalDate;

/**
 * Represents a single book-issue transaction (borrow/return record).
 */
public class Transaction {
    public enum Status { ISSUED, RETURNED }

    private String transactionId;
    private String isbn;
    private String memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null if still issued
    private Status status;

    public Transaction(String transactionId, String isbn, String memberId,
                        LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.status = Status.ISSUED;
    }

    public Transaction(String transactionId, String isbn, String memberId, LocalDate issueDate,
                        LocalDate dueDate, LocalDate returnDate, Status status) {
        this.transactionId = transactionId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public String getTransactionId() { return transactionId; }
    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public Status getStatus() { return status; }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = Status.RETURNED;
    }

    public String toCsv() {
        return String.join(",", transactionId, isbn, memberId,
                issueDate.toString(), dueDate.toString(),
                returnDate == null ? "" : returnDate.toString(), status.name());
    }

    public static Transaction fromCsv(String line) {
        String[] p = line.split(",", -1);
        LocalDate ret = p[5].isEmpty() ? null : LocalDate.parse(p[5]);
        return new Transaction(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4]),
                ret, Status.valueOf(p[6]));
    }

    @Override
    public String toString() {
        return String.format("TxnID: %-6s | ISBN: %-13s | MemberID: %-8s | Issued: %s | Due: %s | Returned: %-10s | Status: %s",
                transactionId, isbn, memberId, issueDate, dueDate,
                returnDate == null ? "-" : returnDate, status);
    }
}
