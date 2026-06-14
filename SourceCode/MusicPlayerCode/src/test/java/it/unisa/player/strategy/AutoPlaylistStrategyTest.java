package it.unisa.player.strategy;

import it.unisa.player.model.Library;
import it.unisa.player.model.Playlist;
import it.unisa.player.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test automatizzati per la validazione delle strategie di filtraggio (Task 27.6 e 28.5).
 */
public class AutoPlaylistStrategyTest {

    private Library testLibrary;
    private Track t1, t2, t3, t4;

    @BeforeEach
    public void setUp() {
        testLibrary = new Library();
        
        t1 = new Track("Brano A", "Autore 1", 180, "Rock", 1990);
        t1.addTag("favourite");
        
        t2 = new Track("Brano B", "Autore 2", 200, "Pop", 2020);
        t2.addTag("new");
        
        t3 = new Track("Brano C", "Autore 3", 150, "Rock", 2020);
        t3.addTag("favourite");
        
        t4 = new Track("Brano D", "Autore 4", 210, "Jazz", 1985);
        
        testLibrary.addTrack(t1);
        testLibrary.addTrack(t2);
        testLibrary.addTrack(t3);
        testLibrary.addTrack(t4);
    }

    @Test
    public void testGenreFilterStrategySuccess() {
        AutoPlaylistGenerator generator = new AutoPlaylistGenerator();
        generator.setStrategy(new GenreFilterStrategy("Rock"));
        
        Playlist result = generator.generate(testLibrary);
        
        assertEquals(2, result.getTracks().size(), "La playlist deve contenere 2 brani Rock.");
        assertTrue(result.getTracks().contains(t1));
        assertTrue(result.getTracks().contains(t3));
    }

    @Test
    public void testYearFilterStrategySuccess() {
        AutoPlaylistGenerator generator = new AutoPlaylistGenerator();
        generator.setStrategy(new YearFilterStrategy(2020));
        
        Playlist result = generator.generate(testLibrary);
        
        assertEquals(2, result.getTracks().size(), "La playlist deve contenere 2 brani del 2020.");
        assertTrue(result.getTracks().contains(t2));
        assertTrue(result.getTracks().contains(t3));
    }

    @Test
    public void testTagFilterStrategySuccess() {
        AutoPlaylistGenerator generator = new AutoPlaylistGenerator();
        generator.setStrategy(new TagFilterStrategy("favourite"));
        
        Playlist result = generator.generate(testLibrary);
        
        assertEquals(2, result.getTracks().size(), "La playlist deve contenere 2 brani con tag 'favourite'.");
        assertTrue(result.getTracks().contains(t1));
        assertTrue(result.getTracks().contains(t3));
        assertFalse(result.getTracks().contains(t2));
    }

    @Test
    public void testGeneratorThrowsExceptionWhenNoStrategySet() {
        AutoPlaylistGenerator generator = new AutoPlaylistGenerator();
        
        assertThrows(IllegalStateException.class, () -> {
            generator.generate(testLibrary);
        }, "Deve lanciare IllegalStateException se la strategia è null.");
    }
}