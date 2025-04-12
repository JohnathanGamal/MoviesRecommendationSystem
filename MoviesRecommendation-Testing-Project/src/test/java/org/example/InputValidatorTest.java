package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {

    InputValidator validator = new InputValidator();

    // Movie title tests
    @Test
    public void testValidSingleWordTitle() {
        assertTrue(validator.isValidMovieTitle("Matrix"));
    }

    @Test
    public void testValidMultiWordTitle() {
        assertTrue(validator.isValidMovieTitle("The Matrix Reloaded"));
    }

    @Test
    public void testTitleWithLowercaseStart() {
        assertFalse(validator.isValidMovieTitle("matrix"));
    }

    @Test
    public void testTitleStartsWithNumber() {
        assertFalse(validator.isValidMovieTitle("2invalid"));
    }

    @Test
    public void testTitleWithSpecialSymbols() {
        assertFalse(validator.isValidMovieTitle("Inv@lid_"));
    }

    @Test
    public void testTitleWithNumbers() {
        assertFalse(validator.isValidMovieTitle("Matrix2"));
    }

    @Test
    public void testEmptyTitle() {
        assertFalse(validator.isValidMovieTitle(""));
    }

    // User name tests
    @Test
    public void testValidFullName() {
        assertTrue(validator.isValidUserName("Issac Amin"));
    }

    @Test
    public void testValidSingleName() {
        assertTrue(validator.isValidUserName("Poula"));
    }

    @Test
    public void testNameWithNumbers() {
        assertFalse(validator.isValidUserName("Issac123"));
    }

    @Test
    public void testNameWithSpecialCharacter() {
        assertFalse(validator.isValidUserName("Andrew!"));
    }

    @Test
    public void testNameWithTrailingSpace() {
        assertFalse(validator.isValidUserName("Poula "));
    }

    @Test
    public void testNameWithLeadingSpace() {
        assertFalse(validator.isValidUserName(" Andrew"));
    }

    @Test
    public void testFullNameWithDoubleSpace() {
        assertFalse(validator.isValidUserName("Poula  Hakem"));
    }

    // User ID tests
    @Test
    public void testValidUserId() {
        assertTrue(validator.isValidUserId("12345678A", new HashSet<>()));
    }

    @Test
    public void testValidAlphaNumericUserId() {
        assertTrue(validator.isValidUserId("123A5678B", new HashSet<>()));
    }

    @Test
    public void testShortUserId() {
        assertFalse(validator.isValidUserId("12345A", new HashSet<>()));
    }

    @Test
    public void testDuplicateUserId() {
        Set<String> ids = new HashSet<>();
        ids.add("12345678A");
        assertFalse(validator.isValidUserId("12345678A", ids));
    }

    @Test
    public void testUserIdWithInvalidSuffix() {
        assertFalse(validator.isValidUserId("12345678@", new HashSet<>()));
    }

    @Test
    public void testUserIdWith9Digits() {
        assertTrue(validator.isValidUserId("123456789", new HashSet<>()));
    }

    @Test
    public void testUserIdStartingWithLetter() {
        assertFalse(validator.isValidUserId("a12345678", new HashSet<>()));
    }

    @Test
    public void testUserIdWithMultiCharacterSuffix() {
        assertFalse(validator.isValidUserId("1234567aa", new HashSet<>()));
    }

    // Movie ID prefix tests
    @Test
    public void testValidMovieIdPrefixMatch() {
        assertTrue(validator.isMovieIdLettersValid("Matrix", "M123"));
    }

    @Test
    public void testValidMovieIdWithNumberPrefix() {
        assertTrue(validator.isMovieIdLettersValid("Fast And Furious 7", "FAF123"));
    }

    @Test
    public void testInvalidMovieIdPrefix() {
        assertFalse(validator.isMovieIdLettersValid("Matrix", "Z123"));
    }

    // Movie ID suffix tests
    @Test
    public void testValidSuffixAndUnique() {
        assertTrue(validator.isMovieIdSuffixValid("MTX123", new HashSet<>()));
    }

    @Test
    public void testDuplicateMovieId() {
        Set<String> ids = new HashSet<>();
        ids.add("MTX123");
        assertFalse(validator.isMovieIdSuffixValid("MTX123", ids));
    }

    @Test
    public void testExtraDigitsInMovieId() {
        assertFalse(validator.isMovieIdSuffixValid("MTX1234", new HashSet<>()));
    }

    @Test
    public void testDuplicateNumbersInMovieId() {
        Set<String> ids = new HashSet<>();
        ids.add("MR123");
        assertFalse(validator.isMovieIdSuffixValid("MTX123", ids));
    }

    @Test
    public void testNonNumericMovieIdSuffix() {
        assertFalse(validator.isMovieIdSuffixValid("MTXABC", new HashSet<>()));
    }

    @Test
    public void testSpecialCharInMovieIdSuffix() {
        assertFalse(validator.isMovieIdSuffixValid("MTX12@", new HashSet<>()));
    }
}