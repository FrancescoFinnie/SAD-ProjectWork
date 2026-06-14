package it.unisa.player.gui;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import it.unisa.player.command.CommandManager;
import it.unisa.player.engine.PlaybackEngine;
import it.unisa.player.model.Library;
import it.unisa.player.model.Track;
import javafx.animation.PauseTransition;
import javafx.collections.transformation.SortedList;
import javafx.application.Platform; 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;     
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import it.unisa.player.command.Command;
import it.unisa.player.command.RemoveTrackFromLibraryCommand;

// Import aggiunti per la gestione della nuova UI dei Tag
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import java.util.Set;
import java.util.HashSet;

/**
 * Il Controller della libreria. Gestisce sia la schermata principale (tabella dei brani)
 * sia la schermata secondaria del form (inserimento e modifica).
 */
public class LibraryController {

    // Elementi della vista principale 
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, Void> deleteColumn; 
    @FXML private TableColumn<Track, String> tagsColumn;

    // Riferimenti allo stato interno e alle dipendenze
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
        
        // Creazione della cella custom per la colonna Tag: Testo (tag attuali) + Bottone "+"
        if (tagsColumn != null) {
            tagsColumn.setCellFactory(param -> new TableCell<Track, String>() {
                private final Button tagBtn = new Button("+");
                private final Label tagsLabel = new Label();
                private final HBox container = new HBox(10, tagsLabel, tagBtn);

                {
                    // Stile del bottone di aggiunta tag
                    tagBtn.setStyle("-fx-background-color: #007aff; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 10;");
                    container.setAlignment(Pos.CENTER_LEFT);

                        tagBtn.setOnAction(event -> {
                        Track track = getTableView().getItems().get(getIndex());
                        if (track != null) {
                            // 1. Raccogliamo tutti i tag univoci già presenti nell'intera libreria
                            Set<String> existingTags = new HashSet<>();
                            if (LibraryController.this.library != null) {
                                for (Track t : LibraryController.this.library.getAllTracks()) {
                                    existingTags.addAll(t.getTags());
                                }
                            }

                            // 2. Creiamo un Dialog personalizzato
                            Dialog<String> dialog = new Dialog<>();
                            dialog.setTitle("Assegnazione Tag");
                            
                            String currentTags = track.getTags().isEmpty() ? "Nessun tag" : String.join(", ", track.getTags());
                            dialog.setHeaderText("Tag attuali: " + currentTags + "\n\nAggiungi un tag a: " + track.getTitle());

                            // Configura i pulsanti
                            ButtonType confirmButtonType = new ButtonType("Aggiungi", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

                            // 3. Creiamo la ComboBox editabile (funge sia da tendina che da campo di testo)
                            ComboBox<String> tagComboBox = new ComboBox<>();
                            tagComboBox.getItems().addAll(existingTags);
                            tagComboBox.setEditable(true);
                            tagComboBox.setPromptText("Seleziona o digita nuovo...");
                            tagComboBox.setPrefWidth(200);

                            GridPane grid = new GridPane();
                            grid.setHgap(10);
                            grid.setVgap(10);
                            grid.add(new Label("Seleziona/Scrivi Tag:"), 0, 0);
                            grid.add(tagComboBox, 1, 0);

                            dialog.getDialogPane().setContent(grid);

                            // Richiediamo il focus sulla combobox appena si apre la finestra
                            Platform.runLater(tagComboBox::requestFocus);

                            // Convertiamo il risultato alla pressione del tasto Aggiungi
                            dialog.setResultConverter(dialogButton -> {
                                if (dialogButton == confirmButtonType) {
                                    return tagComboBox.getValue(); // Restituisce sia testo selezionato che digitato
                                }
                                return null;
                            });

                            // 4. Mostra il popup e salva
                            dialog.showAndWait().ifPresent(tag -> {
                                if (tag != null && !tag.trim().isEmpty()) {
                                    track.addTag(tag.trim().toLowerCase()); // Salva tutto in minuscolo
                                    getTableView().refresh();
                                }
                            });
                        }
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableView().getItems().get(getIndex()) == null) {
                        setGraphic(null);
                    } else {
                        Track track = getTableView().getItems().get(getIndex());
                        List<String> tags = track.getTags();
                        // Se non ci sono tag mostra un trattino, altrimenti la lista
                        tagsLabel.setText(tags.isEmpty() ? "" : String.join(", ", tags));
                        setGraphic(container);
                    }
                }
            });
        }
        
        // Inizializza le azioni avanzate della tabella
        setupTableInteractions();
    }


    // Riceve le istanze dei motori logici dalla classe App e aggiorna la tabella con i dati correnti della libreria
    public void setDependencies(Library library, MainController mainController, CommandManager commandManager) {
        this.library = library;
        this.mainController = mainController;
        this.commandManager = commandManager;

        if (trackTable != null && library != null) {
            // Avvolgiamo in una SortedList per creare la classifica
            SortedList<Track> sortedTracks = new SortedList<>(library.getAllTracks());
            sortedTracks.setComparator((t1, t2) -> Integer.compare(t2.getPlayCount(), t1.getPlayCount()));
            trackTable.setItems(sortedTracks);
            
            // Listener "Smart": Quando il PlaybackEngine notifica una canzone completata, forza il ricalcolo della classifica in tempo reale!
            PlaybackEngine.getInstance().totalPlaysProperty().addListener((obs, oldVal, newVal) -> {
                Platform.runLater(() -> {
                    trackTable.refresh();
                    sortedTracks.setComparator((t1, t2) -> Integer.compare(t2.getPlayCount(), t1.getPlayCount()));
                });
            });
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
                    // Stile del pulsante 
                    deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3b30; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand; -fx-padding: 0 10 0 0;");
                    // Gestione dell'evento di click sul pulsante di cancellazione
                    deleteBtn.setOnAction(event -> {
                        Track track = getTableView().getItems().get(getIndex());
                        
                        // Aggiunta alert di conferma 
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
                // se la riga è vuota, non mostriamo il pulsante
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

        // Gestione del doppio click sulla riga per modificare la traccia
        if (trackTable != null) {
            // Creiamo un timer condiviso per la tabella
            final PauseTransition clickTimer = new PauseTransition(Duration.millis(250));
            
            trackTable.setOnMouseClicked(event -> {
                
                // FIX FONDAMENTALE: Ignora qualsiasi click che non sia il tasto SINISTRO del mouse
                // Evita conflitti e comportamenti inattesi se si clicca col destro.
                if (event.getButton() != MouseButton.PRIMARY) {
                    return;
                }

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

                            int realIndex = LibraryController.this.library.getAllTracks().indexOf(selectedTrack);
                            
                            if (realIndex >= 0 && LibraryController.this.library != null) {
                                // Passiamo l'indice reale al motore
                                it.unisa.player.engine.PlaybackEngine.getInstance().playFromLibrary(LibraryController.this.library, realIndex);
                                System.out.println("Doppio click: avvio traccia " + selectedTrack.getTitle());
                            }
                        }
                }
            });
        }
    }

    // Scatta quando si clicca sul pulsante "+" di aggiunta traccia.
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

    // Rimanda alla vista delle playlist.
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