package org.example;

import java.util.HashSet;
import java.util.List;

public class User {
    private String name;
    private String id;
    private List<String> likedMovieIds;

    public User(String name, String id, List<String> likedMovieIds) {
        this.name = name;
        this.id = id;
        this.likedMovieIds = likedMovieIds;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<String> getLikedMovieIds() {
        return likedMovieIds;
    }
}

