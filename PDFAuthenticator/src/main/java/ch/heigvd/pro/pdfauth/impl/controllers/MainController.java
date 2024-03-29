package ch.heigvd.pro.pdfauth.impl.controllers;

import ch.heigvd.pro.pdfauth.impl.App;
import ch.heigvd.pro.pdfauth.impl.api.APIConnectionHandler;
import ch.heigvd.pro.pdfauth.impl.pdf.Field;
import ch.heigvd.pro.pdfauth.impl.pdf.PDFHandler;
import ch.heigvd.pro.pdfauth.impl.qrcode.QRCodeGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

// Classe permettant de récupérer les valeurs des champs selon les events qui se passe sur la fenêtre principale
public class MainController implements Initializable {

    @FXML
    private TableColumn<Field, String> fieldCol;
    @FXML
    private TableColumn<Field, String> valueCol;
    @FXML
    private TableColumn<Field, String> protectedCol;
    @FXML
    private RadioButton topLeft;
    @FXML
    private RadioButton topRight;
    @FXML
    private RadioButton bottomLeft;
    @FXML
    private RadioButton bottomRight;
    @FXML
    private RadioButton onNewPage;
    @FXML
    private Label protectedBy;
    @FXML
    private TableView<Field> fieldsList;
    @FXML
    private TextField filePath;

    private List<Field> fields;

    /**
     * Fonction permettant de modifier directement des éléments au chargement de la fenêtre
     * @param url -
     * @param resourceBundle -
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            protectedBy.setText(APIConnectionHandler.extractUsernameFromToken("token"));
            fieldCol.setReorderable(false);
            valueCol.setReorderable(false);
            protectedCol.setReorderable(false);

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Fonction liée à l'appui du bouton "Aide" permettant d'afficher une fenêtre de dialogue affichant les étapes à
     * réaliser pour authentifier un PDF
     * @param actionEvent -
     */
    public void openHelp(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aide");
        alert.setHeaderText("Instructions");
        alert.setContentText("1. Ouvrir un document PDF contenant un formulaire électronique avec le bouton \"Ouvrir...\"\n\n" +
                             "2. Cocher les champs à protéger en cliquant sur les coches de la colonne \"À protéger\"\n\n" +
                             "3. Sélectionner un emplacement libre dans le document PDF sur lequel sera apposé le QR-Code ou sélectionner \"Sur une nouvelle page\"\n\n" +
                             "4. Appuyer sur le bouton \"Valider\"");

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /**
     * Fonction liée à l'appui du bouton "Ouvrir" sur la fenêtre. Elle s'occupe d'ouvrir l'explorateur de
     * fichiers et le PDF puis d'appeler la fonction populateTableView afin d'afficher les champs extraits
     * @param actionEvent -
     */
    public void openPDF(ActionEvent actionEvent) {

        File pdf = openFileExplorer();

        // Si le PDF n'est pas null
        if (pdf != null) {

            // Affichage du chemin absolu du PDF dans le TextField
            filePath.setText(pdf.getPath());
            filePath.end();

            try {
                // Extraction des champs
                fields = PDFHandler.extractFields(pdf);

                // Si le PDF ne contient pas de formulaire
                if (fields.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Aucun champ n'a été trouvé.");
                    alert.showAndWait();
                }
            }
            catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
            populateTableView(fields);
        }
    }

    /**
     * Fonction permettant d'ouvrir l'explorateur de fichiers afin de sélectionner un PDF
     * @return PDF sélectionné
     */
    private File openFileExplorer() {
        FileChooser fileChooser = new FileChooser();

        // Personnalisation de l'explorateur (définition du titre, filtre d'extensions de fichiers)
        fileChooser.setTitle("Sélectionner un fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        // Affichage de l'explorateur
        return fileChooser.showOpenDialog(filePath.getScene().getWindow());
    }

    /**
     * Fonction permettant de remplir la TableView des champs extraits du formulaire PDF
     * @param fields liste des champs à afficher
     */
    private void populateTableView(List<Field> fields) {

        // Ne remplit les colonnes que si au moins un champ existe dans le document PDF
        if (fields != null) {

            // Liste permettant de faire le lien entre la classe Field et les colonnes à afficher
            ObservableList<Field> data = FXCollections.observableArrayList();
            fieldsList.setItems(data);

            // Liaison entre la classe Field et les colonnes
            fieldCol.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
            fieldCol.setReorderable(false);
            fieldCol.setResizable(true);

            valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
            valueCol.setReorderable(false);
            valueCol.setResizable(true);

            protectedCol.setCellValueFactory(new PropertyValueFactory<>("isProtected"));
            protectedCol.setReorderable(false);
            protectedCol.setResizable(true);

            // Permet de rendre ces deux colonnes éditables
            fieldCol.setCellFactory(TextFieldTableCell.forTableColumn());
            valueCol.setCellFactory(TextFieldTableCell.forTableColumn());

            // Ajout des données dans l'ObservableList
            data.addAll(fields);
        }
    }

    /**
     * Fonction liée à la modification de la valeur d'une cellule de la colonne "Champ"
     * @param cellEditEvent cellule dont la valeur a été modifiée
     */
    public void userEditFieldName(TableColumn.CellEditEvent<Field, String> cellEditEvent) {
        Field field = fieldsList.getSelectionModel().getSelectedItem();

        // Empêche de modifier la valeur du champ pour mettre un nom de champ vide à la place
        if (!cellEditEvent.getNewValue().isBlank())
            field.setFieldName(cellEditEvent.getNewValue());
    }

    /**
     * Fonction liée à la modification de la valeur d'une cellule de la colonne "Valeur"
     * @param cellEditEvent cellule dont la valeur a été modifiée
     */
    public void userEditValue(TableColumn.CellEditEvent<Field, String> cellEditEvent) {
        Field field = fieldsList.getSelectionModel().getSelectedItem();
        field.setValue(cellEditEvent.getNewValue());
    }

    /**
     * Fonction liée à l'appui du bouton "Valider" sur la fenêtre. Elle va envoyer les champs à protéger à l'API
     * et récupérer la réponse de celle-ci
     * @param actionEvent -
     */
    public void sendData(ActionEvent actionEvent) {

        if (fields != null) {

            String jsonInputString = prepareFieldsToSend();

            try {

                // Ouverture de la connexion avec comme ressource "cert" pour créer un certificat
                HttpURLConnection conn = APIConnectionHandler.getConnection("cert");
                conn.setRequestProperty("Authorization", "Bearer " + APIConnectionHandler.getToken("token"));
                APIConnectionHandler.sendToAPI(conn, jsonInputString);
                String response = APIConnectionHandler.recvFromAPI(conn);
                JSONObject obj = new JSONObject(response);
                int HttpCode = obj.getJSONObject("status").getInt("code");

                // Si l'API renvoie un code 200
                if (HttpCode == HttpURLConnection.HTTP_OK) {
                    String certificateID = obj.getJSONObject("data").getString("idCertificat");
                    BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage(APIConnectionHandler.SITE_BASE + "/view/scan.php?id=" + certificateID);

                    // Si l'utilisateur décide de mettre le QR-Code sur une nouvelle page
                    if (onNewPage.isSelected()) {
                        PDFHandler.insertQRCodeInPDF(new File(filePath.getText()), qrcode);
                    }
                    else { // Sinon, le QR-Code sera placé sur la dernière page du formulaire selon un emplacement choisi

                        // Coordonnées où sera placé le QR-Code dans le document PDF
                        int x = 0;
                        int y = 0;

                        if (topLeft.isSelected()) {
                            x = 50;
                            y = 750;
                        }
                        else if (topRight.isSelected()) {
                            x = 500;
                            y = 750;
                        }
                        else if (bottomLeft.isSelected()) {
                            x = 50;
                            y = 25;
                        }
                        else if (bottomRight.isSelected()) {
                            x = 500;
                            y = 25;
                        }
                        PDFHandler.insertQRCodeInPDF(new File(filePath.getText()), qrcode, x, y);
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.setHeaderText(null);
                    alert.setContentText("QR-Code ajouté au document !");
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.setHeaderText("Aucun champ à protéger sélectionné");
                    String cause = obj.getJSONObject("status").getString("message");
                    alert.setContentText(cause);
                    alert.showAndWait();
                }
            }
            catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setContentText("Veuillez d'abord renseigner un fichier PDF");
            alert.showAndWait();
        }
    }

    /**
     * Fonction permettant de ne garder que les champs qui doivent être protégés et préparer la string Json à envoyer
     * à l'API
     * @return string à envoyer à l'API
     */
    private String prepareFieldsToSend() {

        JSONObject obj = new JSONObject();

        // Comme à la base les JSONObject sont des HashMap qui ne sont pas ordonnées pour des raisons de performance,
        // les champs à protéger ne sont pas disposés dans l'ordre dans lequel ils ont été détectés dans le PDF. Ce qui
        // peut être embêtant lors de la vérification entre le formulaire papier et les champs affichés sur le site web.
        // Grâce à l'API reflect inclue dans Java, il est possible de modifier le comportement de la classe JSONObject
        // pour qu'elle utilise un autre type de HashMap qui lui, est ordonné.
        try {
            java.lang.reflect.Field changeMap = obj.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);             // Permet de rendre accessible la Map à modifier
            changeMap.set(obj, new LinkedHashMap<>()); // Remplacement de HashMap par LinkedHashMap
            changeMap.setAccessible(false);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        // Ajout de la date de signature à l'objet Json
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        obj.put("dateSignature", dateFormat.format(new Date()));

        // Ajout des champs à protéger à l'objet Json
        for (Field field : fields) {

            if (field.getIsProtected().isSelected())
                obj.put(field.getFieldName(), field.getValue());
        }
        return obj.toString();
    }

    /**
     * Fonction liée à l'appui du bouton "Se déconnecter" permettant de renvoyer l'utilisateur à la fenêtre de connexion
     * @param actionEvent -
     */
    public void userLogOut(ActionEvent actionEvent) {

        if (APIConnectionHandler.deleteToken("token")) {
            App a = new App();
            a.changeScene("login.fxml");
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setContentText("Impossible de se déconnecter");
            alert.showAndWait();
        }
    }
}