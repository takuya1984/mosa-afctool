package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;

public class CSVFieldInfoLoader {

	private static boolean isDebug = true;

	public static int cnt = 0;

	private static int KOUMOKU_NO = cnt++;// 項目No
	private static int KOUMOKU_NAME = cnt++;// 項目名
	private static int ZOKUSEI = cnt++;// 属性
	private static int KETASU = cnt++;// 桁数
	private static int KITEICHI = cnt++;// 規定値
	private static int DATA = cnt++;// データ（上りは取得元、下りはセット先）
	private static int BIKOU = cnt++;// 備考

	// 電文仕様のベースディレクトリを取得
	protected static String DBN_INFO_PATH = PropertyLoader.getDirProp()
			.getProperty("basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("denbun")
			+ File.separator;

	public static void main(String[] args) {
		try {
			if (isDebug) {
				List<FieldInfo> fields = new CSVFieldInfoLoader()
						.getFieldInfo("yyyymmdd_hhmmss_sss_X_0305020_1.dat");

				for (FieldInfo info : fields) {
					StdOut.writeDebug(info.getFieldNameJ());
				}
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<FieldInfo> getFieldInfo(String fileName) throws IOException {
		// 2012-04-09_1357_00000_2_2301010_1.dat
		String[] params = fileName.split("_");
		String dbnId = params[params.length - 2];
		String upDwKind = params[params.length - 1].split("\\.")[0];

		return getFieldInfo(dbnId, upDwKind);
	}

	public List<FieldInfo> getFieldInfo(String dbnId, String upDwKind)
			throws IOException {

		List<FieldInfo> list = new ArrayList<FieldInfo>();
		LineReader reader = null;

		try {
			File file = new File(DBN_INFO_PATH + dbnId + "_" + upDwKind
					+ ".csv");
			if (!file.exists()) {
				return list;
			}

			reader = new LineReader(DBN_INFO_PATH + dbnId + "_" + upDwKind
					+ ".csv");

			String buff = null;
			String[] params = null;
			while ((buff = reader.readLine()) != null) {
				params = buff.split("\t");
				if (params.length > KOUMOKU_NAME) {
					FieldInfo info = new FieldInfo();
					info.setFieldNameJ(params[KOUMOKU_NAME]);
					list.add(info);
				}
			}

		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return list;
	}
}
