package it.unisa.player.model;

import java.util.ArrayList;
import java.util.List;

import java.time.Year;

//Tutti i campi della Traccia
public class Track {

    //Costanti esplicite per evitare Magic Numbers
    public static final int MIN_DURATION = 0;

    private String title;
    private String author;
    private int duration; //espressa in secondi
    private String genre;
    private int releaseYear;
    private List<String> tags;

  /**  Costruttore della classe Track. 
     * I parametri vengono validati tramite i rispettivi metodi setter.
     */
    public Track(String title, String author, int duration, String genre, int releaseYear) {
        this.tags = new ArrayList<>();
        // Deleghiamo l'assegnamento ai setter per centralizzare la logica di validazione
        setTitle(title);
        setAuthor(author);
        setDuration(duration);
        setGenre(genre);
        setReleaseYear(releaseYear);
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


    /** Imposta il titolo della traccia.
     * @param title Il titolo della traccia.
     * @throws IllegalArgumentException se il titolo è nullo o vuoto.
     */
    public void setTitle(String title) throws IllegalArgumentException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere nullo o vuoto.");
        }
        this.title = title.trim();
    }

    /**
     * Imposta l'autore della traccia.
     * @param author L'autore della traccia.
     * @throws IllegalArgumentException se l'autore è nullo o vuoto.
     */
    public void setAuthor(String author) throws IllegalArgumentException {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("L'autore non può essere nullo o vuoto.");
        }
        this.author = author.trim();
    }

    /**
     * Imposta la durata in secondi della traccia.
     * @param duration Durata totale in secondi.
     * @throws IllegalArgumentException se la durata è strettamente minore di MIN_DURATION.
     */
    public void setDuration(int duration) throws IllegalArgumentException {
        if (duration < MIN_DURATION) {
            throw new IllegalArgumentException("La durata non può essere negativa.");
        }
        this.duration = duration;
    }

    /**
     * Imposta il genere musicale.
     * @param genre Il genere musicale.
     * @throws IllegalArgumentException se il genere è nullo o vuoto.
     */
    public void setGenre(String genre) throws IllegalArgumentException {
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Il genere non può essere nullo o vuoto.");
        }
        this.genre = genre.trim();
    }

    /**
     * Imposta l'anno di rilascio. Calcola dinamicamente l'anno corrente per la validazione.
     * @param releaseYear Anno di pubblicazione.
     * @throws IllegalArgumentException se l'anno specificato è nel futuro.
     */
    public void setReleaseYear(int releaseYear) throws IllegalArgumentException {
        int currentYear = Year.now().getValue(); // Validazione dinamica basata sul sistema
        if (releaseYear > currentYear) {
            throw new IllegalArgumentException("L'anno di pubblicazione non può essere nel futuro (massimo consentito: " + currentYear + ").");
        }
        this.releaseYear = releaseYear;
    }
}