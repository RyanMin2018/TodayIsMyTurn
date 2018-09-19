package kr.co.sology.fw.util;

/**
 * Value Object For Calendar Data.
 *
 */
public class CalendarVO {

    /**
     * 양력날짜
     */
    public String solar;

    /**
     * 요일
     */
    public int week;

    /**
     * 음력날짜
     */
    public String lunar;

    /**
     * 공휴일여부
     */
    public boolean holiday;

    /**
     * 공휴일명칭
     *
     */
    public String name;

    /**
     * 년월일
     */
    public String ymd;     

}
