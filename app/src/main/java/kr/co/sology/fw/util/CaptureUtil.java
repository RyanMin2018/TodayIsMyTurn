package kr.co.sology.fw.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;

import kr.co.sology.fw.core.GlobalEnv;

/**
 * 스크린샷 처리 유틸리티<br>
 * Utility for screen shot.
 *
 */
public class CaptureUtil {

	/**
	 * 스크린샷을 저장한다.
	 * <pre>
	 * Save the screenshot.
	 * Save the view in the drawing cache, convert it to an image, and store it in the specified location.
	 * </pre>
	 * @param context context
	 * @param layout layout to capture
	 * @return captured or not
	 */
	@SuppressWarnings("unused")
	public static boolean capturePicture(Context context, FrameLayout layout) {
		try {
			layout.setDrawingCacheEnabled(true); // allow view to be stored in drawing cache
			layout.buildDrawingCache(true);      // save the view to the drawing cache
			Bitmap bm = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bm);
			Paint paint = new Paint();
			canvas.drawBitmap(bm, 0, 0, paint);
			layout.draw(canvas);
			File f = saveFile(bm, bm.getHeight());
			layout.buildDrawingCache(false);
			layout.setDrawingCacheEnabled(false);
			broadcastScanning(context, f); // broadcast to media
			return (f!=null);
		} catch (Exception e) {
			Log.e("CaptureUtil", e.toString());
			return false;
		}
	}


	/**
	 * 웹뷰를 캡처한다. 캡처대상은 웹뷰 전체이며, 단말기의 texture가 허용하는 한계까지 저장한다.<br>
	 * Capture the WebView. The capture target is the entire WebView, up to the size limit that the device's texture allows.
	 *
	 * @param context Context
	 * @param webview WebView
	 * @return captured or not
	 */
	public static boolean capturePicture(Context context, WebView webview) {
		int height = 0;
		int max    = 0;
		try {
			max = getTextureSize();
			if (Build.VERSION.SDK_INT>=21) WebView.enableSlowWholeDocumentDraw();
			webview.setDrawingCacheEnabled(true);
			webview.buildDrawingCache(true);
			webview.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			height = (max > 0 && webview.getMeasuredHeight() > max) ? max : webview.getMeasuredHeight();
			Bitmap bm = Bitmap.createBitmap(webview.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bm);
			Paint paint = new Paint();
			canvas.drawBitmap(bm, 0, height, paint);
			webview.draw(canvas);
			File f = saveFile(bm, height);
			webview.buildDrawingCache(false);
			webview.setDrawingCacheEnabled(false);
			broadcastScanning(context, f); // broadcast to media
			return (f!=null);
		} catch (Exception e) {
			Log.e("CaptureUtil", e.toString());
			Log.e("CaptureUtil", webview.getMeasuredWidth() + ":" + webview.getMeasuredHeight());
			Log.e("CaptureUtil", max + ":" + height);
			return false;
		}
	}

//	/**
//	 * 파일을 저장한다. 저장할 위치는 GlobalEnv.strRepositFoler에 정의되어 있다. <br>
//	 * Save File
//	 *
//	 * @param bm bitmap
//	 * @return saved or not
//
//	public static boolean saveFile(Bitmap bm) {
//	String fn = GlobalEnv.strRepositFolder + "/" + Long.toString(System.currentTimeMillis()) + GlobalEnv.strCapturedFileExt;
//	return saveFile(bm, fn);
//	}
//	 */
//
//	/**
//	 * 파일을 저장한다. 저장할 위치는 GlobalEnv.strRepositFoler에 정의되어 있다. <br>
//	 * Save File
//	 *
//	 * @param bm         bitmap
//	 * @param intWidth   width
//	 * @param intHeight  height
//	 * @return saved or not
//
//	public static boolean saveFile(Bitmap bm, int intWidth, int intHeight) {
//	String fn = GlobalEnv.strRepositFolder + "/" + Long.toString(System.currentTimeMillis()) + "_" + Integer.toString(intWidth) + "x" + Integer.toString(intHeight) + GlobalEnv.strCapturedFileExt;
//	return saveFile(bm, fn);
//	}
//	 */

	/**
	 * 파일을 저장한다. 저장할 위치는 GlobalEnv.strRepositFoler에 정의되어 있다. <br>
	 * Save File
	 *
	 * @param bm          bitmap
	 * @param intHeight   height
	 * @return saved or not
	 */
	private static File saveFile(Bitmap bm, int intHeight) {
		String fn = GlobalEnv.strRepositFolder + "/" + Long.toString(System.currentTimeMillis()) + "_" + Integer.toString(bm.getWidth()) + "x" + Integer.toString(intHeight) + GlobalEnv.strCapturedFileExt;
		return saveFile(bm, fn);
	}

	/**
	 * 파일을 저장한다. 저장할 위치는 GlobalEnv.strRepositFoler에 정의되어 있다. <br>
	 * Save File
	 *
	 * @param bm bitmap
	 * @param fn file name
	 * @return saved or not
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static File saveFile(Bitmap bm, String fn) {
		if (bm!=null) {
			File folder = new File(GlobalEnv.strRepositFolder);
			if (!folder.exists()) folder.mkdirs();

			File f = new File(fn);
			FileOutputStream fos;

			try {
				fos = new FileOutputStream(f);
				// bm = Bitmap.createScaledBitmap(bm, bm.getWidth()/3, bm.getHeight()/3, true);
				bm.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				bm.recycle();

				return f;
			} catch (Exception e) {
				Log.d("CaptureUtil.saveFile", e.toString());
			}
		}
		return null;
	}

	public static boolean saveJpgFile(Bitmap bitmap, String ext) {
		String fn = GlobalEnv.strRepositFolder + "/" + Long.toString(System.currentTimeMillis()) + ext;
		File f = saveFile(bitmap, fn);
		return (f!=null);
	}


	/**
	 * 저장할 이미지의 최대 크기를 구한다.<br>
	 * Get the maximum size of the image that can be stored.
	 *
	 * @return size
	 */
	private static int getTextureSize() {
		int[] i = new int[1];
		GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, i, 0);
		return i[0];
	}

	/**
	 * 이미지가 저장되면 미디어스캔을 요청한다.<br>
	 * When the image is saved, it requests the media scan.
	 *
	 * @param context context
	 */
	private static void broadcastScanning(Context context, File file) {
		try {
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(Uri.fromFile(file));
			context.sendBroadcast(intent);
			// context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + GlobalEnv.strRepositFolder)));
		} catch (Exception e) {
			Log.e("CaptureUtil", e.toString());
		}
	}

}
