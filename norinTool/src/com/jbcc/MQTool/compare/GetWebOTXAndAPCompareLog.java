package com.jbcc.MQTool.compare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jbcc.MQTool.constant.LogReaderConstant;
import com.jbcc.MQTool.util.GetCompareLog;

/**
 * 抽出されたWebOTX、APログから比較するデータを取得します。
 *
 */
public class GetWebOTXAndAPCompareLog extends ComparableLog {

	/**
	 * 抽出されたWebOTX、APログから比較するデータを取得します。
	 *
	 * @param LOG_PATH
	 * @param fileKye
	 * @param FileName
	 * @return
	 * @throws IOException
	 */
	@Override
	public List<String> getCompareLog(String LOG_PATH, String[] fileKye,String FileName) throws IOException{

		if (fileKye[3].equals(LogReaderConstant.ONL_LOG) && fileKye[5].equals(LogReaderConstant.UP_LOG)) {
			// オンライン上り
			return GetCompareLog.getFixedLengthDataList(
					LOG_PATH,FileName,LogReaderConstant.OTX_UP_ONL_KEY,0);

		} else if (fileKye[3].equals(LogReaderConstant.ONL_LOG) && fileKye[5].equals(LogReaderConstant.DW_LOG)) {
			// オンライン下り
			return GetCompareLog.getTabDelimitedLogData(
					LOG_PATH,FileName,LogReaderConstant.OTX_DW_ONL_KEY,LogReaderConstant.DELIMITED_TAB,0,false);

		} else if (fileKye[3].equals(LogReaderConstant.CSS_LOG) && fileKye[5].equals(LogReaderConstant.UP_LOG)) {
			// CSS上り
			return GetCompareLog.getTabDelimitedLogData(
					LOG_PATH,FileName,LogReaderConstant.OTX_UP_CSS_KEY,LogReaderConstant.DELIMITED_TAB,0,false);

		} else if (fileKye[3].equals(LogReaderConstant.CSS_LOG) && fileKye[5].equals(LogReaderConstant.DW_LOG)) {
			// CSS下り
			return GetCompareLog.getTabDelimitedLogData(
					LOG_PATH,FileName,LogReaderConstant.OTX_DW_CSS_KEY,LogReaderConstant.DELIMITED_TAB,0,false);
		}
		return new ArrayList<String>();
	}
}
