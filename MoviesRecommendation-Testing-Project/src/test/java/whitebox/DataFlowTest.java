package whitebox;


import org.example.FileHandler;
import org.example.User;
import org.example.Validator;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class DataFlowTest {

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
    private static FileHandler fileHandler;
    @BeforeAll
    public static void setUp() {
        fileHandler = new FileHandler(new TestValidator());
    }

    @Test
    void testWriteRecommendationsSingleUser() throws IOException {
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");

        Map<User, List<String>> map = new HashMap<>();
        map.put(new User("Alice", "12345678A", List.of("M001", "M002")), List.of("Inception", "Tenet"));

        fileHandler.writeRecommendations(file.getAbsolutePath(), map, new ArrayList<>());

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals("Alice,12345678A", lines.get(0));
        assertEquals("Inception, Tenet", lines.get(1));
    }

    @Test
    void testWriteRecommendationsMultipleUsers() throws IOException {
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");

        Map<User, List<String>> map = new HashMap<>();
        map.put(new User("Bob", "23456789B", List.of("M010")), List.of("Interstellar"));
        map.put(new User("Clara", "34567890C", List.of("M020", "M021")), List.of("Matrix", "Avatar"));

        fileHandler.writeRecommendations(file.getAbsolutePath(), map, new ArrayList<>());

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());

        assertTrue(lines.contains("Bob,23456789B"));
        assertTrue(lines.contains("Interstellar"));
        assertTrue(lines.contains("Clara,34567890C"));
        assertTrue(lines.contains("Matrix, Avatar"));
    }

    @Test
    void testWriteRecommendationsErrorPresent() throws IOException {
        TestValidator validator = new TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");

        List<String> errorList = List.of("ERROR: User ID is invalid");

        fileHandler.writeRecommendations(file.getAbsolutePath(), new HashMap<>(), errorList);

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains("ERROR: User ID is invalid"));
    }

}
