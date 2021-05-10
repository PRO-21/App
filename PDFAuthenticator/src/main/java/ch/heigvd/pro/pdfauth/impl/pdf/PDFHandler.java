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
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Classe permettant d'extraire les champs d'un formulaire PDF et d'ajouter une image au PDF
public class PDFHandler {

    private static List<Field> fields;

    /**
     * Fonction permettant d'extraire les champs racine d'un formulaire PDF
     * @param pdf fichier PDF dont il faut extraire les champs
     * @return liste de tous les Fields trouvés dans le formulaire
     */
    public static List<Field> extractFields(File pdf) throws IOException {

        Objects.requireNonNull(pdf);

        // Chargement du document PDF (et de son catalogue)
        PDDocument pdDoc = PDDocument.load(pdf);
        PDDocumentCatalog pdCatalog = pdDoc.getDocumentCatalog();

        // Récupération du formulaire interactif
        PDAcroForm pdAcroForm = pdCatalog.getAcroForm();

        fields = new ArrayList<>();

        // Si le formulaire interactif existe
        if (pdAcroForm != null) {

            // Extraction de tous les champs du formulaire PDF
            for (PDField field : pdAcroForm.getFields()) {
                extractAll(field);
            }
        }
        pdDoc.close();
        return fields;
    }

    /**
     * Fonction récursive permettant d'extraire les sous-champs du formulaire PDF
     * @param field champs possédant possiblement des sous-champs
     */
    private static void extractAll(PDField field) {

        // Ajout des champs à la liste
        fields.add(new Field(field.getPartialName(), field.getValueAsString()));

        // Si le champ n'est pas un champ terminal
        if (field instanceof PDNonTerminalField)  {
            for (PDField child : ((PDNonTerminalField)field).getChildren()) {
                extractAll(child);
            }
        }
    }

    /**
     * Fonction permettant d'ajouter une image sur une nouvelle page d'un document PDF
     * @param pdf document PDF auquel ajouter l'image
     * @param bufferedImage image à ajouter au PDF
     */
    public static void insertQRCodeInPDF(File pdf, BufferedImage bufferedImage) throws IOException {

        Objects.requireNonNull(pdf);
        Objects.requireNonNull(bufferedImage);

        // Chargement du document PDF
        PDDocument doc = PDDocument.load(pdf);

        // Ajout d'une nouvelle page
        PDPage newPage = new PDPage(PDRectangle.A4);
        doc.addPage(newPage);

        // Conversion de la BufferedImage en PDImageXObject pour l'ajouter au PDF
        PDImageXObject pdfImage = JPEGFactory.createFromImage(doc, bufferedImage);

        // Récupération du stream afin de pouvoir "dessiner" l'image dans le PDF
        PDPageContentStream image = new PDPageContentStream(doc, newPage);
        image.drawImage(pdfImage, 50, 750);

        // Fermeture du stream
        image.close();

        // Sauvegarde du document
        String pdfName = pdf.getName().replaceFirst("[.][^.]+$", ""); // Extension retirée
        doc.save(pdf.getParent() + "/" + pdfName + "_authenticated.pdf");

        // Fermeture du document
        doc.close();
    }

    /**
     * Fonction permettant d'ajouter une image sur un PDF en fonction de coordonnées x et y
     * @param pdf document PDF auquel ajouter l'image
     * @param bufferedImage image à ajouter au PDF
     * @param x coordonnée x
     * @param y coordonnée y
     */
    public static void insertQRCodeInPDF(File pdf, BufferedImage bufferedImage, int x, int y) throws IOException {

        Objects.requireNonNull(pdf);
        Objects.requireNonNull(bufferedImage);

        if (x < 0 || y < 0)
            throw new IllegalArgumentException("Coords cannot be negative");

        PDDocument doc = PDDocument.load(pdf);

        // Récupération du nombre de pages afin de mettre le QR-Code sur la dernière
        int lastPageNo = doc.getNumberOfPages();
        PDPage lastPage = doc.getPage(lastPageNo - 1);

        PDImageXObject pdfImage = JPEGFactory.createFromImage(doc, bufferedImage);
        PDPageContentStream image = new PDPageContentStream(doc, lastPage, PDPageContentStream.AppendMode.APPEND, false);
        image.drawImage(pdfImage, x, y);
        image.close();

        String pdfName = pdf.getName().replaceFirst("[.][^.]+$", "");
        doc.save(pdf.getParent() + "/" + pdfName + "_authenticated.pdf");

        doc.close();
    }
}
