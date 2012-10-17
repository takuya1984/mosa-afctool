package com.jbcc.MQTool.compare;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.DbioFieldInfoLoader;
import com.jbcc.MQTool.util.TraceLogReader;

public class GetTraceCompareLog extends ComparableLog {

	private static String TRACE_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logbase")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("07_trace");

	@Override
	public List<String> getCompareLog(String LOG_PATH, String[] fileKye,
			String fileName) throws IOException {

		//filenameの生成
		TraceLogReader tlr = new TraceLogReader(TRACE_BASE + File.separator + fileName);
		List<String> ret = tlr.getList();
		tlr.close();

		//FieldInfo 情報の取得とセット(フィールド情報にはスキップ項目を含めない)
		setFieldInfo(DbioFieldInfoLoader.getFieldInfo(fileName, true));

		return ret;
	}
}
