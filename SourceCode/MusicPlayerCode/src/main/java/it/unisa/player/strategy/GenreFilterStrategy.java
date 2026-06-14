package it.unisa.player.strategy;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;

/**
 * Strategia concreta per filtrare i brani in base al genere musicale.
 */
public class GenreFilterStrategy implements PlaylistCreationStrategy {

    private final String targetGenre;

    public GenreFilterStrategy(String targetGenre) {
        this.targetGenre = targetGenre;
    }

    @Override
    public Playlist createPlaylist(Library library) {
        Playlist generatedPlaylist = new Playlist("Genere: " + targetGenre);
        
        for (Track track : library.getAllTracks()) {
            if (track.getGenre().equalsIgnoreCase(this.targetGenre)) {
                generatedPlaylist.addTrack(track);
            }
        }
        
        return generatedPlaylist;
    }
}