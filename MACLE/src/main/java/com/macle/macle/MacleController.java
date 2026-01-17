package com.macle.macle;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.awt.*;

public class MacleController {
    // Liaison avec le Front-End
    @FXML private TextField animeNameField;
    @FXML private Spinner<Integer> positionSpinner;
    @FXML private CheckBox dehorsCheckBox;

    // Instance de votre logique Back-End
    private Classement monClassement;

    // Cette méthode s'exécute automatiquement au chargement
    @FXML
    public void initialize() {
        // Initialisation de la logique (ex: 20 places)
        monClassement = new Classement(20);

        // Configuration du Spinner (min, max, valeur initiale)
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
        positionSpinner.setValueFactory(valueFactory);
    }

    @FXML
    protected void onAddAnime() {
        // 1. Récupérer les données des composants graphiques
        String nom = animeNameField.getText();
        int position = positionSpinner.getValue();
        boolean estDehors = dehorsCheckBox.isSelected();

        // 2. Appeler la logique Back-End
        if (nom != null && !nom.isEmpty()) {
            if (estDehors) {
                monClassement.add_out(nom); // Utilise la méthode de votre classe Classement
            } else {
                monClassement.add_top(nom, position); // Logique de décalage et ajout
            }

            // 3. Optionnel : Nettoyer le champ après l'ajout
            animeNameField.setText("");
            System.out.println("Ajouté : " + nom);
        }
    }
}