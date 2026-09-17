package com.library.model;

/**
 * Represents a library member who can borrow books.
 */
public class Member {
    private String memberId;
    private String name;
    private String email;
    private String phone;
    private double outstandingFine;

    public Member(String memberId, String name, String email, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.outstandingFine = 0.0;
    }

    public Member(String memberId, String name, String email, String phone, double outstandingFine) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.outstandingFine = outstandingFine;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public double getOutstandingFine() { return outstandingFine; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    public void addFine(double amount) { this.outstandingFine += amount; }

    public boolean payFine(double amount) {
        if (amount <= 0 || amount > outstandingFine + 0.001) return false;
        outstandingFine -= amount;
        return true;
    }

    public String toCsv() {
        return String.join(",", memberId, escape(name), escape(email), escape(phone),
                String.valueOf(outstandingFine));
    }

    public static Member fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Member(p[0], unescape(p[1]), unescape(p[2]), unescape(p[3]), Double.parseDouble(p[4]));
    }

    private static String escape(String s) { return s.replace(",", ";"); }
    private static String unescape(String s) { return s.replace(";", ","); }

    @Override
    public String toString() {
        return String.format("ID: %-8s | %-25s | %-25s | %-12s | Fine Due: Rs.%.2f",
                memberId, name, email, phone, outstandingFine);
    }
}
