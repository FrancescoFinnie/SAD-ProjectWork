package it.unisa.player.engine;

import it.unisa.player.model.Track;
import java.util.List;

public class SequentialIterator implements Iterator {
    private final List<Track> tracks;
    private int cursor;

    public SequentialIterator(List<Track> tracks, int startIndex) {
        this.tracks = tracks;
        // Se l'indice di partenza non è valido, parte da 0
        this.cursor = (startIndex >= 0 && startIndex < tracks.size()) ? startIndex : 0;
    }

    @Override
    public boolean hasNext() {
        return tracks != null && cursor < tracks.size();
    }

    @Override
    public Track next() {
        if (!hasNext()) {
            return null;
        }
        return tracks.get(cursor++);
    }
}