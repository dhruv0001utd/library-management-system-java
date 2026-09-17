# Problem Statement

## Problem Statement

Physical and small institutional libraries often manage book inventories, member records, and
borrowing activity manually using registers or spreadsheets. This is error-prone, hard to search,
and makes it difficult to track overdue books and fines. There is a need for a lightweight,
dependency-free system that lets a librarian manage members, track book inventory, and handle the
issue/return workflow — including automatic fine calculation for late returns — without requiring
a full server/database setup.

## Scope of the Project

The system is a **single-user, command-line application** intended for use by a librarian or
library staff member at a small institution (e.g., a school, college department, or community
library). It covers:

- Member registration and management
- Book inventory management (CRUD)
- Issuing and returning books
- Automatic fine calculation for overdue returns
- Basic reporting (all transactions, overdue books)

**Out of scope** (noted as future enhancements): multi-user/concurrent access, a graphical or web
interface, book reservations/waitlists, and integration with an external database server (the
current version uses file-based CSV storage for simplicity and portability).

## Target Users

- **Librarians / library staff** who need a simple tool to track members, inventory, and loans
  without setting up complex software.
- **Small institutions** (school/college libraries, community libraries) without budget or need
  for a full Library Information System.

## High-Level Features

1. **Member Management** — register, update, remove, and search members; track and settle fines.
2. **Book Inventory Management** — add, update, remove, and search books; track total vs.
   available copies.
3. **Issue / Return Workflow** — issue books (with availability and fine-status checks), return
   books (with automatic overdue fine calculation), and view a member's active loans.
4. **Reporting** — view all transactions and currently overdue books.

## Technology Choices

- **Java (core, no external frameworks)** — chosen to align with the "Programming in Java"
  course syllabus and to keep the project runnable via plain `javac`/`java` from any terminal,
  satisfying the command-line-executability requirement.
- **CSV file-based storage** — chosen over an external DB server so the project has zero setup
  dependencies beyond a JDK, while still demonstrating a proper storage abstraction layer
  (`FileStorage`) that could be swapped for JDBC/a real database with minimal changes elsewhere.
