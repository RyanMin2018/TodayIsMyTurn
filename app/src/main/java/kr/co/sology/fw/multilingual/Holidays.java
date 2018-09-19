package kr.co.sology.fw.multilingual;

import java.util.HashMap;

/**
 * holiday information is set by ScheduleDAO.
 *
 *
 */
public class Holidays {

    private static HashMap<String, String> mapHoliday;

    /**
     *  save holiday information to hashmap
     *
     * @param h holiday data
     */
    public static void setHoliday(HashMap<String, String> h) {
        mapHoliday = h;
    }

    /**
     * get holiday information from hashmap
     *
     * @return holiday data
     */
    public static HashMap<String, String> getHoliday() {
        return mapHoliday;
    }

}
