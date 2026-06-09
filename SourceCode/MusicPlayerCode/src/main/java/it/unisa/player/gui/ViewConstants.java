package it.unisa.player.gui;

/**
 * Classe utility per centralizzare i percorsi delle viste FXML (Clean Code).
 * Evita l'uso di "magic strings" sparse nei caricamenti dei Controller.
 */
public final class ViewConstants {
    private ViewConstants() {} // Costruttore privato: impedisce l'istanziazione

    public static final String MAIN_LAYOUT = "/it/unisa/player/gui/MainLayout.fxml";
    public static final String LIBRARY_VIEW = "/it/unisa/player/gui/LibraryView.fxml";
    public static final String PLAYLIST_VIEW = "/it/unisa/player/gui/PlaylistView.fxml";
    public static final String ADD_TRACK_VIEW = "/it/unisa/player/gui/AddTrackView.fxml";
    public static final String PLAYLIST_FORM_VIEW = "/it/unisa/player/gui/PlaylistFormView.fxml";
    public static final String PLAYLIST_DETAIL_VIEW = "/it/unisa/player/gui/PlaylistDetailView.fxml";
    public static final String SELECT_TRACK_VIEW = "/it/unisa/player/gui/SelectTrack.fxml";
}
