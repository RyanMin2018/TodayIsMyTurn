package kr.co.sology.fw.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 네이버를 이용한 번역 유틸리티<br>
 * Translate Utility through naver.com
 *
 * @author Dolgamza
 */
public class TranslateUtil {

    /**
     * 번역<br>
     * translate to use naver's translate api.
     *
     * @param strMessage message
     * @param source     source language code
     * @param target     target language code
     * @return translated text
     */
    public static String transViaNaver(String strMessage, String source, String target) {

        String clientId     = "B8RkhkDxgng1tT5xSVvX";  // client id
        String clientSecret = "XDZeylQHEe";            // client secret key
        try {
            String text = URLEncoder.encode(strMessage, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/language/translate";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id",     clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            String postParams = "source="+source+"&target="+target+"&text=" + text;
            Log.d("translate", postParams);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            return cropTextFromNaverJson(response.toString(), strMessage);
        } catch (Exception e) {
            Log.e("translate", e.toString());
        }
        return "";
    }

    /**
     * 번역된 결과로부터 원하는 정보만 추출하기<br>
     * crop translated text from json-data.
     *
     * @param str    translated json text
     * @param origin original text
     * @return only text
     */
    private static String cropTextFromNaverJson(String str, String origin) {
        String s = "translatedText:";
        try {
            str = str.replaceAll("\"", "");
            return (str.contains(s)) ? str.substring(str.indexOf(s) + s.length()).replace("}}}", "") : origin;
        } catch (Exception e) {
            Log.e("translate", e.toString());
        }
        return origin;
    }

}
