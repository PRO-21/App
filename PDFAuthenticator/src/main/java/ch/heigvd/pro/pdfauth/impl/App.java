package ch.heigvd.pro.pdfauth.impl;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage stg;

    @Override
    public void start(Stage stage) throws IOException {

        stg = stage;
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("pdf_icon.png")));
        stage.setTitle("PDF Authenticator");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        stg.setScene(new Scene(pane));
    }

    public static void main(String[] args) {
        launch();
    }
}