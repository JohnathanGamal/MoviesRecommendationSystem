package org.integration_test;

import org.example.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class FileHandlerReadUsersIntegrationTest {

    private FileHandler fileHandler;

    private File createTempFile(String content) throws IOException {
        File file = File.createTempFile("testu", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void testValidUserFile() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        Set<String> validMovieIds = new HashSet<>(Arrays.asList("INCP001", "M002"));
        File file = createTempFile("Alice,12345678A\nINCP001,M002\n");

        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), validMovieIds, errors);
        assertEquals(1, result.size());
        assertTrue(errors.isEmpty());
    }

    @Test
    void testInvalidUserName() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Invalid#Name,12345678A\nINCP001,M002\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("INCP001", "M002"), errors);

        assertTrue(errors.get(0).contains("User Name Invalid#Name is wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testInvalidUserId() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Issac,INVALID\nINCP001\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("INCP001"), errors);

        assertTrue(errors.get(0).contains("User Id INVALID is wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testUnknownLikedMovie() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Andrew,12345678C\nUNKNOWN\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("INCP001"), errors);

        assertTrue(errors.get(0).contains("has unknown movie ID UNKNOWN"));
    }

    @Test
    void testUserMissingLikesLine() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Poula,12345678D\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("INCP001"), errors);

        assertEquals(0, result.size());
    }

    @Test
    void testEmptyUserFile() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("INCP001"), errors);

        assertTrue(result.isEmpty());
    }

    @Test
    void testDuplicateUserIds() throws IOException {
        List<String> errors = new ArrayList<>();
        fileHandler = new FileHandler(new InputValidator());
        File file = createTempFile("Alice,12345678A\nINCP001\nBob,12345678A\nM002\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("INCP001", "M002"), errors);

        assertTrue(errors.get(0).contains("User Id 12345678A is wrong"));
        assertEquals(1, result.size());
    }
}