package it.unisa.player.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {
    private Library library;
    private Track track;

    @BeforeEach
    public void setUp() {
        library = new Library();
        track = new Track("Canzone 1", "Autore 1", 200, "Rock", 2010);
    }

}