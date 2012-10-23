package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

public class OtxLogConverter {

	private static boolean debug = false;

	/**
	 * 入力パス
	 */
	private String INPUT_BASE = null;

	/**
	 * 出力パス
	 */
	private String OUTPUT_BASE = null;

	public static void main(String[] args) {
		try {
			if (debug) {
				OtxLogConverter conv = new OtxLogConverter();

//				File target = new File("/Users/kohgami/tmp/script/tmp/");
//				for (File file : target.listFiles()) {
//					conv.readTargets("3", file.getPath());
//				}
				conv.readTargets("3", "Cl=00099_Msg=0801030_Tsq=0002.20121023144352.tmp.2219");
			} else {
				new OtxLogConverter().readTargets(args[0], args[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void readTargets(String mode, String filename) throws IOException {
		String key = "";
		if ("3".equals(mode)) {
			key = "03_otx-css";
		} else if ("4".equals(mode)) {
			key = "04_otx-onl";
		} else if ("5".equals(mode)) {
			key = "05_apinfo";
		} else if ("6".equals(mode)) {
			key = "06_aphost";
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

	public void readTargets(File file) throws IOException {

		if (!file.exists()) {
			return;
		}

		LineReader reader = new LineReader(file);

		String buff = null;
		String[] tokens = null;
		LineWriter writerUp = null;
		LineWriter writerDw = null;

		String dateUp = "";String dateDw = "";
		String timeUp = "";String timeDw = "";
		String msecUp = "";String msecDw = "";
		String headerUp = "";String headerDw = "";
		StringBuffer logBuffUp = new StringBuffer();;
		StringBuffer logBuffDw = new StringBuffer();;
		boolean process = false;
		while ((buff = reader.readLine()) != null) {
			tokens = buff.split(" {1,}");
			if (tokens.length < 4)
				continue;

			// 上りヘッダ情報取得
			if (buff.indexOf("上り共通ヘッダ部") > -1) {
				process = true;
				headerUp = buff.replaceFirst(".*上り共通ヘッダ部=", "");
				dateUp = tokens[0].replaceAll("/", "-");
				timeUp = tokens[1].substring(0, 8).replaceAll(":", "");
				msecUp = tokens[1].substring(9, 12);
				logBuffUp.append(buff + "\n");
				continue;
			}
			if (process && (
					buff.indexOf("上りオンライン業務固有部") > -1 ||
					buff.indexOf("上りCSS業務固有部") > -1)) {
				logBuffUp.append(buff + "\n");
				
				writerUp = new LineWriter(OUTPUT_BASE + 
						dateUp + timeUp + msecUp + "_" + 
						headerUp.substring(0, 5) + "_"+ 
						headerUp.substring(5, 6) + "_"+ 
						headerUp.substring(6, 13) + "_"+ 
						"1.dat");
				writerUp.writeLine(logBuffUp.toString());
				writerUp.close();
				logBuffUp = new StringBuffer();
				process = false;
				continue;
			}
			// 下りヘッダ情報取得
			if (buff.indexOf("下り共通ヘッダ部") > -1) {
				process = true;
				headerDw = buff.replaceFirst(".*下り共通ヘッダ部=", "");
				dateDw = tokens[0].replaceAll("/", "-");
				timeDw = tokens[1].substring(0, 8).replaceAll(":", "");
				msecDw = tokens[1].substring(9, 12);
				logBuffDw.append(buff + "\n");
				continue;
			}
			if (process && buff.indexOf("エラー制御部") > -1) {
				logBuffDw.append(buff + "\n");
				continue;
			}
			if (process && (
					buff.indexOf("下りオンライン業務固有部") > -1 ||
					buff.indexOf("下りCSS業務固有部") > -1)) {
				
				logBuffDw.append(buff + "\n");
				continue;
			}
			// 画面IDの取得
			if (process && buff.indexOf("Ope=") > -1) {
				logBuffDw.append(buff + "\n");
				
				writerDw = new LineWriter(OUTPUT_BASE + 
						dateDw + timeDw + msecDw + "_" + 
						headerDw.substring(0, 5) + "_"+ 
						headerDw.substring(5, 6) + "_"+ 
						headerDw.substring(6, 13) + "_"+ 
						"2.dat");
				writerDw.writeLine(logBuffDw.toString());
				writerDw.close();
				
				logBuffDw = new StringBuffer();
				process = false;
			}
		}
		reader.close();
	}
}
