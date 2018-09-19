package kr.co.sology.fw.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import kr.co.sology.fw.core.GlobalEnv;
import kr.co.sology.todayismyturn.MainActivity;

@SuppressWarnings("unuse")
public class RuntimePermissionUtil {

    private MainActivity mainActivity;
    private static RuntimePermissionUtil instance;

    private RuntimePermissionUtil(MainActivity m) {
        mainActivity = m;
    }

    public static RuntimePermissionUtil getInstance(MainActivity m) {
        if (instance==null) instance = new RuntimePermissionUtil(m);
        return instance;
    }

    /**
     * is granted, or not?
     *
     * @param grantResults grant result
     * @return granted or not
     */
    public boolean isGranted(int[] grantResults) {
        return (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Prompt the user if they do not have permission to verify that they have obtained the authority.
     *
     * @param strPermission permission
     * @param intPermissionRequest permission request code
     * @return granted or not
     */
    private boolean hasPermission(String strPermission, int intPermissionRequest) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(mainActivity, strPermission)!=PackageManager.PERMISSION_DENIED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{strPermission}, intPermissionRequest);
        }
        return false;
    }

    /**
     * permission inspect for the contact information.
     *
     * @return has or not
     */
    public boolean hasPermissionReadContacts() {
        return hasPermission(Manifest.permission.READ_CONTACTS, GlobalEnv.intPermissionRequestReadContacts);
    }

    /**
     * permission inspect for the storage.
     *
     * @return has or not
     */
    public boolean hasPermissionAccessStorage() {
        boolean isOkay = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, GlobalEnv.intPermissionStorage);
        return (isOkay) && hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE, GlobalEnv.intPermissionStorage);
    }

    /**
     * permission inspect for audio.
     *
     * @return has or not
     */
    public boolean hasPermissionReadAudio() {
        return hasPermission("android.permission.RECORD_AUDIO", GlobalEnv.intPermissionReadAudio);
    }

    /**
     * permission inspect for camera.
     *
     * @return has or not
     */
    public boolean hasPermissionCamera() {
        return hasPermission(Manifest.permission.CAMERA, GlobalEnv.intPermissionRequestCamera);
    }

    /**
     * permission inspect for the location information.
     */
    public void hasPermissionLocation() {
        boolean isOkay = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION, GlobalEnv.intPermissionRequestMap);
        if (isOkay) hasPermission(Manifest.permission.ACCESS_FINE_LOCATION,  GlobalEnv.intPermissionRequestMap);
    }


}
