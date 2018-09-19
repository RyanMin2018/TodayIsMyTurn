package kr.co.sology.fw.util;

/**
 * 초성검색 유틸리티<br>
 * Initial sound search Utility.
 *
 */
public class InitialSoundSearchUtil {

    private static final char HANGUL_BEGIN_UNICODE = 44032; // '가'
    private static final char HANGUL_LAST_UNICODE = 55203;  // '힣'
    private static final char HANGUL_BASE_UNIT = 588;       // 각 음당 문자 수
    private static final char[] INITIAL_SOUND = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' }; 
   
   
    /**
     * 초성인지 확인한다.
     *
     * @param searchar character
     * @return initial sound or not
     */
    private static boolean isInitialSound(char searchar){ 
      for(char c:INITIAL_SOUND){ 
        if (c == searchar) return true; 
      } 
      return false; 
    } 
   
    /**
     * 문자의 자음을 가져온다.
     *  
     * @param c charater to check
     * @return initial sound
     */
    private static char getInitialSound(char c) { 
      int hanBegin = (c - HANGUL_BEGIN_UNICODE); 
      int index = hanBegin / HANGUL_BASE_UNIT; 
      return INITIAL_SOUND[index]; 
    } 
   
    /**
     * 한글인지 확인한다.
     *
     * @param c charater
     * @return hangul or not
     */
    private static boolean isHangul(char c) { 
      return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE; 
    } 
   
  
    /** 
     * 검색대상 문장에서 검색할 초성을 가지고 있는지를 확인한다.
     *
     * @param value  검색대상 문장
     * @param search 검색할 초성
     * @return 검색되면 true, 아니면 false
     */ 
    public static boolean test(String value, String search){
      value  = value.toLowerCase();
      search = search.toLowerCase();
      int t;
      int seof = value.length() - search.length(); 
      int slen = search.length(); 
      if(seof < 0) return false; // if the search term is longer, it returns false.
      for (int i = 0;i <= seof;i++) { 
        t = 0; 
        while (t < slen) { 
          if (isInitialSound(search.charAt(t)) && isHangul(value.charAt(i+t))){
            // if char is a chime and value is Hangul
            if(getInitialSound(value.charAt(i+t))==search.charAt(t)) t++; // compare each primitive to the same
            else break; 
          } else { 
            // if char is not primitive
            if(value.charAt(i+t)==search.charAt(t)) t++; // just compare them.
            else break; 
          } 
        } 
        if (t == slen) return true; // returns true if all matches are found.
      } 
      return false; // if it does not find a match, it returns false.
    }
  
}
