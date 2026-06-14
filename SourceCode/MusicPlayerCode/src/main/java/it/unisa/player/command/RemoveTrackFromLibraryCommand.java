package it.unisa.player.command;

import it.unisa.player.model.Library;
import it.unisa.player.model.Track;

/**
 * Comando concreto per gestire la rimozione di una traccia dalla Libreria generale del sistema.
 */
public class RemoveTrackFromLibraryCommand implements Command {
    private final Library receiver; // Il destinatario è la libreria globale
    private final Track track;
    private int index; // Memorizza la posizione originaria nella libreria per l'Undo posizionale

    public RemoveTrackFromLibraryCommand(Library receiver, Track track) {
        this.receiver = receiver;
        this.track = track;
        this.index = -1;
    }

    @Override 
    public void execute() { 
        // Catturiamo la posizione della traccia nella collezione globale della libreria prima di eliminarla
        this.index = receiver.getAllTracks().indexOf(track); 
        
        // Rimuoviamo la traccia dalla libreria
        receiver.removeTrack(track); 
    }

    @Override 
    public void undo() { 
        // Se l'indice è valido, reinseriamo la traccia al suo posto d'origine nella libreria generale
        if (this.index != -1) {
            receiver.getAllTracks().add(this.index, track); 
        }
    }
}