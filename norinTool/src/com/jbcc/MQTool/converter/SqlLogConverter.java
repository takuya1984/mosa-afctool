package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

/**
 * SQLlog抽出クラス.
 *
 */
public class SqlLogConverter {

	private static boolean debug = false;

	/**
	 * 入力パス
	 */
	private String INPUT_BASE = null;

	/**
	 * 出力パス
	 */
	private String OUTPUT_BASE = null;

	/** 読み込み時文字コード */
	private String encode = "UTF-8";

	public static void main(String[] args) {
		try {
			if (debug) {
				SqlLogConverter conv = new SqlLogConverter();

//				File target = new File("/Users/kohgami/tmp/script/tmp/");
//				for (File file : target.listFiles()) {
//					conv.readTargets("3", file.getPath());
//				}
				conv.readTargets("9", "Cl=00046_Msg=0202030_Tsq=0032.20121213153659.tmp.1420");
			} else {
				new SqlLogConverter().readTargets(args[0], args[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ログ抽出処理.
	 * @param mode
	 * @param filename
	 * @throws IOException
	 */
	public void readTargets(String mode, String filename) throws IOException {
		String key = "";
		if ("9".equals(mode)) {
			key = "09_otx-sql";
		} else if ("10".equals(mode)) {
			key = "10_ap-sql";
		}

		INPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ "tmp"
				+ File.separator;

		OUTPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logtemp")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty(key)
				+ File.separator;

		readTargets(new File(INPUT_BASE + filename));
	}

	/**
	 * ログ抽出処理.
	 * @param file 対象ファイル名
	 * @throws IOException
	 */
	public void readTargets(File file) throws IOException {

		if (!file.exists()) return;
		if (file.isDirectory()) return;

		LineReader reader = new LineReader(file, encode);

		String buff = null;
		String[] tokens = null;
		LineWriter writer = null;

		String dateUp = "";String timeUp = "";
		String headerUp = "";
		StringBuffer logBuff = null;
		String filename = "";

		boolean process = false;
		while ((buff = reader.readLine()) != null) {
			tokens = buff.split(" {1,}");
			if (tokens.length < 4)
				continue;

			// 上りヘッダ情報取得
			if (buff.indexOf("上り共通ヘッダ部") > -1) {
				logBuff = new StringBuffer();
				process = true;
				headerUp = buff.replaceFirst(".*上り共通ヘッダ部=", "");
				dateUp = tokens[0].replaceAll("/", "");
				timeUp = tokens[1].replaceAll(":", "");
				if (timeUp.indexOf(".") > -1)
					timeUp = timeUp.replaceAll("\\.", "");
				if (timeUp.indexOf("|") > -1)
					timeUp = timeUp.replaceAll("\\|.*", "");
				
				filename = 
						dateUp.substring(0, 4) + "-" + 
						dateUp.substring(4, 6) + "-" + 
						dateUp.substring(6, 8) + "-" + 
						timeUp + "_" + 
						headerUp.substring(0, 5) + "_"+ 
						headerUp.substring(5, 6) + "_"+ 
						headerUp.substring(6, 13) + "_sql.dat";

				logBuff.append(buff + "\n");
				continue;
			}
			if (process && (
					buff.indexOf("上りオンライン業務固有部") > -1 ||
					buff.indexOf("上りCSS業務固有部") > -1)) {
				logBuff.append(buff + "\n");
				continue;
			}
			if (process && (
					buff.indexOf("=<select") > -1 ||
					buff.indexOf("=<insert") > -1 ||
					buff.indexOf("=<update") > -1 ||
					buff.indexOf("=<delete") > -1
					)) {
				logBuff.append(buff + "\n");
				continue;
			}
			// 下りヘッダ情報取得
			if (process && buff.indexOf("下り共通ヘッダ部") > -1) {
				logBuff.append(buff + "\n");
				continue;
			}
			if (process && buff.indexOf("エラー制御部") > -1) {
				logBuff.append(buff + "\n");
				continue;
			}
			if (process && (
					buff.indexOf("下りオンライン業務固有部") > -1 ||
					buff.indexOf("下りCSS業務固有部") > -1)) {
				
				logBuff.append(buff + "\n");
				continue;
			}
			// 画面IDの取得
			if (process && buff.indexOf("Ope=") > -1) {
				logBuff.append(buff + "\n");
			}
			
			if (process) {
				writer = new LineWriter(OUTPUT_BASE + filename); 
				writer.writeLine(logBuff.toString());
				writer.close();
				process = false;
			}

		}
		reader.close();
	}
}
