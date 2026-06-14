package it.unisa.player.command;

import it.unisa.player.model.Library;
import it.unisa.player.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryUndoCommandTest {

    private Library library;
    private CommandManager commandManager;
    private Track track1;
    private Track track2;
    private Track track3;

    @BeforeEach
    public void setUp() {
        library = new Library();
        commandManager = new CommandManager();

        track1 = new Track("Canzone A", "Autore A", 180, "Pop", 2020);
        track2 = new Track("Canzone B", "Autore B", 210, "Rock", 2021); // Traccia centrale
        track3 = new Track("Canzone C", "Autore C", 150, "Jazz", 2022);

        // Popoliamo lo stato iniziale della libreria
        library.addTrack(track1);
        library.addTrack(track2);
        library.addTrack(track3);
    }

    /**
     * Verifica il Task 24.3: L'Undo della rimozione deve preservare l'indice locale.
     */
    @Test
    public void testUndoRemoveTrackPreservesOriginalIndex() {
        // Verifica che prima di iniziare la traccia 2 sia effettivamente in posizione centrale (indice 1)
        assertEquals(1, library.getAllTracks().indexOf(track2));

        // Rimuoviamo la traccia passando dal CommandManager
        Command removeCmd = new RemoveTrackFromLibraryCommand(library, track2);
        commandManager.executeCommand(removeCmd);

        // Verifichiamo che sia stata rimossa correttamente dal modello
        assertFalse(library.getAllTracks().contains(track2));

        commandManager.undo();

        // La traccia dovrebbe essere reinserita esattamente nella stessa posizione (indice 1)
        assertTrue(library.getAllTracks().contains(track2));
        assertEquals(1, library.getAllTracks().indexOf(track2)); 
    }

    /**
     * Verifica l'Undo dell'aggiunta.
     */
    @Test
    public void testUndoAddTrackRemovesIt() {
        // Creiamo la nuova traccia 
        Track newTrack = new Track("Nuova Canzone", "Nuovo Autore", 200, "HipHop", 2025);

        // Aggiungiamo 
        Command addCmd = new AddTrackToLibraryCommand(library, newTrack);
        commandManager.executeCommand(addCmd);

        // Verifichiamo l'avvenuta aggiunta
        assertTrue(library.getAllTracks().contains(newTrack));

        commandManager.undo();

        // Verifica che la traccia sia stata rimossa correttamente
        assertFalse(library.getAllTracks().contains(newTrack));
    }
}