package it.unisa.player.gui;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import it.unisa.player.command.CommandManager;
import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;

// Import relativi allo Strategy Pattern
import it.unisa.player.strategy.AutoPlaylistGenerator;
import it.unisa.player.strategy.GenreFilterStrategy;
import it.unisa.player.strategy.YearFilterStrategy;
import it.unisa.player.strategy.TagFilterStrategy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class PlaylistFormController {

    @FXML private TextField nameField;
    @FXML private Label errorLabel;
    @FXML private Label titleLabel;

    // Componenti UI per la generazione automatica
    @FXML private VBox generationCriteriaContainer;
    @FXML private ComboBox<String> criterionComboBox;
    @FXML private ComboBox<String> valueComboBox;
    @FXML private VBox automaticSelectionContainer;

    private Library library;
    private MainController mainController;
    private CommandManager commandManager;
    private Playlist playlistToEdit;

    // Costanti per i criteri di generazione
    private static final String CRITERIO_NESSUNO = "Nessuno (Playlist Vuota)";
    private static final String CRITERIO_GENERE = "Genere Musicale";
    private static final String CRITERIO_ANNO = "Anno di Rilascio";
    private static final String CRITERIO_TAG = "Tag Visivo";

    @FXML
    public void initialize() {
        if (criterionComboBox != null) {
            criterionComboBox.getItems().addAll(CRITERIO_NESSUNO, CRITERIO_GENERE, CRITERIO_ANNO, CRITERIO_TAG);
            criterionComboBox.setValue(CRITERIO_NESSUNO);

            // Ascoltatore sul cambio di selezione della prima tendina
            criterionComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                handleCriterionChange(newVal);
            });
        }

        // Il contenitore della seconda tendina è inizialmente invisibile
        if (automaticSelectionContainer != null) {
            automaticSelectionContainer.setVisible(false);
            automaticSelectionContainer.setManaged(false);
        }
    }

    /**
     * Popola la seconda tendina in base al criterio selezionato, rimuovendo i duplicati.
     */
    private void handleCriterionChange(String criterion) {
        if (valueComboBox == null || automaticSelectionContainer == null || library == null) {
            return;
        }

        valueComboBox.getItems().clear();

        if (criterion == null || criterion.equals(CRITERIO_NESSUNO)) {
            automaticSelectionContainer.setVisible(false);
            automaticSelectionContainer.setManaged(false);
            return;
        }

        // Utilizziamo TreeSet per estrarre valori univoci e ordinati alfabeticamente/numericamente
        if (criterion.equals(CRITERIO_GENERE)) {
            Set<String> genres = library.getAllTracks().stream()
                    .map(Track::getGenre)
                    .filter(g -> g != null && !g.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toCollection(TreeSet::new));
            valueComboBox.getItems().addAll(genres);

        } else if (criterion.equals(CRITERIO_ANNO)) {
            Set<String> years = library.getAllTracks().stream()
                    .map(t -> String.valueOf(t.getReleaseYear()))
                    .collect(Collectors.toCollection(TreeSet::new));
            valueComboBox.getItems().addAll(years);

        } else if (criterion.equals(CRITERIO_TAG)) {
            Set<String> tags = new TreeSet<>();
            for (Track track : library.getAllTracks()) {
                if (track.getTags() != null) {
                    tags.addAll(track.getTags());
                }
            }
            valueComboBox.getItems().addAll(tags);
        }

        // Mostra la seconda tendina solo se ci sono dati reali da filtrare
        if (!valueComboBox.getItems().isEmpty()) {
            valueComboBox.getSelectionModel().selectFirst();
            automaticSelectionContainer.setVisible(true);
            automaticSelectionContainer.setManaged(true);
        } else {
            automaticSelectionContainer.setVisible(false);
            automaticSelectionContainer.setManaged(false);
        }
    }

    public void setDependencies(Library library, MainController mainController, CommandManager commandManager) {
        setDependencies(library, null, mainController, commandManager);
    }

    public void setDependencies(Library library, Playlist playlistToEdit, MainController mainController, CommandManager commandManager) {
        this.library = library;
        this.playlistToEdit = playlistToEdit;
        this.mainController = mainController;
        this.commandManager = commandManager;

        // Le tendine di generazione automatica devono essere visibili SOLO in creazione, non in modifica
        boolean isCreationMode = (this.playlistToEdit == null);
        // Nasconde o mostra l'intero contenitore (Etichetta + Prima Tendina)
        if (generationCriteriaContainer != null) {
            generationCriteriaContainer.setVisible(isCreationMode);
            generationCriteriaContainer.setManaged(isCreationMode);
        }

        if (this.playlistToEdit != null) {
            if (titleLabel != null) titleLabel.setText("Modifica Playlist");
            if (nameField != null) nameField.setText(this.playlistToEdit.getName());
        } else {
            if (titleLabel != null) titleLabel.setText("Aggiungi Playlist");
            if (nameField != null) nameField.clear();
            if (criterionComboBox != null) criterionComboBox.setValue(CRITERIO_NESSUNO);
        }
    }

    @FXML
    public void onSaveClick() {
        String name = nameField.getText().trim();
        
        if (name.isEmpty()) {
            showError("Il nome non può essere vuoto.");
            return;
        }

        boolean nameExists = library.getPlaylists().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(name) && p != playlistToEdit);

        if (nameExists) {
            showError("Esiste già una playlist con questo nome.");
            return;
        }

        if (playlistToEdit != null) {
            playlistToEdit.setName(name);
            int index = library.getPlaylists().indexOf(playlistToEdit);
            if (index != -1) {
                library.getPlaylists().set(index, playlistToEdit);
            }
        } else {
            Playlist newPlaylist;
            String selectedCriterion = (criterionComboBox != null) ? criterionComboBox.getValue() : null;

            // Se l'utente ha scelto di generare dinamicamente una playlist
            if (selectedCriterion != null && !selectedCriterion.equals(CRITERIO_NESSUNO)) {
                String targetValue = valueComboBox.getValue();
                if (targetValue == null || targetValue.trim().isEmpty()) {
                    showError("Selezionare un valore valido per il criterio scelto.");
                    return;
                }

                // Utilizzo del pattern Strategy tramite il suo Context
                AutoPlaylistGenerator generator = new AutoPlaylistGenerator();

                if (selectedCriterion.equals(CRITERIO_GENERE)) {
                    generator.setStrategy(new GenreFilterStrategy(targetValue));
                } else if (selectedCriterion.equals(CRITERIO_ANNO)) {
                    try {
                        int year = Integer.parseInt(targetValue);
                        generator.setStrategy(new YearFilterStrategy(year));
                    } catch (NumberFormatException e) {
                        showError("Anno non valido.");
                        return;
                    }
                } else if (selectedCriterion.equals(CRITERIO_TAG)) {
                    generator.setStrategy(new TagFilterStrategy(targetValue));
                }

                // Esecuzione dell'algoritmo di filtraggio
                newPlaylist = generator.generate(this.library);
                // Si assegna il nome scelto dall'utente alla playlist appena generata
                newPlaylist.setName(name);

            } else {
                // Creazione playlist vuota classica
                newPlaylist = new Playlist(name);
            }
            
            // Inserimento con Undo
            if (this.commandManager != null && this.library != null) {
                it.unisa.player.command.Command createCmd = new it.unisa.player.command.CreatePlaylistCommand(this.library, newPlaylist);
                this.commandManager.executeCommand(createCmd);
            } else {
                library.addPlaylist(newPlaylist);
            }
        }

        goBackToPlaylists();
    }

    @FXML
    public void onBackButtonClicked() {
        goBackToPlaylists();
    }

    private void goBackToPlaylists() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ViewConstants.PLAYLIST_VIEW));
            Parent root = loader.load();
            
            PlaylistController targetController = loader.getController();
            if (targetController != null) {
                targetController.setDependencies(this.library, this.mainController, this.commandManager);
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
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }
}