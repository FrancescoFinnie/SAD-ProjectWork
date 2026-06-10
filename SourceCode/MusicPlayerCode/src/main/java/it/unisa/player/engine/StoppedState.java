package it.unisa.player.engine;

// Stato Concreto Stopped.
// Gestisce la transizione verso PlayingState.
public class StoppedState implements PlayerState {
    private final PlaybackEngine engine;

    // Costruttore che accetta il motore
    public StoppedState(PlaybackEngine engine) {
        this.engine = engine;
    }

    // Transizione verso Playing: Permette di avviare la riproduzione del brano da fermo
    @Override 
    public void play(PlaybackEngine engine) { 
        engine.setState(new PlayingState(engine)); 
    }
    
    @Override 
    public void pause(PlaybackEngine engine) {
        // Da fermo non puoi andare in pausa, non fa nulla
    }
    
    @Override 
    public void stop(PlaybackEngine engine) { 
        // Già fermo, non fa nulla
    }
}

