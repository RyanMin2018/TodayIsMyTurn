package kr.co.sology.fw.multilingual;

import android.content.Context;

import java.util.Locale;

import kr.co.sology.todayismyturn.MainActivity;
import kr.co.sology.todayismyturn.R;

/**
 * 다국어지원 클래스
 *
 */
public class LanguageMgr {


    /**
     * 시스템의 언어코드를 얻어온다.<br>
     * get system's language code. ex) ko, zh, ja, en
     *
     * @return 언어코드
     */
    private static String getLocaleLanguageCode() {
        String target = Locale.getDefault().getLanguage();
        // return (target.contains("zh")) ? "zh-CN" : target;
        return target;
    }

    public static String getLocaleCode() {
        return Locale.getDefault().toString();
    }

    /**
     * 브라우저뷰의 홈페이지
     *
     * @return 언어별 홈페이지
     */
    public static String getDefaultUrl() {
        if (getLocaleLanguageCode().contains("ko")) return "https://m.naver.com/";
        if (getLocaleLanguageCode().contains("ja")) return "https://m.yahoo.co.jp/";
        if (getLocaleLanguageCode().contains("zh")) return "http://www.baidu.com/";
        return "https://www.google.com/";
    }


    /**
     * 단말기의 언어설정에 맞게 코드스캔 결과를 검색어로 각국의 메인 검색엔진을 호출한다<br>
     *
     * @return 코드값을 검색할 URL
     */
    public static String getCodeSearchUrl() {
        if (getLocaleLanguageCode().contains("ko")) return "https://m.search.naver.com/search.naver?query=";
        if (getLocaleLanguageCode().contains("ja")) return "https://search.yahoo.co.jp/search?p=";
        if (getLocaleLanguageCode().contains("zh")) return "http://www.baidu.com/s?wd=";
        return "http://www.google.com/search?q=";
    }

}
