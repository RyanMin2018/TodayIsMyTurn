package kr.co.sology.fw.util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import kr.co.sology.fw.ctrl.ToastCtrl;
import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

/**
 * 음성인식 유틸리티<br>
 * Utility that convert speech to characters
 *
 */
public class SpeechToTextUtil {

	private static String strLogId = "SpeechToTextUtil";
	private static SpeechToTextUtil     thisInstance;
	@SuppressLint("StaticFieldLeak")
	private static MainActivity         mainActivity;
	private static Intent               intentAudio;
	private static SpeechToTextCallback callback;
	private static SpeechRecognizer     speechRecognizer;


	/**
	 * 생성자
	 *
	 */
	private SpeechToTextUtil() {
		intentAudio = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intentAudio.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mainActivity.getPackageName());

		/* get language */
		String strLanguage = Locale.getDefault().toString();
		strLanguage = strLanguage.replace("_", "-");
		if (strLanguage.contains("zh")) strLanguage = "cmn-Hans-CN"; // chinese
		Log.d("speech", strLanguage);

		intentAudio.putExtra(RecognizerIntent.EXTRA_LANGUAGE, strLanguage);
	}
	
	/**
	 * 음성인식기능을 준비시킨다.
	 * 
	 * @param m 메인액티비티 인스턴스
	 * @param c 콜백 인스턴스
	 * @return 음성 인식 인스턴스
	 */
	public static SpeechToTextUtil getInstance(MainActivity m, SpeechToTextCallback c) {
		mainActivity = (mainActivity==null)  ? m : mainActivity;
		callback     = (callback==null)      ? c : callback;
		thisInstance = (thisInstance ==null) ? new SpeechToTextUtil() : thisInstance;
		return thisInstance;
	}
	
	/**
	 * 음성인식기능을 시작한다. <br>
	 * 음성인식기능을 사용할 수 없으면, 이를 알린다.
	 * 
	 */
	public void speech() {
		Log.d(strLogId, "called speech()");
		Handler uh = new Handler(Looper.getMainLooper());
		uh.post(new Runnable(){
			@Override
			public void run() {
				if (SpeechRecognizer.isRecognitionAvailable(mainActivity)) {
					speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mainActivity.getApplicationContext());
					speechRecognizer.setRecognitionListener(recognitionlistener);
					speechRecognizer.startListening(intentAudio);
				} else {
					ToastCtrl.showMessageLong(mainActivity, mainActivity.getString(R.string.msg_no_voice));
					close();
				}
			}
		});
	}


	/**
	 * 음성을 받아들이기 위한 리스너
	 * 
	 */
	private static RecognitionListener recognitionlistener = new RecognitionListener() {

		@Override
		public void onReadyForSpeech(Bundle params) {
			callback.startSpeech();
		}

		@Override
		public void onBeginningOfSpeech() {

		}

		@Override
		public void onRmsChanged(float rmsdB) {
            callback.readVolume(Math.round(rmsdB));
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
		}

		@Override
		public void onEndOfSpeech() {
			//Log.d(strLogId, "onEndOfSpeech");
			// close();
		}

		@Override
		public void onError(int error) {
			ToastCtrl.showMessageLong(mainActivity, mainActivity.getString(R.string.msg_no_voice));
			Log.d(strLogId, "onError : " + error);
			close();
		}

		@Override
		public void onResults(Bundle results) {
			String key = SpeechRecognizer.RESULTS_RECOGNITION;
			ArrayList<String> arr = results.getStringArrayList(key);
			callback.getSpeech(((arr!=null && arr.size()>0) ? arr.get(0) : ""));
			close();
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
			Log.d(strLogId, "onPartialResults");

		}

		@Override
		public void onEvent(int eventType, Bundle params) {
			Log.d(strLogId, "onEvent");

		}
	};
	
	/**
	 * 음성인식기능을 종료한다.
	 *
	 */
	private static void close() {
		Log.d("SpeechToTextCtrl.close", "stt is closed.");
		if (speechRecognizer !=null) speechRecognizer.destroy();
		if (callback !=null) callback.stopSpeech();
	}
}



