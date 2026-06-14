package it.unisa.player.command;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;

public class DeletePlaylistCommand implements Command {
    private final Library library;
    private final Playlist playlistToDelete;
    private int originalIndex; 

    public DeletePlaylistCommand(Library library, Playlist playlistToDelete) {
        this.library = library;
        this.playlistToDelete = playlistToDelete;
    }

    @Override
    public void execute() {
        // Memorizziamo l'indice originario
        this.originalIndex = library.getPlaylists().indexOf(playlistToDelete);
        if (this.originalIndex != -1) {
            library.removePlaylist(playlistToDelete); 
        }
    }

    @Override
    public void undo() {
        if (this.originalIndex != -1) {
            // Clausola di controllo: se l'indice memorizzato è diventato superiore 
            // alla dimensione attuale della lista, la mettiamo semplicemente in fondo.
            if (this.originalIndex >= library.getPlaylists().size()) {
                library.getPlaylists().add(playlistToDelete);
            } else {
                // Altrimenti la reinseriamo esattamente al suo posto
                library.getPlaylists().add(this.originalIndex, playlistToDelete);
            }
        }
    }
}