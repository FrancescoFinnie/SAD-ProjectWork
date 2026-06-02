package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PlaylistController {

    // Cambiato da ListView a TableView e TableColumn
    @FXML private TableView<Playlist> playlistTable;
    @FXML private TableColumn<Playlist, String> nameColumn;
    
    // Manteniamo deleteColumn qui per non far arrabbiare l'FXML, ma lo lasciamo inattivo
    @FXML private TableColumn<Playlist, Void> deleteColumn;

    private Library library;
    private Stage primaryStage;

    @FXML
    public void initialize() {
        // Collega la colonna "Nome" all'attributo "name" della classe Playlist
        if (nameColumn != null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }
    }

    public void setDependencies(Library library, Stage primaryStage) {
        this.library = library;
        this.primaryStage = primaryStage;
        
        // Popola la tabella con le playlist
        if (playlistTable != null && library != null) {
            playlistTable.setItems(library.getPlaylists());
        }
    }

    @FXML
    public void onBackToLibraryClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/LibraryView.fxml"));
            Parent root = loader.load();
            
            LibraryController targetController = loader.getController();
            if (targetController != null) {
                targetController.setDependencies(this.library, this.primaryStage);
            }
            
            if (primaryStage != null && primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel ritorno alla libreria principale.");
        }
    }

    // Metodi vuoti per le prossime us
    @FXML
    public void onAddPlaylistClick() {
        System.out.println("Aggiunta playlist non ancora implementata nello Sprint attuale.");
    }

    @FXML
    public void onUndoClick() {
        System.out.println("Undo non ancora implementato.");
    }
}