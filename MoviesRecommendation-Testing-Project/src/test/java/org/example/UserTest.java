package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testConstructorWithValidUser() {
        User user = new User("Joseph", "12345678A", List.of("M001", "M002"));
        assertEquals("Joseph", user.getName());
        assertEquals("12345678A", user.getId());
        assertEquals(List.of("M001", "M002"), user.getLikedMovieIds());
    }

    @Test
    public void testEmptyName() {
        User user = new User("", "12345678D", List.of("M005"));
        assertEquals("", user.getName());
    }

    @Test
    public void testEmptyId() {
        User user = new User("Jonathan", "", List.of("M006"));
        assertEquals("", user.getId());
    }

    @Test
    public void testEmptyLikedMovieIds() {
        User user = new User("Joseph", "12345678E", List.of());
        assertTrue(user.getLikedMovieIds().isEmpty());
    }

    @Test
    public void testGetName() {
        User user = new User("Bavly", "12345678E", List.of());
        assertEquals("Bavly", user.getName());
    }

    @Test
    public void testGetId() {
        User user = new User("Jonathan", "12345678K", List.of());
        assertEquals("12345678K", user.getId());
    }

    @Test
    public void testGetLikedMovieIds() {
        List<String> liked = List.of("M012", "M013");
        User user = new User("Bavly", "12345678A", liked);
        assertEquals(liked, user.getLikedMovieIds());
    }
}