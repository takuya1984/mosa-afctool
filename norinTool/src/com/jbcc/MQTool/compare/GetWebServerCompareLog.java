package com.jbcc.MQTool.compare;

import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.constant.LogReaderConstant;
import com.jbcc.MQTool.util.CSVFieldInfoLoader;
import com.jbcc.MQTool.util.GetCompareLog;


/**
 * 抽出されたWebServerログから比較するデータを取得します。
 *
 */
public class GetWebServerCompareLog extends ComparableLog {

	/**
	 * 抽出されたWebServerログから比較するデータを取得します。
	 *
	 * @param LOG_PATH
	 * @param fileKye
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
//	public List<String> getWebServerCompareLog(final String LOG_PATH, String[] fileKye, String fileName) throws IOException {
	public List<String> getCompareLog(String LOG_PATH, String[] fileKye, String fileName) throws IOException {


		//項目情報の付加
		super.setFieldInfo(new CSVFieldInfoLoader().getFieldInfo(fileName));

		if (fileKye[2].equals(LogReaderConstant.ONL_LOG) && fileKye[4].equals(LogReaderConstant.UP_LOG)) {
			// オンライン上り
			return GetCompareLog.getFixedLengthDataList(
					LOG_PATH,fileName,LogReaderConstant.SOAP_TAG,LogReaderConstant.SOAP_TAG.length() + 1);

//		} else if (fileKye[3].equals(LogReaderConstant.ONL_LOG) && fileKye[5].equals(LogReaderConstant.DW_LOG)) {
//			// オンライン下り
//			return GetCompareLog.getTabDelimitedLogData(
//					LOG_PATH,fileName,LogReaderConstant.SOAP_DW_ONL_TAG,LogReaderConstant.DELIMITED_TAB,LogReaderConstant.SOAP_DW_ONL_TAG.length() + 1,false);
//
//		} else if (fileKye[3].equals(LogReaderConstant.CSS_LOG) && fileKye[5].equals(LogReaderConstant.UP_LOG)) {
//			// CSS上り
//			return GetCompareLog.getTabDelimitedLogData(
//					LOG_PATH,fileName,LogReaderConstant.SOAP_UP_CSS_TAG,LogReaderConstant.DELIMITED_TAB,LogReaderConstant.SOAP_UP_CSS_TAG.length() + 1,false);
//
//		} else if (fileKye[3].equals(LogReaderConstant.CSS_LOG) && fileKye[5].equals(LogReaderConstant.DW_LOG)) {
//			// CSS下り
//			return GetCompareLog.getTabDelimitedLogData(
//					LOG_PATH,fileName,LogReaderConstant.SOAP_DW_CSS_TAG,LogReaderConstant.DELIMITED_TAB,LogReaderConstant.SOAP_DW_CSS_TAG.length() + 1,false);
//		}
		} else {
			return GetCompareLog.getTabDelimitedLogData(
					LOG_PATH,fileName,LogReaderConstant.SOAP_TAG,LogReaderConstant.DELIMITED_TAB,-1,false);
		}

//		return new ArrayList<String>();
	}
}
