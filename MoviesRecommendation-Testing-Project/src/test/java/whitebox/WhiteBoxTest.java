package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.logging.FileHandler;

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

         File file = File.createTempFile("invalid", ".pdf");
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

    @Test
    void testSkipEmptyGenreLine() throws IOException {
        // Setup: validator accepts all titles/IDs
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);

        // File content: valid title line followed by an empty genre line
        String content =
                "Inception,M001\n" +
                        "\n";
        File file = createTempFile(content);

        // Execute
        List<Movie> result = fileHandler.readMovies(file.getAbsolutePath(), errors);

        // Verify: no movies parsed, and no errors reported
        assertTrue(result.isEmpty(), "Should skip record when genre line is empty");
        assertTrue(errors.isEmpty(), "Error list should remain empty");
    }

    //conditional coverage testcases
    @Test
    void testWriteRecommendationsWithError() throws IOException {
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");
        List<String> errorList = List.of("ERROR: Movie Id numbers M001 aren’t unique");

        fileHandler.writeRecommendations(file.getAbsolutePath(), new HashMap<>(), errorList);

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals(1, lines.size());
        assertTrue(lines.getFirst().contains("ERROR: Movie Id numbers M001 aren’t unique"));
    }
    @Test
    void testWriteRecommendationsSuccess() throws IOException {
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        fileHandler = new FileHandler(validator);
        File file = File.createTempFile("recommendations", ".txt");
        Map<User, List<String>> map = new HashMap<>();
        map.put(new User("Poula", "12345678E", List.of("M001")), List.of("Inception"));

        fileHandler.writeRecommendations(file.getAbsolutePath(), map, new ArrayList<>());

        List<String> lines = java.nio.file.Files.readAllLines(file.toPath());
        assertEquals("Poula,12345678E", lines.get(0));
        assertEquals("Inception", lines.get(1));
    }

    //Basis path coverage testcases
    @Test
    void testInValidUserFile() throws IOException {
        List<String> errors = new ArrayList<>();
        Set<String> validMovieIds = new HashSet<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);

        List<User> result = fileHandler.readUsers("file.json",validMovieIds,errors);

        assertEquals(1, errors.size());
        assertTrue(result.isEmpty());
    }
    @Test
    void testInvalidUserId() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Issac,INVALID\nM001\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001"), errors);

        assertTrue(errors.get(0).contains("User Id INVALID is wrong"));
        assertEquals(0, result.size());
    }

    @Test
    void testEmptyUserFile() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        fileHandler = new FileHandler(validator);
        File file = createTempFile("");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001"), errors);

        assertTrue(result.isEmpty());
    }

    @Test
    void testInvalidMovieIdinUser() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = false;
        fileHandler = new FileHandler(validator);
        File file = createTempFile("Alice,12345678A\nUNKNOWN\n");
        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), Set.of("M001"), errors);

        assertTrue(errors.get(0).contains("ERROR: User " + "Alice" + " has unknown movie ID " + "UNKNOWN"));
    }
    @Test
    void testValidUserFile() throws IOException {
        List<String> errors = new ArrayList<>();
        FileHandlerTest.TestValidator validator = new FileHandlerTest.TestValidator();
        validator.acceptAll = true;
        fileHandler = new FileHandler(validator);
        Set<String> validMovieIds = new HashSet<>(Arrays.asList("M001", "M002"));
        File file = createTempFile("Alice,12345678A\nM001,M002\nBob,12345678A\nM001,M002\n");

        List<User> result = fileHandler.readUsers(file.getAbsolutePath(), validMovieIds, errors);
        assertEquals(2, result.size());
        assertTrue(errors.isEmpty());
    }
}
