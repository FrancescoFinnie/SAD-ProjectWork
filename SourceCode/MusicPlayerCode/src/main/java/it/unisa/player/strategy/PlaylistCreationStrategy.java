package it.unisa.player.strategy;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;

/**
 * Interfaccia del pattern Strategy per la generazione automatica di playlist.
 * Definisce il contratto per tutte le logiche algoritmiche di filtraggio.
 */
public interface PlaylistCreationStrategy {
    
    /**
     * Crea e popola una nuova playlist applicando lo specifico algoritmo di filtraggio.
     * @param library La libreria musicale globale da cui attingere i dati.
     * @return Una nuova Playlist contenente esclusivamente i brani che soddisfano il criterio.
     */
    Playlist createPlaylist(Library library);
}