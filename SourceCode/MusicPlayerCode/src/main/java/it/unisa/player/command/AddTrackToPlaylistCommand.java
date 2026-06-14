package it.unisa.player.command;

import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;

/**
 * Comando concreto per gestire l'aggiunta di una traccia a una specifica Playlist.
 */
public class AddTrackToPlaylistCommand implements Command {
    private final Playlist receiver;
    private final Track track;
    private int index; // Memorizza dove la traccia è stata inserita

    public AddTrackToPlaylistCommand(Playlist receiver, Track track) {
        this.receiver = receiver;
        this.track = track;
        this.index = -1;
    }

    @Override 
    public void execute() { 
        // Eseguiamo l'aggiunta della traccia alla playlist
        receiver.addTrack(track); 
        
        // Salviamo l'indice in cui è stata posizionata 
        this.index = receiver.getTracks().indexOf(track);
    }

    @Override 
    public void undo() { 
        // L'operazione inversa dell'aggiunta è la rimozione pulita della traccia appena inserita 
        receiver.removeTrack(track); 
    }
}