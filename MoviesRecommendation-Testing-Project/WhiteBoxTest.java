package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class WhiteBoxTest {
    private List<Movie> movieList;
    private List<User> userList;
    private FileHandler fileHandler;

    private File createTempFile(String content) throws IOException {
        File file = File.createTempFile("test", ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
        return file;
    }

    @BeforeEach
    void setup() {
        movieList = new ArrayList<>();
        userList = new ArrayList<>();
    }

    // Statement Coverage Testcases
    @Test
    void testSingleUserWithUniqueLikedGenre() {
        movieList.add(new Movie("Avengers", "M001", List.of("Action")));
        movieList.add(new Movie("John Wick", "M002", List.of("Action")));
        movieList.add(new Movie("The Notebook", "M003", List.of("Drama")));

        User user = new User("Jonathan", "12345678A", List.of("M001"));
        userList.add(user);


        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        assertEquals(List.of("John Wick"), result.get(user));
    }

    @Test
    void testLikedMovieNotInList() {
        movieList.add(new Movie("Titanic", "M001", List.of("Romance")));
        movieList.add(new Movie("Gladiator", "M002", List.of("Action")));
        movieList.add(new Movie("Interstellar", "M003", List.of("Sci-fi")));

        User user = new User("Poula", "99887766D", List.of("M999"));
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        assertTrue(result.get(user).isEmpty());
    }

    //Branch Coverage Testcases
    @Test
    void testUnsupportedFormat() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        fileHandler = new FileHandler(validator);

        File file = createTempFile("invalid.pdf");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Inception,M001\nAction,Drama\n");
        }

        fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.stream().anyMatch(e -> e.toLowerCase().contains("unsupported format")));
    }
    @Test
    void testValidMovieFile() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Inception,M001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(1, result.size());
        assertTrue(errors.isEmpty());
    }
    @Test
    void testNullGenreLine() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Inception,M001\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertEquals(0, result.size());
        assertTrue(errors.isEmpty());
    }
    @Test
    void testInvalidMovieTitle() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
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
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
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
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Inception,M001\nAction,Drama\nInception,M001\nAction,Drama\n");
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        assertTrue(errors.get(0).contains("Movie Id numbers M001 aren’t unique"));
        assertEquals(1, result.size());
    }

    @Test
    void testSkipTitleAndMalformedLines() throws IOException {
        // Setup: validator accepts all titles/IDs
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);

        // File content: blank line, malformed title line, valid genres, then one well‑formed record
        String content =
                "\n" +
                        "BadLineNoComma\n" +
                        "Action,Comedy\n" +
                        "MovieA,M001\n" +
                        "Action,Genre\n";
        File file = createTempFile(content);

        // Execute
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        // Verify: only the valid “MovieA” record is parsed
        assertEquals(1, result.size(), "Should parse exactly one movie");
        Movie m = result.get(0);
        assertEquals("MovieA", m.getTitle());
        assertEquals("M001", m.getId());
        assertEquals(List.of("Action", "Genre"), m.getGenres());

        // No errors should be reported
        assertTrue(errors.isEmpty(), "Error list should remain empty");
    }
}
