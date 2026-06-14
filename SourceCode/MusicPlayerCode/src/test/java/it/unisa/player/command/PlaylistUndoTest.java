package it.unisa.player.command;

import static org.junit.jupiter.api.Assertions.*;

import it.unisa.player.command.Command;
import it.unisa.player.command.CommandManager;
import it.unisa.player.command.DeletePlaylistCommand;
import it.unisa.player.command.RemoveTrackFromPlaylistCommand;
import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlaylistUndoTest {

    private Library library;
    private CommandManager commandManager;
    private Playlist playlist;
    private Track track1;
    private Track track2;
    private Track track3;

    @BeforeEach
    public void setUp() {
        library = new Library();
        commandManager = new CommandManager();
        
        playlist = new Playlist("Rock Classico");
        track1 = new Track("Bohemian Rhapsody", "Queen", 355, "Rock", 1975);
        track2 = new Track("Stairway to Heaven", "Led Zeppelin", 482, "Rock", 1971);
        track3 = new Track("Back In Black", "AC/DC", 255, "Rock", 1980);
        
        playlist.addTrack(track1);
        playlist.addTrack(track2);
        playlist.addTrack(track3);
        
        library.addPlaylist(playlist);
    }

    @Test
    public void testUndoRimozioneTracciaMantienePosizione() {
        // Verifica situazione iniziale (Indice 1 è Led Zeppelin)
        assertEquals(3, playlist.getTracks().size());
        assertEquals("Stairway to Heaven", playlist.getTracks().get(1).getTitle());

        // Rimuoviamo la traccia centrale (Indice 1) tramite Command
        Command removeCmd = new RemoveTrackFromPlaylistCommand(playlist, track2);
        commandManager.executeCommand(removeCmd);

        // Verifica avvenuta rimozione
        assertEquals(2, playlist.getTracks().size());
        assertNotEquals("Stairway to Heaven", playlist.getTracks().get(1).getTitle());

        commandManager.undo();

        // La traccia deve essere ritornata ESATTAMENTE all'indice 1
        assertEquals(3, playlist.getTracks().size());
        assertEquals("Stairway to Heaven", playlist.getTracks().get(1).getTitle());
    }

    @Test
    public void testUndoEliminazionePlaylistConservaBrani() {
        // Verifica che la playlist contenga i 3 brani
        assertEquals(3, library.getPlaylists().get(0).getTracks().size());

        // Eliminiamo la playlist tramite Command
        Command deleteCmd = new DeletePlaylistCommand(library, playlist);
        commandManager.executeCommand(deleteCmd);

        // Verifica che la libreria sia vuota
        assertTrue(library.getPlaylists().isEmpty());

        commandManager.undo();

        // La playlist è tornata e non ha perso nessun brano interno
        assertEquals(1, library.getPlaylists().size());
        Playlist ripristinata = library.getPlaylists().get(0);
        assertEquals("Rock Classico", ripristinata.getName());
        assertEquals(3, ripristinata.getTracks().size()); // I brani sono salvi!
        assertEquals("Bohemian Rhapsody", ripristinata.getTracks().get(0).getTitle());
    }
}