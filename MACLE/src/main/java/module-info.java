module com.macle.macle {
    // Bibliothèques JavaFX nécessaires
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;    // Pour l'export PNG (SwingFXUtils)

    // Bibliothèque pour le JSON
    requires com.google.gson; // Pour l'export/import JSON

    // Autorise JavaFX et GSON à accéder à vos classes
    opens com.macle.macle to javafx.fxml, com.google.gson;

    exports com.macle.macle;
}