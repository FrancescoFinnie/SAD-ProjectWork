package it.unisa.player.model;

import it.unisa.player.engine.SequentialIterator;
import it.unisa.player.engine.ShuffleIterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import it.unisa.player.engine.Iterator;

public class Playlist implements IterableCollection {
    private String name;
    // List Observable
    private ObservableList<Track> tracks;
    private int playCount = 0; // Traccia le riproduzioni dell'intera playlist

    public Playlist(String name) {
        this.name = name;
        this.tracks = FXCollections.observableArrayList();
    }

    // Getter usati dalla TableView per leggere i dati
    public String getName() { 
        return name; 
    }

    public void setName(String newName) { 
        this.name = newName; 
    }
    
    public ObservableList<Track> getTracks() { 
        return tracks; 
    }

    public boolean addTrack(Track t) {
        if (t == null) return false;

        for (Track existing : tracks) {
            if (existing.getTitle().equalsIgnoreCase(t.getTitle()) &&
                existing.getAuthor().equalsIgnoreCase(t.getAuthor())) {
                return false; // Blocco l'inserimento
            }
        }
        
        tracks.add(t);
        return true;
    }

    /**
     * Rimuove una traccia specifica dalla playlist.
     * @param t La traccia da rimuovere.
     * @return true se la traccia era presente ed è stata rimossa, false altrimenti.
     */
    public boolean removeTrack(Track t) {
        if (t == null) return false;
        
        // La ObservableList rimuove l'oggetto e notifica automaticamente la UI
        return tracks.remove(t);
    }

    @Override
    public Iterator createSequentialIterator(int startIndex) {
        return new SequentialIterator(this.tracks, startIndex);
    }

    @Override
    public Iterator createShuffleIterator(int startIndex) {
        return new ShuffleIterator(this.tracks, startIndex); 
    }

    /**
     * Restituisce il numero di riproduzioni avviate da questa playlist.
     * @return Il contatore attuale.
     */
    public int getPlayCount() {
        return playCount;
    }

    /**
     * Incrementa di uno il contatore delle riproduzioni della playlist.
     */
    public void incrementPlayCount() {
        this.playCount++;
    }


}