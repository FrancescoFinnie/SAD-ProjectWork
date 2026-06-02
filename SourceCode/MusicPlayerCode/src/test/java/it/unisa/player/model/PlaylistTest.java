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

    @Test
    public void testAddTrackWithoutDuplicates() {
        Playlist p = new Playlist("Playlist Test");
        Track t1 = new Track("Brano 1", "Autore 1", 200, "Pop", 2020);
        Track t2 = new Track("brano 1", "AUTORE 1", 200, "Pop", 2020); // Duplicato logico
        Track t3 = new Track("Brano 2", "Autore 2", 210, "Rock", 2021);

        assertTrue(p.addTrack(t1), "La prima traccia deve essere inserita");
        assertFalse(p.addTrack(t2), "Il sistema deve bloccare il duplicato");
        assertTrue(p.addTrack(t3), "La terza traccia deve essere inserita");
        assertEquals(2, p.getTracks().size(), "La playlist deve contenere esattamente 2 brani");
    }

    @Test
    public void testRemoveTrack() {
        Playlist p = new Playlist("Playlist Relax");
        Track t1 = new Track("Brano 1", "Autore 1", 200, "Pop", 2020);
        Track t2 = new Track("Brano 2", "Autore 2", 210, "Rock", 2021);

        // Setup: aggiungiamo due tracce
        p.addTrack(t1);
        p.addTrack(t2);

        // 1. Verifica rimozione traccia esistente
        assertTrue(p.removeTrack(t1), "La rimozione di una traccia esistente deve restituire true");
        assertEquals(1, p.getTracks().size(), "La playlist deve contenere ora esattamente 1 brano");
        assertFalse(p.getTracks().contains(t1), "La traccia rimossa non deve più essere nella lista");

        // 2. Verifica rimozione di una traccia non presente (o già rimossa)
        assertFalse(p.removeTrack(t1), "La rimozione di una traccia non presente deve restituire false");
    }

    @Test
    public void testSetName() {
        // Setup
        Playlist p = new Playlist("Nome Iniziale");
        assertEquals("Nome Iniziale", p.getName(), "Il nome iniziale deve coincidere");

        // Azione: Modifica del nome
        p.setName("Nuovo Nome Playlist");

        // Verifica
        assertEquals("Nuovo Nome Playlist", p.getName(), "Il nome deve essere aggiornato correttamente dopo la modifica");
    }
}