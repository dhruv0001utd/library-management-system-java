# Library Management System

A command-line Library Management System built in core Java, developed as the Evaluated Course
Project for **Programming in Java**. It demonstrates object-oriented design, layered architecture,
file-based persistence, and exception handling in a working end-to-end application.

## Overview

The system lets a librarian manage members, manage the book inventory, and handle the
issue/return workflow for books, including automatic overdue fine calculation. All data is
persisted to CSV files on disk, so state survives between runs, without needing an external
database server.

## Features

**1. Member Management**
- Add, update, remove members
- Search members by name
- Track and pay outstanding fines
- Email format validation

**2. Book Inventory Management**
- Add, update, remove books (CRUD)
- Track total vs. available copies
- Search books by title

**3. Issue / Return & Fine Calculation**
- Issue a book to a member (blocks if no copies available or member has unpaid fines)
- Return a book, with automatic fine calculation for late returns (₹5/day after a 14-day loan period)
- View a member's currently active loans

**4. Reporting**
- View all transactions
- View currently overdue books

## Architecture

The project follows a layered architecture:

```
UI Layer        (app)      → LibraryApp.java — menu-driven console interface
Service Layer   (service)  → BookService, MemberService, TransactionService — business logic
Storage Layer   (storage)  → FileStorage.java — generic CSV read/write
Model Layer     (model)    → Book, Member, Transaction — domain entities
Utility         (util)     → LibraryException — custom checked exception
```

See `statement.md` for the full problem statement and scope, and the project report PDF for
architecture/UML diagrams.

## Technologies / Tools Used

- **Language:** Java 17+ (developed and tested on Java 21)
- **Persistence:** Flat-file CSV storage (no external DB required)
- **Build/Run:** Plain `javac`/`java` — no external build tool required
- **Version Control:** Git

## Project Structure

```
LibraryManagementSystem/
├── src/main/java/com/library/
│   ├── app/LibraryApp.java
│   ├── model/Book.java
│   ├── model/Member.java
│   ├── model/Transaction.java
│   ├── service/BookService.java
│   ├── service/MemberService.java
│   ├── service/TransactionService.java
│   ├── storage/FileStorage.java
│   └── util/LibraryException.java
├── data/                # CSV data files (auto-created at runtime)
├── statement.md
├── README.md
└── .gitignore
```

## Setup & Installation

### Prerequisites
- **JDK 17 or later** installed and on your `PATH`.
  Check with:
  ```bash
  java -version
  javac -version
  ```
  If not installed, download from [Adoptium Temurin](https://adoptium.net/) or install via your
  OS package manager (e.g. `sudo apt install openjdk-21-jdk` on Ubuntu/Debian).

### Clone the repository
```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>
```

## How to Run

1. **Compile** all source files into a `bin` directory:
   ```bash
   find src -name "*.java" > sources.txt
   javac -d bin @sources.txt
   ```

2. **Run** the application:
   ```bash
   java -cp bin com.library.app.LibraryApp
   ```

3. You'll see the main menu:
   ```
   --- MAIN MENU ---
   1. Member Management
   2. Book Inventory Management
   3. Issue / Return Books
   4. Reports
   0. Exit
   ```
   Navigate using the numbered options. Each submenu loops until you choose `0` to go back.

4. Data is automatically saved to `data/books.csv`, `data/members.csv`, and
   `data/transactions.csv` after every change, so you can close and reopen the app without
   losing data.

## Instructions for Testing

A suggested manual test flow:

1. **Add a member:** Main Menu → `1` → `1`, enter an ID (e.g. `M001`), name, a valid email, and phone.
2. **Add a book:** Main Menu → `2` → `1`, enter an ISBN, title, author, genre, and copy count.
3. **Issue the book:** Main Menu → `3` → `1`, enter the ISBN and member ID.
4. **Check reports:** Main Menu → `4` → `1` to see the transaction, or `2` to check overdue books.
5. **Return the book:** Main Menu → `3` → `2`, enter the transaction ID shown when you issued it.
6. **Error handling check:** try adding a member with a duplicate ID, an invalid email, or issuing
   a book with an unknown ISBN — the app should show a clean `[ERROR] ...` message rather than
   crashing.

To reset all data, stop the app and delete the CSV files inside `data/` (they are recreated
automatically on next run).

## Non-Functional Requirements Addressed

- **Performance:** In-memory maps/lists with O(1)–O(n) lookups; CSV writes only on state changes.
- **Reliability:** All service methods validate state before mutating it; failed operations never
  leave partially-written files (writes happen only after in-memory state is confirmed valid).
- **Usability:** Simple numbered menu, input prompts, and clear `[ERROR] ...` messages instead of stack traces.
- **Maintainability:** Layered architecture (UI / service / storage / model) makes each concern
  independently testable and replaceable (e.g., swapping CSV storage for a real database only
  requires changing `FileStorage`).
- **Error Handling:** Centralized custom checked exception (`LibraryException`) surfaces
  domain errors distinctly from unexpected runtime errors.
- **Scalability:** Storage layer is abstracted behind a simple interface-like class, so it can be
  swapped for JDBC/a real database without touching the service or UI layers.

## Future Enhancements

- Migrate storage from CSV to an embedded database (e.g. SQLite) via JDBC
- Add JUnit test suite for the service layer
- Add a REST API layer on top of the existing service layer
- Support book reservations/waitlists

## Author

Developed as an Evaluated Course Project submission for **Programming in Java**.
