package com.fh.smartHouse;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class LogManager {

    private final String logDirectory;

    public LogManager(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    // Create a new log file
    public void createLogFile(String fileName) {
        try {
            Path logFilePath = getLogFilePath(fileName);

            Files.createDirectories(logFilePath.getParent()); // Ensure directories exist

            if (Files.notExists(logFilePath)) {
                Files.createFile(logFilePath);
                System.out.println("Log file created: " + logFilePath);
            } else {
                System.out.println("Log file already exists: " + logFilePath);
            }
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }

    // Delete a log file
    public void deleteLogFile(String fileName) {
        try {
            Path logFilePath = getLogFilePath(fileName);

            if (Files.exists(logFilePath)) {
                Files.delete(logFilePath);
                System.out.println("Log file deleted: " + logFilePath);
            } else {
                System.out.println("Log file does not exist: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error deleting log file: " + e.getMessage());
        }
    }

    // Move a log file
    public void moveLogFile(String fileName, String targetDirectory) {
        try {
            Path logFilePath = getLogFilePath(fileName);
            Path targetPath = getTargetPath(targetDirectory, fileName);

            if (Files.exists(logFilePath)) {
                Files.createDirectories(targetPath.getParent());
                Files.move(logFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Log file moved to: " + targetPath);
            } else {
                System.out.println("Log file does not exist: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Error moving log file: " + e.getMessage());
        }
    }

    // Archive log files older than a specified number of days
    public void archiveOldLogs(int daysOld) {
        try {
            Path logDirectoryPath = getDirectoryPath();
            Path archiveDirectoryPath = getDirectoryPath("archive");

            Files.createDirectories(archiveDirectoryPath);

            Files.list(logDirectoryPath).forEach(file -> {
                try {
                    BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
                    Instant fileTime = attributes.creationTime().toInstant();
                    long fileAgeInDays = TimeUnit.DAYS.convert(Instant.now().toEpochMilli() - fileTime.toEpochMilli(),
                            TimeUnit.MILLISECONDS);

                    if (fileAgeInDays > daysOld && !Files.isDirectory(file)) {
                        Path archivedFilePath = archiveDirectoryPath.resolve(file.getFileName());
                        Files.move(file, archivedFilePath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Archived file: " + file.getFileName());
                    }
                } catch (IOException e) {
                    System.err.println("Error archiving file: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Error accessing log directory: " + e.getMessage());
        }
    }

    // Log general activity
    public void logActivity(String message) {
        String timestamp = "[" + java.time.LocalDateTime.now() + "] ";
        String logEntry = timestamp + message + "\n";
        try {
            Path logFile = Paths.get(System.getProperty("user.home"), "Documents", logDirectory, "system_logs.txt");
            Files.write(logFile, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Logged activity: " + logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

//    public void logActivity(String activity) {
//        String logFileName = "activity_log.txt";
//        Path logFilePath = getLogFilePath(logFileName);
//
//        try {
//            Files.createDirectories(logFilePath.getParent());
//            Files.writeString(logFilePath, activity + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//            System.out.println("Activity logged: " + activity);
//        } catch (IOException e) {
//            System.err.println("Error logging activity: " + e.getMessage());
//        }
//    }

    // Get the full path for a log file
    private Path getLogFilePath(String fileName) {
        String homeDirectory = System.getProperty("user.home");
        return Paths.get(homeDirectory, "Documents", logDirectory, fileName);
    }

    // Get the full path for a directory
    private Path getDirectoryPath() {
        String homeDirectory = System.getProperty("user.home");
        return Paths.get(homeDirectory, "Documents", logDirectory);
    }

    private Path getDirectoryPath(String subDirectory) {
        String homeDirectory = System.getProperty("user.home");
        return Paths.get(homeDirectory, "Documents", logDirectory, subDirectory);
    }

    private Path getTargetPath(String targetDirectory, String fileName) {
        String homeDirectory = System.getProperty("user.home");
        return Paths.get(homeDirectory, "Documents", targetDirectory, fileName);
    }
}
