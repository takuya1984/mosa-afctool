package com.jbcc.MQTool.create;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.EntryPoint;
import com.jbcc.MQTool.controller.PropertyLoader;

public class TraceLogCreator {
	public static void main(String[] args) {
		try {
			new TraceLogCreator().createLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createLog() throws IOException {
		String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logtemp")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("07_trace")
				+ File.separator;
		File target = new File(INPUT_BASE);
		File[] files = target.listFiles();
		for (File file : files) {
			createLog(file);
		}
	}

	public void createLog(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		String logcd = "log_cd=7";
		String[] fileinfos = file.getName().split("_");
		if (fileinfos.length < 3)
			return;
		
		// 時間
		String logOutputDate = fileinfos[0];
		// ISPEC
		String ispec = fileinfos[1];
		// テーブル名
		String tablename = fileinfos[2].split(".log")[0];

		EntryPoint.main(new String[]{
				"RegistLogData", 
				logcd, 
				"log_output_date=".concat(logOutputDate),
				"function_cd=".concat(ispec),
				"log_table_name=".concat(tablename),
				"log_data_file=".concat(file.getName())});
	}
}
