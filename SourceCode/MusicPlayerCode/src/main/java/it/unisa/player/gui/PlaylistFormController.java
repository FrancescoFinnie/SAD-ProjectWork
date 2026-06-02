package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PlaylistFormController {

    @FXML private TextField nameField;
    @FXML private Label errorLabel;

    private Library library;
    private Stage primaryStage;

    public void setDependencies(Library library, Stage primaryStage) {
        this.library = library;
        this.primaryStage = primaryStage;
    }

    @FXML
    public void onSaveClick() {
        String name = nameField.getText().trim();
        
        if (name.isEmpty()) {
            showError("Il nome non può essere vuoto.");
            return;
        }

        // Creazione e controllo duplicati
        Playlist newPlaylist = new Playlist(name);
        boolean success = library.addPlaylist(newPlaylist);

        if (success) {
            // Se ha successo, torna alla schermata delle playlist
            goBackToPlaylists();
        } else {
            showError("Esiste già una playlist con questo nome.");
        }
    }

    @FXML
    public void onBackButtonClicked() {
        goBackToPlaylists(); // Tasto indietro ←
    }

        private void goBackToPlaylists() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/PlaylistView.fxml"));
            Parent root = loader.load();
            
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

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}