package com.library.storage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;

/**
 * Generic CSV-backed persistence helper. Each domain "repository" class
 * (BookRepository, MemberRepository, TransactionRepository) uses this to
 * load/save its records to a dedicated .csv file under /data.
 *
 * This is the storage layer of the app's layered architecture:
 *   UI (LibraryApp) -> Service (BookService/MemberService/TransactionService)
 *   -> Storage (FileStorage) -> Flat files (data/*.csv)
 */
public class FileStorage {

    private final Path filePath;

    public FileStorage(String fileName) {
        this.filePath = Paths.get("data", fileName);
        ensureFileExists();
    }

    private void ensureFileExists() {
        try {
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage file: " + filePath, e);
        }
    }

    /** Reads every non-blank line and converts it using the given parser. */
    public <T> List<T> readAll(Function<String, T> parser) {
        List<T> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    result.add(parser.apply(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed reading storage file: " + filePath, e);
        }
        return result;
    }

    /** Overwrites the file with the given records, serialized via toCsv. */
    public <T> void writeAll(List<T> records, Function<T, String> serializer) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (T record : records) {
                writer.write(serializer.apply(record));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed writing storage file: " + filePath, e);
        }
    }
}
