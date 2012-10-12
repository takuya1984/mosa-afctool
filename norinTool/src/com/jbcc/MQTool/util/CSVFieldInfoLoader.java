package com.jbcc.MQTool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;

public class CSVFieldInfoLoader {

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

	public List<FieldInfo> getFieldInfo(String dbnId, String upDwKind)
			throws IOException {

		List<FieldInfo> list = new ArrayList<FieldInfo>();
		LineReader reader = null;

		try {
			reader = new LineReader(DBN_INFO_PATH + dbnId + "_" + upDwKind);

			String buff = null;
			String[] params = null;
			while ((buff = reader.readLine()) != null) {
				params = buff.split("\t");
				if (params.length == cnt) {
					FieldInfo info = new FieldInfo();
					info.setFieldNameJ(params[KOUMOKU_NAME]);
					list.add(info);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return list;
	}
}
