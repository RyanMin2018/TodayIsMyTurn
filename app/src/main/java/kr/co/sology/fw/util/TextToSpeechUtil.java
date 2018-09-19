package kr.co.sology.fw.util;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

import kr.co.sology.todayismyturn.MainActivity;

/**
 * 문자를 음성을 읽어주는 유틸리티<br>
 * Text to Speech Utility
 * 
 */
public class TextToSpeechUtil {

	private static TextToSpeech tts;
	private static String       strSpeech;
	// private static ImageView    imageView;

	/**
	 * 읽기
	 *
	 * @param mainActivity context
	 * @param strText text to speech
     */
	public static void speech(MainActivity mainActivity, String strText) {
		speech(mainActivity.getApplicationContext(), strText);
	}

	public static  void speech(Context con, String strText) {
		strSpeech = strText;
		tts = new TextToSpeech(con, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status==TextToSpeech.SUCCESS) {
					tts.setLanguage(Locale.getDefault());
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						tts.speak(strSpeech, TextToSpeech.QUEUE_FLUSH, null, this.hashCode() + "");
					} else {
						HashMap<String, String> map = new HashMap<>();
						map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
						//noinspection deprecation
						tts.speak(strSpeech, TextToSpeech.QUEUE_FLUSH, map);
					}
					tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
						@Override
						public void onStart(String utteranceId) {
						}
						@Override
						public void onDone(String utteranceId) {
							stop();
						}
						@Override
						public void onError(String utteranceId) {
						}
					});
				}
			}
		});
	}

	/*
	@SuppressLint("NewApi")
	private static void showSpeaker(final MainActivity mainActivity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					imageView = new ImageView(mainActivity);
					imageView.setScaleType(ImageView.ScaleType.CENTER);
					imageView.setImageResource(R.drawable.speaker);
					mainActivity.getBodyLayout().addView(imageView);
				} catch (Exception e) {
					Log.e("TextToSpeechUtil", e.toString());
				}
			}
		}).start();

	}


	private static void hideSpeaker() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (imageView != null) imageView.setVisibility(View.GONE);
				} catch (Exception e) {
					Log.e("TextToSpeechUtil", "iv.hide() : " + e.toString());
					e.printStackTrace();
				}
			}
		}).start();
	}
	*/


	/**
	 * 멈추기
	 *
	 */
	public static void stop() {
		strSpeech = "";
		try {
			if (tts!=null) {
				tts.stop();
				tts.shutdown();
			}
		} catch (Exception e) {
			Log.e("TextToSpeechUtil", "tts stop() : " + e.toString());
			e.printStackTrace();
		}
	}
	
}

	
