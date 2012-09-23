package com.jbcc.MQTool.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * ８進数表記を何とかして文字列に変換する。
 *
 * @author MOSA-ARCHITECT.COM
 *
 */
public class Oct2String {

	private static String DEFAULT_CHARSET = "EUC-JP";// "Shift_JIS";;

	/**
	 * javaで使用可能な文字セットを標準出力にダンプ
	 */
	public static void dumpCharsetKeys() {
		Set<String> set = Charset.availableCharsets().keySet();
		for (String s : set) {
			System.out.println(s);
		}
	}

	/**
	 * 8進数表記を3文字で1セットの配列に変換する
	 *
	 * @param record
	 * @return
	 */
	public static byte[] record2bytes(String record) {
		if (record == null) {
			return null;
		}

		// 文字列の長さは6の倍数であることを期待して例外処理はしない
		char[] chars = record.replace(" ", "").toCharArray();
		byte[] bytes = new byte[chars.length / 3];
		for (int i = 0; i < (chars.length / 3); i++) {
			bytes[i] = oct2byte(new String(chars, 3 * i, 3));
		}

		return bytes;
	}

	/**
	 * デフォルトのデコード用文字セットを変更します
	 *
	 * @param charset
	 *            新しい文字セット
	 */
	public static void setCharset(String charset) {
		if (Charset.availableCharsets().keySet().contains(charset)) {
			DEFAULT_CHARSET = charset;
		}
	}

	/**
	 * 8進数表記の配列を文字列に変換する
	 *
	 * @param values
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String octArray2string(String[] values)
			throws UnsupportedEncodingException {
		return valueOf(octArray2byteArray(values));
	}

	/**
	 * 8進表記文字列配列をbyte配列に変換する
	 *
	 * @param values
	 * @return
	 */
	public static byte[] octArray2byteArray(String[] values) {
		byte[] bytes = new byte[values.length];
		for (int i = 0; i < values.length; i++) {
			bytes[i] = oct2byte(values[i]);
		}
		return bytes;
	}

	/**
	 * 8進数表記文字列をbyte表現に変換する
	 *
	 * @param str
	 * @return
	 */
	public static byte oct2byte(String str) {
		return Integer.decode("0" + str).byteValue();
	}

	/**
	 * byte配列の文字列変換<br>
	 * DEFAULT_CHARSETに定義された文字セットで変換する
	 *
	 * @param bytes
	 *            変換元byte配列
	 * @return 文字列
	 * @throws UnsupportedEncodingException
	 */
	public static String valueOf(byte... bytes)
			throws UnsupportedEncodingException {
		return new String(bytes, DEFAULT_CHARSET);
	}

	public static String valueOf(byte[] bytes,String charSet)
			throws UnsupportedEncodingException {
		return new String(bytes, charSet);
	}
}
