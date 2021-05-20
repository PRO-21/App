package ch.heigvd.pro.pdfauth.impl;

// Classe utilisée pour éviter d'avoir une erreur lors du lancement de l'application avec java -jar à cause de
// l'héritage avec Application dans le fichier App.java
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
