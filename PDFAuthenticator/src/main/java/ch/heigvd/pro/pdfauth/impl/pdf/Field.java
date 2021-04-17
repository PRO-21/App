package ch.heigvd.pro.pdfauth.impl.pdf;

import javafx.scene.control.CheckBox;

// Classe représentant un champ extrait du PDF à protéger
public class Field {

    final private String fieldName; // Nom du champ (Text box pour le prénom, pour une adresse, etc...)
    final private String value;     // Valeur du champ
    private CheckBox isProtected;   // S'il est à protéger ou pas

    public Field(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
        isProtected = new CheckBox();
    }

    /**
     * Getter retournant le nom du champ
     * @return nom du champ
     */
    public String getFieldName() {
        return fieldName;
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
        this.isProtected = isProtected;
    }

    /**
     * Fonction permettant de retourner une String d'un Field à des fins de test
     * @return String contenant les valeurs d'un Field
     */
    @Override
    public String toString() {
        return "Field{" +
                "fieldName='" + fieldName + '\'' +
                ", value='" + value + '\'' +
                ", isProtected=" + isProtected.isSelected() +
                '}';
    }
}