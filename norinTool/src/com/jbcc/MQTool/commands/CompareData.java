package com.jbcc.MQTool.commands;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.jbcc.MQTool.compare.GetSqlCcompareLog;
import com.jbcc.MQTool.compare.GetVbCompareLog;
import com.jbcc.MQTool.compare.GetWebOTXAndAPCompareLog;
import com.jbcc.MQTool.compare.GetWebServerCompareLog;
import com.jbcc.MQTool.compare.StringCompare;
import com.jbcc.MQTool.constant.LogReaderConstant;
import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.controller.ToolException;

public class CompareData extends ToolCommand {
	
	public void execute (String[] args) throws Exception {
		
		// ログ情報を取得
		List<String[]> logInfoList = this.getCompareLog(args);
		
		// マスターIDを分割
//		String[] masterId = {args[1].substring(0, 2),args[1].substring(2, 4)};
		
		String nonCompareKye = "";
		String nonCompareUpDw = "";
		
		List<List<String>> lists = new ArrayList<List<String>>();
		for (int i = 0;i < 2; i++) {
			String[] logInfo = logInfoList.get(i);
			
			// ファイル名を分割
//			String[] fileKye = logInfo[1].substring(0, logInfo[1].length()-4).split("_");
			String[] fileKye = logInfo[1].substring(0, logInfo[1].length()-4).split("_");
				try {
				if (logInfo[0].equals(LogReaderConstant.MASTER_ID_CLIENT)) {
					// VBクライアントログ
					GetVbCompareLog getVb = new GetVbCompareLog();
					lists.add(getVb.GetVbAndWebCompareLog(LogReaderConstant.CLIENT_PATH,fileKye,logInfo[1]));
					nonCompareKye = fileKye[4];
					nonCompareUpDw = fileKye[5];
					
				} else if (logInfo[0].equals(LogReaderConstant.MASTER_ID_WEB)) {
					// WebServerログ
					GetWebServerCompareLog getWebServer = new GetWebServerCompareLog();
					lists.add(getWebServer.getWebServerCompareLog(LogReaderConstant.WEB_PATH,fileKye,logInfo[1]));
					nonCompareKye = fileKye[4];
					nonCompareUpDw = fileKye[5];
					
				} else if (logInfo[0].equals(LogReaderConstant.MASTER_ID_OTX_CSS)) {
					// WebOTX　CSSログ
					GetWebOTXAndAPCompareLog getOtx = new GetWebOTXAndAPCompareLog();
					lists.add(getOtx.getWebOTXAndAPCompareLog(LogReaderConstant.OTX_CSS_PATH,fileKye,logInfo[1]));
					nonCompareKye = fileKye[4];
					nonCompareUpDw = fileKye[5];
					
				} else if (logInfo[0].equals(LogReaderConstant.MASTER_IDO_TX_ONL)) {
					// WebOTX　オンラインログ
					GetWebOTXAndAPCompareLog getOtx = new GetWebOTXAndAPCompareLog();
					lists.add(getOtx.getWebOTXAndAPCompareLog(LogReaderConstant.OTX_ONL_PATH,fileKye,logInfo[1]));
					nonCompareKye = fileKye[4];
					nonCompareUpDw = fileKye[5];
					
				} else if (logInfo[0].equals(LogReaderConstant.MASTER_IDA_PINFO)) {
					// AP情報系ログ
					GetWebOTXAndAPCompareLog getAp = new GetWebOTXAndAPCompareLog();
					lists.add(getAp.getWebOTXAndAPCompareLog(LogReaderConstant.APINFO_PATH,fileKye,logInfo[1]));
					nonCompareKye = fileKye[4];
					nonCompareUpDw = fileKye[5];
					
				} else if (logInfo[0].equals(LogReaderConstant.MASTER_ID_APHOST)) {
					// AP勘定系ログ
					GetWebOTXAndAPCompareLog getAp = new GetWebOTXAndAPCompareLog();
					lists.add(getAp.getWebOTXAndAPCompareLog(LogReaderConstant.APHOST_PATH,fileKye,logInfo[1]));
					nonCompareKye = fileKye[4];
					nonCompareUpDw = fileKye[5];
					
				} else if (logInfo[0].equals(LogReaderConstant.MASTER_ID_OTX_SQL)) {
					// WebOTX-SQLログ
					GetSqlCcompareLog getSqlLog = new GetSqlCcompareLog();
					lists.add(getSqlLog.getSqlCcompareLog(LogReaderConstant.OTX_SQL_PATH,logInfo[1]));
					nonCompareKye = fileKye[4];
					
				} else if (logInfo[0].equals(LogReaderConstant.MASTER_ID_AP_SQL)) {
					// 情報系AP-SQLログ
					GetSqlCcompareLog getSqlLog = new GetSqlCcompareLog();
					lists.add(getSqlLog.getSqlCcompareLog(LogReaderConstant.AP_SQL_PATH,logInfo[1]));
					nonCompareKye = fileKye[4];
				}
			} catch (FileNotFoundException e) {
				// ファイルが存在しない場合
				throw new ToolException(4);
			}
		}
		
		// 比較除外項目の取得
		String sql = RESOURCE.getSql("GetNonCompare");
		String where =  "where MASTER_ID = ? and KEY = ? ";
		List<String> params = new ArrayList<String>();
		params.add(args[1]);
		params.add(nonCompareKye);
		if (!nonCompareUpDw.equals("")) {
			where = where + " and UP_DOWN_CD = ?";
			params.add(nonCompareUpDw);
		}
		List<Map<String, Object>> result = RESOURCE.selectDB(
				MessageFormat.format(sql, where),params.toArray());
		TreeSet<Integer> nonCompareList = new TreeSet<Integer>();
		for (Map<String, Object> data : result) {
			nonCompareList.add(Integer.parseInt(data.get("ITEM_SEQ").toString()));
		}
		
		// 比較の実行
		StringCompare compeare = new StringCompare();
		compeare.compareAll(lists.get(0), lists.get(1),nonCompareList);	
	}
	
	private List<String[]> getCompareLog(String[] args) throws Exception {
		String[] params = new String[2];
		params[0] = args[1];
		params[1] = args[2];
		
		// 管理テーブルより比較ログ情報を取得
		String sql = RESOURCE.getSql("GetLogInfo");
		String where =  "where ID in( ? , ? )";
		List<Map<String, Object>> result = RESOURCE.selectDB(
				MessageFormat.format(sql, where),params);
		List<String[]> logInfoList = new ArrayList<String[]>();
		String masterId = "";
		for (Map<String, Object> data : result) {
			String[] logInfo = new String[2];
			logInfo[0] = data.get("LOG_CD").toString();
			logInfo[1] = data.get("LOG_DATA_FILE").toString();
			logInfoList.add(logInfo);
			masterId = masterId + logInfo[0];
		}
		
		//ログファイルが２つ取得できない場合
		if (masterId.length() < 2) {
			throw new ToolException(2);
		}
		
		// ログ比較の組み合わせチェック
		boolean err = true;
		for (String master_Id :LogReaderConstant.MASTER_ID_LIST) {
			if (master_Id.equals(masterId)) {
				err = false;
				break;
			}
		}
		// ログ比較の組み合わせが不正な場合
		if (err) throw new ToolException(3);
		
		return logInfoList;
	}
}
