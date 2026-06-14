package it.unisa.player.command;

import it.unisa.player.model.Library;
import it.unisa.player.model.Track;

/**
 * Comando concreto per gestire l'aggiunta di una nuova traccia alla Libreria generale.
 */
public class AddTrackToLibraryCommand implements Command {
    private final Library receiver;
    private final Track track;
    private int index;

    public AddTrackToLibraryCommand(Library receiver, Track track) {
        this.receiver = receiver;
        this.track = track;
        this.index = -1;
    }

    @Override 
    public void execute() { 
        // Inseriamo il brano nella libreria globale
        receiver.addTrack(track); 
        
        // Registriamo la posizione finale di inserimento
        this.index = receiver.getAllTracks().indexOf(track);
    }

    @Override 
    public void undo() { 
        // L'operazione inversa rimuove il brano inserito per errore nella libreria
        receiver.removeTrack(track); 
    }
}