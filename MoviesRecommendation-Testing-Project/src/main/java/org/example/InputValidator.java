package org.example;

import java.util.*;

public class InputValidator implements Validator{

    public boolean isValidMovieTitle(String title) {
        return title.matches("([A-Z][a-zA-Z0-9]*|\\d+)(\\s([A-Z][a-zA-Z0-9]*|\\d+))*");
    }


    public boolean isValidUserName(String name) {
        return name.matches("[A-Za-z]+(\\s[A-Za-z]+)*");
    }

    public boolean isValidUserId(String id, Set<String> existingUserIds) {
        return id.matches("^((\\d{8}[A-Za-z0-9])|(\\d[A-Za-z0-9]{6}\\d[A-Za-z0-9]))$") && id.length() == 9 && !existingUserIds.contains(id);
    }

    public boolean isMovieIdLettersValid(String title, String movieId) {
        String expectedPrefix = title.replaceAll("[^A-Z]", "");
        return movieId.startsWith(expectedPrefix);
    }

    public boolean isMovieIdSuffixValid(String movieId, Set<String> existingIds) {
        if (!movieId.matches("^[A-Za-z]+\\d{3}$")) return false;

        String suffix = movieId.substring(movieId.length() - 3);
        for (String id : existingIds) {
            if (id.endsWith(suffix)) {
                return false;
            }
        }
        return true;
    }

}
