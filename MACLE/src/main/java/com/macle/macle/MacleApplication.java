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
        Scene scene = new Scene(root);
        stage.setTitle("Macle Application");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
