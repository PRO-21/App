package ch.heigvd.pro.pdfauth.impl.controllers;


import ch.heigvd.pro.pdfauth.impl.APIConnectionHandler;
import ch.heigvd.pro.pdfauth.impl.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;


public class LoginController {

    @FXML
    private TextField email;
    @FXML
    private Hyperlink link;
    @FXML
    private Button login;
    @FXML
    private PasswordField password;

    public void userLogIn(ActionEvent actionEvent) throws IOException {
        checkLogin();
    }

    private void checkLogin() throws IOException {

        App a = new App();

        HttpURLConnection conn = APIConnectionHandler.getConnection();

        String jsonInputString = "{\"auth_type\": \"credentials\"," +
                                  "\"email\": \"" + email.getText() + "\"," +
                                  "\"password\": \"" + password.getText() + "\"}";

        APIConnectionHandler.sendToAPI(conn, jsonInputString);
        String response = APIConnectionHandler.recvFromAPI(conn);

        JSONObject obj = new JSONObject(response);
        int HttpCode = obj.getJSONObject("status").getInt("code");

        // Si la requête est valide
        if (HttpCode == 200) {

            String token = obj.getJSONObject("data").getString("token");

            PrintWriter printWriter = new PrintWriter(new FileWriter("src/main/resources/ch/heigvd/pro/pdfauth/impl/token"));
            printWriter.print(token);
            printWriter.close();

            // Accès à la fenêtre principale
            a.changeScene("main.fxml");
        }
        else { // Sinon affiche une erreur et demande de retaper l'email et le mot de passe
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText("Veuillez réessayer");
            String cause = obj.getJSONObject("status").getString("message");
            alert.setContentText(cause);
            alert.showAndWait();
            email.requestFocus();
            password.clear();
        }
    }

    public void userCreateAccount(ActionEvent actionEvent) {
        App a = new App();

        // TODO : Rediriger sur la page de création de compte du site web
        a.getHostServices().showDocument("https://www.google.ch");
    }
}
