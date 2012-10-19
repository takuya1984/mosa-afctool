package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

public class FileConverter {

	private static boolean isDebug = false;

	public static void main(String[] fileNames) throws IOException {
		if (isDebug) {
			FileConverter.convert("/Users/MOSA/test.log", "Shift-JIS", "UTF-8");
		} else {
			for (String file : fileNames) {
				FileConverter.convertSJIStoUTF8(file);
			}
		}
	}

	public static void convertSJIStoUTF8(String file) throws IOException {
		convert(file, "Shift-JIS", "UTF-8");
	}

	public static void convertSJIStoUTF8(File file) throws IOException {
		convert(file, "Shift-JIS", "UTF-8");
	}

	public static void convert(String inFile, String decode, String encode)
			throws IOException {
		convert(new File(inFile), decode, encode);
	}

	/**
	 * ファイルの変換
	 *
	 * @param inFile
	 * @param decode
	 * @param encode
	 * @throws IOException
	 */
	public static void convert(File inFile, String decode, String encode)
			throws IOException {

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
