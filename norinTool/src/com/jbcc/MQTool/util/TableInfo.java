package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableInfo {
	private static String TABLE_INFO_PATH = "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\LOG\\資料20120911\\TABLE\\";
	private static String TRACE_LOG_BASE = "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\script\\log\\07_trace\\";

	public static void main(String[] args) throws IOException {
		for (File f : new File(TRACE_LOG_BASE).listFiles()) {
			List<FieldInfo> listFi = getFieldInfo(f.getName());
			System.out.println(f.getName());

			if (listFi != null) {
				for (FieldInfo fi : listFi) {
					System.out.println(fi);
				}
			}
		}
	}

	/**
	 * ファイルからフィールド定義情報を読み込む
	 *
	 * @param tableInfoFile
	 *            SQLファイルパス
	 * @return フィールド情報リスト
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 */
	public static List<FieldInfo> getFieldInfo(String tableInfoFile)
			throws IOException {
		ArrayList<FieldInfo> al = new ArrayList<FieldInfo>();

		/**
		 * テーブル構造ファイル：KANJOU.XXXXX.sql<br>
		 * トレースファイル：hhMMss.SSS_YYYYY_XXXXX.log
		 */
		String xxxxx = tableInfoFile.substring(tableInfoFile.length() - 9,
				tableInfoFile.length() - 4);
		String filePath = TABLE_INFO_PATH + "KANJOU." + xxxxx + ".sql";

		if (!new File(filePath).exists()) {
			System.out.println("NOT FOUND:" + filePath);
			return null;
		}
		LineReader lr = new LineReader(filePath);
		String buff = null;
		while ((buff = lr.readLine()) != null) {
			if (buff.indexOf("CHAR") >= 0 || buff.indexOf("NUMBER") >= 0) {
				// FIELD定義項目ならFieldInfoをリストに追加
				al.add(new FieldInfo(buff));
			} else if (buff.indexOf("CONSTRAINT") >= 0
					|| buff.indexOf("USING") >= 0) {
				// 定義情報が終わったら抜ける
				break;
			}
		}

		return al;
	}

}
