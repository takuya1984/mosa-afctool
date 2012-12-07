package com.jbcc.MQTool.create;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.EntryPoint;
import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.FileUtil;
import com.jbcc.MQTool.util.LineReader;

/**
 * DBIOログ登録クラス.
 *
 */
public class DbioLogCreator {
	public static void main(String[] args) {
		try {
			new DbioLogCreator().createLog();
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
				+ PropertyLoader.getDirProp().getProperty("08_dbio")
				+ File.separator;
		String OUPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logbase")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("08_dbio")
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
	@SuppressWarnings("resource")
	public int createLog(File file) throws IOException {
		if (!file.exists()) {
			return 1;
		}
		LineReader reader = new LineReader(file);

		String buff = null;// 読み込みバッファ
		String logcd = "log_cd=8";
		String[] fileinfos = file.getName().split("_");
		if (fileinfos.length < 4)
			return 1;
		
		// 時間
		String logOutputDate = fileinfos[0];
		// ISPEC
		String ispec = fileinfos[2];
		// テーブル名
		String tablename = fileinfos[3];
		// 職員番号
		String empNo = "";
		while ((buff = reader.readLine()) != null) {
			if (buff.length() < 23)
				break;
			
			empNo = buff.substring(24, 29);
		}
		reader.close();

		EntryPoint entry = new EntryPoint();
		return entry.execute(
			new String[]{
				"RegistLogData", 
				logcd, 
				"log_output_date=".concat(logOutputDate),
				"function_cd=".concat(ispec),
				"log_table_name=".concat(tablename),
				"log_data_file=".concat(file.getName()),
				"emp_no=".concat(empNo)}
		);
	}
}
