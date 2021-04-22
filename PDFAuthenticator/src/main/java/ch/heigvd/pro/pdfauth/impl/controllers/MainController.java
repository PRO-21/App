package ch.heigvd.pro.pdfauth.impl.controllers;

import ch.heigvd.pro.pdfauth.impl.api.APIConnectionHandler;
import ch.heigvd.pro.pdfauth.impl.pdf.Field;
import ch.heigvd.pro.pdfauth.impl.pdf.PDFieldsExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.json.JSONObject;

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
            protectedBy.setText(APIConnectionHandler.extractUsernameFromToken("src/main/resources/ch/heigvd/pro/pdfauth/impl/token"));
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Fonction appelée lors de l'appui sur le bouton "Ouvrir" sur la fenêtre. Elle s'occupe d'ouvrir l'explorateur de
     * fichiers et le PDF puis d'appeler la fonction populateTableView afin d'afficher les champs extraits
     * @param actionEvent -
     */
    public void openPDF(ActionEvent actionEvent) {

        File pdf = openFileExplorer();

        // Si le PDF n'est pas null
        if (pdf != null) {

            // Affichage du chemin absolu du PDF dans le TextField
            filePath.setText(pdf.getPath());

            try {
                // Extraction des champs
                fields = PDFieldsExtractor.extractFields(pdf);
            }
            catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
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

        // Liste permettant de faire le lien entre la classe Field et les colonnes à afficher
        ObservableList<Field> data = FXCollections.observableArrayList();
        fieldsList.setItems(data);

        // Si les colonnes ont déjà été créées il faut seulement remettre les données dans l'ObservableList
        if (!fieldsList.getColumns().isEmpty()) {
            data.addAll(fields);
        }
        else { // Sinon si la table est vide, il faut créer les colonnes et les remplir

            TableColumn<Field, String> fieldCol = new TableColumn<>("Champ");
            fieldCol.setCellValueFactory(new PropertyValueFactory<>("fieldName")); // Lien entre la classe Field
            fieldCol.setReorderable(false);                                           // et la colonne
            fieldCol.setPrefWidth(140);

            TableColumn<Field, String> valueCol = new TableColumn<>("Valeur");
            valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
            valueCol.setReorderable(false);
            valueCol.setPrefWidth(100);

            TableColumn<Field, CheckBox> protectedCol = new TableColumn<>("À protéger");
            protectedCol.setCellValueFactory(new PropertyValueFactory<>("isProtected"));
            protectedCol.setReorderable(false);
            protectedCol.setStyle("-fx-alignment: CENTER;");
            protectedCol.setMinWidth(50);

            // Ajout des données dans l'ObservableList
            data.addAll(fields);

            // Ajout des colonnes à la TableView
            fieldsList.getColumns().addAll(fieldCol, valueCol, protectedCol);
        }
    }

    /**
     * Fonction appelé lors de l'appui du bouton "Valider" sur la fenêtre. Elle va envoyer les champs à protéger à l'API
     * et récupérer la réponse de celle-ci
     * @param actionEvent -
     */
    public void sendData(ActionEvent actionEvent) {

        if (fields != null) {

            String jsonInputString = prepareFieldsToSend();

            try {
                HttpURLConnection conn = APIConnectionHandler.getConnection("cert");
                conn.setRequestProperty("Authorization", "Bearer " + APIConnectionHandler.getToken("src/main/resources/ch/heigvd/pro/pdfauth/impl/token"));
                APIConnectionHandler.sendToAPI(conn, jsonInputString);
                String response = APIConnectionHandler.recvFromAPI(conn);

                JSONObject obj = new JSONObject(response);

                int HttpCode = obj.getJSONObject("status").getInt("code");

                if (HttpCode == HttpURLConnection.HTTP_OK) {
                    String certificateID = obj.getJSONObject("data").getString("idCertificat");
                    System.out.println("URL : https://pro.simeunovic.ch:8022/protest/view/scan.php?id=" + certificateID);
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Aucun champ à protéger sélectionné");
                    String cause = obj.getJSONObject("status").getString("message");
                    alert.setContentText(cause);
                    alert.showAndWait();
                }

            }
            catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
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
            changeMap.setAccessible(true); // permet de rendre accessible la Map à modifier
            changeMap.set(obj, new LinkedHashMap<>()); // Remplacement de HashMap par LinkedHashMap
            changeMap.setAccessible(false);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        obj.put("dateSignature", dateFormat.format(new Date()));

        for (Field field : fields) {

            if (field.getIsProtected().isSelected())
                obj.put(field.getFieldName(), field.getValue());
        }
        return obj.toString();
    }
}