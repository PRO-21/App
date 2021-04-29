package ch.heigvd.pro.pdfauth.impl.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Classe permettant d'extraire les champs d'un formulaire PDF
public class PDFHandler {

    /**
     * Fonction permettant d'extraire les champs d'un formulaire PDF
     * @param pdf fichier PDF dont il faut extraire les champs
     * @return liste de tous les Fields trouvés dans le formulaire
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

    public static void insertQRCodeInPDF(File pdf, BufferedImage qrcode) throws IOException {

        // Chargement du document PDF
        PDDocument doc = PDDocument.load(pdf);

        // Ajout d'une nouvelle page
        PDPage newPage = new PDPage(PDRectangle.A4);
        doc.addPage(newPage);

        // Conversion de la BufferedImage en PDImageXObject pour l'ajouter au PDF
        PDImageXObject pdfImage = JPEGFactory.createFromImage(doc, qrcode);

        // Récupération du stream afin de pouvoir écrire "dessiner" l'image dans le PDF
        PDPageContentStream image = new PDPageContentStream(doc, newPage);
        image.drawImage(pdfImage, 25, 700);

        // Fermeture du stream
        image.close();

        // Sauvegarde du document
        doc.save("src/main/resources/ch/heigvd/pro/pdfauth/impl/authenticated.pdf");

        // Fermeture du document
        doc.close();
    }

    public static void insertQRCodeInPDF(File pdf, BufferedImage qrcode, double x, double y) throws IOException {

        PDDocument doc = PDDocument.load(pdf);

        PDPage page1 = new PDPage();
        doc.addPage(page1);

        // Retrieve the page
        PDPage page = doc.getPage(1);

        // Creating Object of PDImageXObject for selecting
        // Image and provide the path of file in argument
        PDImageXObject pdfImage = JPEGFactory.createFromImage(doc, qrcode);

        // Creating the PDPageContentStream Object
        // for Inserting Image
        PDPageContentStream image = new PDPageContentStream(doc, page);

        image.drawImage(pdfImage, 55, 370);

        // Closing the page of PDF by closing
        // PDPageContentStream Object
        // && Saving the Document
        image.close();
        doc.save("src/main/resources/ch/heigvd/pro/pdfauth/impl/authenticated.pdf");

        // Closing the Document
        doc.close();
    }
}
