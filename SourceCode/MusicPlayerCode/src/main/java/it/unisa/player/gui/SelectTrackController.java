package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * 
 * Controller responsabile della finestra modale (Popup) per la selezione
 * di un brano dalla libreria globale e il suo inserimento in una playlist specifica.
 * Gestisce la logica visiva del file SelectTrack.fxml.
 */
public class SelectTrackController {

    @FXML private TableView<Track> globalTracksTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private Label errorLabel;

    private Library library;
    private Playlist destinationPlaylist;
    private Stage dialogStage;

    /**
     * Metodo di inizializzazione nativo di JavaFX.
     * Associa le colonne della TableView alle proprietà (getter) della classe Track.
     */
    @FXML
    public void initialize() {
        if (titleColumn != null) titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (authorColumn != null) authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
    }

    /**
     * Iniezione delle dipendenze.
     * Riceve i dati dal controller chiamante per poter operare correttamente.
     * * @param library Il database centrale da cui leggere tutte le tracce disponibili.
     * @param destinationPlaylist La playlist in cui l'utente vuole inserire il brano.
     * @param dialogStage L'oggetto Stage (finestra) corrente, necessario per poterlo chiudere.
     */
    public void setDependencies(Library library, Playlist destinationPlaylist, Stage dialogStage) {
        this.library = library;
        this.destinationPlaylist = destinationPlaylist;
        this.dialogStage = dialogStage;
        
        // Popoliamo la tabella attingendo direttamente dalla libreria globale.
        // Utilizziamo getAllTracks() come definito nel Model della Library.
        if (this.library != null && this.globalTracksTable != null) {
            this.globalTracksTable.setItems(this.library.getAllTracks());
        }
    }

    /**
     * Gestisce l'evento di click sul pulsante "Aggiungi".
     * Estrae la traccia selezionata e invoca la logica di business anti-duplicati del Model.
     */
    @FXML
    public void onAddClick() {
        // 1. Recupero della traccia evidenziata dall'utente nella tabella
        Track selectedTrack = globalTracksTable.getSelectionModel().getSelectedItem();

        // 2. Validazione dell'input: l'utente ha effettivamente cliccato su una riga?
        if (selectedTrack == null) {
            showError("Attenzione: Seleziona un brano dalla lista prima di procedere.");
            return;
        }

        // 3. Invocazione del Model: deleghiamo alla Playlist il controllo logico e l'inserimento
        boolean success = destinationPlaylist.addTrack(selectedTrack);

        // 4. Gestione dell'esito
        if (success) {
            // L'inserimento ha avuto successo. Grazie al pattern ObservableList, 
            // la tabella della playlist sottostante si aggiornerà in tempo reale.
            // Chiudiamo semplicemente il popup modale.
            dialogStage.close(); 
        } else {
            // L'inserimento è fallito (il Model ha rilevato una violazione della regola sui duplicati)
            showError("Impossibile procedere: Il brano è già presente in questa playlist.");
        }
    }

    /**
     * Gestisce l'evento di click sul pulsante "Annulla", chiudendo il popup senza salvare nulla.
     */
    @FXML
    public void onCancelClick() {
        dialogStage.close();
    }

    /**
     * Metodo di supporto per mostrare dinamicamente messaggi di errore nella UI.
     * @param message Il testo dell'errore da mostrare.
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}