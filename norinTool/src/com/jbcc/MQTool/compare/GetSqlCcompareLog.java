package com.jbcc.MQTool.compare;

import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.constant.LogReaderConstant;
import com.jbcc.MQTool.util.GetCompareLog;

public class GetSqlCcompareLog extends ComparableLog {

	public List<String> getCompareLog(final String LOG_PATH, String[] fileKye, String fileName) throws IOException{


		return GetCompareLog.getTabDelimitedLogData(
				LOG_PATH,fileName,LogReaderConstant.SQL_KEY,LogReaderConstant.DELIMITED_COMMA,1,true);
	}
}
