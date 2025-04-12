package org.example;

import java.util.*;

public class RecommendationSystem {

    public static Map<User, List<String>> generateRecommendations(List<User> users, List<Movie> movies) {
        Map<User, List<String>> output = new LinkedHashMap<>();
        Map<String, Movie> idToMovie = new HashMap<>();
        Map<String, List<Movie>> genreToMovies = new HashMap<>();

        for (Movie movie : movies) {
            idToMovie.put(movie.getId(), movie);
            for (String genre : movie.getGenres()) {
                genreToMovies.computeIfAbsent(genre.toLowerCase(), k -> new ArrayList<>()).add(movie);
            }
        }

        for (User user : users) {
            Set<String> recommendedTitles = new LinkedHashSet<>();
            for (String likedId : user.getLikedMovieIds()) {
                Movie likedMovie = idToMovie.get(likedId);
                if (likedMovie == null) continue;
                for (String genre : likedMovie.getGenres()) {
                    for (Movie m : genreToMovies.getOrDefault(genre.toLowerCase(), new ArrayList<>())) {
                        if (!user.getLikedMovieIds().contains(m.getId())) {
                            recommendedTitles.add(m.getTitle());
                        }
                    }
                }
            }
            output.put(user, new ArrayList<>(recommendedTitles));
        }
        return output;
    }
}

