package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.CSVIoCopyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

public class DbioLogConverter {

	// FIXME 出来上がったらデバッグモード削除
	private static boolean debug = false;
	private List<String> KEYWORDS =
			Arrays.asList(new String[] { "INST", "UPDT", "DELE" });

	/**
	 * 入力パス
	 */
	private String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logtarget")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("08_dbio")
			+ File.separator;

	/**
	 * 出力パス
	 */
	private String OUTPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logtemp")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("08_dbio")
			+ File.separator;

	public static void main(String[] args) {
		try {
			if (debug) {
				DbioLogConverter conv = new DbioLogConverter();
				File target = new File(conv.INPUT_BASE);
				for (File file : target.listFiles()) {
					conv.readTargets(file);
				}
			} else {
				new DbioLogConverter().readTargets(args[0]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void readTargets(String filePath) throws IOException {
		readTargets(new File(filePath));
	}

	public void readTargets(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		LineReader reader = new LineReader(file, "Shift-JIS");

		// ISPEC値は入力ファイル名から取得
		String ispec = file.getName().replaceAll("_.*", "");

		String buff = null;// 読み込みバッファ
		int index = 0;
		while ((buff = reader.readLine()) != null) {
			if (buff.length() < 54)
				continue;
			// 開始キーワードでファイルオープン::"INST", "UPDT", "DELE"が対象
			String key = buff.substring(49, 53);
			if (!KEYWORDS.contains(key))
				continue;

			// ファイル出力
			String logOutputDate = buff.substring(0, 8);
			String logTableName = buff.substring(39, 44);
			int startindex = new CSVIoCopyLoader().getStartIndex(logTableName);
			LineWriter writer = new LineWriter(OUTPUT_BASE +
					logOutputDate + "_" + ++index + "_" + ispec + "_" + logTableName + ".dat");
			writer.writeLine(buff.substring(0, 53) + buff.substring(53 + startindex));
			writer.close();
		}
		reader.close();
	}
}
