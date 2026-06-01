package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Track;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;       
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;     
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Il Controller della libreria. Gestisce sia la schermata principale (tabella dei brani)
 * sia la schermata secondaria del form (inserimento e modifica).
 */
public class LibraryController {

    //Elementi della vista principale 
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, Void> deleteColumn; 

    //Riferimenti allo stato interno e alle dipendenze
    private Library library;
    private Stage primaryStage;

    /**
     * Eseguito automaticamente da JavaFX non appena i file FXML vengono caricati.
     * Si occupa esclusivamente di mappare le proprietà dell'oggetto Track alle colonne di testo.
     */
    @FXML
    public void initialize() {
        if (titleColumn != null && authorColumn != null) {
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        }
    }


    //Riceve le istanze dei motori logici dalla classe App e aggiorna la tabella con i dati correnti della libreria
    public void setDependencies(Library library, Stage primaryStage) {
        this.library = library;
        this.primaryStage = primaryStage;
        refreshTable();

        if (trackTable != null && library != null) {
            trackTable.setItems(library.getAllTracks());
        }
    }

    //Svuota la tabella grafica e la ripopola estraendo la lista aggiornata dal modello Library.
    private void refreshTable() {
        if (trackTable != null && library != null) {
            trackTable.getItems().clear();
            trackTable.getItems().addAll(library.getAllTracks());
        }
    }

 @FXML
    public void onAddTrackClick() {
        System.out.println("da implementare");
    }


    //rimanda a playlist viewer (task 14.3)
    @FXML
    public void onViewPlaylistsClick() {
        System.out.println("da implementare");
    }


    @FXML
    public void onUndoClick() {
        // Lasciamolo con un print per questo Sprint
        System.out.println("Funzione Undo non disponibile in questa versione.");
    }
}