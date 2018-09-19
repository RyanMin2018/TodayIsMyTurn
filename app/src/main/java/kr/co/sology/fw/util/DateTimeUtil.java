package kr.co.sology.fw.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 날짜 유틸리티<br>
 * Datetime Utility
 *
 */
public class DateTimeUtil {

  /**
   * Constructor
   * 
   */
  private DateTimeUtil() {}

  /**
   * 오늘 날짜를 가져온다.
   * 
   * @param strSeparator 구분자
   * @return 오늘날짜
   */
  public static String getCurrentDate(String strSeparator) {
    strSeparator = (strSeparator==null) ? "-" : strSeparator; 
    String str = "yyyy" + strSeparator + "MM" + strSeparator + "dd";
    SimpleDateFormat f = new SimpleDateFormat(str, java.util.Locale.KOREA);
    return f.format(new Date());
  }
  
  /**
   * 현재의 시간을 가져온다.
   * 
   * @return current time is formatted "HH:mm:ss"
   */
  @SuppressWarnings("unused")
  public static String getCurrentTime() {
    SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss", java.util.Locale.KOREA);
    return f.format(new Date());
  }
  
  /**
   * 두 날짜 사이의 날짜수를 가져온다.
   * 
   * @param strFromDate  시작일
   * @param strToDate    종료일
   * @param strSeparator 구분자
   * @return 두날짜 사이의 날짜수
   * @throws ParseException parse exception
   */
  @SuppressWarnings("unused")
  public static Long diff(String strFromDate, String strToDate, String strSeparator) throws ParseException {
    strSeparator = StrUtil.nvl(strSeparator, "-");
    strFromDate  = StrUtil.nvl(strFromDate, getCurrentDate(strSeparator));
    strToDate    = StrUtil.nvl(strToDate,   getCurrentDate(strSeparator));
    strSeparator = (strSeparator.equals(".")) ? "[.]" : strSeparator;

    if (!isCorrectDate(strFromDate, strSeparator)) return null;
    if (!isCorrectDate(strToDate, strSeparator)) return null;
    
    SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
    Date          from = f.parse(strFromDate.replaceAll(strSeparator, ""));
    Date            to = f.parse(strToDate.replaceAll(strSeparator, ""));
    return (to.getTime()-from.getTime())/(24*60*60*1000);
  }
  
  /**
   * 검색할 날짜에 -intDiff를 더한 날짜를 구한다
   * 
   * @param strSearchDate 검색할 날짜
   * @param intDiff       더하거나 뺄 날짜(+는 빼고, -는 더함)
   * @param strSeparator  년월일구분자
   * @return -intDiff한 날짜
   * @throws ParseException parse exception
   */
  public static String diff(String strSearchDate, int intDiff, String strSeparator) throws ParseException {
    strSeparator  = StrUtil.nvl(strSeparator, "-");
    strSearchDate = StrUtil.nvl(strSearchDate, getCurrentDate(strSeparator));
    
    if (!isCorrectDate(strSearchDate, strSeparator)) return null;
    
    SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd", java.util.Locale.KOREA);
    String strSep   = (strSeparator.equals(".")) ? "[.]" : strSeparator;
    Date  date = f.parse(strSearchDate.replaceAll(strSep, ""));
    Long  lng = date.getTime() - (long)intDiff*(24*60*60*1000);
    date.setTime(lng);
    String str = f.format(date);
    return str.substring(0, 4) + strSeparator + str.substring(4, 6) + strSeparator + str.substring(6, 8);
    
  }
  
  /**
   * 유효한 날짜 여부를 확인한다
   * 
   * @param strDate      날짜
   * @param strSeparator 년월일구분자
   * @return 유효한날짜인지?
   */
  private static boolean isCorrectDate(String strDate, String strSeparator) {
    int     intYear;
    if (strDate.length()!=10) return false;
    
    strSeparator  = StrUtil.nvl(strSeparator, "-");
    String[] arr  = (strSeparator.equals(".")) ? strDate.replaceAll("[.]", "-").split("-") : strDate.replaceAll(strSeparator, "-").split("-");
    
    int[] intMonthTable = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    
    if (arr[0].length()!=4) return false;
    if (Integer.parseInt(arr[1])>12) return false;
    if (Integer.parseInt(arr[2])<1 || Integer.parseInt(arr[2])>31) return false;
    
    intYear = Integer.parseInt(arr[0]);
    if (intYear%4 == 0 && (intYear%100!=0 || intYear%400==0)) intMonthTable[1]=29;
    return Integer.parseInt(arr[2]) <= intMonthTable[Integer.parseInt(arr[1]) - 1];

  }

  /**
   * 10이하의 숫자가 들어오면 앞에 0을 추가한다
   * 
   * @param i 변경검토대상숫자
   * @return 달력형태의 숫자
   */
  public static String getMonthDateString(int i) {
    return (i<10) ? "0" + Integer.toString(i) : Integer.toString(i);
  }
  
  /**
   * 정수형 년, 월, 일을 포맷에 맞게 변경한다
   *  
   * @param year 년도
   * @param month 월
   * @param date  일
   * @param strSeparator 구분자
   * @return 포맷에 맞는 년월일
   */
  public static String getFormatDate(int year, int month, int date, String strSeparator) {
    String strYear  = Integer.toString(year);
    String strMonth = getMonthDateString(month);
    String strDate  = getMonthDateString(date);
    return strYear + strSeparator + strMonth + strSeparator + strDate;
  }
  
  /**
   * 양력으로 음력 날짜를 조회한다
   * 
   * @param strSolarDate 양력날짜
   * @param strSeparator 년월일구분자
   * @return 음력날짜
   */
  public static String convertSolarToLunar(String strSolarDate, String strSeparator) {
    return LunarData.getLunarDate(strSolarDate, strSeparator);
  }
  
  /**
   * 요일구하기
   * 
   * @param strSolarDate 날짜
   * @param strSeparator 년월일 구분자
   * @return 정수형요일
   */
  private static int getWeek(String strSolarDate, String strSeparator) {
    strSeparator = StrUtil.nvl(strSeparator, "-");
    String[] arrDate = strSolarDate.split((strSeparator.equals("."))?"[.]":strSeparator);
    int intYear  = Integer.parseInt(arrDate[0]);
    int intMonth = Integer.parseInt("1"+arrDate[1])-101;
    int intDate  = Integer.parseInt("1"+arrDate[2])-100;
    Calendar c = Calendar.getInstance();
    c.set(intYear, intMonth, intDate);
    return c.get(Calendar.DAY_OF_WEEK)-1;
  }
  
  /**
   * 한글 요일명칭 구하기
   *
   * @param strSolarDate 날짜
   * @param strSeparator 년월일 구분자
   * @return 한글요일명
   */
  @SuppressWarnings("unused")
  public static String getWeekKorean(String strSolarDate, String strSeparator) {
    String[] w = {"일", "월", "화", "수", "목", "금", "토"};
    return w[getWeek(strSolarDate, strSeparator)];
  }

  /**
   * 영어 요일명칭 구하기
   *
   * @param strSolarDate 날짜
   * @param strSeparator 년월일 구분자
   * @return 영문요일명
   */
  @SuppressWarnings("unused")
  public static String getWeekEnglish(String strSolarDate, String strSeparator) {
    String[] w = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    return w[getWeek(strSolarDate, strSeparator)];
  }

  /**
   * 한자 요일명칭 구하기
   *
   * @param strSolarDate 날짜
   * @param strSeparator 년월일 구분자
   * @return 한자요일명
   */
  @SuppressWarnings("unused")
  public static String getWeekChinese(String strSolarDate, String strSeparator) {
    String[] w = {"日", "月", "火", "水", "木", "金", "土"};
    return w[getWeek(strSolarDate, strSeparator)];
  }

}
