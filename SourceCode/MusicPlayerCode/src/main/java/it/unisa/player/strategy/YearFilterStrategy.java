package it.unisa.player.strategy;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;

/**
 * Strategia concreta per filtrare i brani in base all'anno di pubblicazione (Task 27.3).
 */
public class YearFilterStrategy implements PlaylistCreationStrategy {

    private final int targetYear;

    public YearFilterStrategy(int targetYear) {
        this.targetYear = targetYear;
    }

    @Override
    public Playlist createPlaylist(Library library) {
        Playlist generatedPlaylist = new Playlist("Anno: " + targetYear);
        
        for (Track track : library.getAllTracks()) {
            if (track.getReleaseYear() == this.targetYear) {
                generatedPlaylist.addTrack(track);
            }
        }
        
        return generatedPlaylist;
    }
}