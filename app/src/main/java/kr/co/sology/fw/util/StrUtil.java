package kr.co.sology.fw.util;

import java.io.UnsupportedEncodingException;

/**
 * 문자열 유틸리티<br>
 * String Utility
 *
 */
public class StrUtil {

	/**
	 * 문자열이 null이면 공백으로 변환한다
	 * 
	 * @param str 문자열
	 * @return 변환된 문자열
	 */
	public static String nvl(String str) {
		return (str==null) ? "" : str.trim();
	}
	
	/**
	 * 문자열이 null이면 변경값으로 변환한다
	 * 
	 * @param str            문자열
	 * @param strReplaceWord null일때 변경할 문자열
	 * @return 변환된 문자열
	 */
	public static String nvl(String str, String strReplaceWord) {
		return (str==null || str.trim().equals("")) ? strReplaceWord : str.trim();
	}
	
	/**
	 * 문자열의 CharSet을 변경한다
	 * 
	 * @param str          CharSet을 변경할 문자열
	 * @param fromCharSet  변경전 CharSet
	 * @param toCharSet    변경후 CharSet
	 * @return toCharSet으로 변경한 문자열
	 * @throws UnsupportedEncodingException unsupported encoding exception
	 */
	private static String changeCharSet(String str, String fromCharSet, String toCharSet) throws UnsupportedEncodingException {
		return new String(nvl(str).getBytes(fromCharSet), toCharSet);
	}

	/**
	 * 한글 문자열을 디비에 입력하기 위해 CharSet을 변경한다
	 * 
	 * @param str          CharSet을 변경할 문자열
	 */
	private static String changeCharSet(String str) {
		try {
			return changeCharSet(str, "8859_1", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return str;
		}
	}
	
	/**
	 * 디비에 입력하기 위해 변경한 CharSet을 되돌린다
	 * 
	 * @param str 문자열
	 * @return CharSet을 변경한 문자열
	 */
	@SuppressWarnings("unused")
	public static String revokeCharSet(String str) {
		try {
			return changeCharSet(str, "UTF-8", "8859_1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return str;
		}		
	}
	
	/**
	 * 디비에 입력할 문자열을 점검하고 길이에 맞춰 자른 후 charset을 변경한다
	 * 
	 * @param strParameter   파라미터
	 * @param strReplaceWord 대체어
	 * @param intLength      자를길이
	 * @return 자를길이에 맞춰 자르고 charset을 변경한 문자열
	 */
    @SuppressWarnings("unused")
	public static String getParameter(String strParameter, String strReplaceWord, int intLength) {
		strParameter = nvl(strParameter, strReplaceWord); // check null
		strParameter = strParameter.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", ""); // remove html tag
		strParameter = cutString(strParameter, intLength, ""); 
		strParameter = changeCharSet(strParameter); // change Character Set
		return strParameter;
	}
	
	/**
	 * 정수형 숫자를 패턴에 맞는 문자열로 변경한다
	 * 
	 * @param strPattern 패턴
	 * @param intNumber  숫자
	 * @return 패턴에 맞게 변경된 숫자
	 */
    @SuppressWarnings("unused")
	public static String convertNumberFormat(String strPattern, int intNumber) {
		java.text.DecimalFormat f = new java.text.DecimalFormat(strPattern);
		return f.format(intNumber);
	}

	/**
	 * 더블형 숫자를 패턴에 맞는 문자열로 변경한다
	 * 
	 * @param strPattern 패턴
	 * @param dblNumber  숫자
	 * @return 패턴에 맞게 변경된 숫자
	 */
    @SuppressWarnings("unused")
	public static String convertNumberFormat(String strPattern, double dblNumber) {
		java.text.DecimalFormat f = new java.text.DecimalFormat(strPattern);
		return f.format(dblNumber);
	}

	
	/**
	 * 문자로만 되어 있는지 체크한다.
	 * 
	 * @param str 대상문자열
	 * @return 문자로만 되어 있는지의 여부
	 */
    @SuppressWarnings("unused")
	public static boolean isAlpha(String str) {
		for (int i=0; i<str.length(); i++) {
			if (!Character.isLetterOrDigit(str.charAt(i))) { return false; }
		}
		return (true);
	}

	/**
	 * 숫자로만 되어 있는지 체크한다.
	 * 
	 * @param str 대상문자열
	 * @return 숫자로만 되어 있는지의 여부
	 */
    @SuppressWarnings("unused")
	public static boolean isOnlyNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) { return false; }
		}
		return (true);
	}
	
	
	/**
	 * 세자리수마다 콤마를 찍어준다
	 * 
	 * @param str 세자리수마다 콤마를 찍어줄 숫자로된 문자열
	 * @return 세자리수마다 콤마를 찍은 문자열
	 */
	private static String addComma(String str) {
		StringBuilder strReturn   = new StringBuilder();
		boolean isNegative  = false;
		String  strUnderDot = "";
		String  strComma    = ",";
		
		str = nvl(str, "0");
		if (str.substring(0, 1).equals("-")) { // 음수여부확인
			str        = str.substring(1);
			isNegative = true;
		}
		for (int i=0; i<str.length(); i++) { // 소수점이 있는지 확인
			if (str.charAt(i) == '.') {
				strUnderDot = str.substring(i);
				str         = str.substring(0, i);
				break;
			}
		}
		for (int i=str.length(), j=0; i>0; i--, j++) {
			if ((j%3)==2) {
				if (str.length() == j+1) strReturn.insert(0, str.substring(i - 1, i));
				else strReturn.insert(0, strComma + str.substring(i - 1, i));
			} else strReturn.insert(0, str.substring(i - 1, i));
		}
		
		strReturn = new StringBuilder((isNegative) ? "-" + strReturn : strReturn.toString());
		return strReturn + strUnderDot;
	}
	
	/**
	 * 세자리수마다 콤마를 찍어준다
	 * 
	 * @param a 세자리수마다 콤마를 찍어줄 정수형 숫자
	 * @return 세자리수마다 콤마를 찍은 문자열
	 */
    @SuppressWarnings("unused")
	public static String addComma(int a) {
		return addComma(Integer.toString(a));
	}
	
	/**
	 * 세자리수마다 콤마를 찍어준다
	 * 
	 * @param a 세자리수마다 콤마를 찍어줄 더블형 숫자
	 * @return 세자리수마다 콤마를 찍은 문자열
	 */
    @SuppressWarnings("unused")
	public static String addComma(Double a) {
		return addComma(a.toString());
	}
	
	/**
	 * 세자리수마다 콤마를 찍어준다
	 * 
	 * @param a 세자리수마다 콤마를 찍어줄 롱형 숫자
	 * @return 세자리수마다 콤마를 찍은 문자열
	 */
    @SuppressWarnings("unused")
	public static String addComma(Long a) {
		return addComma(a.toString());
	}
	
	/**
	 * 문자열을 원하는 길이만큼 자른다
	 * 
	 * @param strObj        대상문자열
	 * @param intObjLength  자를길이
	 * @param strTail       자르고 뒤에 붙일 꼬리
	 * @return 원하는 길이만큼 자르고 꼬리를 붙인 문자열
	 */
	private static String cutString(String strObj, int intObjLength, String strTail) {
		int intStringLength  = strObj.length();
		if (intStringLength > intObjLength) {
			char chrObj;
			int intStringCutPos=0;
			for (int intMax=intObjLength; intMax>=0;) {
				intStringCutPos=intMax;
				chrObj = strObj.charAt(intMax);
				if(chrObj<128) { break; }
				else { intStringCutPos--; break; }
			}
			strObj = strObj.substring(0,intStringCutPos+1) + strTail;
		}
		return strObj;
	}

	
	/**
	 * 원문에 대체대상문자열이 있으면 대체문자열로 대체한다
	 * 
	 * @param strSearch  대체대상문자열
	 * @param strReplace 대체문자열
	 * @param strSource  원문
	 * @return 대체문자열로 교체된 원문
	 */
    @SuppressWarnings("unused")
	public static String replaceString(String strSearch, String strReplace, String strSource) {
		int spot;
		String returnString;
		String origSource = strSource;
		spot = strSource.indexOf(strSearch);
		if (spot > -1) { returnString = ""; }
		else { returnString = strSource; }
		while (spot > -1) {
			if (spot == strSource.length() + 1) {
				returnString = returnString.concat(strSource.substring(0, strSource.length() - 1).concat(strReplace));
				strSource = "";
			}
			else if (spot > 0) {
				returnString = returnString.concat(strSource.substring(0, spot).concat(strReplace));
				strSource = strSource.substring(spot + strSearch.length(), strSource.length());
			}
			else {
				returnString = returnString.concat(strReplace);
				strSource = strSource.substring(spot + strSearch.length(), strSource.length());
			}
			spot = strSource.indexOf(strSearch);
		}
		if (!strSource.equals(origSource)) { return returnString.concat(strSource); }
		else { return returnString; }
	}
	
}
