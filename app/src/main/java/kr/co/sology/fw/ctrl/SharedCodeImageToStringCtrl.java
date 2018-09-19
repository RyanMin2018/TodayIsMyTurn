package kr.co.sology.fw.ctrl;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.fw.util.BarCodeImageDecodeUtil;
import kr.co.sology.todayismyturn.R;

public class SharedCodeImageToStringCtrl {

    private MainActivity mainActivity;
    private Intent intent;
    private GeneralCallback callback;

    public SharedCodeImageToStringCtrl(MainActivity m, Intent i, GeneralCallback c) {
        mainActivity = m;
        intent = i;
        callback = c;
        new BarCodeTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class BarCodeTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd = DialogCtrl.getProgressDialog(mainActivity, mainActivity.getString(R.string.check_shared_image)); // progress
        Bitmap bitmap = null;
        String strResult = null;
        @Override
        protected void onPreExecute() {
            pd.show();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri!=null) {
                bitmap = BarCodeImageDecodeUtil.convertBitmap(mainActivity, uri);
                strResult = BarCodeImageDecodeUtil.decode(bitmap);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            pd.dismiss();
            callback.result(((strResult==null)?false:true), strResult);
            super.onPostExecute(aVoid);
        }
    }

}
