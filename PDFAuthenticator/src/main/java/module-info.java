module ch.heigvd.pro.pdfauth.impl {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;

    opens ch.heigvd.pro.pdfauth.impl to javafx.fxml;
    exports ch.heigvd.pro.pdfauth.impl;
    opens ch.heigvd.pro.pdfauth.impl.controllers to javafx.fxml;
    exports ch.heigvd.pro.pdfauth.impl.controllers;
    opens ch.heigvd.pro.pdfauth.impl.pdf to javafx.fxml;
    exports ch.heigvd.pro.pdfauth.impl.pdf;
}