package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationSystemTest {

    private List<Movie> movieList;
    private List<User> userList;

    @BeforeEach
    void setup() {
        movieList = new ArrayList<>();
        userList = new ArrayList<>();
    }

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
    void testUserLikesMovieWithMultipleGenres() {
        movieList.add(new Movie("Deadpool", "M002", List.of("Action", "Comedy")));
        movieList.add(new Movie("Extraction", "M004", List.of("Action")));
        movieList.add(new Movie("The Hangover", "M005", List.of("Comedy")));
        movieList.add(new Movie("The Godfather", "M006", List.of("Drama")));

        User user = new User("Joseph", "87654321B", List.of("M002"));
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        assertEquals(Set.of("Extraction", "The Hangover"), new HashSet<>(result.get(user)));
    }

    @Test
    void testUserHasNoLikedMovies() {
        movieList.add(new Movie("Inception", "M007", List.of("Action")));
        movieList.add(new Movie("Joker", "M008", List.of("Drama")));

        User user = new User("Bavly", "11223344C", new ArrayList<>());
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        assertTrue(result.get(user).isEmpty());
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

    @Test
    void testCaseInsensitiveGenreMatch() {
        movieList.add(new Movie("Avengers", "M010", List.of("Action")));
        movieList.add(new Movie("John Wick 2", "M011", List.of("action")));
        movieList.add(new Movie("Fast And Furious", "M012", List.of("ACTION")));

        User user = new User("Jonathan", "45678901E", List.of("M010"));
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        assertEquals(Set.of("John Wick 2", "Fast And Furious"), new HashSet<>(result.get(user)));
    }

    @Test
    void testUserLikesAllMoviesInGenre() {
        movieList.add(new Movie("Drama One", "M101", List.of("Drama")));
        movieList.add(new Movie("Drama Two", "M102", List.of("Drama")));

        User user = new User("Issac", "22334455F", List.of("M101", "M102"));
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        assertTrue(result.get(user).isEmpty());
    }
    @Test
    void testMultipleUsersOverlappingGenres() {
        movieList.add(new Movie("Equalizer", "M003", List.of("Action")));
        movieList.add(new Movie("Taken", "M004", List.of("Action")));
        movieList.add(new Movie("Avengers Assemble", "M001", List.of("Action")));
        movieList.add(new Movie("John Wick", "M002", List.of("Action")));

        User jonathan = new User("Jonathan", "13579135G", List.of("M001")); // Likes Avengers Assemble
        User joseph = new User("Joseph", "24682468H", List.of("M002"));    // Likes John Wick

        userList.add(jonathan);
        userList.add(joseph);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);

        List<String> expectedForJonathan = List.of("Equalizer", "Taken", "John Wick");
        List<String> expectedForJoseph = List.of("Equalizer", "Taken", "Avengers Assemble");

        assertEquals(new HashSet<>(expectedForJonathan), new HashSet<>(result.get(jonathan)));
        assertEquals(new HashSet<>(expectedForJoseph), new HashSet<>(result.get(joseph)));
    }

    @Test
    void testUserLikesOnlyMovieInGenre() {
        movieList.add(new Movie("Gravity", "M005", List.of("Sci-fi")));

        User user = new User("Bavly", "30303030I", List.of("M005"));
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        assertTrue(result.get(user).isEmpty());
    }

    @Test
    void testEmptyMovieList() {
        User user = new User("Andrew", "40404040J", List.of("M001", "M002"));
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, new ArrayList<>());
        assertTrue(result.get(user).isEmpty());
    }

    @Test
    void testEmptyUserList() {
        movieList.add(new Movie("Frozen", "M001", List.of("Animation")));
        movieList.add(new Movie("Brave", "M002", List.of("Animation")));

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(new ArrayList<>(), movieList);
        assertTrue(result.isEmpty());
    }
    @Test
    void testDuplicateLikedMovieIds() {
        movieList.add(new Movie("Avatar", "M001", List.of("Sci-fi")));
        movieList.add(new Movie("Interstellar", "M002", List.of("Sci-fi")));
        movieList.add(new Movie("The Martian", "M003", List.of("Sci-fi")));

        User user = new User("Andrew", "50505050K", List.of("M001", "M001")); // duplicate liked ID
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);
        List<String> actualRecommendations = result.get(user);

        List<String> expected = List.of("Interstellar", "The Martian");

        // validate content
        assertTrue(actualRecommendations.containsAll(expected));
        assertEquals(expected.size(), actualRecommendations.size());

        // assert no duplicates
        Set<String> actualSet = new HashSet<>(actualRecommendations);
        assertEquals(actualRecommendations.size(), actualSet.size(), "Recommendations contain duplicates!");
    }



    @Test
    void testManyUsersWithDifferentGenres() {
        movieList.add(new Movie("Saw", "M001", List.of("Horror")));
        movieList.add(new Movie("The Conjuring", "M002", List.of("Horror")));

        movieList.add(new Movie("Creed", "M003", List.of("Sport")));
        movieList.add(new Movie("Rocky", "M004", List.of("Sport")));

        movieList.add(new Movie("Up", "M005", List.of("Animation")));
        movieList.add(new Movie("Toy Story", "M006", List.of("Animation")));

        movieList.add(new Movie("The Matrix", "M007", List.of("Sci-fi")));
        movieList.add(new Movie("Interstellar", "M008", List.of("Sci-fi")));

        movieList.add(new Movie("Pride And Prejudice", "M009", List.of("Drama")));
        movieList.add(new Movie("Little Women", "M010", List.of("Drama")));

        movieList.add(new Movie("Prisoners", "M011", List.of("Thriller")));
        movieList.add(new Movie("Gone Girl", "M012", List.of("Thriller")));

        // Each user likes one movie from a genre
        userList.add(new User("Andrew", "60000000A", List.of("M001"))); // Horror
        userList.add(new User("Issac", "60000001B", List.of("M003")));  // Sport
        userList.add(new User("Joseph", "60000002C", List.of("M005"))); // Animation
        userList.add(new User("Poula", "60000003D", List.of("M007")));  // Sci-fi
        userList.add(new User("Bavly", "60000004E", List.of("M009")));  // Drama
        userList.add(new User("Jonathan", "60000005E", List.of("M011"))); // Thriller

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);

        assertEquals(List.of("The Conjuring"), result.get(userList.get(0))); // Andrew
        assertEquals(List.of("Rocky"), result.get(userList.get(1)));         // Issac
        assertEquals(List.of("Toy Story"), result.get(userList.get(2)));     // Joseph
        assertEquals(List.of("Interstellar"), result.get(userList.get(3)));  // Poula
        assertEquals(List.of("Little Women"), result.get(userList.get(4)));  // Bavly
        assertEquals(List.of("Gone Girl"), result.get(userList.get(5)));     // Jonathan
    }


    @Test
    void testAllMoviesFromSameGenre() {
        movieList.add(new Movie("Movie A", "M001", List.of("Action")));
        movieList.add(new Movie("Movie B", "M002", List.of("Action")));
        movieList.add(new Movie("Movie C", "M003", List.of("Action")));
        movieList.add(new Movie("Movie D", "M004", List.of("Action")));
        movieList.add(new Movie("Movie E", "M005", List.of("Action")));
        movieList.add(new Movie("Movie F", "M006", List.of("Action")));
        movieList.add(new Movie("Movie G", "M007", List.of("Action")));
        movieList.add(new Movie("Movie H", "M008", List.of("Action")));
        movieList.add(new Movie("Movie I", "M009", List.of("Action")));
        movieList.add(new Movie("Movie J", "M010", List.of("Action")));

        User user = new User("Issac", "33333333Z", List.of("M001"));

        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);

        List<String> expected = Arrays.asList("Movie B", "Movie C", "Movie D", "Movie E", "Movie F", "Movie G", "Movie H", "Movie I", "Movie J");
        assertEquals(new HashSet<>(expected), new HashSet<>(result.get(user)));
    }

    @Test
    void testUserLikesMovieFromEachGenre() {
        movieList.add(new Movie("Horror One", "M001", List.of("Horror")));
        movieList.add(new Movie("Horror Two", "M002", List.of("Horror")));
        movieList.add(new Movie("Horror Three", "M003", List.of("Horror")));

        movieList.add(new Movie("Drama One", "M004", List.of("Drama")));
        movieList.add(new Movie("Drama Two", "M005", List.of("Drama")));
        movieList.add(new Movie("Drama Three", "M006", List.of("Drama")));

        movieList.add(new Movie("Action One", "M007", List.of("Action")));
        movieList.add(new Movie("Action Two", "M008", List.of("Action")));
        movieList.add(new Movie("Action Three", "M009", List.of("Action")));

        User user = new User("Jonathan", "99999999X", List.of("M001", "M004", "M007"));
        userList.add(user);

        Map<User, List<String>> result = RecommendationSystem.generateRecommendations(userList, movieList);

        List<String> expected = Arrays.asList(
                "Horror Two", "Horror Three",
                "Drama Two", "Drama Three",
                "Action Two", "Action Three"
        );
        assertEquals(new HashSet<>(expected), new HashSet<>(result.get(user)));
    }
}
