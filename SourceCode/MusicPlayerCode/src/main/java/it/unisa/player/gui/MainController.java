package it.unisa.player.gui;

import javafx.scene.Node;
import it.unisa.player.engine.PlaybackEngine;
import it.unisa.player.engine.PlayingState;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * Controller globale dell'applicazione.
 * Gestisce il layout strutturale (MainLayout.fxml), agendo da "guscio" persistente.
 * Contiene i riferimenti ai componenti del Media Player globale (regione Bottom) 
 * e gestisce il contenitore dinamico centrale (regione Center).
 */
public class MainController {

    private PlaybackEngine engine;

    // Variabile booleana per tenere traccia dello stato del loop (US22)
    private boolean isLoopActive = false;
    // --- Contenitore Radice ---
    @FXML
    private BorderPane rootPane;

    // --- Componenti UI del Media Player Globale ---
    @FXML
    private Label currentTrackLabel;
    @FXML
    private Label currentAuthorLabel;
    @FXML
    private ProgressBar songProgressBar;
    // Pulsanti di Riproduzione
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    
    // Pulsanti Modalità e Navigazione 
    @FXML
    private Button skipButton;
    @FXML
    private Button shuffleButton;
    @FXML
    private Button loopButton;

/**
     * Metodo di inizializzazione invocato automaticamente da JavaFX al caricamento del file FXML.
     */
    @FXML
    public void initialize() {
        this.engine = PlaybackEngine.getInstance();

        // Ascolta il cambio traccia per aggiornare i testi
        engine.currentTrackProperty().addListener((observable, oldTrack, newTrack) -> {
            if (newTrack != null) {
                currentTrackLabel.setText(newTrack.getTitle());
                currentAuthorLabel.setText(newTrack.getAuthor());
            } else {
                currentTrackLabel.setText("Nessun brano in riproduzione");
                currentAuthorLabel.setText("-");
            }
        });

        // Ascolta il cambio di stato per nascondere/mostrare i pulsanti (Play/Pause Swap)
        engine.stateProperty().addListener((observable, oldState, newState) -> {
            if (newState instanceof PlayingState) {
                // Se è in riproduzione: nascondi il Play, mostra la Pausa
                playButton.setVisible(false);
                playButton.setManaged(false);
                
                pauseButton.setVisible(true);
                pauseButton.setManaged(true);
            } else {
                // Se è in Pausa o Stop: mostra il Play, nascondi la Pausa
                playButton.setVisible(true);
                playButton.setManaged(true);
                
                pauseButton.setVisible(false);
                pauseButton.setManaged(false);
            }
        });
    }

    /**
     * Imposta dinamicamente il contenuto della regione centrale del layout globale.
     * Questo costrutto permette di navigare tra le diverse viste dell'applicazione 
     * (es. Libreria, Dettaglio Playlist) mantenendo intatto il player multimediale inferiore.
     *
     * @param view Il nodo grafico (FXML root) della nuova schermata da visualizzare.
     */
    public void setCenterView(Node view) {
        // Controllo di sicurezza per evitare NullPointerException
        if (rootPane != null && view != null) {
            // Sostituisce a caldo il nodo figlio corrente nella regione CENTER del BorderPane.
            // I vecchi nodi vengono rimossi automaticamente dal Garbage Collector di JavaFX
            // se non ci sono altre reference, evitando memory leak visivi.
            rootPane.setCenter(view);
        } else {
            System.err.println("Errore di Navigazione: rootPane non inizializzato o vista nulla.");
        }
    }

    

    // Delega eventi UI per Play e Pausa.
    @FXML
    private void handlePlayClick() {
        engine.pressPlay();
    }

    @FXML
    private void handlePauseClick() {
        engine.pressPause();
    }

    @FXML
    private void handleSkipClick() {
        engine.playNext();
    }

    // Variabile booleana per tenere traccia dello stato (acceso/spento)
    private boolean isShuffleActive = false;

    /**
     * Gestisce il click sul pulsante Shuffle
     */
    @FXML
    private void handleShuffleClick() {
        //Inverti lo stato 
        isShuffleActive = !isShuffleActive;

        // Mutua Esclusione : Se attiviamo lo Shuffle, disattiviamo il Loop Singolo sia nel motore che nella grafica
        if (isShuffleActive && isLoopActive) {
            isLoopActive = false;
            engine.setLoopSingleTrackActive(false);
            loopButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #000000; -fx-font-size: 16px; -fx-cursor: hand;");
        }
        
        //Delega al motore il cambio di Iterator a caldo
        engine.enableShuffle(isShuffleActive);
        
        //Feedback Visivo: Cambia colore al bottone per far capire all'utente che è attivo
        if (isShuffleActive) {
            // Verde per indicare Shuffle ON
            shuffleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #1db954; -fx-font-size: 16px; -fx-cursor: hand;");
        } else {
            // Nero per indicare Shuffle OFF
            shuffleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #000000; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleLoopClick() {
        // Inverte lo stato attivo/disattivo
        isLoopActive = !isLoopActive;
        
        // mutua esclusione: Se attiviamo il Loop Singolo, disattiviamo lo Shuffle sia nel motore che nella grafica
        if (isLoopActive && isShuffleActive) {
            isShuffleActive = false;
            engine.enableShuffle(false); // Ripristina l'iteratore sequenziale
            shuffleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #000000; -fx-font-size: 16px; -fx-cursor: hand;");
        }
        
        // Delega al motore l'attivazione/disattivazione del flag condizionale
        engine.setLoopSingleTrackActive(isLoopActive);
        
        // Cambia colore al bottone se attivo
        if (isLoopActive) {
            // Verde per indicare Loop ON
            loopButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #1db954; -fx-font-size: 16px; -fx-cursor: hand;");
        } else {
            // Nero per indicare Loop OFF
            loopButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #000000; -fx-font-size: 16px; -fx-cursor: hand;");
        }
    }

    

}