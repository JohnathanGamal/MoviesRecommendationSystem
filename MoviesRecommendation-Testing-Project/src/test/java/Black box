package bLackBox;

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
    void tc1_noLikedMovies() {
        User user = new User("Alice", "U1", List.of());
        List<Movie> movies = List.of(new Movie("Movie A", "M1", List.of("Action")));
        assertTrue(RecommendationSystem.generateRecommendations(List.of(user), movies).get(user).isEmpty());
    }

    @Test
    void tc2_likedMovieNotInDB() {
        User user = new User("Bob", "U2", List.of("M2"));
        List<Movie> movies = List.of(new Movie("Movie A", "M1", List.of("Action")));
        assertTrue(RecommendationSystem.generateRecommendations(List.of(user), movies).get(user).isEmpty());
    }

    @Test
    void tc3_likedMovieHasNoGenres() {
        Movie m1 = new Movie("Movie A", "M1", List.of());
        User user = new User("Charlie", "U3", List.of("M1"));
        List<Movie> movies = List.of(m1, new Movie("Other", "M2", List.of("Comedy")));
        assertTrue(RecommendationSystem.generateRecommendations(List.of(user), movies).get(user).isEmpty());
    }

    @Test
    void tc4_noOtherMovieSharesGenre() {
        Movie m1 = new Movie("Movie A", "M1", List.of("Action"));
        Movie m2 = new Movie("Movie B", "M2", List.of("Drama"));
        User user = new User("Dana", "U4", List.of("M1"));
        List<Movie> movies = List.of(m1, m2);
        assertTrue(RecommendationSystem.generateRecommendations(List.of(user), movies).get(user).isEmpty());
    }

    @Test
    void tc5_allGenreMatchesAlreadyLiked() {
        Movie m1 = new Movie("Movie A", "M1", List.of("Mystery"));
        Movie m2 = new Movie("Movie B", "M2", List.of("Mystery"));
        User user = new User("Eve", "U5", List.of("M1", "M2"));
        List<Movie> movies = List.of(m1, m2);
        assertTrue(RecommendationSystem.generateRecommendations(List.of(user), movies).get(user).isEmpty());
    }

    @Test
    void tc6_validRecommendationsExist() {
        Movie m1 = new Movie("Adventure 1", "M1", List.of("Adventure"));
        Movie m2 = new Movie("Adventure 2", "M2", List.of("Adventure"));
        Movie m3 = new Movie("Comedy 1", "M3", List.of("Comedy"));
        User user = new User("Frank", "U6", List.of("M1"));
        List<Movie> movies = List.of(m1, m2, m3);
        List<String> recs = RecommendationSystem.generateRecommendations(List.of(user), movies).get(user);
        assertEquals(1, recs.size());
        assertTrue(recs.contains("Adventure 2"));
    }

}
