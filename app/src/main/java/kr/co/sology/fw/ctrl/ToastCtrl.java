package kr.co.sology.fw.ctrl;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

public class ToastCtrl {

    public static void showMessageLong(MainActivity mainActivity, String str) {
        showMessage(mainActivity, Toast.LENGTH_LONG, str);
    }

    public static void showMessageShort(MainActivity mainActivity, String str) {
        showMessage(mainActivity, Toast.LENGTH_SHORT, str);
    }

    private static void showMessage(MainActivity mainActivity, int intInterval, String str) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_toast, (ViewGroup)mainActivity.findViewById(R.id.toast_layout_root));
        ((TextView) view.findViewById(R.id.toast_message)).setText(str);
        Toast toast = new Toast(mainActivity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(view);
        toast.setDuration(intInterval);
        toast.show();
    }

}
