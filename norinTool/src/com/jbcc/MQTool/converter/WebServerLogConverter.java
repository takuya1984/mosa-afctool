package com.jbcc.MQTool.converter;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;
import com.jbcc.MQTool.util.LineWriter;

public class WebServerLogConverter {

	private static boolean debug = false;

	/**
	 * 入力パス
	 */
	private String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logtarget")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("02_web")
			+ File.separator;

	/**
	 * 出力パス
	 */
	private String OUTPUT_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logtemp")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("02_web")
			+ File.separator;

	public static void main(String[] args) {
		try {
			if (debug) {
				WebServerLogConverter conv = new WebServerLogConverter();

//				File target = new File(conv.INPUT_BASE);
//				for (File file : target.listFiles()) {
//					conv.readTargets(file);
//				}
				conv.readTargets(new File(conv.INPUT_BASE + "server.log.tmp"));
			} else {
				new WebServerLogConverter().readTargets(args[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void readTargets(String filePath) throws IOException {
		readTargets(new File(INPUT_BASE + filePath));
	}

	public void readTargets(File file) throws IOException {

		if (!file.exists()) return;
		if (file.isDirectory()) return;

		LineReader reader = new LineReader(file);

		String buff = null;
		String[] tokens = null;
		LineWriter writer = null;

		String date = "";
		String time = "";
		String msec = "";
		String upDown = "";
		String header = "";
		boolean process = false;
		StringBuffer logBuff = null;
		while ((buff = reader.readLine()) != null) {

			// 不要電文チェック
			if (buff.indexOf("<bon:ResponseMessageList") > -1 || 
				buff.indexOf("<faultcode>") > -1 ||
				buff.indexOf("</soapenv:Envelope>") > -1) {
				process = false;
				continue;
			}
			
			if (!process && buff.indexOf(" TRACE ") > -1) { 
				process = true;
				logBuff = new StringBuffer();
				
				// 上り下り区分取得
				if (buff.indexOf("Request Message") > -1)
					upDown = "1";
				else
					upDown = "2";
				
				tokens = buff.split(" {1,}");
				if (tokens.length < 4)
					continue;

				date = tokens[0];
				time = tokens[1].split(",")[0].replaceAll(":", "");
				msec = tokens[1].split(",")[1];
				
			}
			// ヘッダ情報取得
			if (buff.indexOf("<strComUpHeadDt>") > -1) {
				header = buff.replaceFirst(".*<strComUpHeadDt>", "").replaceFirst("</strComUpHeadDt>.*", "");
			} else if (buff.indexOf("<strbufComDwHeadDt>") > -1) {
				header = buff.replaceFirst(".*<strbufComDwHeadDt>", "").replaceFirst("</strbufComDwHeadDt>.*", "");
			}
			
			if (process) 
				logBuff.append(buff + "\n");
			
			if (process && buff.indexOf("</env:Envelope>") > -1) {
				writer = new LineWriter(OUTPUT_BASE + 
						date + "-" + time + msec + "_" + 
						header.substring(0, 5) + "_"+ 
						header.substring(5, 6) + "_"+ 
						header.substring(6, 13) + "_"+ 
						upDown + ".dat");
				process = false;
				writer.writeLine(logBuff.toString());
				writer.close();
				logBuff = null;
			}
		}

		reader.close();
	}
}
