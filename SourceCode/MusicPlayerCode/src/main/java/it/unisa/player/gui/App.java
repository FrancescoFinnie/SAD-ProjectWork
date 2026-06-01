package it.unisa.player.gui;

import it.unisa.player.model.Library;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Questa classe è il punto di partenza dell'intera applicazione.
 * Estende 'Application' di JavaFX, il che la rende responsabile del ciclo di vita 
 * dell'interfaccia grafica.
 */

public class App extends Application {

    /** 
     * Riceve dal sistema la finestra principale vuota che dovremo riempire con i nostri elementi grafici.*/
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Istanziamo la libreria musicale.
        Library library = new Library();

        // Carichiamo dei brani fittizi per evitare che l'applicazione si apra completamente vuota
        library.loadSampleData();

        //Caricamento del file FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/player/gui/LibraryView.fxml"));
        Parent root = loader.load();

        //Recuperiamo il controller associato alla view e gli passiamo le dipendenze necessarie
        LibraryController controller = loader.getController();
        if (controller != null) {
            controller.setDependencies(library, primaryStage);
        }

        //Configurazione della finestra grafica
        primaryStage.setTitle("Music Player - Sprint 1");
        primaryStage.setScene(new Scene(root, 400, 700));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    //il metodo main() fa partire internamente il thread grafico di JavaFX e chiama il metodo start()
    public static void main(String[] args) {
        launch(args);
    }
}