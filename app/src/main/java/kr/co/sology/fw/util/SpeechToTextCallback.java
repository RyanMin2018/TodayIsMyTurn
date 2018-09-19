package kr.co.sology.fw.util;

/**
 * 음성을 문자로 변환하기 위한 콜백 인터페이스
 *
 */
public interface SpeechToTextCallback {
	void getSpeech(String str);
	void startSpeech();
	void readVolume(int intVolume);
	void stopSpeech();
}
