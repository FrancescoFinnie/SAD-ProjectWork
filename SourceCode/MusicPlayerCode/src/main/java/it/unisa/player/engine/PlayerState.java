package it.unisa.player.engine;

// Interfaccia State Pattern.
// Definisce il contratto per le transizioni di stato del player.
public interface PlayerState {
    void play(PlaybackEngine engine);
    void pause(PlaybackEngine engine);
    void stop(PlaybackEngine engine);
}
