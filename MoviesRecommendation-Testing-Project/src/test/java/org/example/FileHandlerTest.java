package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlerTest {

    private FileHandler fileHandler;

    static class TestValidator implements Validator {
        boolean acceptAll = true;
        Set<String> existingMovieIds = new HashSet<>();
        Set<String> existingUserIds = new HashSet<>();

        @Override
        public boolean isValidMovieTitle(String title) {
            return acceptAll || !title.toLowerCase().contains("invalid");
        }

        @Override
        public boolean isMovieIdLettersValid(String title, String id) {
            return acceptAll || !id.toLowerCase().contains("bad");
        }

        @Override
        public boolean isMovieIdSuffixValid(String id, Set<String> ids) {
            return acceptAll || !ids.contains(id);
        }

        @Override
        public boolean isValidUserName(String name) {
            return acceptAll || !name.contains("#");
        }

        @Override
        public boolean isValidUserId(String id, Set<String> userIds) {
            return acceptAll || (!id.equals("INVALID") && !userIds.contains(id));
        }
    }

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
        TestValidator validator = new TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Inception,M001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(1, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testInvalidMovieTitle() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("invalidTitle,M001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.get(0).contains("Movie Title invalidTitle is wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testInvalidMovieIdLetters() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Inception,BAD123\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.get(0).contains("Movie Id letters BAD123 are wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testInvalidMovieIdSuffix() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Inception,M001\nAction,Drama\nInception,M001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.get(0).contains("Movie Id numbers M001 aren’t unique"));
        assertEquals(1, result.size());
    }

    @Test
    void testNullGenreLine() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Inception,M001\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(0, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testEmptyMovieFile() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = createTempFile("");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(result.isEmpty());
    }

    @Test
    void testValidUserFile() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);
        Set<String> validMovieIds = new HashSet<>(Arrays.asList("M001", "M002"));
        File file = createTempFile("Alice,12345678A\nM001,M002\n");

        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), validMovieIds, errors);
        assertEquals(1, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testInvalidUserName() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Invalid#Name,12345678A\nM001,M002\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001", "M002"), errors);

        assertTrue(errors.get(0).contains("User Name Invalid#Name is wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testInvalidUserId() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Issac,INVALID\nM001\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001"), errors);

        assertTrue(errors.get(0).contains("User Id INVALID is wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testUnknownLikedMovie() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Andrew,12345678C\nUNKNOWN\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001"), errors);

        assertTrue(errors.get(0).contains("has unknown movie ID UNKNOWN"));
    }

    @Test
    void testUserMissingLikesLine() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Poula,12345678D\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001"), errors);

        assertEquals(0, result.size());
    }

    @Test
    void testEmptyUserFile() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = createTempFile("");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001"), errors);

        assertTrue(result.isEmpty());
    }

    @Test
    void testReadLargeMovieFile() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);

        File file = File.createTempFile("large", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < 1000; i++) {
                writer.write("Title" + i + ",M" + String.format("%03d", i) + "\n");
                writer.write("Action,Drama\n");
            }
        }

        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(1000, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testWriteRecommendationsSuccess() throws IOException {
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");
        Map<User, List<String>> map = new HashMap<>();
        map.put(new User("Poula", "12345678E", List.of("M001")), List.of("Inception"));

        fileHandler.writeRecommendations(file.getAbsolutePath(), map, new ArrayList<>());

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals("Poula,12345678E", lines.get(0));
        assertEquals("Inception", lines.get(1));
    }

    @Test
    void testWriteRecommendationsWithError() throws IOException {
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");
        List<String> errorList = List.of("ERROR: Movie Id numbers M001 aren’t unique");

        fileHandler.writeRecommendations(file.getAbsolutePath(), new HashMap<>(), errorList);

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals(1, lines.size());
        assertTrue(lines.getFirst().contains("ERROR: Movie Id numbers M001 aren’t unique"));
    }

    @Test
    void testWriteRecommendationsEmptyMap() throws IOException {
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");

        fileHandler.writeRecommendations(file.getAbsolutePath(), new HashMap<>(), new ArrayList<>());

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals(0, lines.size());
    }

    @Test
    void testReadMoviesSkipsBlankLines() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = createTempFile("\nInception,M001\nAction\n\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(1, result.size());
    }


    @Test
    void testUnsupportedFileExtension() throws IOException {
        List<String> errors = new ArrayList<>();
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);

        File file = File.createTempFile("invalid", ".pdf");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Inception,M001\nAction,Drama\n");
        }

        fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.stream().anyMatch(e -> e.toLowerCase().contains("unsupported format")));
    }


}
