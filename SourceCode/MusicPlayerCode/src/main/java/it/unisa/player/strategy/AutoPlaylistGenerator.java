package it.unisa.player.strategy;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;

/**
 * Context dello Strategy Pattern.
 * Delega la generazione della playlist alla strategia configurata a runtime.
 */
public class AutoPlaylistGenerator {

    private PlaylistCreationStrategy strategy;

    public void setStrategy(PlaylistCreationStrategy strategy) {
        this.strategy = strategy;
    }

    public Playlist generate(Library library) throws IllegalStateException {
        if (this.strategy == null) {
            throw new IllegalStateException("Impossibile generare la playlist: nessuna strategia impostata.");
        }
        return this.strategy.createPlaylist(library);
    }
}