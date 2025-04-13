package org.example;

import java.io.*;
import java.util.*;

public class FileHandler {

    private final Validator validator;

    public FileHandler(Validator validator) {
        this.validator = validator;
    }

    public List<Movie> readMovies(String filePath, List<String> errorList) throws IOException {
        List<Movie> movies = new ArrayList<>();
        Set<String> ids = new HashSet<>();

        if (!filePath.toLowerCase().endsWith(".txt")) {
            errorList.add("ERROR: Unsupported format");
            return movies;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String titleLine, genreLine;

            while (true) {
                titleLine = reader.readLine();
                if (titleLine == null) break;
                titleLine = titleLine.trim();
                if (titleLine.isEmpty()) continue;

                genreLine = reader.readLine();
                if (genreLine == null) break;
                genreLine = genreLine.trim();
                if (genreLine.isEmpty()) continue;


                String[] titleParts = titleLine.split(",", 2);
                if (titleParts.length < 2) continue;

                String title = titleParts[0].trim();
                String id = titleParts[1].trim();

                if (!validator.isValidMovieTitle(title)) {
                    errorList.add("ERROR: Movie Title " + title + " is wrong");
                    break;
                }

                if (!validator.isMovieIdLettersValid(title, id)) {
                    errorList.add("ERROR: Movie Id letters " + id + " are wrong");
                    break;
                }

                if (!validator.isMovieIdSuffixValid(id, ids)) {
                    errorList.add("ERROR: Movie Id numbers " + id + " aren’t unique");
                    break;
                }

                List<String> genres = Arrays.asList(genreLine.split("\\s*,\\s*"));
                ids.add(id);
                movies.add(new Movie(title, id, genres));
            }
        }

        return movies;
    }

    public List<User> readUsers(String filePath, Set<String> validMovieIds, List<String> errorList) throws IOException {
        List<User> users = new ArrayList<>();
        Set<String> userIds = new HashSet<>();

        if (!filePath.toLowerCase().endsWith(".txt")) {
            errorList.add("ERROR: Unsupported format");
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String infoLine, likesLine;
            while (true) {

                infoLine = reader.readLine();
                if (infoLine == null) break;
                infoLine = infoLine.trim();
                if (infoLine.isEmpty()) continue;

                likesLine = reader.readLine();
                if (likesLine == null) break;
                likesLine = likesLine.trim();
                if (likesLine.isEmpty()) continue;

                String[] parts = infoLine.split(",", 2);
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                String id = parts[1].trim();

                if (!validator.isValidUserName(name)) {
                    errorList.add("ERROR: User Name " + name + " is wrong");
                    break;
                }
                if (!validator.isValidUserId(id, userIds)) {
                    errorList.add("ERROR: User Id " + id + " is wrong");
                    break;
                }

                List<String> likedIds = Arrays.asList(likesLine.split("\\s*,\\s*"));
                for (String movieId : likedIds) {
                    if (!validMovieIds.contains(movieId)) {
                        errorList.add("ERROR: User " + name + " has unknown movie ID " + movieId);
                        break;
                    }
                }

                userIds.add(id);
                users.add(new User(name, id, likedIds));
            }
        }
        return users;
    }

    public void writeRecommendations(String filePath, Map<User, List<String>> recommendations, List<String> errors) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            if (!errors.isEmpty()) {
                writer.write("Error " + errors.get(0));
                writer.newLine();
                return;
            }

            for (Map.Entry<User, List<String>> entry : recommendations.entrySet()) {
                User user = entry.getKey();
                writer.write(user.getName() + "," + user.getId());
                writer.newLine();
                writer.write(String.join(", ", entry.getValue()));
                writer.newLine();
            }
        }
    }
}
