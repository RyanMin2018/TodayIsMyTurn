package kr.co.sology.fw.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Objects;

import kr.co.sology.todayismyturn.MainActivity;

/**
 * 이어폰을 사용하고 있는지의 여부 확인 유틸리티<br>
 * Utility that the headset plugged or not.
 *
 */
public class HeadsetCheckUtil {

    /**
     * 이어폰을 사용하고 있는지를 알려준다.
     *
     * @param m MainActivity
     * @return 이어폰사용여부
     */
    public static boolean isPlugged(MainActivity m) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Intent iStatus = m.registerReceiver(null, iFilter);
        return Objects.requireNonNull(iStatus).getIntExtra("state", 0)==1;
    }

    public static boolean isPlugged(Context c) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Intent iStatus = c.registerReceiver(null, iFilter);
        return Objects.requireNonNull(iStatus).getIntExtra("state", 0)==1;
    }

}
