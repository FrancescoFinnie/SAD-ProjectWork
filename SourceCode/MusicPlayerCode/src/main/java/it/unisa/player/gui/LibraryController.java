package it.unisa.player.gui;

import java.util.Optional;

import it.unisa.player.model.Library;
import it.unisa.player.model.Track;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private MainController mainController; 

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
        //inizializza le azioni avanzate 
        setupTableInteractions();
    }


    //Riceve le istanze dei motori logici dalla classe App e aggiorna la tabella con i dati correnti della libreria
    public void setDependencies(Library library, MainController mainController) {
        this.library = library;
        this.mainController = mainController;

        if (trackTable != null && library != null) {
            trackTable.setItems(library.getAllTracks());
        }
    }

    /**
     * Configura le logiche di interazione all'interno della tabella (Cestino e Doppio Click).
     */
    private void setupTableInteractions() {
        // Gestione della colonna di cancellazione 
        if (deleteColumn != null) {
            deleteColumn.setCellFactory(param -> new TableCell<Track, Void>() {
                private final Button deleteBtn = new Button("✕"); 
                {
                    //Stile del pulsante 
                    deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3b30; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand; -fx-padding: 0 10 0 0;");
                    //Gestione dell'evento di click sul pulsante di cancellazione
                    deleteBtn.setOnAction(event -> {
                        Track track = getTableView().getItems().get(getIndex());
                        if (LibraryController.this.library != null) {
                            // Creazione dell'Alert grafico di conferma
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Conferma Eliminazione");
                            alert.setHeaderText("Stai per eliminare la traccia");
                            alert.setContentText("Sei sicuro di voler rimuovere \"" + track.getTitle() + "\"?");

                            // Mostra l'alert e attende il click del mouse
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                // Se l'utente clicca OK, cancella la traccia
                                LibraryController.this.library.removeTrack(track);
                                System.out.println("Traccia eliminata: " + track.getTitle());
                            } else {
                                System.out.println("Eliminazione annullata.");
                            }
                        }
                    });
                }
                //se la riga è vuota, non mostriamo il pulsante
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteBtn);
                    }
                }
            });
        }
        //Gestione del doppio click sulla riga per modificare la traccia
        if (trackTable != null) {
            trackTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { 
                    Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
                    if (selectedTrack != null) {
                        try {
                            // Uso della costante al posto della stringa hard-coded
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.ADD_TRACK_VIEW));
                            Parent view = loader.load();

                            TrackFormController formController = loader.getController();
                            if (formController != null) {
                                // Passiamo mainController al posto di primaryStage
                                formController.setDependencies(LibraryController.this.library, LibraryController.this.mainController);
                                formController.setTrackToEdit(selectedTrack);
                            }

                            // Deleghiamo il cambio di vista al MainController
                            if (LibraryController.this.mainController != null) {
                                LibraryController.this.mainController.setCenterView(view);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

//Scatta quando si clicca sul pulsante "+" di aggiunta traccia.
    @FXML
    public void onAddTrackClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.ADD_TRACK_VIEW));
            Parent view = loader.load();
            TrackFormController formController = loader.getController();
            
            if (formController != null) {
                formController.setDependencies(this.library, this.mainController);
            }
            if (this.mainController != null) {
                this.mainController.setCenterView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento di AddTrackView.fxml");
        }
    }

    //rimanda a playlist viewer (task 14.3)
    @FXML
    public void onViewPlaylistsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.PLAYLIST_VIEW));
            Parent view = loader.load();
            PlaylistController targetController = loader.getController();
            
            if (targetController != null) {
                targetController.setDependencies(this.library, this.mainController);
            }
            if (this.mainController != null) {
                this.mainController.setCenterView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nell'apertura della vista Playlist.");
        }
    }


    @FXML
    public void onUndoClick() {
        // Lasciamolo con un print per questo Sprint
        System.out.println("Funzione Undo non disponibile in questa versione.");
    }
}