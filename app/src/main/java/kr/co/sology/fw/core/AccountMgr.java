package kr.co.sology.fw.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import kr.co.sology.todayismyturn.MainActivity;

/**
 * 계정관리자
 *
 */
@SuppressWarnings("unused")
public class AccountMgr {

    /**
     * 디바이스의 고유한 아이디를 가져온다.
     *
     * @param _mainActivity MainActivity
     * @return ANDROID_ID
     */
    @SuppressLint("HardwareIds")
    public static String getSecureAndroidId(MainActivity _mainActivity) {
        return getDeviceId(_mainActivity.getApplicationContext());
    }

    /**
     * 디바이스의 고유한 아이디를 가져온다.
     *
     * @param context Context
     * @return ANDROID_ID
     */
    @SuppressLint("HardwareIds")
    public static String getSecureAndroidId(Context context) {
        return getDeviceId(context);
    }

    /**
     * 디바이스의 고유한 아이디 값을 가져온다.
     *
     * @param context context
     * @return andorid id
     */
    @SuppressLint("HardwareIds")
    private static String getDeviceId(Context context) {
        try {
            String id = null;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                assert tm != null;
                id = tm.getDeviceId();
            }
            if (id == null) return getAndroidId(context);
            else return id;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 디바이스의 고유한 아이디(android.provider.Settings.Secure.ANDROID_ID)를 가져온다.
     * @param context context
     * @return andorid id
     */
    @SuppressLint("HardwareIds")
    private static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceEnvironment() {
        return Build.MODEL + GlobalEnv.strSeparator + Build.VERSION.RELEASE;
    }

}
