package com.macle.macle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MacleController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

}
