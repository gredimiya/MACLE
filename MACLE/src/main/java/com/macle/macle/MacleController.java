package com.macle.macle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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
    // --- Liaison avec le Front-End (Général) ---
    @FXML private TextField animeNameField;
    @FXML private Spinner<Integer> positionSpinner;
    @FXML private CheckBox dehorsCheckBox;
    @FXML private GridPane topGrid;
    @FXML private FlowPane outFlowPane;
    @FXML private Label mainTitleLabel, subTitleLabel;
    @FXML private TextField mainTitleField, subTitleField;
    @FXML private ChoiceBox<Integer> nbPlaceChoiceBox;
    @FXML private VBox mainContainer;

    // --- Options de personnalisation : TITRE ---
    @FXML private Slider titleFontSizeSlider;
    @FXML private ColorPicker titleColorPicker;
    @FXML private CheckBox titleOutlineCheckBox;

    // --- Options de personnalisation : SOUS-TITRE ---
    @FXML private Slider subFontSizeSlider;
    @FXML private ColorPicker subColorPicker;
    @FXML private CheckBox subOutlineCheckBox;

    // --- Options de personnalisation : TOP ---
    @FXML private Slider topFontSizeSlider;
    @FXML private ColorPicker topColorPicker;
    @FXML private CheckBox topOutlineCheckBox;
    @FXML private Slider columnGapSlider;

    // --- Options de personnalisation : DEHORS ---
    @FXML private Slider outFontSizeSlider;
    @FXML private ColorPicker outColorPicker;
    @FXML private CheckBox outOutlineCheckBox;

    // --- Check box pour le gras
    @FXML private CheckBox titleBoldCheckBox;
    @FXML private CheckBox subBoldCheckBox;
    @FXML private CheckBox topBoldCheckBox;
    @FXML private CheckBox outBoldCheckBox;

    @FXML private Slider backgroundOpacitySlider;

    private Classement monClassement;

    @FXML
    public void initialize() {
        // 1. Initialisation du classement (par défaut Top 15)
        monClassement = new Classement(15);

        // 2. Configuration du Spinner (Position)
        // On le rend éditable pour pouvoir taper le numéro directement
        positionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 1));
        positionSpinner.setEditable(true);

        // Filtre pour n'accepter que des chiffres dans l'éditeur du Spinner
        positionSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                positionSpinner.getEditor().setText(oldValue);
            }
        });

        backgroundOpacitySlider.valueProperty().addListener((obs, oldVal, newVal) -> rafraichirToutesLesGrilles());

        if (columnGapSlider != null) {
            columnGapSlider.valueProperty().addListener((obs, oldVal, newVal) -> rafraichirToutesLesGrilles());
        }

        // 3. Configuration du ChoiceBox (Nombre de places)
        nbPlaceChoiceBox.getItems().clear();
        nbPlaceChoiceBox.getItems().addAll(5, 10, 15, 20);
        nbPlaceChoiceBox.setValue(15);
        nbPlaceChoiceBox.setOnAction(e -> onChangeNbPlace());

        // 4. Initialisation des couleurs par défaut (Blanc pour tous)
        titleColorPicker.setValue(javafx.scene.paint.Color.WHITE);
        subColorPicker.setValue(javafx.scene.paint.Color.WHITE);
        topColorPicker.setValue(javafx.scene.paint.Color.WHITE);
        outColorPicker.setValue(javafx.scene.paint.Color.WHITE);

        // 5. Ajout de listeners sur les Sliders pour rafraîchir en temps réel lors du glissement
        titleFontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> rafraichirToutesLesGrilles());
        subFontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> rafraichirToutesLesGrilles());
        topFontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> rafraichirToutesLesGrilles());
        outFontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> rafraichirToutesLesGrilles());

        // 6. Premier affichage de la grille vide
        rafraichirToutesLesGrilles();
    }

    @FXML
    protected void onAddAnime() {
        positionSpinner.increment(0);
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

    @FXML
    protected void rafraichirToutesLesGrilles() {
        if (mainTitleLabel == null) return;

        // 1. Style Titre
        mainTitleLabel.setStyle(genererStyle(titleFontSizeSlider.getValue(),
                titleColorPicker.getValue(),
                titleOutlineCheckBox.isSelected(),
                titleBoldCheckBox.isSelected(), false));

        // 2. Style Sous-Titre
        subTitleLabel.setStyle(genererStyle(subFontSizeSlider.getValue(),
                subColorPicker.getValue(),
                subOutlineCheckBox.isSelected(),
                subBoldCheckBox.isSelected(), false));

        if (mainContainer != null && backgroundOpacitySlider != null) {
            // On récupère le chemin de l'image stocké (ou actuel)
            // Note : Il est préférable de stocker l'URL de l'image dans une variable de classe
            // pour la retrouver ici facilement.
            String currentStyle = mainContainer.getStyle();

            // Calcul de l'opacité du "voile" noir (0 = transparent, 1 = noir total)
            // On inverse la valeur du slider pour que 1 = image claire et 0 = image sombre/invisible
            double opacity = 1.0 - backgroundOpacitySlider.getValue();
            String rgba = "rgba(50, 50, 50, " + opacity + ")"; // #323232 correspond à votre gris d'origine

            // On applique l'image ET le voile de couleur par-dessus
            // L'ordre dans -fx-background-color et -fx-background-image permet la superposition
            mainContainer.setStyle(currentStyle +
                    "; -fx-background-color: " + rgba + ", linear-gradient(to bottom, transparent, transparent);");
        }
        // 4. Rafraîchir les listes
        remplirGrille(topGrid, monClassement.top);
        remplirFlowPane(outFlowPane, monClassement.out);
    }

    // Fonction utilitaire pour générer la chaîne CSS
    private String genererStyle(double size, javafx.scene.paint.Color color, boolean outline, boolean isBold, boolean isTop) {
        StringBuilder style = new StringBuilder();
        style.append("-fx-font-size: ").append(size).append("px; ");
        style.append("-fx-text-fill: ").append(toHexString(color)).append("; ");

        if (isTop) {
            style.append("-fx-font-family: 'Arial Black'; ");
        }

        if (isBold) {
            style.append("-fx-font-weight: bold; ");
        } else {
            style.append("-fx-font-weight: normal; ");
        }

        if (outline) {
            style.append("-fx-effect: dropshadow(three-pass-box, black, 2, 1, 0, 0); ");
        }

        return style.toString();
    }

    private void remplirFlowPane(FlowPane pane, ObservableList<String> liste) {
        pane.getChildren().clear();

        double fontSize = outFontSizeSlider.getValue();
        javafx.scene.paint.Color color = outColorPicker.getValue();
        boolean outline = outOutlineCheckBox.isSelected();
        boolean bold = outBoldCheckBox.isSelected();

        for (int i = 0; i < liste.size(); i++) {
            String nom = liste.get(i);
            if (nom != null && !nom.isEmpty()) {
                Label label = new Label(nom + (i < liste.size() - 1 ? ", " : ""));
                label.setStyle(genererStyle(fontSize, color, outline, bold, false));
                label.setCursor(javafx.scene.Cursor.HAND);

                final int index = i;
                label.setOnMouseClicked(event -> demanderNouvellePosition(nom, index, true));
                pane.getChildren().add(label);
            }
        }
    }

    private void remplirGrille(GridPane grille, ObservableList<String> liste) {
        // 1. Nettoyage et préparation de la grille
        grille.getChildren().clear();
        grille.getRowConstraints().clear();
        grille.setAlignment(Pos.CENTER);

        // 2. Récupération des paramètres de personnalisation
        double fontSize = topFontSizeSlider.getValue();
        javafx.scene.paint.Color color = topColorPicker.getValue();
        boolean outline = topOutlineCheckBox.isSelected();
        boolean bold = (topBoldCheckBox != null && topBoldCheckBox.isSelected());

        // Récupération de l'écartement via le nouveau slider
        double columnGap = columnGapSlider.getValue();

        // 3. Configuration des contraintes de lignes
        for (int i = 0; i < 5; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setPercentHeight(20);
            rc.setValignment(VPos.CENTER);
            grille.getRowConstraints().add(rc);
        }

        // 4. Application de l'écartement dynamique entre les colonnes
        grille.setHgap(columnGap);

        int row = 0;
        int col = 0;

        // 5. Génération des Labels
        for (int i = 0; i < liste.size(); i++) {
            String nom = liste.get(i);
            String prefixe = String.format("%02d. ", i + 1);
            String texteAffiché = prefixe + (nom.isEmpty() ? "........" : nom);

            Label label = new Label(texteAffiché);
            label.getStyleClass().add("anime-name-label");

            // Utilisation de genererStyle avec le paramètre isTop = true
            label.setStyle(genererStyle(fontSize, color, outline, bold, true));

            label.setMaxWidth(Double.MAX_VALUE);
            label.setCursor(javafx.scene.Cursor.HAND);

            final int indexActuel = i;
            label.setOnMouseClicked(event -> demanderNouvellePosition(nom, indexActuel, false));

            grille.add(label, col, row);

            row++;
            if (row >= 5) {
                row = 0;
                col++;
            }
        }
    }

    private String toHexString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    private void demanderNouvellePosition(String nomAnime, int indexAncien, boolean etaitDehors) {
        // 1. Création d'une boîte de dialogue personnalisée pour choisir l'action
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Options pour : " + nomAnime);
        alert.setHeaderText("Que souhaitez-vous faire avec cet anime ?");

        ButtonType btnRenommer = new ButtonType("Renommer");
        ButtonType btnDeplacer = new ButtonType("Déplacer / Sortir");
        ButtonType btnSupprimer = new ButtonType("Supprimer");
        ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnRenommer, btnDeplacer, btnSupprimer, btnAnnuler);

        alert.showAndWait().ifPresent(type -> {
            if (type == btnRenommer) {
                actionRenommer(nomAnime, indexAncien, etaitDehors);
            } else if (type == btnDeplacer) {
                actionDeplacer(nomAnime, indexAncien, etaitDehors);
            } else if (type == btnSupprimer) {
                actionSupprimer(indexAncien, etaitDehors);
            }
        });
    }

    // Action pour Renommer
    private void actionRenommer(String ancienNom, int index, boolean etaitDehors) {
        TextInputDialog dialog = new TextInputDialog(ancienNom);
        dialog.setTitle("Renommer");
        dialog.setHeaderText("Nouveau nom pour : " + ancienNom);
        dialog.setContentText("Nom :");

        dialog.showAndWait().ifPresent(nouveauNom -> {
            if (!nouveauNom.isEmpty()) {
                if (etaitDehors) {
                    monClassement.out.set(index, nouveauNom);
                } else {
                    monClassement.top.set(index, nouveauNom);
                }
                rafraichirToutesLesGrilles();
            }
        });
    }

    // Action pour Supprimer
    private void actionSupprimer(int index, boolean etaitDehors) {
        if (etaitDehors) {
            monClassement.out.remove(index);
        } else {
            monClassement.top.set(index, ""); // On vide la place dans le Top
        }
        rafraichirToutesLesGrilles();
    }

    // Logique de déplacement actuelle isolée
    private void actionDeplacer(String nomAnime, int indexAncien, boolean etaitDehors) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Déplacer");
        dialog.setHeaderText("Déplacement de : " + nomAnime);
        dialog.setContentText("Position (1-" + monClassement.nb_place + ") ou 'D' (Dehors) :");

        dialog.showAndWait().ifPresent(reponse -> {
            try {
                if (reponse.equalsIgnoreCase("D")) {
                    if (!etaitDehors) {
                        monClassement.top.set(indexAncien, "");
                        monClassement.add_out(nomAnime);
                    }
                } else {
                    int nouvellePos = Integer.parseInt(reponse);
                    if (nouvellePos >= 1 && nouvellePos <= monClassement.nb_place) {
                        if (etaitDehors) monClassement.out.remove(indexAncien);
                        else monClassement.top.set(indexAncien, "");

                        monClassement.add_top(nomAnime, nouvellePos);
                    }
                }
                rafraichirToutesLesGrilles();
            } catch (NumberFormatException e) { }
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
                ClassementSave save = new ClassementSave();

                // Données de base
                save.nb_place = monClassement.nb_place;
                save.top = new ArrayList<>(monClassement.top);
                save.out = new ArrayList<>(monClassement.out);
                save.mainTitle = mainTitleLabel.getText();
                save.subTitle = subTitleLabel.getText();

                // Sauvegarde des Tailles et Écart
                save.titleSize = titleFontSizeSlider.getValue();
                save.subSize = subFontSizeSlider.getValue();
                save.topSize = topFontSizeSlider.getValue();
                save.outSize = outFontSizeSlider.getValue();
                save.columnGap = columnGapSlider.getValue();

                // Sauvegarde des Couleurs (en format Hex)
                save.titleColor = toHexString(titleColorPicker.getValue());
                save.subColor = toHexString(subColorPicker.getValue());
                save.topColor = toHexString(topColorPicker.getValue());
                save.outColor = toHexString(outColorPicker.getValue());

                // Sauvegarde des Options (Contour et Gras)
                save.titleOutline = titleOutlineCheckBox.isSelected();
                save.subOutline = subOutlineCheckBox.isSelected();
                save.topOutline = topOutlineCheckBox.isSelected();
                save.outOutline = outOutlineCheckBox.isSelected();

                save.titleBold = titleBoldCheckBox.isSelected();
                save.subBold = subBoldCheckBox.isSelected();
                save.topBold = topBoldCheckBox.isSelected();
                save.outBold = outBoldCheckBox.isSelected();

                gson.toJson(save, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onImportTop() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(mainTitleLabel.getScene().getWindow());

        if (file != null) {
            try (Reader reader = new FileReader(file)) {
                Gson gson = new Gson();
                ClassementSave importe = gson.fromJson(reader, ClassementSave.class);

                // 1. Logique métier et titres
                this.monClassement.nb_place = importe.nb_place;
                this.monClassement.top.setAll(importe.top);
                this.monClassement.out.setAll(importe.out);
                nbPlaceChoiceBox.setValue(importe.nb_place);
                mainTitleField.setText(importe.mainTitle);
                subTitleField.setText(importe.subTitle);
                onUpdateTitles();

                // 2. Restauration des Styles (Sliders et Écart)
                titleFontSizeSlider.setValue(importe.titleSize);
                subFontSizeSlider.setValue(importe.subSize);
                topFontSizeSlider.setValue(importe.topSize);
                outFontSizeSlider.setValue(importe.outSize);
                columnGapSlider.setValue(importe.columnGap);

                // 3. Restauration des Couleurs
                titleColorPicker.setValue(javafx.scene.paint.Color.valueOf(importe.titleColor));
                subColorPicker.setValue(javafx.scene.paint.Color.valueOf(importe.subColor));
                topColorPicker.setValue(javafx.scene.paint.Color.valueOf(importe.topColor));
                outColorPicker.setValue(javafx.scene.paint.Color.valueOf(importe.outColor));

                // 4. Restauration des Options
                titleOutlineCheckBox.setSelected(importe.titleOutline);
                subOutlineCheckBox.setSelected(importe.subOutline);
                topOutlineCheckBox.setSelected(importe.topOutline);
                outOutlineCheckBox.setSelected(importe.outOutline);

                titleBoldCheckBox.setSelected(importe.titleBold);
                subBoldCheckBox.setSelected(importe.subBold);
                topBoldCheckBox.setSelected(importe.topBold);
                outBoldCheckBox.setSelected(importe.outBold);

                rafraichirToutesLesGrilles();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ClassementSave {
        int nb_place;
        ArrayList<String> top;
        ArrayList<String> out;
        String mainTitle;
        String subTitle;

        double titleSize, subSize, topSize, outSize, columnGap;
        String titleColor, subColor, topColor, outColor;
        boolean titleOutline, subOutline, topOutline, outOutline;
        boolean titleBold, subBold, topBold, outBold;
    }

}