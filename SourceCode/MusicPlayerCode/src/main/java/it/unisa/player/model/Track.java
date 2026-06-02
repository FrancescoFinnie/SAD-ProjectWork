package it.unisa.player.model;

import java.util.ArrayList;
import java.util.List;

//Tutti i campi della Traccia
public class Track {
    private String title;
    private String author;
    private int duration; //espressa in secondi
    private String genre;
    private int releaseYear;
    private List<String> tags;

    // Costruttore della traccia
    public Track(String title, String author, int duration, String genre, int releaseYear) {
        this.title = title;
        this.author = author;
        this.duration = duration;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.tags = new ArrayList<>();
    }

    // Getters di ogni campo
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getDuration() { return duration; }
    public String getGenre() { return genre; }
    public int getReleaseYear() { return releaseYear; }

    // controllo del tag (se il tag è già presente non è possibile inserirlo di nuovo) per evitare tag duplicati
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }


    public List<String> getTags() { return tags; }

    // Setters di ogni campo
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
}