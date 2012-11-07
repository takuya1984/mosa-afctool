package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;
import com.jbcc.MQTool.util.StdOut;

/**
 * ファイル文字コード変換クラス.
 *
 */
public class FileConverter {

	private static boolean isDebug = false;

	public static void main(String[] fileNames) throws IOException {
		for (String file : fileNames) {
			FileConverter.convertSJIStoUTF8(file);
		}
	}

	/**
	 * SJISからUTF-8に変換する.
	 * @param file ファイル名
	 * @throws IOException
	 */
	public static void convertSJIStoUTF8(String file) throws IOException {
		convert(file, "Shift-JIS", "UTF-8");
	}

	/**
	 * SJISからUTF-8に変換する.
	 * @param file ファイルオブジェクト
	 * @throws IOException
	 */
	public static void convertSJIStoUTF8(File file) throws IOException {
		convert(file, "Shift-JIS", "UTF-8");
	}

	/**
	 * 文字コード変換処理.
	 * 
	 * @param inFile 変換対象ファイル
	 * @param decode 変換元コード
	 * @param encode 変換対象コード
	 * @throws IOException
	 */
	public static void convert(String inFile, String decode, String encode)
			throws IOException {
		convert(new File(inFile), decode, encode);
	}

	/**
	 * ファイルの変換
	 *
	 * @param inFile 変換対象ファイル
	 * @param decode 変換元コード
	 * @param encode 変換対象コード
	 * @throws IOException
	 */
	public static void convert(File inFile, String decode, String encode)
			throws IOException {

		if (!inFile.exists()) {
			StdOut.write("コピー元：" + inFile.getPath() + " が存在しません");
			return;
		}

		LineReader reader = new LineReader(inFile, decode);

		// ワークファイルの作成
		File outFile = new File(inFile.getPath() + System.currentTimeMillis());
		LineWriter writer = new LineWriter(outFile, encode);

		String buff = null;

		while ((buff = reader.readLine()) != null) {
			writer.writeLine(buff);
		}

		reader.close();
		writer.close();

		if (!isDebug) {
			inFile.delete();
		} else {
			// debug mode:オリジナルを残す
			inFile.renameTo(new File(inFile.getPath() + ".org"));
		}
		outFile.renameTo(inFile);

	}
}
