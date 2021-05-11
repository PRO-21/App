package ch.heigvd.pro.pdfauth.impl;

import ch.heigvd.pro.pdfauth.impl.api.APIConnectionHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

// Classe principale permettant de lancer l'application
public class App extends Application {

    public static Stage stg;

    public static void main(String[] args) {
        launch();
    }

    /**
     * Fonction permettant de lancer l'application et de charger les fenêtres
     * @param stage Fenêtre à afficher
     */
    @Override
    public void start(Stage stage) {

        stg = stage;
        Parent root;

        try {

            if (APIConnectionHandler.tokenExistsAndIsValid("./token")) {
                root = FXMLLoader.load(getClass().getResource("main.fxml"));
            }
            else { // Si le token n'est pas/plus valide, chargement de la fenêtre de login
                root = FXMLLoader.load(getClass().getResource("login.fxml"));
            }

            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("pdf_icon.png")));
            stage.setTitle("PDF Authenticator");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Fonction permettant de changer de fenêtre
     * @param fxml fichier possédant les caractérisiques de la fenêtre à charger
     */
    public void changeScene(String fxml) {

        try {
            Parent pane = FXMLLoader.load(getClass().getResource(fxml));
            stg.setScene(new Scene(pane));
        }
        catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
}