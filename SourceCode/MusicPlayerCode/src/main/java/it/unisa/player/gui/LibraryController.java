package it.unisa.player.gui;

import java.util.Optional;

import it.unisa.player.command.CommandManager;
import it.unisa.player.engine.PlaybackEngine;
import it.unisa.player.model.Library;
import it.unisa.player.model.Track;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
import it.unisa.player.command.Command;
import it.unisa.player.command.RemoveTrackFromLibraryCommand;

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
    private CommandManager commandManager;

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
    public void setDependencies(Library library, MainController mainController, CommandManager commandManager) {
        this.library = library;
        this.mainController = mainController;
        this.commandManager = commandManager;

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
                        
                        //Aggiunta alert di conferma 
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Conferma Eliminazione");
                        alert.setHeaderText("Eliminare definitivamente il brano?");
                        alert.setContentText("Sei sicuro di voler eliminare \"" + track.getTitle() + "\" di " + track.getAuthor() + " dalla libreria?");
                        
                        // Mostra l'alert e attende la risposta dell'utente (OK o ANNULLA)
                        Optional<ButtonType> result = alert.showAndWait();
                        
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                            if (LibraryController.this.library != null) {
                                // MODIFICATO: Passa tramite il CommandManager per registrare l'azione nell'Undo
                                if (LibraryController.this.commandManager != null) {
                                    Command removeCmd = new RemoveTrackFromLibraryCommand(LibraryController.this.library, track);
                                    LibraryController.this.commandManager.executeCommand(removeCmd);
                                    
                                    // Aggiorna istantaneamente la grafica della tabella
                                    if (trackTable != null) {
                                        trackTable.refresh();
                                    }
                                } else {
                                    LibraryController.this.library.removeTrack(track);
                                }
                                System.out.println("Traccia eliminata con successo tramite Command: " + track.getTitle());
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
            // Creiamo un timer condiviso per la tabella
            final PauseTransition clickTimer = new PauseTransition(Duration.millis(250));
            
            trackTable.setOnMouseClicked(event -> {
                Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
                int startIndex = trackTable.getSelectionModel().getSelectedIndex();
        
                    if (selectedTrack != null) {
            
                        // ==========================================
                        // CASO 1: SINGOLO CLICK -> Apri Form Modifica
                        // ==========================================
                        if (event.getClickCount() == 1) { 
                            // Avviamo il timer: se scade senza altri click, si apre il form
                            clickTimer.setOnFinished(e -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.ADD_TRACK_VIEW));
                                    Parent view = loader.load();

                                    TrackFormController formController = loader.getController();
                                    if (formController != null) {
                                        formController.setDependencies(LibraryController.this.library, LibraryController.this.mainController, LibraryController.this.commandManager);
                                        formController.setTrackToEdit(selectedTrack);
                                    }

                                    // Deleghiamo il cambio di vista al MainController
                                    if (LibraryController.this.mainController != null) {
                                        LibraryController.this.mainController.setCenterView(view);
                                    }
                                    System.out.println("Singolo click: apertura form di modifica per " + selectedTrack.getTitle());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                            clickTimer.playFromStart();
                        } 
            
                        // Avvio riproduzione da Doppio Click.
                        // Recupera la libreria corrente e invoca playFromLibrary
                        // sull'engine al doppio click dell'utente sulla tabella.
                        else if (event.getClickCount() == 2) { 
                            // Blocca subito il timer del primo click (evita il cambio scena)
                            clickTimer.stop(); 
                            
                            if (startIndex >= 0 && LibraryController.this.library != null) {
                                // CORRETTO: Passiamo l'intero oggetto library del controller
                                it.unisa.player.engine.PlaybackEngine.getInstance().playFromLibrary(LibraryController.this.library, startIndex);
                                System.out.println("Doppio click: avvio traccia " + selectedTrack.getTitle());
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
                formController.setDependencies(this.library, this.mainController, this.commandManager);
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
                targetController.setDependencies(this.library, this.mainController, this.commandManager);
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
        if (commandManager != null) {
            commandManager.undo(); // Esegue l'annullamento logico sul modello dei dati
            
            if (trackTable != null) {
                trackTable.refresh(); 
            }
            System.out.println("Undo eseguito con successo. Interfaccia grafica della Libreria aggiornata.");
        } else {
            System.err.println("Impossibile eseguire l'Undo: CommandManager non iniettato.");
        }
    }
}