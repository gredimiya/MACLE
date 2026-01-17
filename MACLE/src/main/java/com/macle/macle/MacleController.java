package com.macle.macle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;

public class MacleController {
    // Liaison avec le Front-End
    @FXML private TextField animeNameField;
    @FXML private Spinner<Integer> positionSpinner;
    @FXML private CheckBox dehorsCheckBox;
    @FXML private GridPane topGrid;
    @FXML private FlowPane outFlowPane;
    @FXML private Label mainTitleLabel, subTitleLabel;
    @FXML private TextField mainTitleField, subTitleField;
    @FXML private ChoiceBox<Integer> nbPlaceChoiceBox;
    @FXML private VBox mainContainer;

    private Classement monClassement;

    @FXML
    public void initialize() {
        monClassement = new Classement(15);
        // Configuration du spinner (1 à 20)
        positionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 1));
        nbPlaceChoiceBox.getItems().addAll(5, 10, 15, 20);
        nbPlaceChoiceBox.setValue(15);
        nbPlaceChoiceBox.setOnAction(e -> onChangeNbPlace());
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
        remplirGrille(topGrid, monClassement.top); // Garde la grille pour le Top
        remplirFlowPane(outFlowPane, monClassement.out); // Nouvelle méthode pour le Out
    }

    private void remplirFlowPane(FlowPane pane, ObservableList<String> liste) {
        pane.getChildren().clear();

        for (int i = 0; i < liste.size(); i++) {
            String nom = liste.get(i);
            if (nom != null && !nom.isEmpty()) {
                // Création du texte avec virgule
                String texteAffiché = nom + (i < liste.size() - 1 ? ", " : "");
                Label label = new Label(texteAffiché);

                label.getStyleClass().add("anime-name-label");
                label.setCursor(javafx.scene.Cursor.HAND); // Curseur cliquable

                // Événement de clic pour les éléments "Dehors"
                final int index = i;
                label.setOnMouseClicked(event -> {
                    // On précise 'true' car l'élément vient de la liste Dehors
                    demanderNouvellePosition(nom, index, true);
                });

                pane.getChildren().add(label);
            }
        }
    }

    private void remplirGrille(GridPane grille, ObservableList<String> liste) {
        grille.getChildren().clear();
        grille.setAlignment(Pos.CENTER);

        // Espacement important pour un rendu "Top" professionnel
        grille.setHgap(60.0);
        grille.setVgap(15.0);

        int row = 0;
        int col = 0;

        for (int i = 0; i < liste.size(); i++) {
            String nom = liste.get(i);
            final int indexActuel = i;

            // On affiche toujours le numéro (ex: 01., 02.)
            String prefixe = String.format("%02d. ", i + 1);

            // Si la place est vide, on affiche des pointillés ou on laisse vide après le numéro
            String texteAffiché = prefixe + (nom.isEmpty() ? "........" : nom.toUpperCase());

            Label label = new Label(texteAffiché);
            label.getStyleClass().add("anime-name-label");

            // On garde le clic même sur les places vides pour pouvoir y placer un anime
            label.setCursor(javafx.scene.Cursor.HAND);
            label.setOnMouseClicked(event -> demanderNouvellePosition(nom, indexActuel, false));

            grille.add(label, col, row);

            row++;
            // Passage à la colonne suivante après 5 noms (votre contrainte)
            if (row >= 5) {
                row = 0;
                col++;
            }
        }
    }

    private void demanderNouvellePosition(String nomAnime, int indexAncien, boolean etaitDehors) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Déplacer l'anime");
        dialog.setHeaderText("Déplacement de : " + nomAnime);
        dialog.setContentText("Nouvelle position (1-" + monClassement.nb_place + ") ou 'D' pour rester Dehors :");

        dialog.showAndWait().ifPresent(reponse -> {
            try {
                if (reponse.equalsIgnoreCase("D")) {
                    // Si déjà dehors, on ne fait rien, sinon on le sort
                    if (!etaitDehors) {
                        monClassement.top.set(indexAncien, "");
                        monClassement.add_out(nomAnime);
                    }
                } else {
                    int nouvellePos = Integer.parseInt(reponse);
                    if (nouvellePos >= 1 && nouvellePos <= monClassement.nb_place) {
                        // 1. On retire l'anime de son ancien emplacement
                        if (etaitDehors) {
                            monClassement.out.remove(indexAncien); // Retrait de la liste 'out'
                        } else {
                            monClassement.top.set(indexAncien, ""); // Vide la place dans le 'top'
                        }

                        // 2. On l'ajoute à sa nouvelle position (la logique add_top gère le décalage)
                        monClassement.add_top(nomAnime, nouvellePos);
                    }
                }
                // Mise à jour visuelle globale
                rafraichirToutesLesGrilles();
            } catch (NumberFormatException e) {
                // Ignorer si l'entrée n'est pas un nombre valide
            }
        });
    }

    // OPTION 1 : Mise à jour des titres en temps réel
    @FXML
    private void onUpdateTitles() {
        mainTitleLabel.setText(mainTitleField.getText().isEmpty() ? "TITRE PAR DEFAULT1" : mainTitleField.getText());
        subTitleLabel.setText(subTitleField.getText().isEmpty() ? "SousTitre par défault" : subTitleField.getText());
    }

    // OPTION 2 : Changer la taille du classement
    private void onChangeNbPlace() {
        int nouvelleTaille = nbPlaceChoiceBox.getValue();

        // Appelle la méthode de redimensionnement pour gérer le transfert vers "Dehors"
        monClassement.resize(nouvelleTaille);

        // Met à jour le Spinner pour que l'utilisateur ne puisse plus choisir une position invalide
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, nouvelleTaille, 1);
        positionSpinner.setValueFactory(valueFactory);

        // Rafraîchit l'affichage pour voir les changements
        rafraichirToutesLesGrilles();
    }

    // OPTION 3 : Import d'image de fond
    @FXML
    private void onImportImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(mainTitleLabel.getScene().getWindow());

        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            mainContainer.setStyle("-fx-background-image: url('" + imagePath + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center;");
        }
    }

    @FXML
    private void onExportToPNG() {
        WritableImage image = mainContainer.snapshot(new SnapshotParameters(), null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(mainContainer.getScene().getWindow());

        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onExportToJson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
        File file = fileChooser.showSaveDialog(mainContainer.getScene().getWindow());

        if (file != null) {
            try (Writer writer = new FileWriter(file)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                // On sauvegarde un objet contenant le top et le out
                gson.toJson(monClassement, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onImportTop() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(mainContainer.getScene().getWindow());

        if (file != null) {
            try (Reader reader = new FileReader(file)) {
                Gson gson = new Gson();
                // 1. Lire vers la classe temporaire (types standards)
                ClassementSave importe = gson.fromJson(reader, ClassementSave.class);

                // 2. Mettre à jour manuellement votre classement JavaFX
                this.monClassement.top.setAll(importe.top);
                this.monClassement.out.setAll(importe.out);

                rafraichirToutesLesGrilles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ClassementSave {
        int nb_place;
        ArrayList<String> top;
        ArrayList<String> out;
    }

}