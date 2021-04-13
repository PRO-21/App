package ch.heigvd.pro.pdfauth.impl.controllers;

import ch.heigvd.pro.pdfauth.impl.pdf.PDFieldsExtractor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML
    private TextField filePath;

    private File pdf;

    public void openPDF(ActionEvent actionEvent) throws IOException {

        pdf = openFileExplorer();
        if (pdf != null) {
            filePath.setText(pdf.getPath());
            PDFieldsExtractor.extractFields(pdf);
            List<String> fields = PDFieldsExtractor.extractFields(pdf);
        }
        
    }

    private File openFileExplorer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        return fileChooser.showOpenDialog(filePath.getScene().getWindow());
    }


}