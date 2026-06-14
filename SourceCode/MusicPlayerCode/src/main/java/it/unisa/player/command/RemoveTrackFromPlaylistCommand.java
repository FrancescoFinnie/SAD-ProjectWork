package it.unisa.player.command;

import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;

/**
 * Comando concreto per gestire la rimozione di una traccia da una specifica Playlist.
 * Supporta l'operazione di Undo ripristinando la traccia nell'esatta posizione originaria.
 */
public class RemoveTrackFromPlaylistCommand implements Command {
    private final Playlist receiver; // Il destinatario dell'azione (la playlist)
    private final Track track;       // La traccia da rimuovere
    private int index;               // Variabile di stato interna per memorizzare la posizione originaria

    public RemoveTrackFromPlaylistCommand(Playlist receiver, Track track) {
        this.receiver = receiver;
        this.track = track;
        this.index = -1; // Traccia non trovata 
    }

    @Override 
    public void execute() { 
        // Prima di rimuovere, cerchiamo l'indice della traccia nella lista attuale della playlist.
        this.index = receiver.getTracks().indexOf(track); 
        
        // Eseguiamo la reale rimozione dal modello
        receiver.removeTrack(track); 
    }

    @Override 
    public void undo() { 
        // Se la traccia era effettivamente presente, la reinseriamo esattamente nella stessa posizione (indice) in cui si trovava prima.
        if (this.index != -1) {
            receiver.getTracks().add(this.index, track); 
        }
    }
}