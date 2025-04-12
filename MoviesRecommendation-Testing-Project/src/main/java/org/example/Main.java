package org.example;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String moviesFile = "movies.txt";
        String usersFile = "users.txt";
        String outputFile = "recommendations.txt";

        List<String> errors = new ArrayList<>();
        List<Movie> movies = new ArrayList<>();
        List<User> users = new ArrayList<>();
        FileHandler fileHandler = new FileHandler(new InputValidator());
        try {
            movies = fileHandler.readMovies(moviesFile, errors);
            if (errors.isEmpty()) {
                Set<String> movieIds = new HashSet<>();
                for (Movie m : movies) movieIds.add(m.getId());

                users = fileHandler.readUsers(usersFile, movieIds, errors);
            }

            Map<User, List<String>> recommendations = errors.isEmpty()
                    ? RecommendationSystem.generateRecommendations(users, movies)
                    : new HashMap<>();

            fileHandler.writeRecommendations(outputFile, recommendations, errors);

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        }
    }
}
