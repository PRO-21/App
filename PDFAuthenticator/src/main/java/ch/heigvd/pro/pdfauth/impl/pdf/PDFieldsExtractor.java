package ch.heigvd.pro.pdfauth.impl.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFieldsExtractor {

    public static List<String> extractFields(File pdf) throws IOException {

        PDDocument pdDoc = PDDocument.load(pdf);
        PDDocumentCatalog pdCatalog = pdDoc.getDocumentCatalog();
        PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
        List<String> fields = new ArrayList<>();

        for (PDField field : pdAcroForm.getFields()) {
            fields.add(field.getPartialName() + " : " + field.getValueAsString());
        }

        return fields;
    }
}
