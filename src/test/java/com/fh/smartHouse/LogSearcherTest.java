package com.fh.smartHouse;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogSearcherTest {

    private static final String TEST_LOG_DIR = "test_logs";
    private static final Path TEST_LOG_PATH = Paths.get(System.getProperty("user.home"), "Documents", TEST_LOG_DIR);
    private static Metadata metadata;

    @BeforeAll
    static void setup() throws Exception {
        metadata = new Metadata();
        Files.createDirectories(TEST_LOG_PATH); // Create test directory
    }

    @BeforeEach
    void createTestLogs() throws Exception {
        // Create sample log files
        Files.writeString(TEST_LOG_PATH.resolve("log_2024-11-01.log"), "Test log content for 2024-11-01\n");
        Files.writeString(TEST_LOG_PATH.resolve("log_2024-10-25.log"), "Test log content for 2024-10-25\n");
        Files.writeString(TEST_LOG_PATH.resolve("solar_panel.log"), "Solar panel activity log\n");
    }

    @AfterEach
    void cleanTestLogs() throws Exception {
        // Delete all files in the test directory
        Files.list(TEST_LOG_PATH).forEach(file -> {
            try {
                Files.deleteIfExists(file);
            } catch (Exception e) {
                System.err.println("Error cleaning test logs: " + e.getMessage());
            }
        });
    }

    @AfterAll
    static void tearDown() throws Exception {
        Files.deleteIfExists(TEST_LOG_PATH); // Delete test directory
    }

    @Test
    void testSearchByDate() throws Exception {
        String searchDate = "2024-11-01";

        LogSearcher logSearcher = new LogSearcher(TEST_LOG_DIR, metadata, searchDate);
        logSearcher.start();
        logSearcher.join(); // Wait for the thread to complete

        List<String> results = logSearcher.getSearchResults();
        assertEquals(1, results.size(), "Expected one result for the date 2024-11-01");
        assertTrue(results.get(0).contains("log_2024-11-01"), "Result should contain the correct log file name");
    }

    @Test
    void testSearchByEquipmentName() throws Exception {
        String searchKeyword = "solar_panel";

        LogSearcher logSearcher = new LogSearcher(TEST_LOG_DIR, metadata, searchKeyword);
        logSearcher.start();
        logSearcher.join(); // Wait for the thread to complete

        List<String> results = logSearcher.getSearchResults();
        assertEquals(1, results.size(), "Expected one result for the keyword solar_panel");
        assertTrue(results.get(0).contains("solar_panel.log"), "Result should contain the correct log file name");
    }

    @Test
    void testSearchWithNoResults() throws Exception {
        String searchKeyword = "non_existent_log";

        LogSearcher logSearcher = new LogSearcher(TEST_LOG_DIR, metadata, searchKeyword);
        logSearcher.start();
        logSearcher.join(); // Wait for the thread to complete

        List<String> results = logSearcher.getSearchResults();
        assertTrue(results.isEmpty(), "Expected no results for a non-existent keyword");
    }

    @Test
    void testOpenLogFile() throws Exception {
        String logFileName = "log_2024-10-25.log";

        LogSearcher logSearcher = new LogSearcher(TEST_LOG_DIR, metadata, logFileName);
        logSearcher.openLogFile(logFileName);

        Path logFilePath = TEST_LOG_PATH.resolve(logFileName);
        assertTrue(Files.exists(logFilePath), "Log file should exist");
        String content = Files.readString(logFilePath);
        assertTrue(content.contains("Test log content for 2024-10-25"), "Content should match the expected log content");
    }

    @Test
    void testInvalidRegexException() {
        String invalidRegex = "[invalid regex";

        LogSearcher logSearcher = new LogSearcher(TEST_LOG_DIR, metadata, invalidRegex);

        Exception exception = assertThrows(EMSInvalidRegexException.class, logSearcher::run);
        assertTrue(exception.getMessage().contains("Invalid search pattern"), "Expected an EMSInvalidRegexException for invalid regex");
    }
}
