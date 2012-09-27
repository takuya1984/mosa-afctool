package com.jbcc.MQTool.compare;

import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.util.DbioLogReader;

public class GetDbioCompareLog extends StringCompare implements Compare {

	@Override
	public List<String> getCompareLog(String LOG_PATH, String[] fileKye,
			String fileName) throws IOException {

		//TODO filenameの生成
		DbioLogReader tlr = new DbioLogReader(fileName);
		List<String> ret = tlr.getList();
		tlr.close();
		return ret;
	}
}
