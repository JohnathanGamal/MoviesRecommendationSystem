package org.example;

import java.util.List;

public class Movie {
    private String title;
    private String id;
    private List<String> genres;

    public Movie(String title, String id, List<String> genres) {
        this.title = title;
        this.id = id;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public List<String> getGenres() {
        return genres;
    }
}
