module ch.heigvd.pro.pdfauth.impl {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.apache.pdfbox;

    opens ch.heigvd.pro.pdfauth.impl to javafx.fxml;
    exports ch.heigvd.pro.pdfauth.impl;
    opens ch.heigvd.pro.pdfauth.impl.controllers to javafx.fxml;
    exports ch.heigvd.pro.pdfauth.impl.controllers;
}