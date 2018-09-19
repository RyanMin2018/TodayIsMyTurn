package kr.co.sology.fw.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * barcode image decoder
 * <pre>
 *     never call in ui thread.
 * </pre>
 *
 */
@SuppressWarnings("unused")
public class BarCodeImageDecodeUtil {

    private static String strLogId = "BarCodeImageDecodeUtil";

    /**
     * decode
     *
     * @param bitmap bitmap
     * @return code string
     */
    public static String decode(Bitmap bitmap) {
        String strContent = null;
        if (bitmap!=null) {
            try {
                int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                Reader reader = new MultiFormatReader();
                Result rawResult = reader.decode(binaryBitmap);
                strContent = rawResult.getText();
            } catch (Exception e) {
                Log.d(strLogId, e.toString());
                e.printStackTrace();
            }
        }
        return strContent;
    }

    /**
     * decode from file
     *
     * @param file file
     * @return code string
     */
    public static String decode(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        return decode(bitmap);
    }

    /**
     * decode from share
     *
     * @param context context
     * @param uri     uri
     * @return code string
     */
    public static Bitmap convertBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            Log.d(strLogId, uri.toString());
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.d(strLogId, e.toString());
            e.printStackTrace();
        }
        return bitmap;
    }


}
