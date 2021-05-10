package ch.heigvd.pro.pdfauth.impl.pdf;

import ch.heigvd.pro.pdfauth.impl.qrcode.QRCodeGenerator;
import com.google.zxing.WriterException;
import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

// Classe permettant de tester l'insertion du QR-Code dans un fichier PDF
public class QRCodeInsertionInPDFTests {

    @Test
    public void PDFShouldHaveANewPage() throws WriterException, IOException {

        BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage("test");
        File pdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test.pdf");

        PDDocument doc = PDDocument.load(pdf);
        PageExtractor pe = new PageExtractor(doc);
        int originPageNo = pe.getEndPage() - 1;
        doc.close();

        PDFHandler.insertQRCodeInPDF(pdf, qrcode);

        File authPdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test_authenticated.pdf");
        PDDocument authDoc = PDDocument.load(authPdf);
        PageExtractor pe2 = new PageExtractor(authDoc);
        int newPageNo = pe2.getEndPage() - 1;
        authDoc.close();

        Assertions.assertEquals(originPageNo + 1, newPageNo);
    }

    @Test
    public void insertQRCodeInPDFShouldThrowExceptionIfCoordsAreBothNegative() throws WriterException {

        BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage("test");
        File pdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test.pdf");
        Assertions.assertThrows(IllegalArgumentException.class, () -> PDFHandler.insertQRCodeInPDF(pdf, qrcode, -500, -25));
    }

    @Test
    public void insertQRCodeInPDFShouldThrowExceptionIfCoordXIsNegative() throws WriterException {

        BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage("test");
        File pdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test.pdf");
        Assertions.assertThrows(IllegalArgumentException.class, () -> PDFHandler.insertQRCodeInPDF(pdf, qrcode, -500, 25));
    }

    @Test
    public void insertQRCodeInPDFShouldThrowExceptionIfCoordYIsNegative() throws WriterException {

        BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage("test");
        File pdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test.pdf");
        Assertions.assertThrows(IllegalArgumentException.class, () -> PDFHandler.insertQRCodeInPDF(pdf, qrcode, 500, -25));
    }

    @Test
    public void insertQRCodeInPDFShouldThrowExceptionIfFileIsNull() throws WriterException {

        BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage("test");
        Assertions.assertThrows(NullPointerException.class, () -> PDFHandler.insertQRCodeInPDF(null, qrcode, 500, 25));
    }

    @Test
    public void insertQRCodeInPDFShouldThrowExceptionIfQRCodeIsNull() {

        File pdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/test.pdf");
        Assertions.assertThrows(NullPointerException.class, () -> PDFHandler.insertQRCodeInPDF(pdf, null, 500, 25));
    }

    @Test
    public void insertQRCodeInPDFShouldThrowExceptionIfFilepathIsWrong() throws WriterException {

        File pdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test.pdf");
        BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage("test");
        Assertions.assertThrows(FileNotFoundException.class, () -> PDFHandler.insertQRCodeInPDF(pdf, qrcode, 500, 25));
    }

    @Test
    public void insertQRCodeInPDFShouldThrowExceptionIfFileIsNotAPDF() throws WriterException {

        File word = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_word.docx");
        BufferedImage qrcode = QRCodeGenerator.generateQRCodeImage("test");
        Assertions.assertThrows(FileNotFoundException.class, () -> PDFHandler.insertQRCodeInPDF(word, qrcode, 500, 25));
    }

}
