package org.example;

import java.util.Set;

public interface Validator {
    boolean isValidMovieTitle(String title);
    boolean isMovieIdLettersValid(String title, String id);
    boolean isMovieIdSuffixValid(String id, Set<String> ids);
    boolean isValidUserName(String name);
    boolean isValidUserId(String id, Set<String> existingIds);
}
