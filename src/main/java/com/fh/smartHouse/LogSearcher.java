package com.fh.smartHouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogSearcher extends Thread {
    private final String logDirectory;
    private final Metadata metadata;
    private final String searchCriteria;
    private List<String> searchResults = new ArrayList<>();
    private volatile boolean isCancelled = false; // Flag to allow search cancellation

    public LogSearcher(String logDirectory, Metadata metadata, String searchCriteria) {
        this.logDirectory = logDirectory;
        this.metadata = metadata;
        this.searchCriteria = searchCriteria;
    }

    @Override
    public void run() {
        try {
            if (searchCriteria.matches("\\d{4}-\\d{2}-\\d{2}")) {
                searchResults = searchByDate(searchCriteria);
            } else if (searchCriteria.startsWith("content:")) {
                String keyword = searchCriteria.replace("content:", "").trim();
                searchResults = searchByContent(keyword);
            } else {
                searchResults = searchByEquipment(searchCriteria);
            }

            if (isCancelled) {
                System.out.println("Search was cancelled.");
                return;
            }

            // Print search results
            if (searchResults.isEmpty()) {
                System.out.println("No results found for: " + searchCriteria);
            } else {
                System.out.println("Search results for " + searchCriteria + ":");
                searchResults.forEach(System.out::println);
            }
        } catch (EMSException e) {
            System.err.println("An error occurred during search: " + e.getMessage());
        }
    }

    // Method to cancel the search operation
    public void cancelSearch() {
        isCancelled = true;
    }

    // Search log files by date in their names
    public List<String> searchByDate(String date) throws EMSException {
        if (isCancelled) return List.of();
        List<String> matchedFiles = new ArrayList<>();
        String regex = ".*" + date + ".*\\.log$";
        Pattern pattern = compilePattern(regex);

        String homeDirectory = System.getProperty("user.home");
        Path logDirPath = Paths.get(homeDirectory, "Documents", logDirectory);

        // Search files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, "*.log")) {
            for (Path entry : stream) {
                if (isCancelled) break; // Exit loop if search is cancelled
                Matcher matcher = pattern.matcher(entry.getFileName().toString());
                if (matcher.matches()) {
                    matchedFiles.add(entry.toString());
                }
            }
            metadata.logSearch(date, "searched by date");
        } catch (NoSuchFileException e) {
            throw new EMSFileNotFoundException("Log directory not found: " + logDirectory, e);
        } catch (IOException e) {
            throw new EMSFileReadException("Error reading log directory or files", e);
        }

        return matchedFiles;
    }

    // Search log files by equipment name in their names
    public List<String> searchByEquipment(String equipmentName) throws EMSException {
        if (isCancelled) return List.of();
        List<String> matchedFiles = new ArrayList<>();
        String regex = ".*" + equipmentName + ".*\\.log$";
        Pattern pattern = compilePattern(regex);

        String homeDirectory = System.getProperty("user.home");
        Path logDirPath = Paths.get(homeDirectory, "Documents", logDirectory);

        // Search files
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, "*.log")) {
            for (Path entry : stream) {
                if (isCancelled) break; // Exit loop if search is cancelled
                Matcher matcher = pattern.matcher(entry.getFileName().toString());
                if (matcher.matches()) {
                    matchedFiles.add(entry.toString());
                }
            }
            metadata.logSearch(equipmentName, "searched by equipment");
        } catch (NoSuchFileException e) {
            throw new EMSFileNotFoundException("Log directory not found: " + logDirectory, e);
        } catch (IOException e) {
            throw new EMSFileReadException("Error reading log directory or files", e);
        }

        return matchedFiles;
    }

    // Search log files by content (keywords)
    public List<String> searchByContent(String keyword) throws EMSException {
        if (isCancelled) return List.of();
        List<String> matchedLines = new ArrayList<>();

        String homeDirectory = System.getProperty("user.home");
        Path logDirPath = Paths.get(homeDirectory, "Documents", logDirectory);

        // Search file content
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, "*.log")) {
            for (Path file : stream) {
                if (isCancelled) break; // Exit loop if search is cancelled
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(keyword)) {
                            matchedLines.add("File: " + file.getFileName() + " | " + line);
                        }
                    }
                }
            }
            metadata.logSearch(keyword, "searched by content");
        } catch (NoSuchFileException e) {
            throw new EMSFileNotFoundException("Log directory not found: " + logDirectory, e);
        } catch (IOException e) {
            throw new EMSFileReadException("Error reading log directory or files", e);
        }

        return matchedLines;
    }

    // Open a log file and display its content
    public void openLogFile(String fileName) throws EMSException {
        if (isCancelled) return;
        String homeDirectory = System.getProperty("user.home");
        Path logFilePath = Paths.get(homeDirectory, "Documents", logDirectory, fileName);

        // Check if the file exists
        if (Files.exists(logFilePath)) {
            try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
                String line;
                System.out.println("\nContents of " + fileName + ":");
                while ((line = reader.readLine()) != null) {
                    if (isCancelled) break; // Exit loop if search is cancelled
                    System.out.println(line);
                }
                metadata.logOperation(fileName, "Log file opened");
            } catch (IOException e) {
                throw new EMSFileReadException("Error reading log file: " + fileName, e);
            }
        } else {
            throw new EMSFileNotFoundException("Log file does not exist: " + fileName);
        }
    }

    // Utility method to compile a regex pattern
    private Pattern compilePattern(String regex) throws EMSInvalidRegexException {
        try {
            return Pattern.compile(regex);
        } catch (Exception e) {
            throw new EMSInvalidRegexException("Invalid search pattern: " + regex, e);
        }
    }

    // Get search results (useful if needed in the main thread)
    public List<String> getSearchResults() {
        return new ArrayList<>(searchResults);
    }
}

