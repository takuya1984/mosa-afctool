package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

public class TraceLogConverter {

	private static boolean debug = true;

	private static String SJIS = "Shift-JIS";

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
			+ PropertyLoader.getDirProp().getProperty("logbase")
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

	public void readTargets(String filePath) throws IOException {
		readTargets(new File(filePath));
	}

	public void readTargets(File file) throws IOException {
		LineReader reader = new LineReader(file, SJIS);
		LineWriter writer = new LineWriter(OUTPUT_BASE + "test.txt");

		String buff = null;

		while ((buff = reader.readLine()) != null) {
			writer.writeLine(buff);
		}

		reader.close();
		writer.close();
	}

}
