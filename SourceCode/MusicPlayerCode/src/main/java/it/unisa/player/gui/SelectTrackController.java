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
import it.unisa.player.command.Command;
import it.unisa.player.command.CommandManager;
import it.unisa.player.command.AddTrackToPlaylistCommand;

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
    private CommandManager commandManager;

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
    public void setDependencies(Library library, Playlist destinationPlaylist, Stage dialogStage, CommandManager commandManager) {
        this.library = library;
        this.destinationPlaylist = destinationPlaylist;
        this.dialogStage = dialogStage;
        this.commandManager = commandManager;
        
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

/**
     * Gestisce l'evento di click sul pulsante "Aggiungi".
     * Esegue l'inserimento mediante il pattern Command per permettere il rollback (Undo).
     */
    @FXML
    public void onAddClick() {
        // Recupero della traccia evidenziata dall'utente nella tabella
        Track selectedTrack = globalTracksTable.getSelectionModel().getSelectedItem();

        // Validazione dell'input: l'utente ha effettivamente cliccato su una riga?
        if (selectedTrack == null) {
            showError("Attenzione: Seleziona un brano dalla lista prima di procedere.");
            return;
        }

        // Controllo preventivo anti-duplicati 
        if (destinationPlaylist.getTracks().contains(selectedTrack)) {
            showError("Impossibile procedere: Il brano è già presente in questa playlist.");
            return;
        }

        // Invocazione ed esecuzione guidata dal Pattern Command
        if (this.commandManager != null) {
            // Istanziamo ed eseguiamo il comando posizionale 
            Command addCmd = new AddTrackToPlaylistCommand(this.destinationPlaylist, selectedTrack);
            this.commandManager.executeCommand(addCmd);
            System.out.println("Traccia aggiunta alla playlist tramite AddTrackToPlaylistCommand!");
        } else {
            // Fallback di sicurezza se il gestore non è configurato
            destinationPlaylist.addTrack(selectedTrack);
        }

        // Chiusura del popup modale 
        dialogStage.close(); 
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