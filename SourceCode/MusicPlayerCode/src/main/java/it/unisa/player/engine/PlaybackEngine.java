package it.unisa.player.engine;

import it.unisa.player.model.Library;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import it.unisa.player.model.Track;
import it.unisa.player.model.IterableCollection;

public class PlaybackEngine {
    private static PlaybackEngine instance;


    // Setup State Context e Iterator.
    // Dichiarazione delle properties e del riferimento astratto Iterator per il pattern.

    // Field obbligatori richiesti dai checkpoint
    private final ObservableList<Track> queue;
    private final ObjectProperty<Track> currentTrack;
    private final ObjectProperty<PlayerState> stateProperty;
    
    // Riferimento astratto al pattern Iterator
    private Iterator iterator;

        // Proprietà per far avanzare la barra nella GUI
    private final DoubleProperty progressProperty;
    
    //Riferimento astratto alla collezione corrente (Libreria o Playlist)
    private IterableCollection currentCollection;

    private PlaybackEngine() {
        this.queue = FXCollections.observableArrayList();
        this.currentTrack = new SimpleObjectProperty<>();
        this.stateProperty = new SimpleObjectProperty<>(new StoppedState(this));
        this.progressProperty = new SimpleDoubleProperty(0.0);
        
        // Ascoltatore automatico. Ogni volta che la canzone cambia, 
        // azzeriamo il timer per evitare che riparta dal tempo della canzone precedente.
        this.currentTrack.addListener((observable, oldTrack, newTrack) -> {
            resetTimer();
        });
    }

    public static synchronized PlaybackEngine getInstance() {
        if (instance == null) {
            instance = new PlaybackEngine();
        }
        return instance;
    }

    // Implementazione deleghe State Pattern.
    // I metodi setState, pressPlay, pressPause e pressStop delegano l'esecuzione allo stato concreto attualmente attivo.
    
    public void setState(PlayerState newState) { 
        this.stateProperty.set(newState); 
    }
    
    public PlayerState getState() { return this.stateProperty.get(); }
    public ObjectProperty<PlayerState> stateProperty() { return stateProperty; }

    public void pressPlay() { this.getState().play(this); }
    public void pressPause() { this.getState().pause(this); }
    public void pressStop() { this.getState().stop(this); }


    // Avvio riproduzione da libreria.
    // Svuota/popola la queue, crea un SequentialIterator a partire dall'indice fornito e invoca la riproduzione.
    public void playFromLibrary(Library library, int startIndex) {
        //this.currentPlaylistContext = null; 
        this.currentCollection = library; // Salva la collezione corrente

        this.queue.clear();
        if (library != null) {
            this.queue.addAll(library.getAllTracks());
        }

        if (library != null && !library.getAllTracks().isEmpty()) {
            //Chiede alla libreria di fabbricare l'iteratore
            this.iterator = currentCollection.createSequentialIterator(startIndex);
            
            if (this.iterator.hasNext()) {
                this.currentTrack.set(this.iterator.next());
                this.setState(new StoppedState(this));
                this.pressPlay();
            }
        }
    }

    public void playNext() {
        if (iterator != null && iterator.hasNext()) {
            this.currentTrack.set(iterator.next());
            this.setState(new PlayingState(this)); 
            
            System.out.println("Skip completato. Brano corrente: " + getCurrentTrack().getTitle());
        } else {
            System.out.println("Fine della coda raggiunta.");
        }
    }

    public void skipToNextPlaylist() {
        System.out.println("Playlist terminata! Tentativo di passare alla playlist successiva...");
        this.pressStop();
    }

    // Getters per JavaFX UI
    public ObservableList<Track> getQueue() { return queue; }
    public ObjectProperty<Track> currentTrackProperty() { return currentTrack; }
    public Track getCurrentTrack() { return currentTrack.get(); }

    private final IntegerProperty currentTimeProperty = new SimpleIntegerProperty(0);

    public void resetTimer() {
        this.currentTimeProperty.set(0);
        this.progressProperty.set(0.0);
    }

}
