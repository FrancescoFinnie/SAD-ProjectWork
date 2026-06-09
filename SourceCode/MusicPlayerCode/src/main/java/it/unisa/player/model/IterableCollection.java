package it.unisa.player.model;

import it.unisa.player.engine.Iterator;

public interface IterableCollection {
    Iterator createSequentialIterator(int startIndex);
    Iterator createShuffleIterator(int startIndex);
}
