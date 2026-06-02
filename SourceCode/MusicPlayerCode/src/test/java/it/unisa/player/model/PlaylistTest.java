package it.unisa.player.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlaylistTest {
    private Playlist playlist;
    private Library library;

    @BeforeEach
    public void setUp() {
        playlist = new Playlist("La Mia Playlist");
        library = new Library();
    }

    @Test
    public void testPlaylistCreation() {
        assertEquals("La Mia Playlist", playlist.getName());
        assertNotNull(playlist.getTracks());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    public void testMockPlaylistRetrieval() {
        //Testiamo il recupero della playlist fittizia creata
        library.loadSampleData(); // Questo metodo aggiungerà una playlist finta
        
        assertFalse(library.getPlaylists().isEmpty(), "La lista delle playlist non deve essere vuota dopo il caricamento dati!");
        
        // Verifichiamo che la playlist finta inserita da Library sia stata letta correttamente
        Playlist mockPlaylist = library.getPlaylists().get(0);
        assertNotNull(mockPlaylist.getName(), "La playlist mock deve avere un nome");
    }
}