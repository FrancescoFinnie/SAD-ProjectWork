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

    private Playlist playlistToEdit;

    public void setDependencies(Library library, Stage primaryStage) {
        this.library = library;
        this.primaryStage = primaryStage;
    }

    /**
     * Iniezione dipendenze principale (Supporta sia Creazione che Modifica).
     */
    public void setDependencies(Library library, Playlist playlistToEdit, Stage primaryStage) {
        this.library = library;
        this.playlistToEdit = playlistToEdit;
        this.primaryStage = primaryStage;

        // Task 11.3: Logica di auto-compilazione. Se stiamo modificando, riempiamo il campo col vecchio nome
        if (this.playlistToEdit != null) {
            nameField.setText(this.playlistToEdit.getName());
        }
    }

    @FXML
    public void onSaveClick() {
        String name = nameField.getText().trim();
        
        // --- 1. CLAUSOLE DI GUARDIA (Validazione Input) ---
        
        // Guardia 1: Nome vuoto
        if (name.isEmpty()) {
            showError("Il nome non può essere vuoto.");
            return; // Blocca tutto
        }

        // Guardia 2: Controllo duplicati unificato (Vale sia per Creazione che per Modifica)
        // Se playlistToEdit è null (Creazione), controlla tutte. Se non è null (Modifica), esclude se stessa.
        boolean nameExists = library.getPlaylists().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name) && p != playlistToEdit);

        if (nameExists) {
            showError("Esiste già una playlist con questo nome.");
            return; // Blocca tutto
        }

        // --- 2. ESECUZIONE (Se arriviamo qui, i dati sono validi al 100%) ---
        
        if (playlistToEdit != null) {
            // Modifica (US11)
            playlistToEdit.setName(name);
            int index = library.getPlaylists().indexOf(playlistToEdit);
            if (index != -1) {
                library.getPlaylists().set(index, playlistToEdit); // Forza refresh UI
            }
        } else {
            // Creazione (US7)
            library.addPlaylist(new Playlist(name)); // Sappiamo già che avrà successo
        }

        // --- 3. CONCLUSIONE ---
        goBackToPlaylists();
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