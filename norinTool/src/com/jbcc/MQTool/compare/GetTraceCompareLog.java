package com.jbcc.MQTool.compare;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.TraceLogReader;

public class GetTraceCompareLog extends StringCompare implements Compare {

	private static String TRACE_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logbase")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("07_trace");

	@Override
	public List<String> getCompareLog(String LOG_PATH, String[] fileKye,
			String fileName) throws IOException {

		//TODO filenameの生成
		TraceLogReader tlr = new TraceLogReader(TRACE_BASE + File.separator + fileName);
		List<String> ret = tlr.getList();
		tlr.close();

		return ret;
	}
}
