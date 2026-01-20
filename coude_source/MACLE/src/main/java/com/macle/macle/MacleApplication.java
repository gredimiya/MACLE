package com.macle.macle;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
import javafx.application.Application;
import java.io.IOException;

public class MacleApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MacleApplication.class.getResource("macle-view.fxml"));
        Parent root = fxmlLoader.load();

        // On force une taille paysage (Largeur x Hauteur)
        Scene scene = new Scene(root, 1280, 720);

        scene.getStylesheets().add(getClass().getResource("/style/MainSceneStyle.css").toExternalForm());

        stage.setTitle("MACLE Ranking");
        stage.setScene(scene);

        // Optionnel : Empêcher la fenêtre d'être trop petite
        stage.setMinWidth(1000);
        stage.setMinHeight(600);

        stage.show();
    }
}
