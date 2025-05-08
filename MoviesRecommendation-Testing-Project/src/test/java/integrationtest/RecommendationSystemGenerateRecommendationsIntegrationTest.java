package org.integration_test;

import org.example.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RecommendationSystemGenerateRecommendationsIntegrationTest {

    @Test
    void recommendsAllUnseenMoviesInLikedGenres() {
        // 2 movies, same genre
        Movie m1 = new Movie("Star Wars", "SW001", List.of("Sci-fi"));
        Movie m2 = new Movie("Interstellar", "IS002", List.of("Sci-fi"));
        User u = new User("Alice", "123456789", List.of("SW001"));

        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(List.of(u), List.of(m1, m2));

        assertEquals(List.of("Interstellar"), recs.get(u));
    }

    @Test
    void doesNotRecommendLikedMoviesOrOutOfGenre() {
        Movie m1 = new Movie("Coco", "CC001", List.of("Animation"));
        Movie m2 = new Movie("Up", "UU002", List.of("Animation"));
        Movie m3 = new Movie("Braveheart", "BR003", List.of("Drama"));
        User u = new User("Bob", "987654321", List.of("CC001", "UU002"));

        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(List.of(u), List.of(m1, m2, m3));
        assertTrue(recs.get(u).isEmpty());
    }

    @Test
    void emptyUserMoviesYieldsNoRecommendations() {
        Movie m = new Movie("Hero", "HE001", List.of("Action"));
        User u = new User("Carl", "000000001", Collections.emptyList());
        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(List.of(u), List.of(m));
        assertTrue(recs.get(u).isEmpty());
    }

    @Test
    void handlesMultipleUsersWithDistinctInterests() {
        Movie sci = new Movie("Gravity", "G0001", List.of("Sci-fi"));
        Movie anim = new Movie("Up", "U0002", List.of("Animation"));
        Movie act = new Movie("Bond", "B0003", List.of("Action"));

        User sciFan = new User("SciFan", "111111111", List.of("G0001"));
        User animFan = new User("AnimFan", "222222222", List.of("U0002"));

        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(
                List.of(sciFan, animFan), List.of(sci, anim, act));

        assertTrue(recs.get(sciFan).isEmpty(), "SciFan should have no unseen genres");
        assertTrue(recs.get(animFan).isEmpty(), "AnimFan should have no unseen genres");
    }

    @Test
    void genreCasingIsInsensitive() {
        Movie m1 = new Movie("Blockbuster", "BB01", List.of("Action"));
        Movie m2 = new Movie("Another Hit", "BB02", List.of("action"));
        User u = new User("Jonathan", "123456783", List.of("BB01"));

        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(List.of(u), List.of(m1, m2));
        assertEquals(List.of("Another Hit"), recs.get(u));
    }

    @Test
    void ignoresUnknownIdsGracefully() {
        // user likes a movie with ID not in list
        Movie m1 = new Movie("Matrix", "MX001", List.of("Sci-fi"));
        User u = new User("Foo", "999999999", List.of("NO_SUCH_ID"));
        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(List.of(u), List.of(m1));
        assertTrue(recs.get(u).isEmpty());
    }

    @Test
    void nullsAndEmptiesCauseNoCrash() {
        Movie m = new Movie("Test", "T000", List.of("T"));
        User user = new User("Empty", "111000000", null);
        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m));
        assertTrue(recs.get(user).isEmpty());
    }

    @Test
    void duplicateLikedIdsGiveNoDuplicatesInOutput() {
        Movie m1 = new Movie("TestMovie", "TM01", List.of("Docu"));
        Movie m2 = new Movie("Another", "TM02", List.of("Docu"));
        User user = new User("Sam", "112233445", List.of("TM01", "TM01"));

        Map<User, List<String>> recs = RecommendationSystem.generateRecommendations(List.of(user), List.of(m1, m2));
        assertEquals(List.of("Another"), recs.get(user));
    }
}