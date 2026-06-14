package it.unisa.player.engine;

import org.junit.jupiter.api.BeforeAll; // AGGIUNTA
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.unisa.player.model.Track;
import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;

import javafx.application.Platform; // AGGIUNTA

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaybackEngineTest {

    private PlaybackEngine engine;
    private List<Track> testTracks;
    private Playlist mockPlaylist;
    private it.unisa.player.model.Library mockLibrary;


    // Inizializzazione Toolkit JavaFX per Timeline.
    // Necessario per far funzionare il test di playFromLibrary (Task 15.2).
    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Il toolkit è già avviato (es. esecuzioni multiple), ignoriamo
        }
    }

    @BeforeEach
    public void setUp() {
        engine = PlaybackEngine.getInstance();
        engine.setState(new StoppedState(engine)); 

        engine.getQueue().clear();
        engine.setGlobalLibrary(null);
        
        testTracks = new ArrayList<>();
        Track t1 = new Track("Brano 1", "Artista 1", 120, "Pop", 2021);
        Track t2 = new Track("Brano 2", "Artista 2", 150, "Rock", 2020);
        
        testTracks.add(t1);
        testTracks.add(t2);

        // Setup della playlist fittizia per i test
        mockPlaylist = new Playlist("Playlist Test");
        mockPlaylist.addTrack(t1);
        mockPlaylist.addTrack(t2);

        mockLibrary = new it.unisa.player.model.Library();
        mockLibrary.addTrack(t1);
        mockLibrary.addTrack(t2);
    }


    // Test: Verifica del metodo playFromLibrary.
    // Controlla che invocando il metodo la queue si popoli e 
    // che la prima traccia diventi il currentTrack.
    @Test
    public void testPlayFromLibrary() {

        engine.playFromLibrary(mockLibrary, 0);
        // Verifica 1: La coda è stata popolata
        assertEquals(2, engine.getQueue().size(), "La coda deve contenere esattamente 2 brani.");
        
        // Verifica 2: Il currentTrack è stato impostato sul brano richiesto (indice 0)
        assertNotNull(engine.getCurrentTrack(), "La traccia corrente non deve essere nulla.");
        assertEquals("Brano 1", engine.getCurrentTrack().getTitle(), "Il titolo della traccia corrente deve essere 'Brano 1'.");
    }
    @Test
    public void testPlayFromPlaylist() {
        // Facciamo partire dalla SECONDA traccia (indice 1)
        engine.playFromPlaylist(mockPlaylist, 1);
        
        // Verifica 1: Il contesto attuale del motore è diventato la playlist
        assertEquals(mockPlaylist, engine.getCurrentPlaylistContext(), "Il contesto playlist non è stato aggiornato.");
        
        // Verifica 2: La queue è popolata con i brani della playlist
        assertEquals(2, engine.getQueue().size(), "La coda deve contenere esattamente i 2 brani della playlist.");
        
        // Verifica 3: La traccia corrente è partita dall'indice 1 (Brano 2)
        assertEquals("Brano 2", engine.getCurrentTrack().getTitle(), "Il brano in riproduzione dovrebbe essere il secondo.");
    }

    // Test: Verifica delle transizioni dello State Pattern.
    // Simula i comandi dell'utente per testare i cambi di stato.
    @Test
    public void testStateTransitions_PlayPauseStop() {
        //Partiamo da fermi 
        assertTrue(engine.getState() instanceof StoppedState, "Deve partire da StoppedState");
        
        //Da Stopped premiamo Play -> In riproduzione
        engine.pressPlay();
        assertTrue(engine.getState() instanceof PlayingState, "Dopo play() deve essere PlayingState");
        
        //Da Playing premiamo Pause -> In pausa
        engine.pressPause();
        assertTrue(engine.getState() instanceof PausedState, "Dopo pause() deve essere PausedState");
        
        //Da Paused premiamo di nuovo Play -> Riprende 
        engine.pressPlay();
        assertTrue(engine.getState() instanceof PlayingState, "Da pausa, play() riporta a PlayingState");
        
        //Da Playing premiamo Stop -> Torna in stop
        engine.pressStop();
        assertTrue(engine.getState() instanceof StoppedState, "Dopo stop() torna a StoppedState");
    }


    @Test
    public void testPlaylistModificationDuringPlayback() {

        Track t3 = new Track("Brano 3 (Nuovo)", "Artista 3", 120, "Pop", 2020);
        
        // Avvia la playlist originale
        engine.playFromPlaylist(mockPlaylist, 0);
        
        //stiamo suonando Brano 1
        assertEquals("Brano 1", engine.getCurrentTrack().getTitle(), "Il brano corrente dovrebbe essere Brano 1");
        assertEquals(2, engine.getQueue().size(), "La coda visiva dovrebbe avere 2 brani");

        //L'utente aggiunge un nuovo brano alla playlist MENTRE la musica suona
        mockPlaylist.addTrack(t3);

        //La coda si aggiorna a 3, ma la traccia in esecuzione non viene interrotta
        assertEquals(3, engine.getQueue().size(), "La coda visiva dovrebbe essersi aggiornata a 3 brani");
        assertEquals("Brano 1", engine.getCurrentTrack().getTitle(), "L'aggiunta non deve interrompere il brano corrente");

        engine.playNext();
        assertEquals("Brano 2", engine.getCurrentTrack().getTitle(), "Lo skip deve portare a Brano 2");
        
        engine.playNext();
        assertEquals("Brano 3 (Nuovo)", engine.getCurrentTrack().getTitle(), "Lo skip successivo deve trovare il brano appena aggiunto");
    }

    @Test
    public void testSkipToNextPlaylistSuccessfully() {

        Library library = new Library();
        
        Playlist p1 = new Playlist("Playlist Rock");
        p1.addTrack(new Track("Rock 1", "Band", 120, "Rock", 2000));
        
        Playlist p2 = new Playlist("Playlist Pop");
        p2.addTrack(new Track("Pop 1", "Singer", 120, "Pop", 2010));

        library.addPlaylist(p1);
        library.addPlaylist(p2);
        
        // Inietta la libreria nel motore
        engine.setGlobalLibrary(library);

        // Avvia la prima playlist
        engine.playFromPlaylist(p1, 0);

        //L'utente richiede lo skip dell'intera playlist
        engine.skipToNextPlaylist();

        //Il motore passa al primo brano della seconda playlist
        assertEquals("Pop 1", engine.getCurrentTrack().getTitle(), "Dovrebbe essere passato al primo brano della Playlist Pop");
        assertEquals(p2, engine.getCurrentPlaylistContext(), "Il contesto corrente dovrebbe essere la Playlist Pop");
        assertTrue(engine.getState() instanceof PlayingState, "Il motore dovrebbe essere in riproduzione");
    }

    @Test
    public void testSkipPlaylistAtTheEndStopsEngine() {
        Library library = new Library();
        Playlist p1 = new Playlist("Playlist Unica");
        p1.addTrack(new Track("Brano Solo", "Autore", 120, "Pop", 2000));
        
        library.addPlaylist(p1);
        engine.setGlobalLibrary(library);
        
        engine.playFromPlaylist(p1, 0);

        //L'utente richiede lo skip ma è già all'ultima playlist
        engine.skipToNextPlaylist();

        // Verifica: Il sistema arresta la riproduzione e azzera la UI
        assertTrue(engine.getState() instanceof StoppedState, "Il motore dovrebbe fermarsi forzatamente");
        assertNull(engine.getCurrentTrack(), "Il brano corrente dovrebbe essere nullo");
        assertTrue(engine.getQueue().isEmpty(), "La coda visiva dovrebbe essere stata svuotata");
        assertNull(engine.getCurrentPlaylistContext(), "Il contesto della playlist dovrebbe essere stato resettato");
    }

}