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

    @Test
    public void testAddPlaylistWithoutDuplicates() {
        Playlist p1 = new Playlist("Allenamento");
        Playlist p2 = new Playlist("allenamento"); // Stesso nome, case diverso

        // La prima deve essere aggiunta con successo
        assertTrue(library.addPlaylist(p1));
        
        // La seconda deve essere bloccata dal sistema anti-duplicato
        assertFalse(library.addPlaylist(p2));
        
        // Verifichiamo che in memoria ci sia una sola playlist
        assertEquals(1, library.getPlaylists().size());
    }

}