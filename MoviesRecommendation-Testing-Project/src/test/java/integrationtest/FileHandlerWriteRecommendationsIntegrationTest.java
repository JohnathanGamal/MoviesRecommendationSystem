package org.integration_test;

import org.example.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class FileHandlerWriteRecommendationsIntegrationTest {

    private FileHandler fileHandler;

    @Test
    void testWriteRecommendationsSuccess() throws IOException {
        fileHandler = new FileHandler(new InputValidator());
        File file = File.createTempFile("recommendations", ".txt");
        Map<User, List<String>> map = new HashMap<>();
        map.put(new User("Poula", "12345678E", List.of("INCP001")), List.of("Inception"));
        map.put(new User("Rami", "98765432Q", List.of("INCP001")), List.of("Matrix", "Avatar"));

        fileHandler.writeRecommendations(file.getAbsolutePath(), map, new ArrayList<>());

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals("Poula,12345678E", lines.get(0));
        assertEquals("Inception", lines.get(1));
        assertEquals("Rami,98765432Q", lines.get(2));
        assertEquals("Matrix, Avatar", lines.get(3));
    }

    @Test
    void testWriteRecommendationsWithError() throws IOException {
        fileHandler = new FileHandler(new InputValidator());
        File file = File.createTempFile("recommendations", ".txt");
        List<String> errorList = List.of("ERROR: Movie Id numbers INCP001 aren’t unique");

        fileHandler.writeRecommendations(file.getAbsolutePath(), new HashMap<>(), errorList);

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals(1, lines.size());
        assertTrue(lines.getFirst().contains("ERROR: Movie Id numbers INCP001 aren’t unique"));
    }

    @Test
    void testWriteRecommendationsEmptyMap() throws IOException {
        fileHandler = new FileHandler(new InputValidator());
        File file = File.createTempFile("recommendations", ".txt");

        fileHandler.writeRecommendations(file.getAbsolutePath(), new HashMap<>(), new ArrayList<>());

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals(0, lines.size());
    }
}