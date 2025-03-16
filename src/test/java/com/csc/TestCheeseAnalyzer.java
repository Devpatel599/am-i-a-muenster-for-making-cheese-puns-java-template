package com.csc;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCheeseAnalyzer {

    @Test
    void testFileProcessing() {
        CheeseAnalyzer.main(new String[]{});

        File outputFile = new File("output.txt");
        assertTrue(outputFile.exists(), "Output file should be created.");

        File missingIdsFile = new File("missing_ids.txt");
        assertTrue(missingIdsFile.exists(), "Missing IDs file should be created.");
    }

    @Test
    void testOutputContent() throws IOException {
        File outputFile = new File("output.txt");
        assertTrue(outputFile.exists(), "Output file should exist.");

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        assertFalse(lines.isEmpty(), "Output file should contain data.");
        assertTrue(lines.stream().anyMatch(l -> l.contains("Number of cheeses using pasteurized milk")),
                "Output should contain pasteurized cheese count.");
        assertTrue(lines.stream().anyMatch(l -> l.contains("Number of cheeses using raw milk")),
                "Output should contain raw cheese count.");
    }

    @Test
    void testMissingIdsFileContent() throws IOException {
        File missingIdsFile = new File("missing_ids.txt");
        assertTrue(missingIdsFile.exists(), "Missing IDs file should exist.");

        List<String> ids = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(missingIdsFile))) {
            String id;
            while ((id = reader.readLine()) != null) {
                ids.add(id);
            }
        }

        assertFalse(ids.isEmpty(), "Missing IDs file should contain data.");
        assertTrue(ids.get(0).matches("\\d+"), "Missing IDs file should contain only numbers.");
    }
}
