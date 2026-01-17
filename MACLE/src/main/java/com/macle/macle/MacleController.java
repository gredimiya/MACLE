package com.macle.macle;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.ObservableList;

public class MacleController {
    // Liaison avec le Front-End
    @FXML private TextField animeNameField;
    @FXML private Spinner<Integer> positionSpinner;
    @FXML private CheckBox dehorsCheckBox;
    @FXML private GridPane topGrid;
    @FXML private GridPane outGrid;

    private Classement monClassement;

    @FXML
    public void initialize() {
        monClassement = new Classement(20);
        // Configuration du spinner (1 à 20)
        positionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
    }

    @FXML
    protected void onAddAnime() {
        String nom = animeNameField.getText();
        if (nom == null || nom.isEmpty()) return;

        if (dehorsCheckBox.isSelected()) {
            monClassement.add_out(nom);
        } else {
            monClassement.add_top(nom, positionSpinner.getValue());
        }

        animeNameField.clear();
        rafraichirToutesLesGrilles();
    }

    private void rafraichirToutesLesGrilles() {
        // Rafraîchir le Top
        remplirGrille(topGrid, monClassement.top);

        // Rafraîchir le Out
        remplirGrille(outGrid, monClassement.out);
    }

    private void remplirGrille(GridPane grille, ObservableList<String> liste) {
        grille.getChildren().clear();
        int row = 0;
        int col = 0;

        for (String nom : liste) {
            if (nom != null && !nom.isEmpty()) {
                Label label = new Label(nom);
                label.getStyleClass().add("anime-label"); // Optionnel : ajoutez une classe CSS

                grille.add(label, col, row);

                row++;
                if (row >= 10) { // On change de colonne toutes le 10 lignes
                    row = 0;
                    col++;
                }
            }
        }
    }
}