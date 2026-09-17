package com.library.service;

import com.library.model.Book;
import com.library.storage.FileStorage;
import com.library.util.LibraryException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Module 2: Book Inventory Management.
 * Handles CRUD operations on books, backed by CSV file storage.
 */
public class BookService {

    private final FileStorage storage = new FileStorage("books.csv");
    private final Map<String, Book> books = new LinkedHashMap<>();

    public BookService() {
        for (Book b : storage.readAll(Book::fromCsv)) {
            books.put(b.getIsbn(), b);
        }
    }

    public void addBook(Book book) throws LibraryException {
        if (books.containsKey(book.getIsbn())) {
            throw new LibraryException("A book with ISBN " + book.getIsbn() + " already exists.");
        }
        books.put(book.getIsbn(), book);
        persist();
    }

    public void updateBook(String isbn, String title, String author, String genre, Integer totalCopies)
            throws LibraryException {
        Book book = getBookOrThrow(isbn);
        if (title != null && !title.isBlank()) book.setTitle(title);
        if (author != null && !author.isBlank()) book.setAuthor(author);
        if (genre != null && !genre.isBlank()) book.setGenre(genre);
        if (totalCopies != null) book.setTotalCopies(totalCopies);
        persist();
    }

    public void removeBook(String isbn) throws LibraryException {
        Book book = getBookOrThrow(isbn);
        if (book.getAvailableCopies() != book.getTotalCopies()) {
            throw new LibraryException("Cannot remove book with copies currently issued.");
        }
        books.remove(isbn);
        persist();
    }

    public Book getBookOrThrow(String isbn) throws LibraryException {
        Book b = books.get(isbn);
        if (b == null) throw new LibraryException("No book found with ISBN: " + isbn);
        return b;
    }

    public List<Book> searchByTitle(String keyword) {
        String k = keyword.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(k))
                .collect(Collectors.toList());
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public void persist() {
        storage.writeAll(new ArrayList<>(books.values()), Book::toCsv);
    }
}
