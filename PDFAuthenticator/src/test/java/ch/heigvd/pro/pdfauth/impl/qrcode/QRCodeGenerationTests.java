package ch.heigvd.pro.pdfauth.impl.qrcode;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// Classe permettant de tester la génération d'un QR-Code
public class QRCodeGenerationTests {

    @Test
    public void BufferedImageShouldNotBeNull() throws WriterException {
        Assertions.assertNotNull(QRCodeGenerator.generateQRCodeImage("test"));
    }

    @Test
    public void generateQRCodeImageShouldThrowException() {
        Assertions.assertThrows(NullPointerException.class, () -> QRCodeGenerator.generateQRCodeImage(null));
    }
}
