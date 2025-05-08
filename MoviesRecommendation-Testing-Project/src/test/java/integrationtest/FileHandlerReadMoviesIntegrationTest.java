package org.integration_test;

import org.example.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class FileHandlerReadMoviesIntegrationTest {

    private FileHandler fileHandler;

    private File createTempFile(String content) throws IOException {
        File file = File.createTempFile("test", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void testValidMovieFile() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Inception,INCP001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(1, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testInvalidMovieTitle() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("invalidTitle,INCP001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.get(0).contains("Movie Title invalidTitle is wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testInvalidMovieIdLetters() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Inception,BAD123\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.get(0).contains("Movie Id letters BAD123 are wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testInvalidMovieIdSuffix() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Inception,INCP001\nAction,Drama\nInception,INCP001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.get(0).contains("Movie Id numbers INCP001 aren’t unique"));
        assertEquals(1, result.size());
    }

    @Test
    void testNullGenreLine() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Inception,INCP001\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(0, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testEmptyMovieFile() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(result.isEmpty());
    }

    @Test
    void testReadLargeMovieFile() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());

        File file = File.createTempFile("large", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < 50; i++) {
                writer.write("Test Movie" + ", TM" + String.format("%03d", i) + "\n");
                writer.write("Action,Drama\n");
            }
        }

        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(50, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testReadMoviesSkipsBlankLines() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("\nInception,INCP002\nAction\n\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(1, result.size());
    }

    @Test
    void testUnsupportedFileExtension() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());

        File file = File.createTempFile("invalid", ".pdf");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Inception,INCP001\nAction,Drama\n");
        }

        fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.stream().anyMatch(e -> e.toLowerCase().contains("unsupported format")));
    }
}