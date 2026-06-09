package it.unisa.player.engine;

import it.unisa.player.model.Track;

public interface Iterator {
    boolean hasNext();
    Track next();
}