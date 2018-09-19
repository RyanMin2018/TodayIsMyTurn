package kr.co.sology.fw.util;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

/**
 * QR Code Generator
 *
 */
public class QRCodeUtil {

    /**
     * get qr code image
     *
     * @param content content to encode
     * @param intSize size
     * @param intForegroundColor fore ground color
     * @param intBackgroundColor back ground color
     * @return bitmap
     */
    public static Bitmap getQRCode(String content, int intSize, int intForegroundColor, int intBackgroundColor) {
        Bitmap bitmap = null;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            bitmap = toBitmap(qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, intSize, intSize), intForegroundColor, intBackgroundColor);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * bitmatrix to bitmap
     *
     * @param matrix matrix
     * @param intForegroundColor fore ground color
     * @param intBackgroundColor back ground color
     * @return bitmap
     */
    private static Bitmap toBitmap(BitMatrix matrix, int intForegroundColor, int intBackgroundColor) {
        int intWidth = matrix.getWidth();
        int intHeight = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(intWidth, intHeight, Bitmap.Config.RGB_565);
        for (int x=0; x<intWidth; x++) {
            for (int y=0; y<intHeight; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? intForegroundColor : intBackgroundColor);
            }
        }
        return bitmap;
    }

    /**
     * bitmap to base64
     *
     * @param bitmap bitmap
     * @return base64
     */
    public static String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

}
