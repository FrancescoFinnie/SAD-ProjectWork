package it.unisa.player.engine;

import org.junit.jupiter.api.BeforeAll; // AGGIUNTA
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.unisa.player.model.Track;

import javafx.application.Platform; // AGGIUNTA

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaybackEngineTest {

    private PlaybackEngine engine;
    private List<Track> testTracks;


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
        
        testTracks = new ArrayList<>();
        Track t1 = new Track("Brano 1", "Artista 1", 120, "Pop", 2021);
        Track t2 = new Track("Brano 2", "Artista 2", 150, "Rock", 2020);
        
        testTracks.add(t1);
        testTracks.add(t2);

    }


    // Test: Verifica del metodo playFromLibrary.
    // Controlla che invocando il metodo la queue si popoli e 
    // che la prima traccia diventi il currentTrack.
    @Test
    public void testPlayFromLibrary() {
        
        // Verifica 1: La coda è stata popolata
        assertEquals(2, engine.getQueue().size(), "La coda deve contenere esattamente 2 brani.");
        
        // Verifica 2: Il currentTrack è stato impostato sul brano richiesto (indice 0)
        assertNotNull(engine.getCurrentTrack(), "La traccia corrente non deve essere nulla.");
        assertEquals("Brano 1", engine.getCurrentTrack().getTitle(), "Il titolo della traccia corrente deve essere 'Brano 1'.");
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

}