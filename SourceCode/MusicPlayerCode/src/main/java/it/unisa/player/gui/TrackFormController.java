package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Track;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TrackFormController {

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private Label formTitleLabel;

    private Library library;
    private Stage primaryStage;
    
    //Tiene traccia del brano che si sta modificando (se null, allora stiamo inserendo)
    private Track trackToEdit = null;

    // Riceve le dipendenze necessarie
    public void setDependencies(Library library, Stage primaryStage) {
        this.library = library;
        this.primaryStage = primaryStage;
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
    //Scatta quando si clicca sul pulsante "Salva" all'interno del form di aggiunta/modifica traccia.
    @FXML
    public void onSaveNewTrack() {
        try {
            // Estrazione delle stringhe dai campi di input
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();

            //Parsing e validazione dell'Anno (Limite 2026)
            int year = Integer.parseInt(yearField.getText().trim());
            if (year > 2026) {
                System.err.println("Errore di compilazione: l'anno non può essere superiore al 2026!");
                return; 
            }

            //Parsing della durata (mm:ss o secondi)
            String durationText = durationField.getText().trim();
            int duration;
            if (durationText.contains(":")) {
                String[] parts = durationText.split(":");
                duration = (Integer.parseInt(parts[0]) * 60) + Integer.parseInt(parts[1]); 
            } else {
                duration = Integer.parseInt(durationText);
            }

            //Decidiamo se siamo in modalità aggiunta o modifica in base alla presenza di trackToEdit
            if (trackToEdit == null) {
                //modalità aggiunta: creiamo un nuovo oggetto Track e lo aggiungiamo alla Library
                Track newTrack = new Track(title, author, duration, genre, year);
                library.addTrack(newTrack);
                System.out.println("Traccia aggiunta con successo!");
            } else {
                //modalità modifica 
                // Aggiorniamo l'oggetto che si trova già nella lista della Library
                trackToEdit.setTitle(title);
                trackToEdit.setAuthor(author);
                trackToEdit.setDuration(duration);
                trackToEdit.setGenre(genre);
                trackToEdit.setReleaseYear(year);
                //Aggiorna la posizione nella lista per notificare la riga della tabella
                int index = library.getAllTracks().indexOf(trackToEdit);
                if (index != -1) {
                    library.getAllTracks().set(index, trackToEdit);
                }
                System.out.println("Traccia modificata con successo!");
            }
            
            // Ritorna alla schermata principale
            onBackButtonClicked();
            
        } catch (NumberFormatException e) {
            System.err.println("Errore di compilazione: controlla i campi numerici!");
        }
    }

    //Scatta quando si clicca sull'icona della freccia per tornare indietro alla schermata principale.
    @FXML
    public void onBackButtonClicked() {
        try {
            //Ricarichiamo la vista principale della libreria
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/LibraryView.fxml"));
            Parent root = loader.load();
            //Recuperiamo il controller associato alla view e gli passiamo le dipendenze necessarie
            LibraryController nextController = loader.getController();
            if (nextController != null) {
                nextController.setDependencies(this.library, this.primaryStage);
            }
            
            if (primaryStage != null && primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}