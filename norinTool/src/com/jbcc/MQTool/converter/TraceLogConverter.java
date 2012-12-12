package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

/**
 * Tracelog抽出クラス.
 *
 */
public class TraceLogConverter {

	private static boolean debug = false;

	private List<String> KEYWORDS = Arrays.asList(new String[] { "SELECT",
			"INSERT", "UPDATE", "DELETE" });

	/**
	 * 入力パス
	 */
	private String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logtarget")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("07_trace")
			+ File.separator;

	/**
	 * 出力パス
	 */
	private String OUTPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logtemp")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("07_trace")
			+ File.separator;

	public static void main(String[] args) {
		try {
			if (debug) {
				TraceLogConverter conv = new TraceLogConverter();

				File target = new File(conv.INPUT_BASE);
				for (File file : target.listFiles()) {
					conv.readTargets(file);
				}
			} else {
				new TraceLogConverter().readTargets(args[0]);
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
	public void readTargets(String filePath) throws IOException {
		readTargets(new File(filePath));
	}

	/**
	 * ログ抽出処理.
	 * @param file 対象ファイル名
	 * @throws IOException
	 */
	public void readTargets(File file) throws IOException {

		if (!file.exists()) return;
		if (file.isDirectory()) return;

		LineReader reader = new LineReader(file, "Shift-JIS");

		String buff = null;
		String[] tokens = null;
		LineWriter writer = null;

		String time = null;
		String tableName = null;

		// ISPEC番号は入力ファイル名から

//		String ispec = file.getName().replaceAll(".*_", "")
//				.replaceAll("\\..*", "");
		// ファイル名[TRACE_<ISPEC値>_<オリジナルファイル識別子>_<yyyymmdd>.txt]から以下の情報を取得		
		String[] filename = file.getName().split("_");
		String ispec = "";String date = "";
		if (filename.length > 1)
			ispec = filename[1];
		if (filename.length > 3)
			date = filename[3].replaceAll("\\..*", "");
		
		int dataNo = 0, lastDataNo = 0, maxLength = 0;
		String prev = null;

		while ((buff = reader.readLine()) != null) {

			tokens = buff.split(" {1,}");

			// 開始キーワードでファイルオープン::select,update,insert,deleteが対象
			if (tokens.length >= 3 && tokens[1].equals("DB:")) {
				if (!KEYWORDS.contains(tokens[3])) {
					// 対象行ではないので読み込み続行
					continue;
				}
			} else {
				continue;
			}

			// 13:05:28.458 DB: FP SELECT CUR FRM ICP02.OHEG0:PRODUCTION
			// --> 130528.458_$ISPEC_OHEG0.log
			time = tokens[0].replaceAll(":", "");
			tableName = tokens[tokens.length - 1].replaceAll(":.*", "")
					.replaceAll("^.*\\.", "");

			writer = new LineWriter(OUTPUT_BASE + 
					date.substring(0, 4) + "-" +
					date.substring(4, 6) + "-" +
					date.substring(6, 8) + "-" +
					time + "_" + ispec + "_"
					+ tableName + ".log");

			// AFTERが出るまでリード
			while ((buff = reader.readLine()) != null) {
				if (buff.startsWith("AFTER")) {
					break;
				}
			}

			// 書き込み開始
			while ((buff = reader.readLine()) != null) {
				if (buff.startsWith("RID=")) {
					// 終了ワードでファイルクローズ
					writer.close();
					break;
				}

				dataNo = Integer.parseInt(buff.substring(0, 8));// データ番号、+5刻み

				// *が出たら最終ワードで埋める
				if (buff.charAt(9) == '*') {
					prev = "@ @ @ @ @".replaceAll("@", prev);
					for (int i = 0; i < ((dataNo - lastDataNo) / 5) - 1; i++) {
						writer.writeLine(prev);
					}
				}

				// 固定長を切り取る。足りない場合はbuff.length()まで。
				maxLength = 78;
				if (buff.length() < maxLength) {
					maxLength = buff.length();
				}

				// データ書き込み。連続するスペースは１つにする
				buff = buff.substring(10, maxLength).replaceAll(" {1,}", " ");
				writer.writeLine(buff);

				// 最終ワードをバッファリング
				if (buff.indexOf(" ") >= 0) {
					prev = buff.substring(buff.lastIndexOf(" ") + 1);
				}

				lastDataNo = dataNo;
			}
		}

		reader.close();
	}
}
