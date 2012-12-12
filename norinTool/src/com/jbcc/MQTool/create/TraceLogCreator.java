package com.jbcc.MQTool.create;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.EntryPoint;
import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.FileUtil;

/**
 * tracelog登録クラス.
 *
 */
public class TraceLogCreator {
	public static void main(String[] args) {
		try {
			new TraceLogCreator().createLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ログ登録処理.
	 * @throws IOException
	 */
	public void createLog() throws IOException {
		String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logtemp")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("07_trace")
				+ File.separator;
		String OUPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logbase")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("07_trace")
				+ File.separator;
		File target = new File(INPUT_BASE);
		File[] files = target.listFiles();
		for (File file : files) {
			int ret = createLog(file);
			if (ret == 0) {
				// ファイル移動
				FileUtil copy = new FileUtil();
				copy.copy(INPUT_BASE + "/" + file.getName(), OUPUT_BASE + "/" + file.getName());
				file.delete();
			}
		}
	}

	/**
	 * ログのDB登録.
	 * @param file 対象ファイル
	 * @throws IOException
	 */
	public int createLog(File file) throws IOException {
		if (!file.exists()) {
			return 1;
		}
		String logcd = "log_cd=7";
		String[] fileinfos = file.getName().split("_", 3);
		if (fileinfos.length < 3)
			return 1;
		
		// 日付 + 時間
		String logOutputDate = fileinfos[0];
		if (logOutputDate.indexOf(".") > -1)
			logOutputDate = logOutputDate.replaceAll("-", "").replaceAll("\\.", "");

		// ISPEC
		String ispec = fileinfos[1];
		// テーブル名
		String tablename = fileinfos[2].split(".log")[0];

		EntryPoint entry = new EntryPoint();
		return entry.execute(
			new String[]{
				"RegistLogData", 
				logcd, 
				"log_output_date=".concat(logOutputDate),
				"function_cd=".concat(ispec),
				"log_table_name=".concat(tablename),
				"log_data_file=".concat(file.getName())});
	}
}
