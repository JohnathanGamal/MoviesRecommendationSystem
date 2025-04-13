package org.example;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    @Test
    void testValidMovieCreation() {
        Movie movie = new Movie("Inception", "M001", Arrays.asList("Sci-Fi", "Thriller"));
        assertEquals("Inception", movie.getTitle());
        assertEquals("M001", movie.getId());
        assertEquals(Arrays.asList("Sci-Fi", "Thriller"), movie.getGenres());
    }


    @Test
    void testEmptyTitle() {
        Movie movie = new Movie("", "M004", Arrays.asList("Action"));
        assertEquals("", movie.getTitle());
    }

    @Test
    void testEmptyId() {
        Movie movie = new Movie("Batman", "", Arrays.asList("Action"));
        assertEquals("", movie.getId());
    }

    @Test
    void testEmptyGenresList() {
        Movie movie = new Movie("Matrix", "M005", Collections.emptyList());
        assertTrue(movie.getGenres().isEmpty());
    }

    @Test
    void testLongTitle() {
        String longTitle = "A".repeat(1000);
        Movie movie = new Movie(longTitle, "M006", Arrays.asList("Drama"));
        assertEquals(1000, movie.getTitle().length());
    }

    @Test
    void testGetTitle() {
        Movie movie = new Movie("Cars", "M011", Arrays.asList("Kids"));
        assertEquals("Cars", movie.getTitle());
    }

    @Test
    void testGetId() {
        Movie movie = new Movie("Cars", "ID777", Arrays.asList("Kids"));
        assertEquals("ID777", movie.getId());
    }

    @Test
    void testGetGenres() {
        List<String> genres = Arrays.asList("Action", "Thriller");
        Movie movie = new Movie("Bond", "M012", genres);
        assertEquals(genres, movie.getGenres());
    }

    @Test
    void testLargeGenreList() {
        String[] genreArray = new String[1000];
        Arrays.fill(genreArray, "Genre");
        List<String> genres = Arrays.asList(genreArray);
        Movie movie = new Movie("Epic", "M014", genres);
        assertEquals(1000, movie.getGenres().size());
    }

}
