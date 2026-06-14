package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import it.unisa.player.command.CommandManager;

public class PlaylistFormController {

    @FXML private TextField nameField;
    @FXML private Label errorLabel;

    @FXML private Label titleLabel; // Mappa la Label del titolo presente nel file FXML


    private Library library;
    private MainController mainController;
    private CommandManager commandManager; // Per eseguire comandi di modifica alla libreria

    private Playlist playlistToEdit;

/**
     * Iniezione dipendenze per CREAZIONE (Aggiungi Playlist).
     */
    public void setDependencies(Library library, MainController mainController, CommandManager commandManager) {
        setDependencies(library, null, mainController,commandManager); // Richiama il metodo sottostante passando null
    }

    /**
     * Iniezione dipendenze principale (Supporta sia Creazione che Modifica).
     */
    public void setDependencies(Library library, Playlist playlistToEdit, MainController mainController, CommandManager commandManager) {
        this.library = library;
        this.playlistToEdit = playlistToEdit;
        this.mainController = mainController;
        this.commandManager = commandManager;

        if (this.playlistToEdit != null) {
            // --- MODALITÀ MODIFICA ---
            if (titleLabel != null) {
                titleLabel.setText("Modifica Playlist"); // Cambia la scritta dell'interfaccia
            }
            if (nameField != null) {
                nameField.setText(this.playlistToEdit.getName()); // Riempe il campo col vecchio nome
            }
        } else {
            // --- MODALITÀ CREAZIONE ---
            if (titleLabel != null) {
                titleLabel.setText("Aggiungi Playlist"); // Ripristina la scritta originale
            }
            if (nameField != null) {
                nameField.clear(); // Svuota il campo di testo
            }
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
            Playlist newPlaylist = new Playlist(name);
            
            if (this.commandManager != null && this.library != null) {
                // Istanziamo ed eseguiamo il comando tramite CommandManager per supportare l'Undo
                it.unisa.player.command.Command createCmd = new it.unisa.player.command.CreatePlaylistCommand(this.library, newPlaylist);
                this.commandManager.executeCommand(createCmd);
                System.out.println("Playlist creata con successo tramite CreatePlaylistCommand!");
            } else {
                library.addPlaylist(newPlaylist);
            }
        }

        // --- 3. CONCLUSIONE ---
        goBackToPlaylists();
    }

    @FXML
    public void onBackButtonClicked() {
        goBackToPlaylists(); // Tasto indietro ←
    }

    private void goBackToPlaylists() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.PLAYLIST_VIEW));
            Parent root = loader.load();
            
            PlaylistController targetController = loader.getController();
            if (targetController != null) {
                targetController.setDependencies(this.library, this.mainController,this.commandManager);
            }
            

            if (this.mainController != null) {
                this.mainController.setCenterView(root);
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