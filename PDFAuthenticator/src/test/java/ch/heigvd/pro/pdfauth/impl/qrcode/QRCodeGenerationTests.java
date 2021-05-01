package ch.heigvd.pro.pdfauth.impl.qrcode;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QRCodeGenerationTests {

    @Test
    public void BufferedImageShouldNotBeNull() throws WriterException {
        Assertions.assertNotNull(QRCodeGenerator.generateQRCodeImage("test"));
    }
}
