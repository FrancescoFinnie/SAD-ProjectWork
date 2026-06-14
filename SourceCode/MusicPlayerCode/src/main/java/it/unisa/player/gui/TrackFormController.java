package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Track;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import it.unisa.player.command.AddTrackToLibraryCommand;
import it.unisa.player.command.Command;
import it.unisa.player.command.CommandManager;

public class TrackFormController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private Label formTitleLabel;

private Library library;
    private MainController mainController;
    private CommandManager commandManager; // Per eseguire comandi di modifica alla libreria

    //Tiene traccia del brano che si sta modificando (se null, allora stiamo inserendo)
    private Track trackToEdit = null;

    // Riceve le dipendenze necessarie per delegare la navigazione
    public void setDependencies(Library library, MainController mainController, CommandManager commandManager) {
        this.library = library;
        this.mainController = mainController;
        this.commandManager = commandManager;
    }

    // Permette al LibraryController di passare la traccia da modificare
    public void setTrackToEdit(Track track) {
        this.trackToEdit = track;
        if (track != null) {
            // Se siamo in modifica, pre-compiliamo i campi
            if (formTitleLabel != null) formTitleLabel.setText("Modifica Traccia");
            titleField.setText(track.getTitle());
            authorField.setText(track.getAuthor());
            genreField.setText(track.getGenre());
            yearField.setText(String.valueOf(track.getReleaseYear()));

            int min = track.getDuration() / 60;
            int sec = track.getDuration() % 60;
            durationField.setText(String.format("%02d:%02d", min, sec));
        }
    }


    /**
     * Scatta quando si clicca sul pulsante "Salva" all'interno del form di aggiunta/modifica traccia.
     * Estrae i dati, li passa al Dominio e intercetta eventuali violazioni delle regole di business.
     */
    @FXML
    public void onSaveNewTrack() {
        try {
            // Estrazione delle stringhe dai campi di input
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();

            // Parsing dell'Anno: la validazione logica è ora demandata esclusivamente al Modello (Track)
            int year = Integer.parseInt(yearField.getText().trim());

            // Parsing della durata (mm:ss o secondi)
            String durationText = durationField.getText().trim();
            int duration;
            if (durationText.contains(":")) {
                String[] parts = durationText.split(":");
                duration = (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]);
            } else {
                duration = Integer.parseInt(durationText);
            }

            // Decidiamo se siamo in modalità aggiunta o modifica in base alla presenza di trackToEdit
            if (trackToEdit == null) {
                // Modalità aggiunta: crea l'istanza convalidata dal Modello
                Track newTrack = new Track(title, author, duration, genre, year);
                
                if (this.commandManager != null) {
                    Command addCmd = new AddTrackToLibraryCommand(this.library, newTrack);
                    this.commandManager.executeCommand(addCmd);
                } else {
                    this.library.addTrack(newTrack);
                }
                System.out.println("Traccia aggiunta con successo tramite Command!");
            } else {
                // Modalità modifica: l'assegnamento passa dai setter che solleveranno eccezioni in caso di input non validi
                trackToEdit.setTitle(title);
                trackToEdit.setAuthor(author);
                trackToEdit.setDuration(duration);
                trackToEdit.setGenre(genre);
                trackToEdit.setReleaseYear(year);

                // Aggiorna la posizione nella lista per notificare la riga della tabella
                int index = library.getAllTracks().indexOf(trackToEdit);
                if (index != -1) {
                    library.getAllTracks().set(index, trackToEdit);
                }
                System.out.println("Traccia modificata con successo!");
            }
            
            // Si ritorna alla schermata principale solo se non sono state sollevate eccezioni
            onBackButtonClicked();

        } catch (NumberFormatException e) {
            // Intercettazione errori di conversione stringa-numero (es. lettere nel campo durata)
            showErrorAlert("Errore di Formattazione", "Controlla che anno e durata contengano solo numeri validi.");
        } catch (IllegalArgumentException e) {
            // Intercettazione delle eccezioni di business logic sollevate dalla classe Track
            showErrorAlert("Dati Non Validi", e.getMessage());
        }
    }

    /**
     * Metodo di supporto (Clean Code) per generare e mostrare un Alert grafico di errore all'utente.
     * Estrae la logica di creazione UI per mantenere il metodo principale snello e leggibile.
     * * @param header L'intestazione in grassetto dell'Alert.
     * @param content Il messaggio di dettaglio dell'errore (spesso catturato dal Modello).
     */
    private void showErrorAlert(String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Errore di Salvataggio");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

@FXML
    public void onBackButtonClicked() {
        try {
            // Ricarichiamo la vista principale tramite la costante (Clean Code)
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.LIBRARY_VIEW));
            Parent view = loader.load();

            // Recuperiamo il controller e gli passiamo il mainController
            LibraryController nextController = loader.getController();
            if (nextController != null) {
                nextController.setDependencies(this.library, this.mainController, this.commandManager);
            }

            // Deleghiamo il cambio scena al MainController senza ricaricare tutta la finestra
            if (this.mainController != null) {
                this.mainController.setCenterView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}