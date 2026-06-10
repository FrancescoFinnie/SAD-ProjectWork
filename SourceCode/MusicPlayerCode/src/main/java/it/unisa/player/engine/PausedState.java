package it.unisa.player.engine;

// Stato Concreto Paused.
// Gestisce le transizioni verso PlayingState e StoppedState.
public class PausedState implements PlayerState {
    private final PlaybackEngine engine;

    // Costruttore che memorizza il riferimento al motore
    public PausedState(PlaybackEngine engine) {
        this.engine = engine;
    }

    // Transizione verso Playing: Permette di riprendere la riproduzione del brano congelato
    @Override 
    public void play(PlaybackEngine engine) { 
        engine.setState(new PlayingState(engine)); 
    }
    
    @Override 
    public void pause(PlaybackEngine engine) {
        // Già in pausa, non fa nulla
    }
    
    // Transizione verso lo Stop: Permette l'interruzione definitiva del brano congelato
    @Override 
    public void stop(PlaybackEngine engine) { 
        engine.setState(new StoppedState(engine)); 
    }
}