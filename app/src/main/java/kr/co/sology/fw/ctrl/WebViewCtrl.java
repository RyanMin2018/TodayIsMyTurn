package kr.co.sology.fw.ctrl;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.Objects;

import kr.co.sology.fw.core.GlobalEnv;
import kr.co.sology.fw.multilingual.LanguageMgr;
import kr.co.sology.fw.util.CaptureUtil;
import kr.co.sology.fw.util.HeadsetCheckUtil;
import kr.co.sology.fw.util.NetworkConnectUtil;
import kr.co.sology.fw.util.QRCodeUtil;
import kr.co.sology.fw.util.RuntimePermissionUtil;
import kr.co.sology.fw.util.SnapShotCallback;
import kr.co.sology.fw.util.SnapShotUtil;
import kr.co.sology.fw.util.SpeechToTextCallback;
import kr.co.sology.fw.util.SpeechToTextUtil;
import kr.co.sology.fw.util.StrUtil;
import kr.co.sology.fw.util.TextToSpeechUtil;
import kr.co.sology.fw.util.TranslateUtil;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

/**
 * WebView Controller.
 *
 *
 */
@SuppressLint("InflateParams")
public class WebViewCtrl {

	private String       strLogId      = "WebViewCtrl";

	private MainActivity mainActivity;
	private WebView      webView;     // WEBVIEW
	private ProgressBar  progressBar; // LOADING STATUS
	private EditText     urlAddress;  // ADDRESS INPUT BOX. This variable is also referred to externally. ex) AdvertiseMessage

//	private boolean isLongClickToScreenCapture = false; // WHETHER TO ALLOW LONG EVENTS
	private boolean isHistoryClear             = false; // WHETHER OR NOT TO ERASE HISTORY
	private boolean isCaptured                 = false; // WHETHER THE SCREEN CAPTURE IS COMPLETE
	private boolean isDailyPopupOpened         = false; // WHETHER A DAILY POPUP IS FLOATING

	/**
	 * Constructor
	 *
	 * @param c MainActivity
	 */
	public WebViewCtrl(MainActivity c, ConstraintLayout layout) {
		mainActivity = c;
		try {
            View.inflate(mainActivity, R.layout.layout_webview, layout);
            initProgressBar();
            initAddressBar();
            initWebView();
        } catch (Exception e) {
		    Log.e(strLogId, e.toString());
        }
	}

	////////////////////////////////////////////////////////////////
	//
	// INITIALIZATION
	//
	////////////////////////////////////////////////////////////////

	/**
	 * 진행바/로딩바 초기화
	 */
	private void initProgressBar() {
		progressBar = mainActivity.findViewById(R.id.webview_progressbar);
	}

	/**
	 * URL 입력창 초기화
	 *
	 */
	private void initAddressBar() {
		/* ADDRESS BAR */
		urlAddress = mainActivity.findViewById(R.id.webview_addr);
		urlAddress.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int id, KeyEvent ke) {
				switch (id) {
					case 0 : // ENTER KEY
						InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
						assert imm != null;
						imm.hideSoftInputFromWindow(urlAddress.getWindowToken(),0);
						loadPage(urlAddress.getText().toString());
						break;
				}
				return false;
			}
		});

		urlAddress.setVisibility(View.GONE); // HIDE ADDRESS-AREA

	}


	/**
	 * 웹뷰 초기화
	 *
	 */
	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	private void initWebView() {
		webView = mainActivity.findViewById(R.id.webview_webview);
		// webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // IF THIS IS SET, THE SCREEN FLICKER DISAPPEARS BUT THE MOVIE DOES NOT PLAY.
		WebSettings ws = webView.getSettings();
		ws.setDefaultTextEncodingName("utf-8");
		ws.setBuiltInZoomControls(false);           // DEACTIVATE ZOOM CONTROL
		ws.setJavaScriptEnabled(true);              // ACTIVATE JAVA-SCRIPT
		ws.setDomStorageEnabled(true);              // ACTIVATE LOCAL STORAGE
		ws.setAppCacheEnabled(false);
		ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
		ws.setLoadWithOverviewMode(true);
		ws.setUseWideViewPort(true);
		ws.setSaveFormData(false);
		ws.setAllowFileAccess(true);
		webView.getSettings().setSupportMultipleWindows(true); // add for movie

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             ws.setSafeBrowsingEnabled(false);
        }

        webView.addJavascriptInterface(new AndroidBridge(), GlobalEnv.strJavascriptDomainKey); // ACTIVATE JAVA-SCRIPT INTERFACE
		webView.setWebChromeClient(new WebChromeOverride());
		webView.setWebViewClient(new WebViewOverride());

	}

	/**
	 * Override On Key Listener.
	 *
	 */
	private void controlOnKeyListener() {
		webView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction()==KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK) { // 백버튼 클릭
						if (isDailyPopupOpened) { // 일정팝업이 열려있으면 닫는다
							try {
								runScript("closePopup('scale');"); // @see Schedule.js, ScheduleForm.js
							} catch (Exception e) {
								Log.e(strLogId, e.toString());
							}
							isDailyPopupOpened = false;
						} else { // 일정팝업이 닫혀 있으면, GOBACK() 메소드를 호출하거나 메인엑티비티의 ONBACKPRESSED() 메소드를 호출한다.
							if (webView.canGoBack()) webView.goBack();
							else mainActivity.onBackPressed();
						}
						return true;
					}
					// if (keyCode == KeyEvent.KEYCODE_MENU) EasterEgg(); // 메뉴버튼 클릭 : Call EasterEgg
				}
				return false;
			}
		});
	}

    ////////////////////////////////////////////////////////////////
	//
	// PAGE LOAD
	//
	////////////////////////////////////////////////////////////////

	/**
	 * load page
	 *
	 * @param intKey     key
	 * @param strUrl     url
	 */
	private void loadPage(int intKey, String strUrl) {
		isHistoryClear             = true;

		switch (intKey) {
			case GlobalEnv.intMenuIdxBrowser :   // CALL WEB BROWSER PAGE
				// urlAddress.setVisibility(View.VISIBLE);
				if (strUrl==null || strUrl.equals("")) strUrl = LanguageMgr.getDefaultUrl();
				webView.loadUrl(strUrl);
				break;
			case GlobalEnv.intMenuIdxUserGuide :
				String strHelpUrl = GlobalEnv.strAssetsPath + "/UserGuide.htm";
				webView.loadUrl(strHelpUrl);
				break;
		}
		// controlOnLongClickListener(); // SET LONG-CLICK-LISTENER EXCEPT BROWSER-MODE
		controlOnKeyListener();
		if (intKey==GlobalEnv.intMenuIdxBrowser && !NetworkConnectUtil.isNetworkConnect(mainActivity)) ToastCtrl.showMessageLong(mainActivity, mainActivity.getString(R.string.msg_not_connect));
	}

	/**
	 * load page with url
	 *
	 * @param strUrl url
	 */
	public void loadPage(String strUrl) {
		strUrl = StrUtil.nvl(strUrl, LanguageMgr.getDefaultUrl());
		if (!strUrl.substring(0, 4).equals("http") && !strUrl.substring(0, 4).equals("file") && !strUrl.contains(GlobalEnv.strCustomProtocol)) {
			strUrl = "http://" + strUrl;
		}
		if (strUrl.substring(0,4).equals("file")) this.loadPage(GlobalEnv.intMenuIdxSchedule, strUrl);
		else if (strUrl.contains(GlobalEnv.strCustomProtocol)) webView.loadUrl(strUrl.replace(GlobalEnv.strCustomProtocol, "http://"));
		else this.loadPage(GlobalEnv.intMenuIdxBrowser, strUrl);
	}

	////////////////////////////////////////////////////////////////
	//
	// INTERFACE
	//
	////////////////////////////////////////////////////////////////

	/**
	 * 외부 클래스에서 웹뷰가 호출한 웹 페이지의 자바스크립트를 실행하게 하기 위한 인터페이스
	 *
	 *
     * @param str javaScript syntax to execute
     */
    private void runScript(String str) {
		webView.loadUrl("javascript:" + str);
	}


	////////////////////////////////////////////////////////////////
	//
	// OVRRIDE WEBVIEW-CLIENT & WEB-CHROME-CLIENT
	//
	////////////////////////////////////////////////////////////////

	/**
	 * <a> 태그를 클릭했을때, "tel:", "mailto:", "sms:" 등의 프로토콜은 외부 액티비티에게 넘긴다
	 *
	 * @param view WebView
	 * @param url  href attribute's value at a tag
	 */
	private void setExternalActivityExecution(WebView view, String url) {
		if (url.startsWith("tel:")) {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
			mainActivity.startActivity(intent);
		} else if (url.startsWith("mailto:") || url.startsWith("sms:")) {
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
			mainActivity.startActivity(intent);
		} else if (url.startsWith("market:")) {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			mainActivity.startActivity(intent);
		} else {
			progressBar.setVisibility(View.VISIBLE);
			view.loadUrl(url);
		}
	}

	/**
	 * Override WebViewClient.
	 *
	 */
	private class WebViewOverride extends WebViewClient {

		/**
		 * <a> 태그 클릭 이벤트 처리
		 *
		 * @param view WebView
		 * @param url  href attribute's value at a tag
		 * @return success or not
		 */
		@SuppressWarnings("deprecation")
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			setExternalActivityExecution(view, url);
			return true;
		}

		/**
		 * 웹페이지 로딩이 시작되면 실행한다
		 *
		 * @param view WebView
		 * @param url  page to load
		 * @param favicon favicon
		 */
		@Override
		public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			progressBar.setVisibility(View.VISIBLE); // 로딩진행바
			if (urlAddress !=null) urlAddress.setText(url); // 브라우저뷰이면 url 주소창에 주소를 대입한다
		}

		/**
		 * 웹페이지 로딩이 완료되면 실행한다
		 *
		 * @param view WebView
		 * @param url  page is loaded
		 */
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			progressBar.setVisibility(View.GONE);
			if (isHistoryClear || url.contains(GlobalEnv.strDefaultWebPage)) { // If the page is opened from loadPage() or url is "Schedule.htm", the history is cleared.
				webView.clearHistory();
				isHistoryClear = false;
			}
			isDailyPopupOpened = false;
		}

		/**
		 * 입력창에서 resubmit이 발생하면
		 *
		 * @param view WebView
		 * @param dontResend don't resend
		 * @param resend message
		 */
		@Override
		public void onFormResubmission(WebView view, Message dontResend, Message resend) {
			resend.sendToTarget();
		}

	} // End Of WebViewOverride Class

	/**
	 * Override WebChromeClient for communication with JavaScript.
	 *
	 */
	private class WebChromeOverride extends WebChromeClient {

		/**
		 * Page loading progress
		 *
		 * @param view        WebView
		 * @param newProgress updated progress
		 */
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			progressBar.setProgress(newProgress);
		}

		/**
		 * Handling 'alert()' commands in JavaScript
		 *
		 * @param view    WebView
		 * @param url     page is called
		 * @param message message
		 * @param result  result
		 * @return show or not
		 */
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		    AlertDialog.Builder ab = DialogCtrl.getAlertDialog(mainActivity, message);
		    ab.setPositiveButton(android.R.string.ok, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
			ab.create();
			ab.show();
			return true;
		}

		/**
		 * Handling 'confirm()' commands in JavaScript
		 *
		 * @param view     WebView
		 * @param url      page is called
		 * @param message  message
		 * @param result   result
		 * @return show or not
		 */
		@Override
		public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder ab = DialogCtrl.getAlertDialog(mainActivity, message);
            ab.setPositiveButton(android.R.string.ok, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					});
			ab.setNegativeButton(android.R.string.cancel, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					});
            ab.create();
            ab.show();
			return true;
		}

	} // End of WebChromeOverride Class


	////////////////////////////////////////////////////////////////
	//
	// WEBVIEW AND WEB-PAGE COMMUNICATION THROUGH JAVASCRIPT
	//
	////////////////////////////////////////////////////////////////

	/**
	 * 어플리케이션과 웹페이지를 자바스크립트로 인터페이스한다
	 *
	 */
	private class AndroidBridge {

		/**
		 * Capture Screen
		 *
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public void capture() { // CAPTURE SCREEN
			try {
				new CaptureTask().execute();
			} catch (Exception e) {
				Log.e(strLogId, e.toString());
			}
		}

		/**
		 * Take Picture
		 *
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public void takePicture() {
            Handler uh = new Handler(Looper.getMainLooper());
            uh.post(new Runnable() {
                @Override
                public void run() {
                    // CameraUtil.getInstance(mainActivity);
                    SnapShotCallback callback = new SnapShotCallback() {
                        @Override
                        public void end(boolean isSuccess) {
                            if (isSuccess) ((Vibrator) Objects.requireNonNull(mainActivity.getSystemService(Context.VIBRATOR_SERVICE))).vibrate(100);
                            else ((Vibrator) Objects.requireNonNull(mainActivity.getSystemService(Context.VIBRATOR_SERVICE))).vibrate(new long[]{100,100,100, 100}, -1);
                        }
                    };
                    new SnapShotUtil(mainActivity, callback);
                }
            });
		}


		/**
		 * Speeh to Text
		 *
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public void listen(String strTargetField) { // SEARCH BY SPEECH
			startSpeechToText(strTargetField);
		}

		/**
		 * Text to Speech
		 *
		 * @param str text to read.
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public void textToSpeech(final String str) { // TEXT TO SPEECH
			Handler uh = new Handler(Looper.getMainLooper());
			uh.post(new Runnable() {
				@Override
				public void run() {
					startTextToSpeech(str);
				}
			});
		}

		/**
		 * Stop Speech
		 *
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public void stopSpeech() {
			Handler uh = new Handler(Looper.getMainLooper());
			uh.post(new Runnable() {
				@Override
				public void run() {
					stopTextToSpeech();
				}
			});
		}

		/**
		 * Is Headset Connected?
		 *
		 * @return headset is connected or not
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public boolean isHeadSetPlugged() {
			return HeadsetCheckUtil.isPlugged(mainActivity);
		}


		/**
		 * call toast
		 *
		 * @param str Message
		 */
        @SuppressWarnings("unused")
		@JavascriptInterface
		public void viewMessage(final String str) {
			Handler uh = new Handler(Looper.getMainLooper());
			uh.post(new Runnable() {
				@Override
				public void run() {
					ToastCtrl.showMessageLong(mainActivity, str);
				}
			});
		}

		/**
		 * Vibrate
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public void vibrate() {
			Vibrator vibe = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
            assert vibe != null;
            vibe.vibrate(300);
		}

		/**
		 * 다국어 사용을 위해 R.string에 등록된 키로 값을 호출
		 * <pre>
		 * R.string is organized by language and automatically imported according to the language setting of the terminal.
		 *
		 * </pre>
		 *
		 * @param strStringId id in R.string (strings.xml)
		 * @return string
		 */
        @SuppressWarnings("unused")
		@JavascriptInterface
		public String getString(String strStringId) {
			return mainActivity.getString(mainActivity.getResources().getIdentifier(strStringId, "string", mainActivity.getPackageName()));
		}

		////////////////////////////////////////////////////////////////
		//
		// SHARING
		//
		////////////////////////////////////////////////////////////////

		/**
		 * Share
		 *
		 * @param strSubject title
		 * @param strText    content
		 */
        @SuppressWarnings("unused")
		@JavascriptInterface
		public void share(String strSubject, String strText) {
			try {
				Intent msg = new Intent(Intent.ACTION_SEND);
				msg.addCategory(Intent.CATEGORY_DEFAULT);
				msg.putExtra(Intent.EXTRA_SUBJECT, strSubject);
				msg.putExtra(Intent.EXTRA_TEXT, strText);
				msg.setType("text/plain");
				mainActivity.startActivity(Intent.createChooser(msg, mainActivity.getString(R.string.share)));
			} catch (Exception e) {
				Log.e(strLogId, "share() : " + e.toString());
			}
		}

		/**
		 * 네트워크에 연결되어 있는지
		 *
		 * @return result of connecting to network
		 */
		@SuppressWarnings("unused")
		@JavascriptInterface
		public boolean isConnectNetwork() {
			return NetworkConnectUtil.isNetworkConnect(mainActivity);
		}


		/**
		 * Get QRCode
		 * @param strMsg message to generate qr code
		 * @return base64 format bitmap data
		 */
        @SuppressWarnings("unused")
        @JavascriptInterface
        public String getQRCode(String strMsg) {
            return QRCodeUtil.getBase64String(QRCodeUtil.getQRCode(strMsg, 150, Color.BLACK, Color.WHITE));
        }

	}


	////////////////////////////////////////////////////////////////
	//
	// ASYNCHRONOUS COMMUNICATION TASK
	//
	////////////////////////////////////////////////////////////////

	/**
	 * 스크린 캡처를 위한 비동기 처리
	 *
	 */
	@SuppressLint("StaticFieldLeak")
	private class CaptureTask extends AsyncTask<Void, Integer, Void> {
		ProgressDialog pd = DialogCtrl.getProgressDialog(mainActivity, mainActivity.getString(R.string.scrap_msg_saving));
		@Override
		protected void onPreExecute() {
			isCaptured = false;
			pd.show();
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			captureScreen();
			return null;
		}
		@Override
		protected void onPostExecute(Void aVoid) {
			pd.dismiss();
			if (isCaptured) ToastCtrl.showMessageShort(mainActivity, mainActivity.getString(R.string.scrap_msg_save_ok));
			else ToastCtrl.showMessageShort(mainActivity, mainActivity.getString(R.string.scrap_msg_save_fail));
			super.onPostExecute(aVoid);
		}
	}

	/**
	 * 번역을 위한 비동기 처리
	 */
	@SuppressLint("StaticFieldLeak")
	private class TranslateTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			return TranslateUtil.transViaNaver(params[0], params[1], params[2]);
		}
		@Override
		protected void onPostExecute(String str) {
			super.onPostExecute(str);
		}
	}

	////////////////////////////////////////////////////////////////
	//
	// SPEECH RECOGNITION
	//
	////////////////////////////////////////////////////////////////

    private String strTargetField;

	/**
	 * STT 시작. "android.permission.RECORD_AUDIO" 권한 필요.
	 */
	private void startSpeechToText(String strField) {
        strTargetField = strField;
		if (RuntimePermissionUtil.getInstance(mainActivity).hasPermissionReadAudio()) SpeechToTextUtil.getInstance(mainActivity, callbackSpeechToText).speech();
	}

	/**
	 * STT 수신결과 콜백
	 *
	 */
	private SpeechToTextCallback callbackSpeechToText = new SpeechToTextCallback() {
		@Override
		public void getSpeech(String str) { // FORWARD A SPEECH-RECOGNIZED STRING TO A WEB PAGE
			// runScript("$('#searchwho').val('"+str+"');");
            str = strTargetField.replace("%val%", str).replace("_space_", " ");
            // Log.d(strLogId, str);
            runScript(str);
		}

		@Override
		public void readVolume(int intVolume) {
		    String strVolume = Integer.toString((intVolume*2) + 80);
			runScript("$('#mic img').css('width', '"+strVolume+"px').css('height', '"+strVolume+"');");
		}

		@Override
		public void startSpeech() { // WHEN SPEECH RECOGNITION IS STARTED
			runScript("$('#mic img').attr('src', 'img/mic.png');");
		}

		@Override
		public void stopSpeech() { // WHEN SPEECH RECOGNITION IS FINISHED
            runScript("$('#mic').hide();");
            runScript("$('#mic img').attr('src', 'img/wait.gif').css('width', '80px').css('height', '80px');");
		}
	};


	////////////////////////////////////////////////////////////////
	//
	// TEXT-TO-SPEECH
	//
	////////////////////////////////////////////////////////////////

	/**
	 * Start text to speech
	 *
	 * @param strText text to read
	 */
	private void startTextToSpeech(String strText) {
		try {
			if  (strText!=null) {
				ToastCtrl.showMessageLong(mainActivity, mainActivity.getString(R.string.msg_voice_prepared));
				TextToSpeechUtil.speech(mainActivity, strText);
			}
		} catch (Exception e) {
			Log.e(strLogId, e.toString());
		}
	}

	/**
	 * Stop text to speech
	 *
	 */
	private void stopTextToSpeech() {
		try {
			TextToSpeechUtil.stop();
		} catch (Exception e) {
			Log.e(strLogId, e.toString());
		}
	}


	////////////////////////////////////////////////////////////////
	//
	// SCREEN-CAPTURE
	//
	////////////////////////////////////////////////////////////////

	/**
	 * 스크린캡처. "android.permission.READ_EXTERNAL_STORAGE" 권한 필요.
	 *
	 */
	private void captureScreen() {
		if (RuntimePermissionUtil.getInstance(mainActivity).hasPermissionAccessStorage()) {
			isCaptured = CaptureUtil.capturePicture(mainActivity, webView);
		}
	}

//	/**
//	 * downloader
//	 *
//	 * @param strUrl url
//   */
//	private void publishPortableDocumentFormat(final String strUrl) {
//	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(strUrl));
//	DownloadManager downloadManager = (DownloadManager)mainActivity.getSystemService(Context.DOWNLOAD_SERVICE);
//	downloadManager.enqueue(request);
//	}

}
