package az.qrfood.backend.common;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for generating QR codes.
 * <p>
 * This class uses the ZXing library to encode text into QR code images,
 * which can then be returned as a byte array.
 * </p>
 */
public class QrCodeGenerator {

    /**
     * Generates a QR code as a byte array (PNG format) from the given text.
     *
     * @param text   The text content to encode into the QR code.
     * @param width  The desired width of the QR code image in pixels.
     * @param height The desired height of the QR code image in pixels.
     * @return A byte array representing the QR code image in PNG format.
     * @throws Exception if an error occurs during QR code generation (e.g., encoding issues).
     */
    public static byte[] generateQRCode(String text, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
        return baos.toByteArray();
    }
}