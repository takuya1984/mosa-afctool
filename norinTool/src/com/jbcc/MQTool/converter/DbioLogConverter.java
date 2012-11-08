package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.controller.ToolException;
import com.jbcc.MQTool.util.CSVIoCopyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

/**
 * DBIOlog抽出クラス.
 *
 */
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

	/**
	 * ログ抽出処理.
	 * @param filePath 対象ファイル名
	 * @throws IOException
	 */
	public void readTargets(String filePath) throws Exception {
		readTargets(new File(filePath));
	}

	/**
	 * ログ抽出処理.
	 * @param file 対象ファイル名
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public void readTargets(File file) throws Exception {
		if (!file.exists()) return;
		if (file.isDirectory()) return;

		LineReader reader = new LineReader(file, "Shift-JIS");

		// ISPEC値は入力ファイル名から取得
		String[] filenames = file.getName().split("_");
		if (filenames.length < 2) {
			String msg = "ファイル名の形式が異なります. file=" + file.getName() + " RDBOUTLOG_<ISPEC値>_yyyymmdd.log";
			throw new ToolException(msg);
		}
		String ispec = filenames[1];

		String buff = null;// 読み込みバッファ
		int index = 0;
		while ((buff = reader.readLine()) != null) {
			if (buff.length() < 64)
				continue;
			// 開始キーワードでファイルオープン::"INST", "UPDT", "DELE"が対象
			String key = buff.substring(60, 64);
			if (!KEYWORDS.contains(key))
				continue;

			// ファイル出力
			String logOutputDate = buff.substring(0, 14);
			String logTableName = buff.substring(50, 55);
			int startindex = new CSVIoCopyLoader().getStartIndex(logTableName);
			LineWriter writer = new LineWriter(OUTPUT_BASE +
					logOutputDate + "_" + ++index + "_" + ispec + "_" + logTableName + ".dat");
			writer.writeLine(buff.substring(0, 64) + buff.substring(64 + startindex));
			writer.close();
		}
		reader.close();
	}
}
