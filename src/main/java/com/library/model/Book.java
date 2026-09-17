package com.library.model;

/**
 * Represents a single book in the library inventory.
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    // Used when loading from storage, where availableCopies may differ from totalCopies
    public Book(String isbn, String title, String author, String genre, int totalCopies, int availableCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre) { this.genre = genre; }

    public void setTotalCopies(int totalCopies) {
        int borrowed = this.totalCopies - this.availableCopies;
        this.totalCopies = totalCopies;
        this.availableCopies = Math.max(0, totalCopies - borrowed);
    }

    public boolean borrowCopy() {
        if (availableCopies <= 0) return false;
        availableCopies--;
        return true;
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    /** Serialize to a CSV row for file storage. */
    public String toCsv() {
        return String.join(",", isbn, escape(title), escape(author), escape(genre),
                String.valueOf(totalCopies), String.valueOf(availableCopies));
    }

    public static Book fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Book(p[0], unescape(p[1]), unescape(p[2]), unescape(p[3]),
                Integer.parseInt(p[4]), Integer.parseInt(p[5]));
    }

    private static String escape(String s) { return s.replace(",", ";"); }
    private static String unescape(String s) { return s.replace(";", ","); }

    @Override
    public String toString() {
        return String.format("ISBN: %-13s | %-30s | %-20s | %-12s | Total: %2d | Available: %2d",
                isbn, title, author, genre, totalCopies, availableCopies);
    }
}
