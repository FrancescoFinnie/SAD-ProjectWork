package it.unisa.player.gui;

import java.util.Optional;

import it.unisa.player.engine.PlaybackEngine;
import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlaylistController {

    
    @FXML private TableView<Playlist> playlistTable;
    @FXML private TableColumn<Playlist, String> nameColumn;
    
    @FXML private TableColumn<Playlist, Void> deleteColumn;

    @FXML private TableColumn<Playlist, Void> editColumn;

    private Library library;
    private MainController mainController;

    @FXML
    public void initialize() {
        // 1. Collega la colonna "Nome"
        if (nameColumn != null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        }

        // 2. Configura la colonna del cestino con UI Consistency
        if (deleteColumn != null) {
            deleteColumn.setCellFactory(param -> new TableCell<Playlist, Void>() {
                
                // Usiamo la stessa "✕" usata nel LibraryController
                private final Button deleteBtn = new Button("✕"); 

                {
                    // Stile clonato al millimetro dal LibraryController
                    deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3b30; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand; -fx-padding: 0 10 0 0;");
                    
                    // Azione al click
                    deleteBtn.setOnAction(event -> {
                        Playlist playlist = getTableView().getItems().get(getIndex());
                        
                        // Creazione dell'Alert di conferma (Requisito Task 12.2)
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Conferma Eliminazione");
                        alert.setHeaderText("Eliminazione Playlist");
                        alert.setContentText("Sei sicuro di voler eliminare la playlist '" + playlist.getName() + "'?");

                        // Mostra l'alert e aspetta la risposta dell'utente
                        Optional<ButtonType> result = alert.showAndWait();
                        
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            if (library != null) {
                                // Rimuove l'oggetto dal Model
                                library.removePlaylist(playlist);
                                System.out.println("Playlist eliminata: " + playlist.getName());
                                
                            }
                        }
                    });
                }

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

        // --- Task 11.2: Configurazione Colonna Modifica (Pulsante "✎") ---
        if (editColumn != null) {
            editColumn.setCellFactory(param -> new TableCell<Playlist, Void>() {
                
                private final Button editBtn = new Button("✎");

                {
                    // Stile visivo: testo blu, sfondo trasparente, cursore a manina
                    editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #007aff; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand; -fx-padding: 0 10 0 0;");
                    
                    editBtn.setOnAction(event -> {
                        // 1. Capisce quale playlist c'è in questa specifica riga
                        Playlist playlistToEdit = getTableView().getItems().get(getIndex());
                        
                        // 2. Invoca il metodo per aprire il form di modifica
                        openPlaylistFormForEdit(playlistToEdit);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(editBtn);
                    }
                }
            });
        }

        // --- INTEGRAZIONE US13: Doppio click per aprire il dettaglio della playlist task 13.3---
        if (playlistTable != null) {
            playlistTable.setRowFactory(tv -> {
                javafx.scene.control.TableRow<Playlist> row = new javafx.scene.control.TableRow<>();
                
                // AGGIUNTA: Creiamo un timer di 250 millisecondi per questa riga
                final PauseTransition pause = new PauseTransition(Duration.millis(250));
                
                row.setOnMouseClicked(event -> {
                    // Controlla che la riga non sia vuota
                    if (!row.isEmpty() && row.getItem() != null) {
                        Playlist clickedPlaylist = row.getItem();
                        
                        if (event.getClickCount() == 1) {
                            // 1 CLICK = Fai partire il timer. Se l'utente non clicca di nuovo, apri il dettaglio.
                            pause.setOnFinished(e -> openPlaylistDetail(clickedPlaylist));
                            pause.playFromStart();
                        } else if (event.getClickCount() == 2) {
                            // 2 CLICK = Blocca il timer del primo click e avvia la musica!
                            pause.stop();
                            System.out.println("Doppio click su playlist! Avvio riproduzione...");
                            PlaybackEngine.getInstance().playFromPlaylist(clickedPlaylist, 0);
                        }
                    }
                });
                return row;
            });
        }
    }
    /**
     * Esegue il cambio scena verso la vista di dettaglio della playlist (US13).
     * @param targetPlaylist La playlist su cui l'utente ha fatto doppio click.
     */
    private void openPlaylistDetail(Playlist targetPlaylist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/PlaylistDetailView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            // Recuperiamo il controller della nuova vista
            PlaylistDetailController detailController = loader.getController();
            if (detailController != null) {
                // Passiamo la libreria, la singola playlist cliccata e lo stage
                detailController.setDependencies(this.library, this.mainController, targetPlaylist);
            }
            
            // Effettuiamo il cambio di scena
            if (this.mainController != null) {
                this.mainController.setCenterView(root);
            }
        }catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nell'apertura della vista di dettaglio della playlist.");
        }
    }

    // Iniezione del MainController per delegare la navigazione centrale
    public void setDependencies(Library library, MainController mainController) {
        this.library = library;
        this.mainController = mainController;
        // Popola la tabella con le playlist
        if (playlistTable != null && library != null) {
            playlistTable.setItems(library.getPlaylists());
        }
    }
    
    @FXML
    public void onBackToLibraryClick() {
        try {
            // REFACTORING TD2.4: Uso di ViewConstants
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.LIBRARY_VIEW));
            Parent root = loader.load();
            
            LibraryController targetController = loader.getController();
            if (targetController != null) {
                targetController.setDependencies(this.library, this.mainController);
            }
            
            // REFACTORING TD2.4: Switch dinamico al centro del BorderPane globale
            if (this.mainController != null) {
                this.mainController.setCenterView(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel ritorno alla libreria principale.");
        }
    }

    // Metodi vuoti per le prossime us
    @FXML
    public void onAddPlaylistClick() {
        try {
            // REFACTORING TD2.4: Uso di ViewConstants
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.PLAYLIST_FORM_VIEW));
            Parent root = loader.load();
            
            PlaylistFormController formController = loader.getController();
            if (formController != null) {
                // REFACTORING TD2.4: Inietto mainController
                formController.setDependencies(this.library, this.mainController);
            }
            
            // REFACTORING TD2.4: Switch dinamico al centro del BorderPane globale
            if (this.mainController != null) {
                this.mainController.setCenterView(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nell'apertura della vista PlaylistForm.");
        }
    }

        /**
     * [Task 11.2] Carica il form in modalità Modifica per la playlist selezionata.
     */
    private void openPlaylistFormForEdit(Playlist playlist) {
        try {
            // REFACTORING TD2.4: Uso di ViewConstants
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.PLAYLIST_FORM_VIEW));
            javafx.scene.Parent root = loader.load();

            PlaylistFormController targetController = loader.getController();
            if (targetController != null) {
                // REFACTORING TD2.4: Inietto mainController al posto dello Stage
                targetController.setDependencies(this.library, playlist, this.mainController);
            }

            // REFACTORING TD2.4: Switch dinamico al centro del BorderPane globale
            if (this.mainController != null) {
                this.mainController.setCenterView(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nell'apertura di PlaylistFormView in modalità Modifica.");
        }
    }


    @FXML
    public void onUndoClick() {
        System.out.println("Undo non ancora implementato.");
    }
}