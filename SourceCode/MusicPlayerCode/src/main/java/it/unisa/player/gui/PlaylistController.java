package it.unisa.player.gui;

import java.util.Optional;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;

public class PlaylistController {

    
    @FXML private TableView<Playlist> playlistTable;
    @FXML private TableColumn<Playlist, String> nameColumn;
    
    @FXML private TableColumn<Playlist, Void> deleteColumn;

    private Library library;
    private Stage primaryStage;

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
                                
                                // NOTA IMPORTANTE: Non chiamiamo nessun refreshTable() qui!
                                // Poiché usiamo una ObservableList, la UI si aggiorna da sola.
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


        // --- INTEGRAZIONE US13: Doppio click per aprire il dettaglio della playlist task 13.3---
        if (playlistTable != null) {
            playlistTable.setRowFactory(tv -> {
                javafx.scene.control.TableRow<Playlist> row = new javafx.scene.control.TableRow<>();
                row.setOnMouseClicked(event -> {
                    // Controlla che la riga non sia vuota e che l'utente abbia fatto esattamente 2 click
                    if (!row.isEmpty() && event.getClickCount() == 2) {
                        Playlist clickedPlaylist = row.getItem();
                        openPlaylistDetail(clickedPlaylist);
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
                detailController.setDependencies(this.library, targetPlaylist, this.primaryStage);
            }
            
            // Effettuiamo il cambio di scena
            if (primaryStage != null && primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nell'apertura della vista di dettaglio della playlist.");
        }
    }

    public void setDependencies(Library library, Stage primaryStage) {
        this.library = library;
        this.primaryStage = primaryStage;
        
        // Popola la tabella con le playlist
        if (playlistTable != null && library != null) {
            playlistTable.setItems(library.getPlaylists());
        }
    }

    @FXML
    public void onBackToLibraryClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/LibraryView.fxml"));
            Parent root = loader.load();
            
            LibraryController targetController = loader.getController();
            if (targetController != null) {
                targetController.setDependencies(this.library, this.primaryStage);
            }
            
            if (primaryStage != null && primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel ritorno alla libreria principale.");
        }
    }

    // Metodi vuoti per le prossime us
    @FXML
    public void onAddPlaylistClick() {
        System.out.println("Aggiunta playlist non ancora implementata nello Sprint attuale.");
    }

    @FXML
    public void onUndoClick() {
        System.out.println("Undo non ancora implementato.");
    }
}