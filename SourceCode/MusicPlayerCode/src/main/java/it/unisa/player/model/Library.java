package it.unisa.player.model;


public class Library {
    //Le liste sono observable per l'aggiornamento automatico delle view
    private javafx.collections.ObservableList<Track> tracks;

    public Library() {
        this.tracks = javafx.collections.FXCollections.observableArrayList();
    }

    public javafx.collections.ObservableList<Track> getAllTracks() { return tracks; }


    // Inizializzazione con dati di prova
    public void loadSampleData() {
        Track t1 = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975);
        Track t2 = new Track("Stairway to Heaven", "Led Zeppelin", 482, "Rock", 1971);
        Track t3 = new Track("Hotel California", "Eagles", 390, "Rock", 1976);

        tracks.add(t1);
        tracks.add(t2);
        tracks.add(t3);

    }
}