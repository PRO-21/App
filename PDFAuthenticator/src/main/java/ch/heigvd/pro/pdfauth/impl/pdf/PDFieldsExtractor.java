package ch.heigvd.pro.pdfauth.impl.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Classe permettant d'extraire les champs d'un formulaire PDF
public class PDFieldsExtractor {

    /**
     * Fonction permettant d'extraire les champs d'un formulaire PDF
     * @param pdf fichier PDF dont il faut extraire les champs
     * @return liste de tous les Fields trouvés dans le formulaire
     * @throws IOException
     */
    public static List<Field> extractFields(File pdf) throws IOException {

        // Chargement du document PDF (et de son catalogue)
        PDDocument pdDoc = PDDocument.load(pdf);
        PDDocumentCatalog pdCatalog = pdDoc.getDocumentCatalog();

        // Récupération du formulaire interactif
        PDAcroForm pdAcroForm = pdCatalog.getAcroForm();

        List<Field> fields = new ArrayList<>();

        // Si le formulaire interactif existe
        if (pdAcroForm != null) {

            // Extraction de tous les champs du formulaire PDF
            for (PDField field : pdAcroForm.getFields()) {
                fields.add(new Field(field.getPartialName(), field.getValueAsString()));
            }
        }
        pdDoc.close();
        return fields;
    }
}
