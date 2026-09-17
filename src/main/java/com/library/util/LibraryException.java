package com.library.util;

/**
 * Custom checked exception used across the application for
 * domain-specific error conditions (e.g. book not found, no copies
 * available, member already exists, invalid fine payment, etc.)
 */
public class LibraryException extends Exception {
    public LibraryException(String message) {
        super(message);
    }
}
