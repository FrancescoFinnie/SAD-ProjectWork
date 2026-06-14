package it.unisa.player.engine;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
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
import javafx.collections.ListChangeListener;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;



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
    private Library globalLibrary;

    public void setGlobalLibrary(Library globalLibrary) {
        this.globalLibrary = globalLibrary;
    }

    private PlaybackEngine() {
        this.queue = FXCollections.observableArrayList();
        this.currentTrack = new SimpleObjectProperty<>();
        this.stateProperty = new SimpleObjectProperty<>(new StoppedState(this));
        this.progressProperty = new SimpleDoubleProperty(0.0);
        
        // Ascoltatore automatico.
        // Ogni volta che la canzone cambia, 
        // azzeriamo il timer per evitare che riparta dal tempo della canzone precedente.
        this.currentTrack.addListener((observable, oldTrack, newTrack) -> {
            resetTimer();
            
            // --- Sincronizzazione Statistiche ---
            if (newTrack != null) {
                newTrack.incrementPlayCount();
                // Se la riproduzione è partita dal contesto di una playlist, incrementiamo anche quella
                if (this.currentPlaylistContext != null) {
                    this.currentPlaylistContext.incrementPlayCount();
                }
            }

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

        // se viene riprodotto un brano, il timer parte
        //altrimenti (pausa), viene fermato
        if (newState instanceof PlayingState) {
            startSimulationTimer();
        } else {
            stopSimulationTimer();
        }
    }
    
    public PlayerState getState() { return this.stateProperty.get(); }
    public ObjectProperty<PlayerState> stateProperty() { return stateProperty; }

    public void pressPlay() { 
        if (this.currentTrack.get() != null) { // Impedisce avvii accidentali se la coda è finita
            this.getState().play(this);
        }
    }    public void pressPause() { this.getState().pause(this); }
    public void pressStop() { this.getState().stop(this); }

    private ListChangeListener<Track> collectionListener;
    private ObservableList<Track> currentObservedList;
    
    private void attachCollectionListener(ObservableList<Track> sourceList) {
        // 1. Rimuove il vecchio listener se si passa a un'altra playlist/libreria
        if (collectionListener != null && currentObservedList!= null) {
            this.queue.removeListener(collectionListener);
        }
        currentObservedList = sourceList;

        collectionListener = change -> {
            while (change.next()) {
                // Sincronizza la coda visiva della UI
                queue.setAll(sourceList);
                
                // Trova il brano che sta suonando in questo istante
                Track playing = currentTrack.get();
                int newIndex = 0;
                
                if (playing != null) {
                    newIndex = sourceList.indexOf(playing);
                    if (newIndex < 0) newIndex = 0; // Se il brano corrente è stato rimosso, riparte da 0
                }
                
                // Ricrea l'iteratore a caldo per non far saltare la musica in esecuzione
                if (currentCollection != null) {
                    Iterator newIter;
                    if (isShuffleActive) {
                        newIter = currentCollection.createShuffleIterator(newIndex);
                    } else {
                        newIter = currentCollection.createSequentialIterator(newIndex);
                    }
                    
                    // Consuma il brano corrente per allineare l'iteratore alla traccia successiva
                    if (newIter.hasNext() && sourceList.contains(playing)) {
                        newIter.next();
                    }
                    
                    setIterator(newIter);
                    System.out.println("Coda aggiornata a runtime. Indice ricalcolato: " + newIndex);
                }
            }
        };

        // 2. Attacca il nuovo listener alla lista sorgente
        if (sourceList != null) {
            sourceList.addListener(collectionListener);
        }
    }

    // Avvio riproduzione da libreria.
    // Svuota/popola la queue, crea un SequentialIterator a partire dall'indice fornito e invoca la riproduzione.
    public void playFromLibrary(Library library, int startIndex) {
        this.currentPlaylistContext = null; 
        this.currentCollection = library; // Salva la collezione corrente

        if (library != null) {
            attachCollectionListener(library.getAllTracks());
            this.queue.setAll(library.getAllTracks());
        } else {
            this.queue.clear();
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

    // Aggiunto riferimento currentPlaylistContext per tracciare la provenienza.
    // Implementato playFromPlaylist per clonare la queue, avviare il SequentialIterator
    // delegando a pressPlay() e tracciare correttamente la playlist in uso.

    private Playlist currentPlaylistContext = null;

    public Playlist getCurrentPlaylistContext() {
        return currentPlaylistContext;
    }

    public void playFromPlaylist(Playlist playlist, int startIndex) {
        this.currentPlaylistContext = playlist;
        this.currentCollection = playlist; // Salva la playlist come collezione corrente

        if (playlist.getTracks() != null) {
            attachCollectionListener(playlist.getTracks());
            this.queue.setAll(playlist.getTracks());
        } else {
            this.queue.clear();
        }

        if (playlist.getTracks() != null && !playlist.getTracks().isEmpty()) {
            //Chiede alla playlist di fabbricare l'iteratore
            this.iterator = currentCollection.createSequentialIterator(startIndex);
            
            if (this.iterator.hasNext()) {
                this.currentTrack.set(this.iterator.next());
                this.setState(new StoppedState(this));
                this.pressPlay();
            }
        }
    }

    public void setIterator(Iterator newIterator) {
        this.iterator = newIterator;
    }

    private boolean isShuffleActive = false;

    public void enableShuffle(boolean enable) {
        // Se non c'è una collezione attiva o la coda è vuota, si ferma
        if (currentCollection == null || queue.isEmpty()) return;
        
        this.isShuffleActive = enable;

        // Trova l'indice del brano attuale guardando la coda della UI
        int currentIndex = queue.indexOf(getCurrentTrack());
        if (currentIndex < 0) currentIndex = 0;
        
        if (enable) {
            //Genera l'iteratore tramite Factory Method della collezione
            Iterator newIter = currentCollection.createShuffleIterator(currentIndex);
            newIter.next(); // Consuma la traccia attuale per allineamento
            setIterator(newIter);
            System.out.println("Modalità Shuffle Attivata.");
        } else {
            //Genera l'iteratore tramite Factory Method della collezione
            Iterator newIter = currentCollection.createSequentialIterator(currentIndex);
            newIter.next(); // Consuma la traccia attuale per allineamento
            setIterator(newIter);
            System.out.println("Modalità Sequenziale Ripristinata.");
        }
    }

    public void playNext() {
        if (iterator != null && iterator.hasNext()) {
            this.currentTrack.set(iterator.next());
            this.setState(new PlayingState(this)); 
            
            System.out.println("Skip completato. Brano corrente: " + getCurrentTrack().getTitle());
        } else {
            System.out.println("Fine della coda raggiunta.");
            if (currentPlaylistContext != null) {
                skipToNextPlaylist();
            } else {
                this.pressStop();
                this.resetTimer();           // Azzera la barra e il testo
                this.currentTrack.set(null); // Sgancia la traccia, impedendo il re-play
            }
        }
    }

    public void skipToNextPlaylist() {
        System.out.println("Salto alla playlist successiva richiesto...");

        if (globalLibrary == null || currentPlaylistContext == null) {
            this.pressStop();
            return;
        }

        //Recupera tutte le playlist e trova l'indice di quella attuale
        ObservableList<Playlist> allPlaylists = globalLibrary.getPlaylists();
        int currentIndex = allPlaylists.indexOf(currentPlaylistContext);

        //Controllo se esiste una playlist successiva nell'elenco
        if (currentIndex >= 0 && currentIndex < allPlaylists.size() - 1) {
            
            // Cambio sequenziale della playlist
            Playlist nextPlaylist = allPlaylists.get(currentIndex + 1);
            System.out.println("Passaggio automatico alla playlist: " + nextPlaylist.getName());
            
            // playFromPlaylist ferma la riproduzione, ricarica la lista, azzera l'indice a 0 e fa pressPlay()
            this.playFromPlaylist(nextPlaylist, 0); 
            
        } else {
            
            // Termina la coda globale
            System.out.println("Ultima playlist terminata. Arresto del motore.");
            this.pressStop();            // Forza la transizione verso lo StoppedState
            this.resetTimer();           // Azzera la progress bar e il timer (currentTimeProperty = 0)
            this.currentTrack.set(null); // Svuota la UI
            this.queue.clear();          // Svuota la coda visiva
            this.currentPlaylistContext = null;
        }
    }

    // Getters per JavaFX UI
    public ObservableList<Track> getQueue() { return queue; }
    public ObjectProperty<Track> currentTrackProperty() { return currentTrack; }
    public Track getCurrentTrack() { return currentTrack.get(); }

    private final IntegerProperty currentTimeProperty = new SimpleIntegerProperty(0);

    // Proprietà che scatta ogni volta che una canzone termina, utilissima per avvisare la GUI di aggiornare le classifiche!
    private final IntegerProperty totalPlaysProperty = new SimpleIntegerProperty(0);
    public IntegerProperty totalPlaysProperty() { return totalPlaysProperty; }

    // --- INIZIO AGGIUNTA: Getters per le JavaFX Properties ---
    public DoubleProperty progressProperty() {
        return progressProperty;
    }

    public IntegerProperty currentTimeProperty() {
        return currentTimeProperty;
    }

    public IntegerProperty totalDurationProperty() {
        return totalDurationProperty;
    }
    // --- FINE AGGIUNTA ---

    public void resetTimer() {
        this.currentTimeProperty.set(0);
        this.progressProperty.set(0.0);
    }


    // Feat(Engine): Timeline e controllo fine traccia.
    // Se il brano termina (secondi >= durata), invoca playNext().

    private Timeline playbackTimer;

    private final IntegerProperty totalDurationProperty = new SimpleIntegerProperty(0);

    public void startSimulationTimer() {
        if (playbackTimer != null) playbackTimer.stop();
        playbackTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            
            Track current = getCurrentTrack();
            if (current == null) {
                stopSimulationTimer();
                return;
            }

            // Incrementa i secondi passati
            currentTimeProperty.set(currentTimeProperty.get() + 1);
            int currentSecs = currentTimeProperty.get();
            
            int trackDuration = current.getDuration();
            if (trackDuration <= 0) trackDuration = 1; 
            
            totalDurationProperty.set(trackDuration);

            // Blindatura ANTI-OVERFLOW (es. impedisce di mostrare 2:51 / 2:50)
            if (currentSecs > trackDuration) currentSecs = trackDuration;

            // Aggiorna la barra
            double percentCompleted = (double) currentSecs / trackDuration;
            progressProperty.set(percentCompleted);
            
            if (currentSecs >= trackDuration) {
                System.out.println("Traccia completata. Incremento statistiche...");
                
                // Incremento effettivo delle statistiche a fine brano ---
                current.incrementPlayCount(); 
                if (currentPlaylistContext != null) currentPlaylistContext.incrementPlayCount();
                
                // Trigger per avvisare i Controller grafici di riordinare la tabella
                totalPlaysProperty.set(totalPlaysProperty.get() + 1); 

                if (isLoopSingleTrackActive) {
                    System.out.println("Loop attivo! Faccio ripartire il brano corrente");
                    resetTimer();
                } else {
                    playNext(); 
                }
            }
        }));
        playbackTimer.setCycleCount(Timeline.INDEFINITE);
        playbackTimer.play();
    }

    public void stopSimulationTimer() {
        if (playbackTimer != null) {
            playbackTimer.stop();
        }
    }

    // flag per la modalità Loop Traccia Singola 
    private boolean isLoopSingleTrackActive = false;

    public void setLoopSingleTrackActive(boolean active) {
        this.isLoopSingleTrackActive = active;
        System.out.println("Modalità Loop Singolo: " + (active ? "ATTIVATA" : "DISATTIVATA"));
    }

    public boolean isLoopSingleTrackActive() {
        return isLoopSingleTrackActive;
    }

}
