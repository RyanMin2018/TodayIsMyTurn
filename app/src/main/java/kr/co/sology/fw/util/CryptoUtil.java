package kr.co.sology.fw.util;

import org.apache.commons.codec.binary.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 암호화 유틸리티<br>
 * Crypto Utility. AES/CBC/PKCS5Padding
 *
 */
@SuppressWarnings("unused")
public class CryptoUtil {

	private static String strCpytoMethod = "AES/CBC/PKCS5Padding";
	
	private static byte[] getKeyByte(String strKey) {
		try {
			strKey = (strKey==null) ? "" : strKey.trim();
			strKey = (strKey + ".sology.co.kr___").substring(0,16);
			return strKey.getBytes("UTF-8");
		} catch (Exception e) {
			return null;
		}
	}
	
	private static Key getKey(String strKey) {
		try {
			byte[] b = getKeyByte(strKey);
			return (b!=null) ? new SecretKeySpec(getKeyByte(strKey), "AES") : null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 암호화한다.<br>
	 * encoding.
	 *
	 * @param strKey   crypto key
	 * @param strValue text
     * @return encoded text
     */
	public static String encode(String strKey, String strValue) {
		String str = "";
		try {
			Key k = getKey(strKey);
			if (k!=null) {
				Cipher c = Cipher.getInstance(strCpytoMethod);
				c.init(Cipher.ENCRYPT_MODE,  k, new IvParameterSpec(getKeyByte(strKey)));
				byte[] en = c.doFinal(strValue.getBytes("UTF-8"));
				str = new String(Base64.encodeBase64(en));
			}
		} catch (Exception e) {
			return strValue;
		}
		return str;
	}

	/**
	 * 복화화한다.<br>
	 * decoding
	 *
	 * @param strKey    crypto key
	 * @param strValue  text
     * @return decoded text
     */
	public static String decode(String strKey, String strValue) {
		String str = "";
		try {
			Key k = getKey(strKey);
			if (k!=null) {
				Cipher c = Cipher.getInstance(strCpytoMethod);
				c.init(Cipher.DECRYPT_MODE,  k, new IvParameterSpec(getKeyByte(strKey)));
				byte[] de = Base64.decodeBase64(strValue.getBytes("UTF-8"));
				str = new String(c.doFinal(de), "UTF-8");
			}
		} catch (Exception e) {
			return strValue;
		}
		return str;		
	}
	
	
}
