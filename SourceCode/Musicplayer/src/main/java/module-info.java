module it.unisa {
    requires javafx.controls;
    requires javafx.fxml;

    opens it.unisa to javafx.fxml;
    exports it.unisa;
}
