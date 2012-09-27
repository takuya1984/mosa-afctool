package com.jbcc.MQTool.compare;

import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.util.TraceLogReader;

public class GetTraceCompareLog extends StringCompare implements Compare {

	@Override
	public List<String> getCompareLog(String LOG_PATH, String[] fileKye,
			String fileName) throws IOException {

		//TODO filenameの生成
		TraceLogReader tlr = new TraceLogReader(fileName);
		List<String> ret = tlr.getList();
		tlr.close();

		return ret;
	}
}
