package ch.heigvd.pro.pdfauth.impl.controllers;

import ch.heigvd.pro.pdfauth.impl.api.APIConnectionHandler;
import ch.heigvd.pro.pdfauth.impl.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;

// Classe permettant de récupérer les valeurs des champs selon les events qui se passe sur la fenêtre de connexion
public class LoginController {

    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    /**
     * Fonction appelée lorsque l'utilisateur appuie sur le bouton "Se connecter"
     * @param actionEvent -
     */
    public void userLogIn(ActionEvent actionEvent) {

        try {
            checkLogin();
        }
        catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Fonction permettant de vérifier les identifiants de l'utilisateur en communiquant avec l'API
     */
    private void checkLogin() throws IOException {

        App a = new App();

        HttpURLConnection conn = APIConnectionHandler.getConnection("auth");

        JSONObject jsonInput = new JSONObject();
        jsonInput.put("auth_type", "credentials");
        jsonInput.put("email", email.getText());
        jsonInput.put("password", password.getText());

        APIConnectionHandler.sendToAPI(conn, jsonInput.toString());
        String response = APIConnectionHandler.recvFromAPI(conn);
        conn.disconnect();

        JSONObject obj = new JSONObject(response);
        int HttpCode = obj.getJSONObject("status").getInt("code");

        // Si la requête est valide
        if (HttpCode == HttpURLConnection.HTTP_OK) {

            String token = obj.getJSONObject("data").getString("token");
            APIConnectionHandler.createToken(token, "token");

            // Accès à la fenêtre principale
            a.changeScene("main.fxml");
        }
        else { // Sinon affiche une erreur et demande de retaper l'email et le mot de passe
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setHeaderText("Veuillez réessayer");
            String cause = obj.getJSONObject("status").getString("message");
            alert.setContentText(cause);
            alert.showAndWait();
            email.requestFocus();
            password.clear();
        }
    }

    /**
     * Fonction appelée lorsque l'utilisateur clique sur le lien pour qu'il crée un compte
     * @param actionEvent -
     */
    public void userCreateAccount(ActionEvent actionEvent) {
        App a = new App();
        a.getHostServices().showDocument(APIConnectionHandler.SITE_BASE + "/view/signup.php");
    }
}
