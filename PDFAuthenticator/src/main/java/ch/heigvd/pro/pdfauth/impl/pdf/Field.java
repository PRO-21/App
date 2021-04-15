package ch.heigvd.pro.pdfauth.impl.pdf;

import javafx.scene.control.CheckBox;

// Classe représentant un champ extrait du PDF à protéger
public class Field {

    final private String fieldName;
    final private String value;
    private CheckBox isProtected;

    public Field(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
        isProtected = new CheckBox();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }

    public CheckBox getIsProtected() {
        return isProtected;
    }

    public void setIsProtected(CheckBox isProtected) {
        this.isProtected = isProtected;
    }

    @Override
    public String toString() {
        return "Field{" +
                "fieldName='" + fieldName + '\'' +
                ", value='" + value + '\'' +
                ", isProtected=" + isProtected.isSelected() +
                '}';
    }
}
