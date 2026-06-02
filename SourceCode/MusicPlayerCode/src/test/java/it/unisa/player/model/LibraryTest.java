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

    @Test
    public void testRemovePlaylist() {
        // Creiamo e aggiungiamo una playlist
        Playlist p = new Playlist("Playlist da cancellare");
        library.addPlaylist(p);
        
        // Assicuriamoci che sia stata aggiunta
        assertEquals(1, library.getPlaylists().size());
        
        // Eliminiamola passando direttamente l'oggetto
        library.removePlaylist(p);
        
        // Verifichiamo che la memoria sia tornata vuota
        assertEquals(0, library.getPlaylists().size());
        assertFalse(library.getPlaylists().contains(p));
    }

}