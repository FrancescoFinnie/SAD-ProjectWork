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

    @Test
    public void testGetTracksReturnsObservableList() {
        // 1. Setup
        Playlist p = new Playlist("Playlist Rock");
        Track t = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975);
            
        // 2. Verifica che la lista esista e sia vuota all'inizio (Sintassi JUnit 5: l'oggetto prima, il messaggio dopo)
        assertNotNull(p.getTracks(), "La lista delle tracce non deve essere null");
        assertTrue(p.getTracks().isEmpty(), "La lista deve essere vuota appena creata");
            
        // 3. Aggiunta traccia (simuliamo quello che farà poi la US8) e verifica
        p.getTracks().add(t);
        assertEquals(1, p.getTracks().size(), "La lista deve contenere esattamente 1 traccia");
        assertEquals(t, p.getTracks().get(0), "La traccia deve essere quella inserita");
    }
}