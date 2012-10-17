package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.PropertyLoader;

public class CSVIoCopyLoader {

	private static boolean isDebug = true;
	private static String FILENAME = "IOCOPY.csv"; // IOCOPY定義ファイル名

	// configureディレクトリを取得
	protected static String IOCOPY_INFO_PATH = PropertyLoader.getDirProp()
			.getProperty("basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("configure")
			+ File.separator;

	public static void main(String[] args) {
		try {
			if (isDebug) {
				int index = new CSVIoCopyLoader().getStartIndex("OPID0");
				StdOut.writeDebug(String.valueOf(index));
			} else {
				int index = new CSVIoCopyLoader().getStartIndex(args[0]);
				StdOut.writeDebug(String.valueOf(index));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getStartIndex(String ispec) throws IOException {

		LineReader reader = null;
		int index = 0;
		try {
			reader = new LineReader(IOCOPY_INFO_PATH + FILENAME);

			String buff = null;
			String[] values = null;
			while ((buff = reader.readLine()) != null) {
				if (!buff.startsWith(ispec))
					continue;
				
				values = buff.split(",");
				if (values.length > 2) {
					index = Integer.parseInt(values[1].trim());
					break;
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return index;
	}
}
