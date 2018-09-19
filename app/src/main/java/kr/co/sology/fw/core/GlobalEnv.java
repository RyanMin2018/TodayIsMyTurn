package kr.co.sology.fw.core;

import android.os.Environment;

/**
 * Global Environment Variables
 * 
 */
public class GlobalEnv {


	private static final String strApplicationKey   = "todayismyturn";

	/*************************************************************
	 *
	 * Variables for User Interface
	 *
	 *************************************************************/

	public static final int intDialogMessageTextSize = 14; // font size in dialog


	/*************************************************************
	 *
	 * Variables for Crypto
	 *
	 *************************************************************/

	public static final String strCryptoKey           = "roopretelcham"; // salt


	/*************************************************************
	 *
	 * Variables for Local Storage And Database
	 *
	 *************************************************************/
	
	public static final String strRepositName 		  = strApplicationKey; // local storage name
	public static final String strDatabaseName        = strApplicationKey + ".db"; // sqlite database name
	public static final String strRepositFolder 	  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + strApplicationKey; // starage path
	public static final String strAssetsPath          = "file:///android_asset"; // asset path
	public static final String strDefaultWebPage      = strAssetsPath + "/index.htm";
	public static final String strCapturedFileExt 	  = ".png"; // captured image file ext

	/* local storage key set */
	public static final String strWidgetStyleKey      = "WidgetStyle"; // widget style key
	public static final String strProfileKey          = "profile"; // profile/name-card key
	public static final String strLanguageCode        = "Language"; // language code key
	public static final String strHolidayVersion      = "HolidayVersion"; // holiday version

	/* profile backup key */
	public static final String strProfileKeyForBackup = "ProfileData:::";

	/*************************************************************
	 *
	 * Variables for javascript interface
	 *
	 *************************************************************/

	public static final String strJavascriptDomainKey = "anapp"; // application key for javascript
	public static final String strSeparator           = "____";  // separator between datum and datum

	/*************************************************************
	 *
	 * Variables for AndroidManifest or intent key
	 *
	 *************************************************************/

	public static final String strCalendarPrevKey 			   = ".service.WidgetProvider.action.LoadPrevCalendar";          // click event id for previous month button in widget
	public static final String strCalendarTodayKey 			   = ".service.WidgetProvider.action.LoadTodayCalendar";         // click event id for this month button in widget
	public static final String strCalendarNextKey 			   = ".service.WidgetProvider.action.LoadNextCalendar";          // click event id for next month button in widget
	public static final String strCalendarLoadActivityKey 	   = ".service.WidgetProvider.action.LoadCalendarActivity";      // calendar widget id
	public static final String strCalendarLoadSetupActivityKey = ".service.WidgetProvider.action.LoadCalendarSetupActivity"; // calendar widget setup activity id

	/* key set for communication between widget and activity */
	public static final String strRepositoryYearKey            = "year";  // key for selected year
	public static final String strRepositoryMonthKey           = "month"; // key for selected month
	public static final String strKeyDate                      = "date";  // key for selected date
	public static final String strRequestUrl                   = "url";   // key for url

	public static final String strBroadCastReceiverFilterIdForAlarm  = "kr.co.sology.DolgamzaNote.service.ALARM";


	/*************************************************************
	 *
	 * Key Set for Left Menu
	 *
	 * Must compare with "MainActivity.class's arrMenuItem, arrDrawable.
	 *
	 *************************************************************/

	public static int       intMenuIconSize       =  20; // icon size
	public static final int intMenuIdxSchedule    =   0; // menu index of schedule
	public static final int intMenuIdxPhonebook   =   1; // menu index of phone book
	public static final int intMenuIdxProfile     =   2; // menu index of profile|my name card
	public static final int intMenuIdxCodeScan    =   3; // menu index of code scan
	public static final int intMenuIdxBrowser     =   4; // menu index of browser
	public static final int intMenuIdxMap         = 905; // menu index of map - unused
	public static final int intMenuIdxScrap       = 906; // menu index of scrap book - unused
	public static final int intMenuIdxClose       = 908; // menu index of close - unused
	public static final int intMenuIdxUserGuide   =   5; // menu index of user guide
	public static final int intScheduleRegistPage = 101; // page index of schedule registration for 'share' or 'shortcuts'

	/*************************************************************
	 *
	 * Key Set for StartActivityForResult
	 *
	 *************************************************************/

	public static final int    intRequestCodeScanKey  = 100; // from code scan
	public static final int    intRequestContactKey   = 102; // from phone-book
	public static final int    intRequestProfileKey   = 103; // from profile
	public static final int    intRequestGoogleApiResolutionKey = 301; // from google account selection

	public static final String strScanResultKey       = "SCAN_RESULT";
	public static final String strScanResultFormatKey = "SCAN_RESULT_FORMAT";

	/*************************************************************
	 *
	 * Permission Keys
	 *
	 * Must Compare MainActivity and AndroidManifest Permission Part
	 *
	 *************************************************************/

	public static final int intPermissionRequestReadPhoneStat = 101; // phone state
	public static final int intPermissionRequestReadContacts  = 102; // contact
	public static final int intPermissionStorage              = 105; // Storage
	public static final int intPermissionReadAudio            = 106; // Audio
	public static final int intPermissionRequestCamera        = 103; // Camera
	public static final int intPermissionRequestMap           = 107; // Map

	
	/*************************************************************
	 *
	 * WebPages Or Protocols
	 *
	 *************************************************************/

	private static final String strDefaultWebUrl   = "http://dolgamzanote.pythonanywhere.com/"; // default web page url
	public  static final String strMarketUrl       = "market://details?id=kr.co.sology.DolgamzaNote"; // Market Address
	public  static final String strCustomProtocol  = "dolgamza://"; // custom protocol for share...
	public  static final String strProfileProtocol = "contact://";  // custom protocol for phone book
	public  static final String strWifiProtocol    = "wifi://"; // custom protocol for wifi auto-change
	// public static final String strProfileUrl      = "https://chart.googleapis.com/chart?chs=150x150&cht=qr&chl="; // qr-code

	private static final String strDomainUrl       = strDefaultWebUrl + "app/ServerSide/"; // back-end server url
	public  static final String strSaveKeyUrl      = strDomainUrl + "SaveFCMKey/"; // url for saving firebase key
	public  static final String strVersionUrl      = strDomainUrl + "Version/?lang="; // url for Version Check
	// public static final String strSplashUrl       = strDefaultWebUrl + "/media/SplashImage.jpg"; // Splash Page
	public  static final String strHoldiayurl      = strDomainUrl + "Holiday/?lang="; // url for holiday data download


	public static final String strNotTypeKey      = "notitype";
	public static final String strNotTypeFCM      = "fcm";
	public static final String strNotTypeAlarm    = "alarm";

	/*************************************************************
	 *
	 * Key set for FCM
	 *
	 *************************************************************/

	public static final String strKeyTitle        = "title";   // title
	public static final String strKeyMessage      = "message"; // message
	public static final String strKeyImageUrl     = "imgurl";  // image url
	public static final String strKeyLinkUrl      = "linkurl"; // link url
	public static final String strScheduleIdKey   = "scheduleid"; // key to schedule information to determine if it is on a schedule

	public static final String strFCMServerKey    = "AAAA_65J53k:APA91bE3av6Z6L36uawZ6xhAcmpmMs75aryUXaUar14SSHhWwvmrlHmfWmrwHPvT7XkcNFVEhEM5rwhHFKZnOSQvVIkDolmjKTlHLISueW5Kd5q8K6o0yMzdCaQkkk4nioiHBXZDelTrUguN7aUf1assiffAaXWc2w";


	/*************************************************************
	 *
	 * Variables for Alarm
	 *
	 *************************************************************/

	public static final String strAlarmDefault    = "000000000000"; // default time set
	public static final String strMornigCallTime  = "0730"; // morning call time
	public static final String strAlarmTimeKey    = "dt";

	/*
	 * 네이버 음성인식 클라이언트 아이디
	 * Naver Developer Client Id to use Clova Speech Recognition
	 */
	// public static final String strNaverSpeechRecognitionClientId = "nfRtzJMRJA9w1vMchqxN";

	/*************************************************************
	 *
	 * Key set for Version Control
	 *
	 *************************************************************/

	public static final String strApplicationVersionKey = "applicationVersion:";
	public static final String strMessageVersionKey     = "messageVersion:";
	public static final String strUrlKey                = "url:";
	public static final String strMessageKey            = "message:";
	public static final String strHolidayVersionKey     = "holidayVersion:";
}


