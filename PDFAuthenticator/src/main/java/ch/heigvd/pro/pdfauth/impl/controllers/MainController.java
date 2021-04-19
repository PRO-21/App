package ch.heigvd.pro.pdfauth.impl.controllers;

import ch.heigvd.pro.pdfauth.impl.pdf.Field;
import ch.heigvd.pro.pdfauth.impl.pdf.PDFieldsExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Classe permettant de récupérer les valeurs des champs selon les events qui se passe sur la fenêtre principale
public class MainController {

    @FXML
    private Label protectedBy;
    @FXML
    private TableView<Field> fieldsList;
    @FXML
    private TextField filePath;

    private List<Field> fields;

    /**
     * Fonction appelée lors de l'appui sur le bouton "Ouvrir" sur la fenêtre. Elle s'occupe d'ouvrir l'explorateur de
     * fichiers et le PDF puis d'appeler la fonction populateTableView afin d'afficher les champs extraits
     * @param actionEvent -
     */
    public void openPDF(ActionEvent actionEvent) {

        File pdf = openFileExplorer();

        // Si le PDF n'est pas null
        if (pdf != null) {

            // Affichage du chemin absolu du PDF dans le TextField
            filePath.setText(pdf.getPath());

            try {
                // Extraction des champs
                fields = PDFieldsExtractor.extractFields(pdf);
            }
            catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
            populateTableView(fields);
        }
    }

    /**
     * Fonction permettant d'ouvrir l'explorateur de fichiers afin de sélectionner un PDF
     * @return PDF sélectionné
     */
    private File openFileExplorer() {
        FileChooser fileChooser = new FileChooser();

        // Personnalisation de l'explorateur (définition du titre, filtre d'extensions de fichiers)
        fileChooser.setTitle("Sélectionner un fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        // Affichage de l'explorateur
        return fileChooser.showOpenDialog(filePath.getScene().getWindow());
    }

    /**
     * Fonction permettant de remplir la TableView des champs extraits du formulaire PDF
     * @param fields liste des champs à afficher
     */
    private void populateTableView(List<Field> fields) {

        // Liste permettant de faire le lien entre la classe Field et les colonnes à afficher
        ObservableList<Field> data = FXCollections.observableArrayList();
        fieldsList.setItems(data);

        // Si les colonnes ont déjà été créées il faut seulement remettre les données dans l'ObservableList
        if (!fieldsList.getColumns().isEmpty()) {
            data.addAll(fields);
        }
        else { // Sinon si la table est vide, il faut créer les colonnes et les remplir

            TableColumn<Field, String> fieldCol = new TableColumn<>("Champ");
            fieldCol.setCellValueFactory(new PropertyValueFactory<>("fieldName")); // Lien entre la classe Field
            fieldCol.setReorderable(false);                                           // et la colonne
            fieldCol.setPrefWidth(140);

            TableColumn<Field, String> valueCol = new TableColumn<>("Valeur");
            valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
            valueCol.setReorderable(false);
            valueCol.setPrefWidth(100);

            TableColumn<Field, CheckBox> protectedCol = new TableColumn<>("À protéger");
            protectedCol.setCellValueFactory(new PropertyValueFactory<>("isProtected"));
            protectedCol.setReorderable(false);
            protectedCol.setStyle("-fx-alignment: CENTER;");
            protectedCol.setMinWidth(50);

            // Ajout des données dans l'ObservableList
            data.addAll(fields);

            // Ajout des colonnes à la TableView
            fieldsList.getColumns().addAll(fieldCol, valueCol, protectedCol);
        }
    }

    /**
     * Fonction appelé lors de l'appui du bouton "Valider" sur la fenêtre. Elle va envoyer les champs à protéger à l'API
     * et récupérer la réponse de celle-ci
     * @param actionEvent -
     */
    public void sendData(ActionEvent actionEvent) {

        List<Field> fieldsToProtect = keepOnlyFieldsToProtect();
        System.out.println(fieldsToProtect);
    }

    public List<Field> keepOnlyFieldsToProtect() {

        List<Field> fieldsToProtect = new ArrayList<>();

        for (Field field : fields) {

            if (field.getIsProtected().isSelected())
                fieldsToProtect.add(field);
        }

        return fieldsToProtect;
    }
}