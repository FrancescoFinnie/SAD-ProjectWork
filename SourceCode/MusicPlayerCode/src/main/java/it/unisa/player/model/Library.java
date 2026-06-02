package it.unisa.player.model;


public class Library {
    //Le liste sono observable per l'aggiornamento automatico delle view
    private javafx.collections.ObservableList<Track> tracks;
    private javafx.collections.ObservableList<Playlist> playlists;
    
    public Library() {
        this.tracks = javafx.collections.FXCollections.observableArrayList();
        this.playlists = javafx.collections.FXCollections.observableArrayList();
    }

    public void addTrack(Track t) { tracks.add(t); }
    public void removeTrack(Track t) { tracks.remove(t); }
    public javafx.collections.ObservableList<Track> getAllTracks() { return tracks; }
    public javafx.collections.ObservableList<Playlist> getPlaylists() { return playlists; }

    /**
     * Aggiunge una nuova playlist alla libreria, impedendo i duplicati.
     * @param p La playlist da aggiungere.
     * @return true se l'aggiunta ha successo, false se esiste già una playlist con lo stesso nome.
     */
    public boolean addPlaylist(Playlist p) {
        for (Playlist existing : playlists) {
            if (existing.getName().equalsIgnoreCase(p.getName())) {
                return false; // Trovato un duplicato, rifiuta l'aggiunta
            }
        }
        playlists.add(p);
        return true; // Aggiunta con successo
    }

    public void removePlaylist(Playlist p) { playlists.remove(p); }

    // Inizializzazione con dati di prova
    public void loadSampleData() {
        Track t1 = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975);
        Track t2 = new Track("Stairway to Heaven", "Led Zeppelin", 482, "Rock", 1971);
        Track t3 = new Track("Hotel California", "Eagles", 390, "Rock", 1976);

        tracks.add(t1);
        tracks.add(t2);
        tracks.add(t3);

        Playlist p1 = new Playlist("Classici Rock");
        playlists.add(p1);

    }
}