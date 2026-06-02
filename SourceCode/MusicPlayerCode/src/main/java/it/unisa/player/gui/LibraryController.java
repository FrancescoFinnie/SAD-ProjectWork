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
        //inizializza le azioni avanzate 
        setupTableInteractions();
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
                            LibraryController.this.library.removeTrack(track);
                            System.out.println("Traccia eliminata: " + track.getTitle());
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
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/AddTrackView.fxml"));
                            Parent root = loader.load();
                            
                            //Recuperiamo il controller del form e gli passiamo le dipendenze necessarie
                            TrackFormController formController = loader.getController();
                            if (formController != null) {
                                formController.setDependencies(this.library, this.primaryStage);
                                // Passiamo la traccia: il formController si occuperà di pre-compilarsi
                                formController.setTrackToEdit(selectedTrack);
                            }
                            
                            if (primaryStage != null && primaryStage.getScene() != null) {
                                primaryStage.getScene().setRoot(root);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
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
        try {
            //Carica la vista del form di aggiunta traccia
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/AddTrackView.fxml"));
            Parent root = loader.load();
            
            //Recuperiamo il controller del form e gli passiamo le dipendenze necessarie
            TrackFormController formController = loader.getController();
            if (formController != null) {
                // Passiamo le dipendenze al controller del form dedicatogli
                formController.setDependencies(this.library, this.primaryStage);
            }
            
            //Mostriamo la vista del form al posto della vista principale
            if (primaryStage != null && primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento di AddTrackView.fxml: " + e.getMessage());
        }
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