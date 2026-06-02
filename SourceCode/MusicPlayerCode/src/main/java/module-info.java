module musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens it.unisa.player.gui to javafx.fxml;

    exports it.unisa.player.gui;
    exports it.unisa.player.model;
}