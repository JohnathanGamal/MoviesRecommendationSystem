package Blackbox;

import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BlackBoxTest {

    private InputValidator validator;
    private FileHandler fileHandler;
    private Set<String> userIds;
    private Set<String> movieIds;

    @BeforeEach
    void setUp() {
        validator = new InputValidator();
        fileHandler = new FileHandler(validator);
        userIds = new HashSet<>();
        movieIds = new HashSet<>();
        movieIds.add("AVT123");
    }

    // === Equivalence Class Partitioning Tests on InputValidator ===

    // --- Movie Title ---
    @Test
    void validMovieTitle_capitalWordsOnly() {
        assertTrue(validator.isValidMovieTitle("The Matrix"));
    }

    @Test
    void invalidMovieTitle_lowercaseStart() {
        assertFalse(validator.isValidMovieTitle("the Matrix"));
    }

    @Test
    void validMovieTitle_withNumbers() {
        assertTrue(validator.isValidMovieTitle("Room 101"));
    }

    @Test
    void invalidMovieTitle_specialChars() {
        assertFalse(validator.isValidMovieTitle("Iron@Man"));
    }

    // --- User Name ---
    @Test
    void validUserName_singleWord() {
        assertTrue(validator.isValidUserName("Alice"));
    }

    @Test
    void validUserName_multiWord() {
        assertTrue(validator.isValidUserName("Alice Smith"));
    }

    @Test
    void invalidUserName_containsDigit() {
        assertFalse(validator.isValidUserName("Alice1"));
    }

    @Test
    void invalidUserName_startsWithSpace() {
        assertFalse(validator.isValidUserName(" Alice"));
    }

    // --- User ID ---
    @Test
    void validUserId_format1() {
        assertTrue(validator.isValidUserId("12345678A", userIds));
    }

    @Test
    void validUserId_format2() {
        assertTrue(validator.isValidUserId("1ABCDEF2G", userIds));
    }

    @Test
    void invalidUserId_wrongLength() {
        assertFalse(validator.isValidUserId("1234567A", userIds));
    }

    @Test
    void invalidUserId_duplicate() {
        userIds.add("12345678A");
        assertFalse(validator.isValidUserId("12345678A", userIds));
    }

    // --- Movie ID Prefix ---
    @Test
    void validMovieIdPrefix() {
        assertTrue(validator.isMovieIdLettersValid("The Batman", "TB123"));
    }

    @Test
    void invalidMovieIdPrefix_missingLetter() {
        assertFalse(validator.isMovieIdLettersValid("The Batman", "T123"));
    }

    // --- Movie ID Suffix ---
    @Test
    void validMovieIdSuffix_unique() {
        assertTrue(validator.isMovieIdSuffixValid("AVT124", movieIds));
    }

    @Test
    void invalidMovieIdSuffix_duplicateSuffix() {
        assertFalse(validator.isMovieIdSuffixValid("XYZ123", movieIds));
    }

    @Test
    void invalidMovieIdSuffix_wrongFormat() {
        assertFalse(validator.isMovieIdSuffixValid("123XYZ", movieIds));
    }

    //////////////////////////// Decision Table on RecommendationSystem /////////////////////////////
@Test
void rule1_recommendationPossible() {
    Movie m1 = new Movie("Action One", "M1", List.of("Action"));
    Movie m2 = new Movie("Action Two", "M2", List.of("Action"));
    User user = new User("User1", "U1", List.of("M1"));
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1, m2)).get(user);
    assertEquals(List.of("Action Two"), recs);
}

@Test
void rule2_noMatchingGenres() {
    Movie m1 = new Movie("Drama One", "M1", List.of("Drama"));
    Movie m2 = new Movie("Comedy One", "M2", List.of("Comedy"));
    User user = new User("User2", "U2", List.of("M1"));
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1, m2)).get(user);
    assertTrue(recs.isEmpty());
}

@Test
void rule3_likedMovieHasNoGenres() {
    Movie m1 = new Movie("Unknown Genre", "M1", List.of());
    Movie m2 = new Movie("Any Genre", "M2", List.of("Action"));
    User user = new User("User3", "U3", List.of("M1"));
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1, m2)).get(user);
    assertTrue(recs.isEmpty());
}

@Test
void rule4_noGenresNoMatch() {
    Movie m1 = new Movie("No Genre", "M1", List.of());
    Movie m2 = new Movie("Other", "M2", List.of("Drama"));
    User user = new User("User4", "U4", List.of("M1"));
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1, m2)).get(user);
    assertTrue(recs.isEmpty());
}

@Test
void rule5_noLikedMovies() {
    Movie m1 = new Movie("Action One", "M1", List.of("Action"));
    User user = new User("User5", "U5", List.of());
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1)).get(user);
    assertTrue(recs.isEmpty());
}

@Test
void rule6_noLikesAndNoGenres() {
    Movie m1 = new Movie("No Genre", "M1", List.of());
    User user = new User("User6", "U6", List.of());
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1)).get(user);
    assertTrue(recs.isEmpty());
}

@Test
void rule7_noGenreMatchEvenIfLikedMovieHasGenres() {
    Movie m1 = new Movie("Action One", "M1", List.of("Action"));
    Movie m2 = new Movie("Drama One", "M2", List.of("Drama"));
    User user = new User("User7", "U7", List.of("M1"));
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1, m2)).get(user);
    assertTrue(recs.isEmpty());
}

@Test
void rule8_noLikesAndNoMatch() {
    Movie m1 = new Movie("Drama One", "M1", List.of("Drama"));
    User user = new User("User8", "U8", List.of());
    List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1)).get(user);
    assertTrue(recs.isEmpty());
}

    ////////////////////////////Equivalence Class Partitioning Tests on FileHandler /////////////////////////////

    @Test
    void testValidMovieInput() throws IOException {
        File tempFile = Files.createTempFile("movies", ".txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("Avengers,AVG123\n");
            writer.write("Action, Sci-Fi\n");
        }

        List<String> errors = new ArrayList<>();
        List<Movie> result = fileHandler.readMovies(tempFile.getAbsolutePath(), errors);

        assertTrue(errors.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Avengers", result.get(0).getTitle());
    }

    @Test
    void testInvalidMovieTitle() throws IOException {
        File tempFile = Files.createTempFile("movies", ".txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("avengers,AVG123\n");
            writer.write("Action\n");
        }

        List<String> errors = new ArrayList<>();
        fileHandler.readMovies(tempFile.getAbsolutePath(), errors);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("Movie Title"));
    }

    @Test
    void testDuplicateMovieIdSuffix() throws IOException {
        File tempFile = Files.createTempFile("movies", ".txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("Avengers,AVG123\n");
            writer.write("Action\n");
            writer.write("Batman,BTG123\n");
            writer.write("Action\n");
        }

        List<String> errors = new ArrayList<>();
        fileHandler.readMovies(tempFile.getAbsolutePath(), errors);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("Movie Id numbers"));
    }

    @Test
    void testUnsupportedFileExtension() throws IOException {
        File tempFile = Files.createTempFile("badfile", ".csv").toFile();
        List<String> errors = new ArrayList<>();
        fileHandler.readMovies(tempFile.getAbsolutePath(), errors);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("Unsupported format"));
    }
    //////////////////////////// ECP Tests on FileHandler.readUsers() ////////////////////////////

    @Test
    void testValidUserInput() throws IOException {
        File tempFile = Files.createTempFile("users", ".txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("John Doe,12345678A\n");
            writer.write("MOV123\n");
        }

        List<String> errors = new ArrayList<>();
        Set<String> validMovieIds = Set.of("MOV123");
        List<User> users = fileHandler.readUsers(tempFile.getAbsolutePath(), validMovieIds, errors);

        assertTrue(errors.isEmpty());
        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getName());
    }

    @Test
    void testInvalidUserName() throws IOException {
        File tempFile = Files.createTempFile("users", ".txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("J0hn,12345678A\n");  // Contains digit → invalid
            writer.write("MOV123\n");
        }

        List<String> errors = new ArrayList<>();
        Set<String> validMovieIds = Set.of("MOV123");
        fileHandler.readUsers(tempFile.getAbsolutePath(), validMovieIds, errors);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("User Name"));
    }

    @Test
    void testInvalidUserId() throws IOException {
        File tempFile = Files.createTempFile("users", ".txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("Alice,12345\n");  // Invalid ID: too short
            writer.write("MOV123\n");
        }

        List<String> errors = new ArrayList<>();
        Set<String> validMovieIds = Set.of("MOV123");
        fileHandler.readUsers(tempFile.getAbsolutePath(), validMovieIds, errors);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("User Id"));
    }

    @Test
    void testUnknownLikedMovieId() throws IOException {
        File tempFile = Files.createTempFile("users", ".txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("Bob,12345678B\n");
            writer.write("XYZ123\n");  // Invalid: unknown movie ID
        }

        List<String> errors = new ArrayList<>();
        Set<String> validMovieIds = Set.of("MOV123");
        fileHandler.readUsers(tempFile.getAbsolutePath(), validMovieIds, errors);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("has unknown movie ID"));
    }

    @Test
    void testReadUsersUnsupportedFileExtension() throws IOException {
        File tempFile = Files.createTempFile("users", ".csv").toFile();
        List<String> errors = new ArrayList<>();
        Set<String> validMovieIds = Set.of("MOV123");

        fileHandler.readUsers(tempFile.getAbsolutePath(), validMovieIds, errors);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).contains("Unsupported format"));
    }
    //////////////////////////// ECP Tests for FileHandler.writeRecommendations() ////////////////////////////

    @Test
    void testWriteRecommendations_userWithTwoMovies() throws IOException {
        File outputFile = Files.createTempFile("recommendations", ".txt").toFile();

        User user = new User("Alice", "12345678A", List.of("M1"));
        Map<User, List<String>> recommendations = new LinkedHashMap<>();
        recommendations.put(user, List.of("Movie A", "Movie B"));

        List<String> errors = new ArrayList<>();
        fileHandler.writeRecommendations(outputFile.getAbsolutePath(), recommendations, errors);

        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertEquals(2, lines.size());
        assertEquals("Alice,12345678A", lines.get(0));
        assertEquals("Movie A, Movie B", lines.get(1));
    }

    @Test
    void testWriteRecommendations_userWithNoMovies() throws IOException {
        File outputFile = Files.createTempFile("recommendations", ".txt").toFile();

        User user = new User("Bob", "98765432B", List.of("M1"));
        Map<User, List<String>> recommendations = new LinkedHashMap<>();
        recommendations.put(user, new ArrayList<>());

        List<String> errors = new ArrayList<>();
        fileHandler.writeRecommendations(outputFile.getAbsolutePath(), recommendations, errors);

        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertEquals(2, lines.size());
        assertEquals("Bob,98765432B", lines.get(0));
        assertEquals("", lines.get(1));  // Empty recommendation line
    }

    @Test
    void testWriteRecommendations_emptyMap() throws IOException {
        File outputFile = Files.createTempFile("recommendations", ".txt").toFile();

        Map<User, List<String>> recommendations = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        fileHandler.writeRecommendations(outputFile.getAbsolutePath(), recommendations, errors);

        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertEquals(0, lines.size());  // No lines written
    }
    // === Boundary Value Analysis Tests on InputValidator ===

    // --- isValidUserId (length = 9, pattern match, uniqueness) ---
    @Test
    void bva_userId_exactlyNineChars_pattern1() {
        assertTrue(validator.isValidUserId("12345678A", new HashSet<>())); // Valid length & format
    }

    @Test
    void bva_userId_tooShort() {
        assertFalse(validator.isValidUserId("1234567A", new HashSet<>())); // 8 chars
    }

    @Test
    void bva_userId_tooLong() {
        assertFalse(validator.isValidUserId("1234567890", new HashSet<>())); // 10 chars
    }

    @Test
    void bva_userId_exactlyNine_pattern2() {
        assertTrue(validator.isValidUserId("1ABCDEF2G", new HashSet<>())); // Valid alt pattern
    }

    @Test
    void bva_userId_valid_but_duplicate() {
        Set<String> existing = new HashSet<>(Set.of("1ABCDEF2G"));
        assertFalse(validator.isValidUserId("1ABCDEF2G", existing));
    }
    // --- isMovieIdLettersValid (prefix matching test) ---
    @Test
    void bva_movieIdPrefix_exactMatch() {
        assertTrue(validator.isMovieIdLettersValid("The Batman", "TB123")); // "TB" match
    }

    @Test
    void bva_movieIdPrefix_missingLetter() {
        assertFalse(validator.isMovieIdLettersValid("The Batman", "T123")); // Missing "B"
    }

    @Test
    void bva_movieIdPrefix_multiWordWithDigits() {
        assertTrue(validator.isMovieIdLettersValid("Iron Man 2", "IM123")); // Only "IM"
    }

    @Test
    void bva_movieIdPrefix_lowercaseTitleMiss() {
        assertTrue(validator.isMovieIdLettersValid("Iron man", "IM456")); // Only "I"
    }

    @Test
    void bva_movieIdPrefix_singleCharTitle() {
        assertTrue(validator.isMovieIdLettersValid("A", "A123")); // Edge case with 1 char
    }
}



