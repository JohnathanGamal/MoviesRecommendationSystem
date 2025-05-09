package org.example;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Accept input/output filenames from command-line if provided
        String moviesFile = (args.length > 0) ? args[0] : "movies.txt";
        String usersFile = (args.length > 1) ? args[1] : "users.txt";
        String outputFile = (args.length > 2) ? args[2] : "recommendations.txt";


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
