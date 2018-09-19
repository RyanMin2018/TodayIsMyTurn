package kr.co.sology.fw.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;

import kr.co.sology.fw.ctrl.DialogCtrl;
import kr.co.sology.fw.ctrl.GeneralCallback;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

@SuppressWarnings("unused")
public class WifiUtil {


    public static String getWifiAddress(MainActivity m) {
        WifiManager wm = (WifiManager)m.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // ConnectivityManager cm = (ConnectivityManager)m.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert wm != null;
        if (wm.disconnect()) {
            DhcpInfo dhcpInfo = wm.getDhcpInfo() ;
            int serverIp = dhcpInfo.gateway;

            @SuppressLint("DefaultLocale")
            String ipAddress = String.format(
                    "%d.%d.%d.%d",
                    (serverIp & 0xff),
                    (serverIp >> 8 & 0xff),
                    (serverIp >> 16 & 0xff),
                    (serverIp >> 24 & 0xff));

            return ipAddress;
        } else {
            return "0.0.0.0";
        }
    }

    /**
     * Wifi Auto Connector
     *
     * @param mainActivity main activity
     * @param strSSID      ssid
     * @param strPw        password
     */
    public static void connectWifi(final MainActivity mainActivity, final GeneralCallback gc, final String strSSID, final String strPw) {
        WifiManager wm = (WifiManager)mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wm != null;
        if (wm.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
            wm.setWifiEnabled(true);
            new WifiSetEnableTask(mainActivity, gc, strSSID, strPw).execute();
        } else new WifiAutoSetTask(mainActivity, gc, strSSID, strPw).execute();
    }

    /**
     * Set Enable Wifi
     *
     */
    private static class WifiSetEnableTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        MainActivity mainActivity;
        GeneralCallback callback;
        String strSSID;
        String strPw;
        BufferedReader br;
        ProgressDialog pd;

        /**
         * constructor
         *
         * @param _m        main activity
         * @param _gc       callback
         * @param _strSSID  ssid
         * @param _strPw    password
         */
        WifiSetEnableTask(MainActivity _m, GeneralCallback _gc, String _strSSID, String _strPw) {
            mainActivity = _m;
            callback     = _gc;
            strSSID      = _strSSID;
            strPw        = _strPw;
            pd           = DialogCtrl.getProgressDialog(mainActivity, mainActivity.getString(R.string.wifi_auto_connecting));
        }

        @Override
        protected void onPreExecute() {
            if (pd!=null) pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                BroadcastReceiver br = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        WifiManager wm = (WifiManager)mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        assert wm != null;
                        if (wm.getWifiState()==WifiManager.WIFI_STATE_ENABLED) {
                            pd.dismiss();
                            new WifiAutoSetTask(mainActivity, callback, strSSID, strPw).execute();
                        }
                    }
                };
                mainActivity.registerReceiver(br, intentFilter);
            } catch (Exception e) {
                Log.e("WifiAutoSetTask", e.toString());
            }
            return null;
        }
    }


    /**
     * Wifi Auto Connector
     *
     */
    private static class WifiAutoSetTask extends AsyncTask<Void, Void, Void> {
        @SuppressLint("StaticFieldLeak")
        MainActivity mainActivity;
        GeneralCallback callback;
        String strSSID;
        String strPw;
        BufferedReader br;
        ProgressDialog pd;

        /**
         * Constructor
         *
         * @param _m       main activity
         * @param _strSSID ssid
         * @param _strPw   password
         */
        WifiAutoSetTask(MainActivity _m, GeneralCallback _gc, String _strSSID, String _strPw) {
            mainActivity = _m;
            callback     = _gc;
            strSSID      = _strSSID;
            strPw        = _strPw;
            if (pd==null) pd = DialogCtrl.getProgressDialog(mainActivity, mainActivity.getString(R.string.wifi_auto_connecting));
        }

        @Override
        protected void onPreExecute() {
            if (pd!=null) pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = String.format("\"%s\"", strSSID);
                wc.preSharedKey = String.format("\"%s\"", strPw);
                WifiManager wm = (WifiManager) mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                assert wm != null;
                int intId = wm.addNetwork(wc);

                wm.disconnect();
                wm.enableNetwork(intId, true);
                wm.reconnect();
                sendBroadcast();

            } catch (Exception e) {
                Log.e("WifiAutoSetTask", e.toString());
            }
            return null;
        }

        /**
         * broadcast & broadcast receiver
         *
         */
        private void sendBroadcast() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            BroadcastReceiver br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    NetworkInfo ni = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (ni.isConnected()) { // if wifi is fully connected
                        if (pd!=null) pd.dismiss();
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.result(true, mainActivity.getString(R.string.wifi_conected));
                            }
                        });

                    }
                }
            };
            mainActivity.registerReceiver(br, intentFilter);
        }


    }

}
