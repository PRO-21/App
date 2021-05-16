package ch.heigvd.pro.pdfauth.impl.pdf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.io.IOException;
import java.util.List;


// Classe permettant de tester l'extraction des champs d'un formulaire PDF.
// Comme les objet Field utilisent des CheckBox, il a fallu ajouter une dépendence Maven (TestFX) pour permettre de
// tester des objets de JavaFX et lier cette classe de test à la classe ApplicationTest de TestFX.
// Les tests passent en local avec l'IDE et Maven mais pas avec les GitHub Actions
public class FieldsExtractionTests extends ApplicationTest {

    @Test
    public void extractorShouldExtractAllFields() throws IOException {
        List<Field> fields = PDFHandler.extractFields(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test.pdf"));
        Assertions.assertEquals(17, fields.size());
    }

    @Test
    public void extractorShouldExtractAllFieldsInComplexForm() throws IOException {
        List<Field> fields = PDFHandler.extractFields(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test_multiple_pages.pdf"));
        Assertions.assertEquals(108, fields.size());
    }

    @Test
    public void fieldsListShouldBeEmptyIfPDFFormNotExists() throws IOException {
        List<Field> fields = PDFHandler.extractFields(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test_without_form.pdf"));
        Assertions.assertTrue(fields.isEmpty());
    }

    @Test
    public void extractorShouldThrowExceptionIfNotPDF() {
        Assertions.assertThrows(IOException.class, () ->
                PDFHandler.extractFields(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test_word.docx")));
    }

    @Test
    public void extractorShouldExtractFieldsCorrectly() throws IOException {
        List<Field> fields = PDFHandler.extractFields(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test.pdf"));
        Assertions.assertEquals("Albert",         fields.get(0).getValue());
        Assertions.assertEquals("Dupontel",       fields.get(1).getValue());
        Assertions.assertEquals("Rue de la gare", fields.get(2).getValue());
        Assertions.assertEquals("56",             fields.get(3).getValue());
        Assertions.assertEquals("",               fields.get(4).getValue());
        Assertions.assertEquals("1400",           fields.get(5).getValue());
        Assertions.assertEquals("Yverdon",        fields.get(6).getValue());
        Assertions.assertEquals("[Switzerland]",  fields.get(7).getValue());
        Assertions.assertEquals("[Man]",          fields.get(8).getValue());
        Assertions.assertEquals("180",            fields.get(9).getValue());
        Assertions.assertEquals("Yes",            fields.get(10).getValue());
        Assertions.assertEquals("Off",            fields.get(11).getValue());
        Assertions.assertEquals("Yes",            fields.get(12).getValue());
        Assertions.assertEquals("Yes",            fields.get(13).getValue());
        Assertions.assertEquals("Off",            fields.get(14).getValue());
        Assertions.assertEquals("Off",            fields.get(15).getValue());
        Assertions.assertEquals("[Blue]",         fields.get(16).getValue());
    }

    @Test
    public void extractorShouldExtractSubfieldCorrectly() throws IOException {
        List<Field> fields = PDFHandler.extractFields(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test_multiple_pages.pdf"));
        Assertions.assertEquals("A1 et B", fields.get(14).getValue());
    }

    @Test
    public void extractorShouldThrowExceptionIfFakePDF() {
        Assertions.assertThrows(IOException.class, () ->
                PDFHandler.extractFields(new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test_fake_pdf.pdf")));
    }
}
