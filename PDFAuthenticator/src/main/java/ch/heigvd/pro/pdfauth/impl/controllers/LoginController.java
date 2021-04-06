package ch.heigvd.pro.pdfauth.impl.controllers;

import javafx.application.HostServices;
import javafx.scene.input.MouseEvent;

public class LoginController {

    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void onMouseClick(MouseEvent mouseEvent) {
        hostServices.showDocument("https://www.google.ch");
    }

    public void onButtonClick(MouseEvent mouseEvent) {
    }
}
