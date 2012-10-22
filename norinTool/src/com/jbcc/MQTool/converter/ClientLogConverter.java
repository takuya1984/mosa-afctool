package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

public class ClientLogConverter {

	private static boolean debug = false;

	/**
	 * 入力パス
	 */
	private String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logtarget")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("01_client")
			+ File.separator;

	/**
	 * 出力パス
	 */
	private String OUTPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("log")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("01_client")
			+ File.separator;

	public static void main(String[] args) {
		try {
			if (debug) {
				ClientLogConverter conv = new ClientLogConverter();

//				File target = new File(conv.INPUT_BASE);
//				for (File file : target.listFiles()) {
//					conv.readTargets(file);
//				}
				conv.readTargets(new File(conv.INPUT_BASE + "ClientToday_3_20120409.log"));
			} else {
				new ClientLogConverter().readTargets(args[0]);
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

		String buff = null;
		String[] tokens = null;
		LineWriter writer = null;

		String date = null;
		String time = null;
		String upDown = "";
		String header = "";
		boolean process = false;
		while ((buff = reader.readLine()) != null) {

			tokens = buff.split(" {1,}");
			if (tokens.length < 4)
				continue;

			if (!process) { 
				process = true;
				date = tokens[0].replaceAll("/", "-");
				time = tokens[1].replaceAll(":", "");
				
				// 上り下り区分取得
				if ("送信".equals(tokens[3]))
					upDown = "1";
				else
					upDown = "2";
				
				// ヘッダー取得
				if (buff.indexOf(" Com ") > -1) {
					header = buff.split(" Com ")[1];
				}
				writer = new LineWriter(OUTPUT_BASE + 
						date + time + "_" + 
						header.substring(0, 6) + "_"+ 
						header.substring(6, 7) + "_"+ 
						header.substring(7, 13) + "_"+ 
						upDown + ".dat");
			}
			writer.writeLine(buff);
			
			if (process && "Css".equals(tokens[4])) {
				process = false;
				writer.close();
			}
		}

		reader.close();
	}
}
