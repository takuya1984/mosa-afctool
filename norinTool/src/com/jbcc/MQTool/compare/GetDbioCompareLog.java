package com.jbcc.MQTool.compare;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.DbioFieldInfoLoader;
import com.jbcc.MQTool.util.DbioLogReader;

public class GetDbioCompareLog extends ComparableLog {

	private static String DBIO_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logbase")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("08_dbio");

	@Override
	public List<String> getCompareLog(String LOG_PATH, String[] fileKye,
			String fileName) throws IOException {

		// filenameの生成
		DbioLogReader tlr = new DbioLogReader(DBIO_BASE + File.separator + fileName);
		List<String> ret = tlr.getList();
		tlr.close();

		//FieldInfo 情報の取得とセット
		super.fieldInfo = DbioFieldInfoLoader.getFieldInfo(fileName, true);

		return ret;
	}
}
