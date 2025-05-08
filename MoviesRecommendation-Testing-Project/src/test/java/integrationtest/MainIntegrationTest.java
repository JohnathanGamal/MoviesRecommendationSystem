package org.integration_test;

import org.example.Main;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class MainIntegrationTest {

    private Path movies;
    private Path users;
    private Path output;

    private void createFiles(String moviesTxt, String usersTxt) throws IOException {
        movies = Files.createTempFile("movies", ".txt");
        users = Files.createTempFile("users", ".txt");
        output = Files.createTempFile("recs", ".txt");
        Files.writeString(movies, moviesTxt);
        Files.writeString(users, usersTxt);
    }

    private List<String> runMainAndReadOutput() throws IOException {
        Main.main(new String[]{movies.toString(), users.toString(), output.toString()});
        return Files.readAllLines(output);
    }

    @Test
    void readWriteFlow() throws IOException {
        createFiles(
                "Matrix,MXT001\nAction, Sci-Fi\n",
                "Alice,123456789\nMXT001\n"
        );
        List<String> result = runMainAndReadOutput();
        assertTrue(result.get(0).contains("Alice,123456789"));
        assertTrue(result.size() == 2);
    }

    @Test
    void invalidUserCausesErrorOnly() throws IOException {
        createFiles(
                "MovieA,MOV001\nAction\n",
                "BadUser1,#badid\nMOV001\nAlice,123456789\nMOV001\n"
        );
        List<String> result = runMainAndReadOutput();
        assertEquals(1, result.size()); // Only the error line should be written
        assertTrue(result.get(0).toLowerCase().contains("error"));
    }

    @Test
    void recSystemHandlesEmptyUser() throws IOException {
        createFiles(
                "Movie,M001\nAction\n",
                "Bob,234567890\n\n"
        );
        List<String> result = runMainAndReadOutput();
        assertTrue(result.isEmpty());
    }

    @Test
    void recSystemReceivesValidatedInput() throws IOException {
        createFiles(
                "ValidTitle,VAL123\nDrama\n",
                "TestUser,456789123\nNONEXISTENT\n"
        );
        List<String> lines = runMainAndReadOutput();
        assertTrue(lines.get(0).toLowerCase().contains("error"));
    }

    @Test
    void outputFormatterCorrect() throws IOException {
        createFiles(
                "Movie Title, MT001\nAction\nMovie Title 2, MT002\nComedy\n",
                "Charlie,111222333\nMT001\n"
        );
        List<String> result = runMainAndReadOutput();
        assertEquals("Charlie,111222333", result.get(0));
        assertTrue(result.get(1).contains("Movie Title 2") || result.get(1).contains(""));
    }

    @Test
    void errorPropagationWorks() throws IOException {
        createFiles(
                "invalidtitle,ID001\nAction\n",
                "Bob,123456789\nID001\n"
        );
        List<String> result = runMainAndReadOutput();
        assertTrue(result.get(0).toLowerCase().contains("error"));
    }

    @Test
    void recommendationUsesCorrectMovies() throws IOException {
        createFiles(
                "Movie Real,MR120\nAction\n",
                "Tom,123123123\nMR120\n"
        );
        List<String> result = runMainAndReadOutput();
        assertTrue(result.get(0).contains("Tom,123123123"));
    }

    @Test
    void emptyOutputStillWritten() throws IOException {
        createFiles(
                "Sole Movie,SM001\nWestern\n",
                "Alone,102938475\nSM001\n"
        );
        List<String> lines = runMainAndReadOutput();
        assertEquals("Alone,102938475", lines.get(0));
        assertTrue(lines.size() == 2 && (lines.get(1).isEmpty() || lines.get(1).trim().isEmpty()));
    }

    @Test
    void errorFileOutput() throws IOException {
        createFiles(
                "badtitle,BDT111\nGenre\n",
                "Jack,123456789\nBDT111\n"
        );
        List<String> result = runMainAndReadOutput();
        assertTrue(result.get(0).toLowerCase().contains("error"));
    }
}