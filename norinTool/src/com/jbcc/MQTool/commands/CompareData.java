package com.jbcc.MQTool.commands;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.jbcc.MQTool.compare.Compare;
import com.jbcc.MQTool.compare.StringCompare;
import com.jbcc.MQTool.constant.LogReaderConstant;
import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.controller.ToolException;

public class CompareData extends ToolCommand {
	
	public void execute (String[] args) throws Exception {
		
		// ログ情報を取得
		List<String[]> logInfoList = this.getCompareLog(args);
		
		CompareConfigMap configMap = new CompareConfigMap();
		
		LogInfo loginfo = new LogInfo();
		List<List<String>> lists = new ArrayList<List<String>>();
		for (int i = 0;i < 2; i++) {
			String[] logInfo = logInfoList.get(i);
			
			// ファイル名を分割
			String[] fileKye = logInfo[1].substring(0, logInfo[1].length()-4).split("_");
			loginfo.setNonCompareKye(fileKye[4]);
			loginfo.setNonCompareUpDw(fileKye[5]);
			try {
				if (!configMap.getMap().containsKey(logInfo[0]))
					throw new ToolException(4);
				
				CompareConfig config = configMap.getMap().get(logInfo[0]);
				Compare compare = getCompre(config.getCompareClass());
				lists.add(compare.getCompareLog(config.getPath(), fileKye, logInfo[1]));
				
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
		params.add(loginfo.getNonCompareKye());
		if (!loginfo.getNonCompareUpDw().equals("")) {
			where = where + " and UP_DOWN_CD = ?";
			params.add(loginfo.getNonCompareUpDw());
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
	
	/**
	 * Compare実行クラスの取得.
	 * 
	 * @param name クラス名
	 * @return Compareクラス
	 * @throws Exception
	 */
	private Compare getCompre(String name) throws Exception {
		try {
			Class<?> clazz = Class.forName("com.jbcc.MQTool.compare." + name);
			return (Compare) clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("各ログ取得クラスの取得に失敗しました。", e);
		}
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
	
	/**
	 * ログ情報格納オブジェクト
	 *
	 */
	private class LogInfo {
		public String getNonCompareKye() {
			return nonCompareKye;
		}
		public void setNonCompareKye(String nonCompareKye) {
			this.nonCompareKye = nonCompareKye;
		}
		public String getNonCompareUpDw() {
			return nonCompareUpDw;
		}
		public void setNonCompareUpDw(String nonCompareUpDw) {
			this.nonCompareUpDw = nonCompareUpDw;
		}
		private String nonCompareKye = "";
		private String nonCompareUpDw = "";

	}
	
	/**
	 * コンフィグ情報格納Map
	 *
	 */
	private class CompareConfigMap {
		private Map<String, CompareConfig> map;
		public Map<String, CompareConfig> getMap() {
			return map;
		}
		public CompareConfigMap() {
			if (map == null)
				map = new HashMap<String,CompareConfig>();
			
			// オブジェクト情報の初期設定
			map.put(LogReaderConstant.MASTER_ID_CLIENT, initializeConfig("GetVbCompareLog", LogReaderConstant.CLIENT_PATH));
			map.put(LogReaderConstant.MASTER_ID_WEB, initializeConfig("GetWebServerCompareLog", LogReaderConstant.WEB_PATH));
			map.put(LogReaderConstant.MASTER_ID_OTX_CSS, initializeConfig("GetWebOTXAndAPCompareLog", LogReaderConstant.OTX_CSS_PATH));
			map.put(LogReaderConstant.MASTER_IDO_TX_ONL, initializeConfig("GetWebOTXAndAPCompareLog", LogReaderConstant.OTX_ONL_PATH));
			map.put(LogReaderConstant.MASTER_IDA_PINFO, initializeConfig("GetWebOTXAndAPCompareLog", LogReaderConstant.APINFO_PATH));
			map.put(LogReaderConstant.MASTER_ID_APHOST, initializeConfig("GetWebOTXAndAPCompareLog", LogReaderConstant.APHOST_PATH));
			map.put(LogReaderConstant.MASTER_ID_OTX_SQL, initializeConfig("GetSqlCcompareLog", LogReaderConstant.OTX_SQL_PATH));
			map.put(LogReaderConstant.MASTER_ID_AP_SQL, initializeConfig("GetSqlCcompareLog", LogReaderConstant.AP_SQL_PATH));
		}
		private CompareConfig initializeConfig(String name, String path) {
			CompareConfig config = new CompareConfig();
			config.compareClass = name;
			config.path = path;
			return config;
		}
	}
	
	/**
	 * コンフィグ情報
	 *
	 */
	public class CompareConfig {
		private String compareClass;
		public String getCompareClass() {
			return compareClass;
		}
		public void setCompareClass(String compareClass) {
			this.compareClass = compareClass;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		private String path;
	}
}