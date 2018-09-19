package kr.co.sology.fw.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import kr.co.sology.todayismyturn.MainActivity;

public class SnapShotUtil {

    private SnapShotCallback callback;
    private Camera camera;
    private int intDegree = 90; // fixed portrait mode

    public SnapShotUtil(MainActivity mainActivity, SnapShotCallback c) {
        callback = c;
        SurfaceView surfaceView     = new SurfaceView(mainActivity.getApplicationContext());
        SurfaceHolder surfaceHolder = surfaceView.getHolder();

        try {
            camera = Camera.open();
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(intDegree);

            SurfaceTexture st = new SurfaceTexture(10);
            camera.setPreviewTexture(st);

            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            int intWidth = 0, intHeight = 0;
            for (Camera.Size size : sizes) { // get supported max size
                if (intWidth < size.width) {
                    intWidth = size.width;
                    intHeight = size.height;
                }
            }
            parameters.setPreviewSize(intWidth, intHeight); // preview size
            parameters.setPictureSize(intWidth, intHeight); // picture size
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); // auto-focus
            camera.setParameters(parameters);
            camera.startPreview();

            Camera.PictureCallback pictureCallbackJpeg = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (CaptureUtil.saveJpgFile(rotate(bitmap), ".jpg")) {
                        releaseCameraAndCallback(true);
                    }
                }
            };
            camera.takePicture(null, null, pictureCallbackJpeg);
        } catch (Exception e) {
            releaseCameraAndCallback(false);
            Log.e("SnapShotUtil", e.toString());
            e.printStackTrace();
        }
    }

    /**
     * release camera
     *
     */
    private void releaseCameraAndCallback(boolean isSuccess) {
        if (camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        callback.end(isSuccess);
    }

    /**
     * rotate bitmap
     *
     * @param bitmap  bitmap
     * @return bitmap
     */
    private Bitmap rotate(Bitmap bitmap) {
        if(bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(intDegree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch(OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

}
