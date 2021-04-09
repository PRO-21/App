package ch.heigvd.pro.pdfauth.impl.controllers;


import ch.heigvd.pro.pdfauth.impl.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        URL url = new URL("https://pro.simeunovic.ch:8022/protest/api/auth");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        String jsonInputString = "{\"auth_type\": \"credentials\"," +
                                  "\"email\": \"" + email.getText() + "\"," +
                                  "\"password\": \"" + password.getText() + "\",}";

        // Exception lors de la récupération du stream
        try (BufferedOutputStream bos = new BufferedOutputStream(con.getOutputStream())) {
            byte[] input = jsonInputString.getBytes();
            bos.write(input, 0, input.length);
            bos.flush();
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }



        if (email.getText().equals("test@test.test") && password.getText().equals("1234")) {
            a.changeScene("main.fxml");
        }
    }

    public void userCreateAccount(ActionEvent actionEvent) {
        App a = new App();
        a.getHostServices().showDocument("https://www.google.ch");
    }
}
