package it.unisa.player.strategy;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;

/**
 * Strategia concreta per filtrare i brani in base a un tag visivo (Task 28.2).
 */
public class TagFilterStrategy implements PlaylistCreationStrategy {

    private final String targetTag;

    public TagFilterStrategy(String targetTag) {
        this.targetTag = targetTag;
    }

    @Override
    public Playlist createPlaylist(Library library) {
        Playlist generatedPlaylist = new Playlist("Tag: " + targetTag);
        
        for (Track track : library.getAllTracks()) {
            if (track.hasTag(this.targetTag)) {
                generatedPlaylist.addTrack(track);
            }
        }
        
        return generatedPlaylist;
    }
}