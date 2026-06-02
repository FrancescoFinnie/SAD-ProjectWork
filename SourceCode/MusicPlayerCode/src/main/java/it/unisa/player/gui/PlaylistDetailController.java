package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PlaylistDetailController {

    @FXML private Label playlistNameLabel;
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;

    private Library library;
    private Playlist currentPlaylist; // Mantiene lo stato: quale playlist stiamo visualizzando
    private Stage primaryStage;

    @FXML
    public void initialize() {
        if (titleColumn != null) titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (authorColumn != null) authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
    }

    /**
     * Dependency Injection: passiamo al controller tutto ciò di cui ha bisogno per operare.
     */
    public void setDependencies(Library library, Playlist playlist, Stage primaryStage) {
        this.library = library;
        this.currentPlaylist = playlist;
        this.primaryStage = primaryStage;

        if (this.currentPlaylist != null) {
            // Aggiorniamo la View
            playlistNameLabel.setText(currentPlaylist.getName());
            
            // PATTERN OBSERVER: colleghiamo la tabella direttamente alla lista in memoria della playlist.
            // Qualsiasi aggiunta o rimozione alla ObservableList aggiornerà automaticamente la UI.
            trackTable.setItems(currentPlaylist.getTracks());
        }
    }

        /**
     * Tasto "←" per tornare all'elenco di tutte le playlist.
     * Distrugge questa vista e ricarica il PlaylistController.
     */
    @FXML
    public void onBackButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/PlaylistView.fxml"));
            Parent root = loader.load();
            
            // Dobbiamo ri-iniettare le dipendenze nel controller principale
            PlaylistController targetController = loader.getController();
            if (targetController != null) {
                targetController.setDependencies(this.library, this.primaryStage);
            }
            
            if (primaryStage != null && primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel ritorno alla vista Playlist.");
        }
    }
}