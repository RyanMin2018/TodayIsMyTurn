package kr.co.sology.fw.util;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.Objects;

/**
 * 네트워크 연결 여부 확인 유틸리티<br>
 * Utility to check if a connection to the network(WIFI & mobile) is established
 *
 */
@SuppressWarnings("unused")
public class NetworkConnectUtil {

	  /**
	   * 네트워크가 연결되어 있는지를 알려준다.
	   * 
	   * @return connected or not
	   */
	  public static boolean isNetworkConnect(Context con) {
	  	try {
			ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
			return (Objects.requireNonNull(cm).getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() || cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
		} catch (Exception e) {
	  		e.printStackTrace();
	  		return false;
		}
	  }
	
	
}
