package kr.co.sology.fw.ctrl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

public class DialogCtrl {

    /**
     * Custom Progress Dialog Style.
     *
     * @return progress dialog
     */
    public static ProgressDialog getProgressDialog(MainActivity mainActivity, String strMessage) {
        ProgressDialog pd = new ProgressDialog(mainActivity, R.style.SologyTheme_DialogStyle);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(strMessage);
        return pd;
    }

    public static AlertDialog.Builder getAlertDialog(MainActivity mainActivity, String strMessage) {
        AlertDialog.Builder ab = new AlertDialog.Builder(mainActivity, R.style.SologyTheme_DialogStyle);
        ab.setCancelable(true);
        ab.setMessage(strMessage);
        return ab;
    }

}
