package it.unisa.player.engine;

//  Stato Concreto Playing.
// Gestisce le transizioni verso PausedState e StoppedState.
public class PlayingState implements PlayerState {
    private final PlaybackEngine engine;

    // Costruttore che accetta il motore
    public PlayingState(PlaybackEngine engine) {
        this.engine = engine;
    }

    @Override 
    public void play(PlaybackEngine engine) {
        // Già in riproduzione, non fa nulla
    }
    
    // Transizione verso Paused: Permette di congelare la riproduzione del brano
    @Override 
    public void pause(PlaybackEngine engine) { 
        engine.setState(new PausedState(engine)); 
    }
    
    // Transizione verso Stopped: Permette l'interruzione definitiva del brano in riproduzione
    @Override 
    public void stop(PlaybackEngine engine) { 
        engine.setState(new StoppedState(engine)); 
    }
}
