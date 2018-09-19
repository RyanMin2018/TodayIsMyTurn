package kr.co.sology.fw.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import kr.co.sology.todayismyturn.MainActivity;

/**
 * Camera Utility
 *
 */
public class CameraUtil implements SurfaceHolder.Callback {

    private static final ThreadLocal<CameraUtil> instance = new ThreadLocal<>(); // this instance
    private MainActivity  mainActivity;
    private FrameLayout   layout;
    private Camera        camera;
    private SurfaceView   surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean       isPreviewMode = false;

    private CameraUtil(MainActivity m, FrameLayout fl) {
        mainActivity = m;
        layout       = fl;

    }

    /**
     * get instance for single-tone
     *
     * @param m mainActivity
     * @return this instance
     */
    public static CameraUtil getInstance(MainActivity m, FrameLayout fl) {
        if (instance.get() ==null) instance.set(new CameraUtil(m, fl));
        instance.get().initialize();
        return instance.get();
    }

    /**
     * initialize Camera Mode, SurfaceView
     *
     */
    private void initialize() {
        if (!isPreviewMode) {
            if (surfaceView==null || !surfaceView.isShown()) {
                surfaceView  = new SurfaceView(mainActivity.getApplicationContext());

                // surfaceView.setZOrderOnTop(true);
                surfaceHolder = surfaceView.getHolder();
                // surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
                surfaceHolder.addCallback(instance.get());

                layout.addView(surfaceView);
            }
            surfaceView.setVisibility(View.VISIBLE); // call surfaceCreated & surfaceChanged
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera!=null) camera.takePicture(null, null, PictureCallbackJpeg);
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera==null) camera = Camera.open();
        if (camera!=null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                // set size
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
                int intWidth = 0, intHeight = 0;
                for (Camera.Size size : sizes) { // get supported max size
                    if (intWidth<size.width) {
                        intWidth = size.width;
                        intHeight = size.height;
                    }
                }
                Log.d("CameraUtil", Integer.toString(intWidth) + "x" + Integer.toString(intHeight));
                parameters.setPreviewSize(intWidth, intHeight); // preview size
                parameters.setPictureSize(intWidth, intHeight); // picture size
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE); // auto-focus
                camera.setParameters(parameters);
                camera.startPreview();
                isPreviewMode = true;
                // camera.takePicture(null, null, PictureCallbackJpeg);
            } catch (Exception e) {
                isPreviewMode = false;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        isPreviewMode = false;
    }

    /**
     * picture callback
     *
     */
    private Camera.PictureCallback PictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (CaptureUtil.saveJpgFile(bitmap, ".jpg")) surfaceView.setVisibility(View.GONE); // call surfaceDestroyed
        }
    };
}
