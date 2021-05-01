package ch.heigvd.pro.pdfauth.impl.pdf;

import ch.heigvd.pro.pdfauth.impl.qrcode.QRCodeGenerator;
import com.google.zxing.WriterException;
import org.apache.pdfbox.multipdf.PageExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

        File authPdf = new File("src/test/java/ch/heigvd/pro/pdfauth/impl/pdf/test_folder/authenticated.pdf");
        PDDocument authDoc = PDDocument.load(authPdf);
        PageExtractor pe2 = new PageExtractor(authDoc);
        int newPageNo = pe2.getEndPage() - 1;
        authDoc.close();

        Assertions.assertEquals(originPageNo + 1, newPageNo);
    }

}
