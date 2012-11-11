package com.jbcc.MQTool.commands;

import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jbcc.MQTool.compare.ComparableLog;
import com.jbcc.MQTool.compare.StringCompare;
import com.jbcc.MQTool.constant.LogReaderConstant;
import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.controller.ToolException;

public class CompareData extends ToolCommand {

	/**
	 * ログファイルの比較
	 * @param args　
	 *（ID指定　　　　　 ：第一引数：実行クラス名(CompareData)、第二引数：KANRI.ID、第三引数：KANRI.ID）
	 *（ログファイル名指定：第一引数：実行クラス名(CompareData)、第二引数：マスターID、第三引数：ファイル名１、第三引数：ファイル名２）
	 */
	public void execute(String[] args) throws Exception {

		List<LogInfo> logInfoList = null;
		if ( args.length == 3) {
			// ログ情報をDBから取得
			logInfoList = this.getCompareLog(args);
		} else {
			// ログ情報をファイル名から取得
			logInfoList = this.getCompareLogFileInfo(args);
		}
		

		CompareConfigMap configMap = new CompareConfigMap();

		LogInfo loginfo = logInfoList.get(0);
		String masterId = "";
		List<List<String>> lists = new ArrayList<List<String>>();
		ComparableLog[] comparableLogs = new ComparableLog[2];
		for (int i = 0; i < 2; i++) {
			LogInfo logInfo = logInfoList.get(i);
			masterId = masterId + logInfo.getLogCd();

			// ファイル名を分割
			String[] fileKey = logInfo.getLogDataFile()
					.substring(0, logInfo.getLogDataFile().length() - 4)
					.split("_");

			try {
				if (!configMap.getMap().containsKey(logInfo.getLogCd()))
					throw new ToolException(4);

				CompareConfig config = configMap.getMap().get(
						logInfo.getLogCd());
				comparableLogs[i] = getComparableLog(config.getCompareClass());
				lists.add(comparableLogs[i].getCompareLog(config.getPath(),
						fileKey, logInfo.getLogDataFile()));

			} catch (FileNotFoundException e) {
				// ファイルが存在しない場合
				e.printStackTrace();
				throw new ToolException(4);
			}
		}

		// 比較除外項目の取得
		String sql = RESOURCE.getSql("GetNonCompare");
		String where = "where MASTER_ID = ? and KEY = ? ";
		List<String> params = new ArrayList<String>();
		params.add(masterId);
		if (!loginfo.getDenbunCd().equals("")) {
			params.add(loginfo.getDenbunCd());
		}
		if (!loginfo.getLogTableName().equals("")) {
			params.add(loginfo.getLogTableName());
		}
		if (!loginfo.getCompareUpDw().equals("")) {
			where = where + " and UP_DOWN_CD = ?";
			params.add(loginfo.getCompareUpDw());
		}
		List<Map<String, Object>> result = RESOURCE.selectDB(
				MessageFormat.format(sql, where), params.toArray());
		ArrayList<Integer> nonCompareList = new ArrayList<Integer>();
		for (Map<String, Object> data : result) {
			nonCompareList.add(Integer
					.parseInt(data.get("ITEM_SEQ").toString()));
		}

		// 比較の実行
		StringCompare compare = new StringCompare();

		compare.setFieldInfo(comparableLogs);// 項目情報セット
		compare.compareAll(lists.get(0), lists.get(1), nonCompareList);

	}

	/**
	 * Compare実行クラスの取得.
	 *
	 * @param name
	 *            クラス名
	 * @return Compareクラス
	 * @throws Exception
	 */
	private ComparableLog getComparableLog(String name) throws Exception {
		try {
			Class<?> clazz = Class.forName("com.jbcc.MQTool.compare." + name);
			return (ComparableLog) clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("各ログ取得クラスの取得に失敗しました。", e);
		}
	}

	/**
	 *DBからログ情報を取得
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private List<LogInfo> getCompareLog(String[] args) throws Exception {
		String[] params = new String[2];
		params[0] = args[1];
		params[1] = args[2];

		// 管理テーブルより比較ログ情報を取得
		String sql = RESOURCE.getSql("GetLogInfo");
		String where = "where ID in( ? , ? )";
		List<Map<String, Object>> result = RESOURCE.selectDB(
				MessageFormat.format(sql, where), params);
		List<LogInfo> logInfoList = new ArrayList<LogInfo>();
		String masterId = "";
		for (Map<String, Object> data : result) {
			LogInfo loginfo = new LogInfo();
			loginfo.setLogCd(this.getString(data.get("LOG_CD")));
			loginfo.setLogDataFile(this.getString(data.get("LOG_DATA_FILE")));
			loginfo.setCompareUpDw(this.getString(data.get("UP_DOWN_CD")));
			loginfo.setDenbunCd(this.getString(data.get("DENBUN_CD")));
			loginfo.setLogTableName(this.getString(data.get("LOG_TABLE_NAME")));
			logInfoList.add(loginfo);
			masterId = masterId + loginfo.getLogCd();
		}

		// ログファイルが２つ取得できない場合
		if (masterId.length() < 2) {
			throw new ToolException(2);
		}
		
		// マスターIDチェック
		this.checkMasterId(masterId);

		return logInfoList;
	}
	
	/**
	 * ログファイル名からログ情報を取得	
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private List<LogInfo> getCompareLogFileInfo(String[] args) throws Exception {
		
		String master_Id = args[1];
		String fileName[] = new String[2];
		fileName[0] = args[2];
		fileName[1] = args[3];
		
		// マスターIDチェック
		this.checkMasterId(master_Id);
		
		// マスターIDを分割
		String id[] = new String[2];
		if (master_Id.length() == 2) {
			id[0] = master_Id.substring(0, 1);
			id[1] = master_Id.substring(1, 2);
		} else if (master_Id.indexOf("0") == 2) {
			id[0] = master_Id.substring(0, 1);
			id[1] = master_Id.substring(1, 3);
		} else {
			id[0] = master_Id.substring(0, 2);
			id[1] = master_Id.substring(2, 3);
		}
		
		// ファイル名より情報を取得
		List<LogInfo> logInfoList = new ArrayList<LogInfo>();
		for (int i = 0; i < 2; i++) {
			
			LogInfo loginfo = new LogInfo();
			// 拡張子を除外し_区切りで分割
			String fileKeys[] = (fileName[i].substring(0, fileName[i].length()-4)).split("_");
			// ファイル名より『KANRI.LOG_CD』と同等の値を取得
			loginfo.setLogCd(this.getString(id[i]));
			// ファイル名より『KANRI.LOG_DATA_FILE』と同等の値を取得
			loginfo.setLogDataFile(this.getString(fileName[i]));
			
			if (id[i].equals("7")) {
				String tablName = "";
				// Traceログ ファイル名より『KANRI.LOG_TABLE_NAME』と同等の値を取得
				for (int n = 2; n < fileKeys.length; n++) {
					if (!tablName.equals("")) tablName = tablName + "_";
					tablName = tablName + this.getString(fileKeys[n]);
				}
				loginfo.setLogTableName(tablName);
			} else if (id[i].equals("8")) {
				// DBIOログ ファイル名より『KANRI.LOG_TABLE_NAME』と同等の値を取得
				loginfo.setLogTableName(this.getString(fileKeys[3]));
			} else {
				// ファイル名より『KANRI.DENBUN_CD』と同等の値を取得
				loginfo.setDenbunCd(this.getString(fileKeys[3]));
				if (!id[i].equals("9") && !id[i].equals("10")) {
					// ファイル名より『KANRI.UP_DOWN_CD』と同等の値を取得
					loginfo.setCompareUpDw(this.getString(fileKeys[4]));
				}
			}
			logInfoList.add(loginfo);
		}
		return logInfoList;
	}
	/**
	 * マスターIDの組み合わせをチェック
	 * @param masterId
	 * @throws Exception
	 */
	private void checkMasterId(String masterId) throws Exception {
		// ログ比較の組み合わせチェック
		boolean err = true;
		for (String master_Id : LogReaderConstant.MASTER_ID_LIST) {
			if (master_Id.equals(masterId)) {
				err = false;
				break;
			}
		}
		// ログ比較の組み合わせが不正な場合
		if (err)
			throw new ToolException(3);
	}

	private String getString(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	/**
	 * ログ情報格納オブジェクト
	 *
	 */
	private class LogInfo {
		public String getLogCd() {
			return logCd;
		}

		public void setLogCd(String logCd) {
			this.logCd = logCd;
		}

		public String getLogDataFile() {
			return logDataFile;
		}

		public void setLogDataFile(String logDataFile) {
			this.logDataFile = logDataFile;
		}

		public String getDenbunCd() {
			return denbunCd;
		}

		public void setDenbunCd(String denbunCd) {
			this.denbunCd = denbunCd;
		}

		public String getLogTableName() {
			return logTableName;
		}

		public void setLogTableName(String logTableName) {
			this.logTableName = logTableName;
		}

		public String getCompareUpDw() {
			return compareUpDw;
		}

		public void setCompareUpDw(String compareUpDw) {
			this.compareUpDw = compareUpDw;
		}

		private String logCd = "";
		private String logDataFile = "";
		private String denbunCd = "";
		private String logTableName = "";
		private String compareUpDw = "";

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
				map = new HashMap<String, CompareConfig>();

			// オブジェクト情報の初期設定
			map.put(LogReaderConstant.MASTER_ID_CLIENT,
					initializeConfig("GetVbCompareLog",
							LogReaderConstant.CLIENT_PATH));
			map.put(LogReaderConstant.MASTER_ID_WEB,
					initializeConfig("GetWebServerCompareLog",
							LogReaderConstant.WEB_PATH));
			map.put(LogReaderConstant.MASTER_ID_OTX_CSS,
					initializeConfig("GetWebOTXAndAPCompareLog",
							LogReaderConstant.OTX_CSS_PATH));
			map.put(LogReaderConstant.MASTER_IDO_TX_ONL,
					initializeConfig("GetWebOTXAndAPCompareLog",
							LogReaderConstant.OTX_ONL_PATH));
			map.put(LogReaderConstant.MASTER_IDA_PINFO,
					initializeConfig("GetWebOTXAndAPCompareLog",
							LogReaderConstant.APINFO_PATH));
			map.put(LogReaderConstant.MASTER_ID_APHOST,
					initializeConfig("GetWebOTXAndAPCompareLog",
							LogReaderConstant.APHOST_PATH));
			map.put(LogReaderConstant.MASTER_ID_OTX_SQL,
					initializeConfig("GetSqlCcompareLog",
							LogReaderConstant.OTX_SQL_PATH));
			map.put(LogReaderConstant.MASTER_ID_AP_SQL,
					initializeConfig("GetSqlCcompareLog",
							LogReaderConstant.AP_SQL_PATH));

			map.put(LogReaderConstant.MASTER_ID_TRACE,
					initializeConfig("GetTraceCompareLog",
							LogReaderConstant.TRACE_PATH));
			map.put(LogReaderConstant.MASTER_ID_DBIO,
					initializeConfig("GetDbioCompareLog",
							LogReaderConstant.DBIO_PATH));
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