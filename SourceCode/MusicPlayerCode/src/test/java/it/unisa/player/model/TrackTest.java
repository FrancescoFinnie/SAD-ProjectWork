package it.unisa.player.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrackTest {
    private Track track;

    @BeforeEach
    public void setUp() {
        track = new Track("Titolo Test", "Autore Test", 180, "Pop", 2020);
    }

    @Test
    public void testTrackCreation() {
        assertEquals("Titolo Test", track.getTitle());
        assertEquals(180, track.getDuration());
    }

    @Test
    public void testSetters() {
        track.setTitle("Nuovo Titolo");
        track.setReleaseYear(2023);
        
        assertEquals("Nuovo Titolo", track.getTitle());
        assertEquals(2023, track.getReleaseYear());
    }

    //Verifica del funzionamento di addTrack()
    @Test
    public void testLibraryAddTrack() {
        Library library = new Library();
        library.loadSampleData();
        int sizeIniziale = library.getAllTracks().size();

        library.addTrack(track); // Aggiunge la traccia di test "Titolo Test"

        assertEquals(sizeIniziale + 1, library.getAllTracks().size());
        assertTrue(library.getAllTracks().contains(track));
    }

    //Verifica del funzionamento di removeTrack()
    @Test
    public void testLibraryRemoveTrack() {
        Library library = new Library();
        library.loadSampleData();
        library.addTrack(track); 
        
        int sizePrimaDellaRimozione = library.getAllTracks().size();

        library.removeTrack(track); // Testiamo la rimozione

        assertEquals(sizePrimaDellaRimozione - 1, library.getAllTracks().size());
        assertFalse(library.getAllTracks().contains(track));
    }

}