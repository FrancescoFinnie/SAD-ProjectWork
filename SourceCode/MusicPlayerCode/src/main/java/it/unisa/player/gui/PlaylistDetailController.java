package it.unisa.player.gui;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;
import it.unisa.player.engine.PlaybackEngine;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PlaylistDetailController {

    @FXML private Label playlistNameLabel;
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, Void> deleteColumn;

    private Library library;
    private Playlist currentPlaylist; // Mantiene lo stato: quale playlist stiamo visualizzando
    private MainController mainController;

   /**
     * Metodo di inizializzazione nativo di JavaFX.
     * Qui leghiamo le colonne della UI alle proprietà della classe Track.
     * Associa le colonne ai dati e configura i pulsanti di azione.
     */
    @FXML
    public void initialize() {
        // Configurazione standard delle colonne dati
        if (titleColumn != null) titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        if (authorColumn != null) authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        
        if (deleteColumn != null) {
            deleteColumn.setCellFactory(param -> new javafx.scene.control.TableCell<>() {
                
                // Creiamo il bottone "✕" 
                private final javafx.scene.control.Button deleteBtn = new javafx.scene.control.Button("✕");

                {
                    // Stile visivo: testo rosso, sfondo trasparente, cursore a manina per UI Consistency
                    deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff3b30; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand;");
                    
                    
                    deleteBtn.setOnAction(event -> {
                        // 1. Recupera l'oggetto Track associato alla riga cliccata
                        Track trackToRemove = getTableView().getItems().get(getIndex());
                        
                        // 2. Invoca direttamente il metodo di eliminazione sulla playlist in memoria
                        if (currentPlaylist != null) {
                            currentPlaylist.removeTrack(trackToRemove);
                            // La UI si aggiornerà istantaneamente da sola grazie all'ObservableList
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    // Mostra il pulsante solo se la riga contiene effettivamente dei dati
                    if (empty || getIndex() >= getTableView().getItems().size()) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteBtn);
                    }
                }
            });
        }
                

        // Avvio riproduzione da Doppio Click su brano della Playlist.
        // Intercetta l'evento sulla tabella delegando al PlaybackEngine.

        trackTable.setOnMouseClicked(event -> {
            // Se l'utente fa doppio click e ha effettivamente selezionato una riga
            if (event.getClickCount() == 1 && trackTable.getSelectionModel().getSelectedItem() != null) {
                onPlayPlaylistClicked();
            }
        });
    }

    /**
     * Dependency Injection: passiamo al controller tutto ciò di cui ha bisogno per operare.
     */
    public void setDependencies(Library library, MainController mainController, Playlist playlist) {
        this.library = library;
        this.currentPlaylist = playlist;
        this.mainController = mainController;

        if (this.currentPlaylist != null) {
            // Aggiorniamo la View
            playlistNameLabel.setText(currentPlaylist.getName());
            
            // PATTERN OBSERVER: colleghiamo la tabella direttamente alla lista in memoria della playlist.
            // Qualsiasi aggiunta o rimozione alla ObservableList aggiornerà automaticamente la UI.
            trackTable.setItems(currentPlaylist.getTracks());
        }
    }

    /**
     * Tasto "←" per tornare all'elenco di tutte le playlist.
     * Distrugge questa vista e ricarica il PlaylistController.
     */
    @FXML
    public void onBackButtonClicked() {
        try {
            // REFACTORING TD2.4: Uso di ViewConstants
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.PLAYLIST_VIEW));
            Parent root = loader.load();
            
            // Dobbiamo ri-iniettare le dipendenze nel controller principale
            PlaylistController targetController = loader.getController();
            if (targetController != null) {
                targetController.setDependencies(this.library, this.mainController);
            }
            
            // REFACTORING TD2.4: Deleghiamo il cambio scena alla vista centrale del MainController
            if (this.mainController != null) {
                this.mainController.setCenterView(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore nel ritorno alla vista Playlist.");
        }
    }

    /**
     * Intercetta il click sul pulsante "+" della UI.
     * Il suo scopo è fermare temporaneamente l'interazione con la schermata corrente 
     * e aprire in sovrimpressione la vista modale per selezionare un nuovo brano.
     */
    @FXML
    public void onAddTrackClick() {
        try {
            // 1. Caricamento del file FXML che definisce l'aspetto visivo del Popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/SelectTrack.fxml"));
            javafx.scene.Parent root = loader.load();

            // 2. Creazione e configurazione della finestra modale (Popup)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Seleziona Brano dalla Libreria");
            
            // APPLICATION_MODAL impedisce all'utente di cliccare sulla finestra principale sottostante
            // finché il popup di selezione non viene chiuso o completato.
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL); 
            dialogStage.setScene(new javafx.scene.Scene(root));

            // 3. Iniezione delle Dipendenze (Dependency Injection)
            // Recuperiamo il controller appena istanziato dal loader e gli passiamo il "motore" (Library)
            // e la "destinazione" (currentPlaylist) su cui dovrà applicare l'aggiunta del brano.
            SelectTrackController controller = loader.getController();
            if (controller != null) {
                controller.setDependencies(this.library, this.currentPlaylist, dialogStage);
            }

            // 4. Mostra la finestra a schermo e sospende l'esecuzione di questo metodo
            // finché il dialogStage non viene chiuso dall'utente (tramite i tasti Aggiungi o Annulla).
            dialogStage.showAndWait();
            
        } catch (Exception e) {
            // Gestione di sicurezza in caso di file FXML mancante o errori di inizializzazione
            e.printStackTrace();
            System.err.println("Errore critico: Impossibile caricare l'interfaccia SelectTrack.fxml.");
        }
    }
    // Intercetta il click sul bottone Play generale o il doppio click, 
    // individua l'indice di partenza (0 se non selezionato) 
    // e delega il lancio al PlaybackEngine.
    @FXML
    public void onPlayPlaylistClicked() {
        if (currentPlaylist == null || currentPlaylist.getTracks().isEmpty()) {
            return; // Niente da riprodurre
        }

        int startIndex = 0;
        
        // Seleziona la traccia cliccata nella tabella (se c'è), altrimenti parte da 0
        Track selectedTrack = trackTable.getSelectionModel().getSelectedItem();
        if (selectedTrack != null) {
            startIndex = currentPlaylist.getTracks().indexOf(selectedTrack);
        }

        // Recupera il motore e invoca la riproduzione con il contesto playlist
        PlaybackEngine.getInstance().playFromPlaylist(currentPlaylist, startIndex);
    }

}