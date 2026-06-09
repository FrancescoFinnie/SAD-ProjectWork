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

    /** 
     * Verifica il corretto inserimento dei dati e il funzionamento dei getter
     * quando i parametri rispettano tutti i vincoli del dominio (Golden Path).
     */
    @Test
    public void testTrackCreationAndGettersSuccess() {
        Track track = new Track("Stormbringer", "Deep Purple", 240, "Hard Rock", 1974);
        assertEquals("Stormbringer", track.getTitle());
        assertEquals("Deep Purple", track.getAuthor());
        assertEquals(240, track.getDuration());
        assertEquals("Hard Rock", track.getGenre());
        assertEquals(1974, track.getReleaseYear());
    }

    /**
     * Verifica che i setter aggiornino correttamente i campi in assenza di violazioni.
     */
    @Test
    public void testSettersUpdateCorrectly() {
        Track track = new Track("Iniziale", "Iniziale", 120, "Pop", 2000);
        track.setTitle("Modificato");
        track.setDuration(150);
        assertEquals("Modificato", track.getTitle());
        assertEquals(150, track.getDuration());
    }

    /**
     * Verifica che il sistema sollevi IllegalArgumentException in caso di titoli non validi.
     * Copre i casi di puntatore null, stringa vuota e stringa di soli spazi.
     */
    @Test
    public void testInvalidTitleThrowsException() {
        Track track = new Track("Valid Title", "Valid Author", 100, "Jazz", 2010);

        // Caso 1: Stringa vuota
        assertThrows(IllegalArgumentException.class, () -> {
            track.setTitle("");
        }, "L'inserimento di un titolo vuoto deve sollevare IllegalArgumentException");

        // Caso 2: Stringa di soli spazi (verificando l'effetto del .trim())
        assertThrows(IllegalArgumentException.class, () -> {
            track.setTitle("   ");
        }, "L'inserimento di un titolo di soli spazi deve sollevare IllegalArgumentException");

        // Caso 3: Puntatore null
        assertThrows(IllegalArgumentException.class, () -> {
            track.setTitle(null);
        }, "L'inserimento di un titolo null deve sollevare IllegalArgumentException");
    }

    /**
     * Verifica che il sistema impedisca l'assegnamento di durate negative.
     * Testa il valore limite immediatamente sotto lo zero (-1).
     */
    @Test
    public void testNegativeDurationThrowsException() {
        Track track = new Track("Valid Title", "Valid Author", 100, "Jazz", 2010);

        assertThrows(IllegalArgumentException.class, () -> {
            track.setDuration(-1);
        }, "Una durata negativa deve sollevare IllegalArgumentException");
    }

    /**
     * Verifica che l'anno di pubblicazione non possa essere proiettato nel futuro
     * rispetto all'anno corrente di sistema.
     */
    @Test
    public void testFutureReleaseYearThrowsException() {
        Track track = new Track("Valid Title", "Valid Author", 100, "Jazz", 2010);
        int currentYear = java.time.Year.now().getValue();

        assertThrows(IllegalArgumentException.class, () -> {
            track.setReleaseYear(currentYear + 1);
        }, "Un anno nel futuro rispetto al sistema deve sollevare IllegalArgumentException");
    }

}