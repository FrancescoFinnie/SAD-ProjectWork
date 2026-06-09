package it.unisa.player.engine;

import it.unisa.player.model.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShuffleIterator implements Iterator {
    private final List<Track> shuffledTracks;
    private int cursor;

    public ShuffleIterator(List<Track> tracks, int startIndex) {
        this.shuffledTracks = new ArrayList<>(tracks);
        this.cursor = 0;

        if (!shuffledTracks.isEmpty()) {
            // Salva l'elemento da cui l'utente vuole iniziare lo shuffle
            Track startingTrack = (startIndex >= 0 && startIndex < tracks.size()) ? tracks.get(startIndex) : tracks.get(0);
            
            // Mescola la lista
            Collections.shuffle(shuffledTracks);
            
            // Codice Pulito: Sposta il brano iniziale in cima alla lista mescolata per farlo suonare per primo
            shuffledTracks.remove(startingTrack);
            shuffledTracks.add(0, startingTrack);
        }
    }

    @Override
    public boolean hasNext() {
        return cursor < shuffledTracks.size();
    }

    @Override
    public Track next() {
        if (!hasNext()) {
            return null;
        }
        return shuffledTracks.get(cursor++);
    }
}