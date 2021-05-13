package ch.heigvd.pro.pdfauth.impl.pdf;

import javafx.scene.control.CheckBox;

import java.util.Objects;

// Classe représentant un champ extrait du PDF à protéger
public class Field {

    private String fieldName; // Nom du champ (Text box pour le prénom, pour une adresse, etc...)
    private String value;     // Valeur du champ
    private CheckBox isProtected;   // S'il est à protéger ou pas

    /**
     * Constructeur permettant d'instancier un objet Field
     * @param fieldName nom du champ
     * @param value     valeur du champ
     */
    public Field(String fieldName, String value) {

        Objects.requireNonNull(fieldName);
        Objects.requireNonNull(value);

        this.fieldName = fieldName;
        this.value = value;
        isProtected = new CheckBox();
    }

    public void setFieldName(String fieldName) {
        Objects.requireNonNull(fieldName);
        this.fieldName = fieldName;
    }

    /**
     * Getter retournant le nom du champ
     * @return nom du champ
     */
    public String getFieldName() {
        return fieldName;
    }


    public void setValue(String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    /**
     * Getter retournant la valeur du champ
     * @return valeur du champ
     */
    public String getValue() {
        return value;
    }

    /**
     * Getter utilisé par la fonction MainController.populateTableView afin de récupérer les CheckBoxes des champs
     * @return CheckBox du champ
     */
    public CheckBox getIsProtected() {
        return isProtected;
    }

    /**
     * Setter utilisé par la fonction MainController.populateTableView afin de modifier les CheckBoxes des champs
     * @param isProtected CheckBox à définir pour le champ
     */
    public void setIsProtected(CheckBox isProtected) {

        Objects.requireNonNull(isProtected);
        this.isProtected = isProtected;
    }

    /**
     * Fonction permettant de retourner une String d'un Field à des fins de test
     * @return String contenant les valeurs d'un Field
     */
    @Override
    public String toString() {
        return "\nField{" +
                "fieldName='" + fieldName + '\'' +
                ", value='" + value + '\'' +
                ", isProtected=" + isProtected.isSelected() +
                "}";
    }
}
