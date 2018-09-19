package kr.co.sology.fw.ctrl;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.sology.fw.core.GlobalEnv;
import kr.co.sology.fw.multilingual.LanguageMgr;
import kr.co.sology.fw.util.NetworkConnectUtil;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

@SuppressWarnings("unused")
public class UpgradeCtrl {

    private String            strLogId = "UpgradeControl";
    private MainActivity      mainActivity;
    private SharedPreferences sp;

    public UpgradeCtrl(MainActivity m) {
        mainActivity = m;
        new VersionCheckTask().execute();
    }

    /**
     *  get Shared Preferences
     *
     * @return SharedPreferences
     */
    private SharedPreferences getSharedPreferences() {
        if (sp==null) sp = mainActivity.getSharedPreferences(GlobalEnv.strRepositName, Context.MODE_PRIVATE);
        return sp;
    }

    /**
     * Asynchronous task for getting new version or notice.
     *
     */
    @SuppressLint("StaticFieldLeak")
    private class VersionCheckTask extends AsyncTask<Void, Void, Void> {
        private int intCurrentVersion = getApplicationVersion();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (NetworkConnectUtil.isNetworkConnect(mainActivity)) {
                try {
                    URL url = new URL(GlobalEnv.strVersionUrl + LanguageMgr.getLocaleCode());
                    Log.d(strLogId, url.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    if (conn != null) {
                        conn.setConnectTimeout(2000);
                        conn.setUseCaches(false);
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            String s;
                            while ((s = br.readLine()) != null) {
                                if (s.trim().contains(GlobalEnv.strApplicationVersionKey))
                                    intCurrentVersion = Integer.parseInt(s.split(GlobalEnv.strApplicationVersionKey)[1]);
                            }
                            br.close();
                        }
                    }
                    if (conn != null) conn.disconnect();

                } catch (Exception e) {
                    Log.e(strLogId, e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (intCurrentVersion>getApplicationVersion()) {
                showNewVersion();
            }
        }
    }




    /**
     * Get application version code.
     *
     * @return application version
     */
    private int getApplicationVersion() {
        int intVersionCode = 0;
        try {
            intVersionCode = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            Log.e(strLogId, "getApplicationVersion() : " + e.toString());
        }
        return intVersionCode;
    }

    /**
     * Guide the new version and request download
     *
     */
    private void showNewVersion() {
        AlertDialog.Builder ab = DialogCtrl.getAlertDialog(mainActivity, mainActivity.getString(R.string.version_new_notify));
        ab.setCancelable(true);
        ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalEnv.strMarketUrl));
                mainActivity.startActivity(intent);
                mainActivity.finish();
            }
        });
        ab.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //
            }
        });
        AlertDialog alert = ab.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
        ((TextView) alert.findViewById(android.R.id.message)).setTextSize(GlobalEnv.intDialogMessageTextSize);
    }


}
