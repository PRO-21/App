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
import org.apache.pdfbox.multipdf.PageExtractor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Classe permettant d'extraire les champs d'un formulaire PDF et d'ajouter une image au PDF
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

    /**
     * Fonction permettant d'ajouter une image sur une nouvelle page d'un document PDF
     * @param pdf document PDF auquel ajouter l'image
     * @param bufferedImage image à ajouter au PDF
     */
    public static void insertQRCodeInPDF(File pdf, BufferedImage bufferedImage) throws IOException {

        // Chargement du document PDF
        PDDocument doc = PDDocument.load(pdf);

        // Ajout d'une nouvelle page
        PDPage newPage = new PDPage(PDRectangle.A4);
        doc.addPage(newPage);

        // Conversion de la BufferedImage en PDImageXObject pour l'ajouter au PDF
        PDImageXObject pdfImage = JPEGFactory.createFromImage(doc, bufferedImage);

        // Récupération du stream afin de pouvoir écrire "dessiner" l'image dans le PDF
        PDPageContentStream image = new PDPageContentStream(doc, newPage);
        image.drawImage(pdfImage, 50, 750);

        // Fermeture du stream
        image.close();

        // Sauvegarde du document
        // TODO : Ajouter le QR-Code dans le même fichier, ici c'est juste pour pas péter l'original
        doc.save(pdf.getParent() + "/authenticated.pdf");

        // Fermeture du document
        doc.close();
    }

    /**
     * Fonction permettant d'ajouter une image sur un PDF
     * @param pdf document PDF auquel ajouter l'image
     * @param bufferedImage image à ajouter au PDF
     * @param x coordonnée x
     * @param y coordonnée y
     */
    public static void insertQRCodeInPDF(File pdf, BufferedImage bufferedImage, int x, int y) throws IOException {

        PDDocument doc = PDDocument.load(pdf);

        PageExtractor pe = new PageExtractor(doc);
        int pageNo = pe.getEndPage() - 1;
        PDPage page = doc.getPage(pageNo);

        PDImageXObject pdfImage = JPEGFactory.createFromImage(doc, bufferedImage);

        PDPageContentStream image = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, false);

        image.drawImage(pdfImage, x, y);

        image.close();
        doc.save("src/main/resources/ch/heigvd/pro/pdfauth/impl/authenticated.pdf");
        doc.close();
    }
}
