package kr.co.sology.fw.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import kr.co.sology.fw.multilingual.Holidays;

/**
 * Calendar utility
 *
 */
@SuppressWarnings("unused")
public class CalendarUtil {

  private CalendarUtil() {}

  /**
   * 검색한 날짜의 정수형 요일을 알려준다.
   *
   * @param strDate      검색할 날짜
   * @param strSeparator 날짜구분자
   * @return 요일(정수형)
   */
  public static int getWeekDay(String strDate, String strSeparator) {
    strSeparator = StrUtil.nvl(strSeparator, "-");
    strDate      = (strSeparator.equals(".")) ? strDate.replaceAll("[.]", "-") : strDate.replaceAll(strSeparator, "-");
    String[] arr       = strDate.split("-");
    Calendar oCalendar = Calendar.getInstance();
    oCalendar.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])-1, Integer.parseInt(arr[2]));
    return oCalendar.get(Calendar.DAY_OF_WEEK)-1;
  }
  
  /**
   * 검색한 날짜의 공휴일정보를 알려준다.
   * 
   * @param strDate      검색한 날짜
   * @param strSeparator 날짜구분자
   * @return 공휴일 명칭
   */
  private static String getDateName(String strDate, String strSeparator) {
    strDate = (strSeparator.equals(".")) ? strDate.replaceAll("[.]", "") : strDate.replaceAll(strSeparator, "");
    HashMap<String, String> h = Holidays.getHoliday();
    return (h!=null && h.get(strDate)!=null) ? h.get(strDate) : "";
  }

  /**
   * 검색한 날짜가 공휴일인지의 여부를 알려준다.
   * 
   * @param strDate      검색할 날짜
   * @param strSeparator 날짜구분자
   * @return 공휴일여부
   */
  private static boolean isDayOff(String strDate, String strSeparator) {
    strDate = (strSeparator.equals(".")) ? strDate.replaceAll("[.]", "") : strDate.replaceAll(strSeparator, "");
    HashMap<String, String> h = Holidays.getHoliday();
    return (h != null && h.get(strDate) != null);
  }
  

  /**
   * JSON 타입의 달력정보를 가져온다.
   * 
   * @param year         년도
   * @param month        월
   * @param strSeparator 날짜구분자
   * @return 달력
   * @throws ParseException parse exception
   */
  public static String getCalendar(int year, int month, String strSeparator) throws ParseException {
    strSeparator = StrUtil.nvl(strSeparator, "-");
    if (year < 2000 || year > 2050 || month < 1 || month > 12) return null;
    if (strSeparator.length()!=1) return null;
    
    int intLastYear = year;
    int intLastMonth = month;

    if (month>11) {
      intLastYear++;
      intLastMonth = 0;
    } 
    
    String strLastDate = Objects.requireNonNull(DateTimeUtil.diff(DateTimeUtil.getFormatDate(intLastYear, intLastMonth + 1, 1, strSeparator), 1, strSeparator)).substring(8,10);
    int    intLastDate = Integer.parseInt(strLastDate);
    StringBuffer sb    = new StringBuffer();
    
    /* PREVIOUS MONTH */
    int j=0;
    for (int i=getWeekDay(DateTimeUtil.getFormatDate(year, month, 1, strSeparator), strSeparator); i>0; i--) {
      String strSolarDate = DateTimeUtil.diff(DateTimeUtil.getFormatDate(year, month, 1, strSeparator), i, strSeparator);
      sb.append(getCalendarNode(
            strSolarDate, // .substring(5),                                                 // strSolarDate
            j++,                                                                       // intWeek
            "",                                                                        // strLunarDate
            isDayOff(strSolarDate, strSeparator),                                      // isHoliday
            getDateName(strSolarDate, strSeparator),                                   // strDateName
            strSolarDate
           ));       
    }
    
    /* CURRENT MONTH */
    for (int i=1; i<intLastDate+1; i++) {
      String strLunarDate = DateTimeUtil.convertSolarToLunar(DateTimeUtil.getFormatDate(year, month, i, strSeparator), strSeparator);
      if (strLunarDate.length()>5) strLunarDate = strLunarDate.substring(5);
      sb.append(getCalendarNode(
            Integer.toString(year) + strSeparator + DateTimeUtil.getMonthDateString(month) + strSeparator + DateTimeUtil.getMonthDateString(i),                            // strSolarDate
            j++%7,                                                                                                                 // intWeek
            strLunarDate,                                                                                                          // strLunarDate
            isDayOff(DateTimeUtil.getFormatDate(year, month, i, strSeparator), strSeparator),                                      // isHoliday
            getDateName(DateTimeUtil.getFormatDate(year, month, i, strSeparator), strSeparator),                                   // strDateName
            Integer.toString(year) + strSeparator + DateTimeUtil.getMonthDateString(month) + strSeparator + DateTimeUtil.getMonthDateString(i)
          )); 
    }
    
    /* NEXT MONTH */
    for (int i=1; i<(7-getWeekDay(DateTimeUtil.getFormatDate(year, month, intLastDate, strSeparator), strSeparator)); i++) {
      String strSolarDate = DateTimeUtil.diff(DateTimeUtil.getFormatDate(year, month, intLastDate, strSeparator), -i, strSeparator);
      sb.append(getCalendarNode(
           strSolarDate, // .substring(5),                                                 // strSolarDate
           j++%7,                                                                     // intWeek
           "",                                                                        // strLunarDate
           isDayOff(strSolarDate, strSeparator),                                      // isHoliday
           getDateName(strSolarDate, strSeparator),                                   // strDateName
           strSolarDate
           )); 
    }
    return getCalendarBlock(Integer.toString(year) + strSeparator + DateTimeUtil.getMonthDateString(month), sb);
  }
  
  /**
   * 달력의 JSON 블럭 탬플릿에 담는다.
   * 
   * @param strMonth         블럭제목 (월)
   * @param strDataBlock     블럭노드 (날짜들)
   * @return 블럭
   */
  private static String getCalendarBlock(String strMonth, StringBuffer strDataBlock) {
    return "{\"title\":\"" + strMonth + "\", \"content\":[" + strDataBlock.substring(0, strDataBlock.length()-1) + "]}";
  }
  
  /**
   * 달력의 블럭노드 탬플릿에 담는다.
   * 
   * @param strSolarDate  양력날짜
   * @param intWeek       정수형 요일
   * @param strLunarDate  음력날짜
   * @param isHoliday     공휴일여부
   * @param strDateName   공휴일명칭
   * @return 블럭노드
   */
  private static String getCalendarNode(String strSolarDate, int intWeek, String strLunarDate, boolean isHoliday, String strDateName, String strDate) {
    return "{\"solar\":\"" + strSolarDate + "\",\"week\":" + Integer.toString(intWeek) + ",\"lunar\":\"" + strLunarDate + "\",\"holiday\":" + isHoliday + ",\"name\":\"" + strDateName + "\",\"ymd\":\"" + strDate + "\"},";
  }
  
  
  /**
   * JSON 타입의 달력 배열 (위젯용. 음력날짜는 필요없다)
   * 
   * @param year         년도
   * @param month        월
   * @param strSeparator 날짜구분자
   * @return 달력배열
   * @throws ParseException parse exception
   */
  public static ArrayList<CalendarVO> getCalendarArray(int year, int month, String strSeparator) throws ParseException {

    strSeparator = StrUtil.nvl(strSeparator, "-");
    if (year < 1881 || year > 2040 || month < 1 || month > 12) return null;
    if (strSeparator.length()!=1) return null;
    
    int intLastYear = year;
    int intLastMonth = month;

    if (month>11) {
      intLastYear++;
      intLastMonth = 0;
    } 
    
    String strLastDate = Objects.requireNonNull(DateTimeUtil.diff(DateTimeUtil.getFormatDate(intLastYear, intLastMonth + 1, 1, strSeparator), 1, strSeparator)).substring(8,10);
    int    intLastDate = Integer.parseInt(strLastDate);
   ArrayList<CalendarVO> arr = new ArrayList<>();
    
    /* Previous Month */
    int j=0;
    for (int i=getWeekDay(DateTimeUtil.getFormatDate(year, month, 1, strSeparator), strSeparator); i>0; i--) {
      String strSolarDate = DateTimeUtil.diff(DateTimeUtil.getFormatDate(year, month, 1, strSeparator), i, strSeparator);
      arr.add(getCalendarVO(
            Objects.requireNonNull(strSolarDate).substring(5),          // strSolarDate
            j++,                                                        // intWeek
              // strLunarDate
            isDayOff(strSolarDate, strSeparator),                       // isHoliday
            getDateName(strSolarDate, strSeparator),                    // strDateName
            strSolarDate
           ));       
    }
    
    /* Current Month */
    for (int i=1; i<intLastDate+1; i++) {
        // String strLunarDate = DateTimeUtil.convertSolarToLunar(DateTimeUtil.getFormatDate(year, month, i, strSeparator), strSeparator);
        // if (strLunarDate.length()>4) strLunarDate = strLunarDate.substring(5);
    	arr.add(getCalendarVO(
            DateTimeUtil.getMonthDateString(month) + strSeparator + DateTimeUtil.getMonthDateString(i),                            // strSolarDate
            j++%7,                                                                                                                 // intWeek
                // strLunarDate
            isDayOff(DateTimeUtil.getFormatDate(year, month, i, strSeparator), strSeparator),                                      // isHoliday
            getDateName(DateTimeUtil.getFormatDate(year, month, i, strSeparator), strSeparator),                                   // strDateName
            Integer.toString(year) + strSeparator + DateTimeUtil.getMonthDateString(month) + strSeparator + DateTimeUtil.getMonthDateString(i)
          )); 
    }

    /* Next Month */
    int intCurrentCnt = (7*6) - arr.size() + 1;
    // for (int i=1; i<(7-getWeekDay(DateTimeUtil.getFormatDate(year, month, intLastDate, strSeparator), strSeparator)); i++) {
    for (int i=1; i<intCurrentCnt; i++) {
      String strSolarDate = DateTimeUtil.diff(DateTimeUtil.getFormatDate(year, month, intLastDate, strSeparator), -i, strSeparator);
      arr.add(getCalendarVO(
           Objects.requireNonNull(strSolarDate).substring(5),          // strSolarDate
           j++%7,                                                      // intWeek
              // strLunarDate
           isDayOff(strSolarDate, strSeparator),                       // isHoliday
           getDateName(strSolarDate, strSeparator),                    // strDateName
           strSolarDate
           )); 
    }
    return arr;
  }

  /**
   * 달력VO에 정보를 담아 전달한다.
   *
   * @param strSolarDate 앙력날짜
   * @param intWeek      정수형 요일
   * @param isHoliday    공휴일여부
   * @param strDateName  공휴일명칭
   * @param strDate      년월일
   * @return 달력VO
   */
  private static CalendarVO getCalendarVO(String strSolarDate, int intWeek, boolean isHoliday, String strDateName, String strDate) {
	  CalendarVO vo = new CalendarVO();
	  vo.solar = strSolarDate;
	  vo.week = intWeek;
	  vo.lunar = "";
	  vo.holiday = isHoliday;
	  vo.name = strDateName;
	  vo.ymd = strDate;
	  return vo;
  } 
  
  
}
