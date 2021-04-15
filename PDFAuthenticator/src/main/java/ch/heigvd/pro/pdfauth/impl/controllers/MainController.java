package ch.heigvd.pro.pdfauth.impl.controllers;

import ch.heigvd.pro.pdfauth.impl.pdf.Field;
import ch.heigvd.pro.pdfauth.impl.pdf.PDFieldsExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML
    private TableView<Field> fieldsList;
    @FXML
    private TextField filePath;

    private List<Field> fields;

    public void openPDF(ActionEvent actionEvent) throws IOException {

        File pdf = openFileExplorer();

        if (pdf != null) {
            filePath.setText(pdf.getPath());
            PDFieldsExtractor.extractFields(pdf);
            fields = PDFieldsExtractor.extractFields(pdf);
            populateTableView(fields);
        }
    }

    private File openFileExplorer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        return fileChooser.showOpenDialog(filePath.getScene().getWindow());
    }

    private void populateTableView(List<Field> fields) {

        ObservableList<Field> data = FXCollections.observableArrayList();

        // Si les colonnes ont déjà été créées il faut seulement refresh la table
        if (!fieldsList.getColumns().isEmpty()) {
            fieldsList.setItems(data);
            data.addAll(fields);
            fieldsList.refresh();
        }
        else { // Sinon si la table est vide, il faut créer les colonnes et les remplir

            TableColumn<Field, String> fieldCol = new TableColumn<>("Champ");
            fieldCol.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
            fieldCol.setReorderable(false);

            TableColumn<Field, String> valueCol = new TableColumn<>("Valeur");
            valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
            valueCol.setReorderable(false);

            TableColumn<Field, CheckBox> protectedCol = new TableColumn<>("À protéger");
            protectedCol.setCellValueFactory(new PropertyValueFactory<>("isProtected"));
            protectedCol.setReorderable(false);
            protectedCol.setStyle("-fx-alignment: CENTER;");

            data = FXCollections.observableArrayList();

            data.addAll(fields);
            fieldsList.setItems(data);

            fieldsList.getColumns().addAll(fieldCol, valueCol, protectedCol);
        }
    }

    public void sendData(ActionEvent actionEvent) {

        System.out.println(fields);
    }
}