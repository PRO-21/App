package ch.heigvd.pro.pdfauth.impl.qrcode;

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

// Classe mettant à disposition une fonction statique permettant de générer un QR-Code
public class QRCodeGenerator {

    /**
     * Fonction permettant de générer un QR-Code selon un texte fourni en paramètre
     * @param text texte duquel créer le QR-Code
     * @return image du QR-Code
     */
    public static BufferedImage generateQRCodeImage(String text) throws WriterException {

        // Création d'un QR-Code de 50x50 pixels
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 50, 50);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
