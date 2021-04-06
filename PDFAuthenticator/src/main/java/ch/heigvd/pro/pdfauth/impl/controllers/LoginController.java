package ch.heigvd.pro.pdfauth.impl.controllers;


import ch.heigvd.pro.pdfauth.impl.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

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
        if (email.getText().equals("test@test.test") && password.getText().equals("1234")) {
            a.changeScene("main.fxml");
        }
    }

    public void userCreateAccount(ActionEvent actionEvent) {
        App a = new App();
        a.getHostServices().showDocument("https://www.google.ch");
    }
}
