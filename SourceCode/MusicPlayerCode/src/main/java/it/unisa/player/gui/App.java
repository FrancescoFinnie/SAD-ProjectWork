package it.unisa.player.gui;

import it.unisa.player.command.CommandManager;
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
        // Istanziamo il CommandManager globale che gestirà tutte le operazioni di Undo/Redo.
        CommandManager commandManager = new CommandManager();

        // Carichiamo dei brani fittizi per evitare che l'applicazione si apra completamente vuota
        library.loadSampleData();

        //Caricamento del file FXML
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(ViewConstants.MAIN_LAYOUT));
        Parent root = mainLoader.load();

        MainController mainController = mainLoader.getController();
        mainController.setLibrary(library);

        // 2. Carica la vista iniziale (Libreria)
        FXMLLoader libLoader = new FXMLLoader(getClass().getResource(ViewConstants.LIBRARY_VIEW));
        Parent libraryView = libLoader.load();

        // 3. Inietta il mainController al posto dello Stage
        LibraryController libController = libLoader.getController();
        if (libController != null) {
            libController.setDependencies(library, mainController, commandManager);
        }

        // 4. Incastra la libreria al centro del Layout Globale
        mainController.setCenterView(libraryView);

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