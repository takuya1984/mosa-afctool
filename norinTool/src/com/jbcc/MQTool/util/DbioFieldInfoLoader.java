package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.controller.ToolException;

public class DbioFieldInfoLoader {

	// DDL情報のベースディレクトリを取得
	protected static String TABLE_INFO_PATH = PropertyLoader.getDirProp()
			.getProperty("basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("ddl");

	private static FieldInfo GLOBAL = new FieldInfo(
			"GLOBAL SKIP(8)スキップ項目GLOBAL");
	private static FieldInfo MAINT = new FieldInfo("MAINT SKIP(1)スキップ項目MAINT");

	/**
	 * ファイルからフィールド定義情報を読み込む
	 *
	 * @param tableInfoFile
	 *            SQLファイルパス
	 * @return フィールド情報リスト
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 * @throws ToolException
	 */
	public static List<FieldInfo> getFieldInfo(File f) throws IOException {
		return getFieldInfo(f, false);
	}

	public static List<FieldInfo> getFieldInfo(String s, boolean isDbioLog)
			throws IOException {
		return getFieldInfo(new File(s), isDbioLog);
	}

	private static List<FieldInfo> getFieldInfo(File f, boolean isDbioLog)
			throws IOException {
		ArrayList<FieldInfo> al = new ArrayList<FieldInfo>();
		HashMap<String, FieldInfo> hm = new HashMap<String, FieldInfo>();

		String tableInfoFile = f.getName();
		/**
		 * テーブル構造ファイル：KANJOU.XXXXX.sql<br>
		 * トレースファイル：hhMMss.SSS_YYYYY_XXXXX.log
		 */
		String xxxxx = tableInfoFile.substring(tableInfoFile.length() - 9,
				tableInfoFile.length() - 4);
		String filePath = TABLE_INFO_PATH + "/KANJOU." + xxxxx + ".sql";

		if (!new File(filePath).exists()) {
			StdOut.write("can't get field info:" + filePath);
			return null;
		}

		LineReader lr = new LineReader(filePath, "Shift_JIS");
		String buff = null;
		FieldInfo fi = null;
		while ((buff = lr.readLine()) != null) {
			if (buff.indexOf("CHAR") >= 0 || buff.indexOf("NUMBER") >= 0) {
				// FIELD定義項目ならFieldInfoをリストに追加
				fi = new FieldInfo(buff);
				al.add(fi);
				hm.put(fi.getFieldName(), fi);
			} else if (buff.indexOf("COMMENT ON COLUMN") >= 0) {
				// FIELDの日本語コメントを付与
				buff = buff.replaceAll("^.*" + xxxxx + "\\.", "");
				String[] work = buff.split(" IS ");
				fi = hm.get(work[0]);
				fi.setFieldNameJ(work[1].replaceAll("[';]", ""));

			} else if (buff.indexOf("PRIMARY KEY") >= 0) {
				// プライマリキーの設定
				String[] keys = buff.replaceAll("^.*\\(| |\\)", "").split(",");
				for (int i = 0; i < keys.length; i++) {
					hm.get(keys[i]).setPrimary(true);
				}

			}
		}
		lr.close();

		// プライマリキーを除いてGLOBAL,MAINTをadd
		if (!isDbioLog) {
			for (int i = 0; i < al.size(); i++) {
				fi = al.get(i);
				if (fi.isPrimary()) {
					continue;
				}

				// GLOBAL,MAINTを入れていいか？
				if (!al.contains(GLOBAL) && fi.compareTo(GLOBAL) >= 0) {
					al.add(i, GLOBAL);
				}
				if (!al.contains(MAINT) && fi.compareTo(MAINT) >= 0) {
					al.add(i, MAINT);
				}
			}
			// 最後までGLOBAL,MAINTが挿入されていなかったら？
			if (!al.contains(GLOBAL)) {
				al.add(GLOBAL);
			}
			if (!al.contains(MAINT)) {
				al.add(MAINT);
			}
		}
		// 開始オフセットのセット
		int prev = 0;// 1つ前のオフセット
		for (int i = 0; i < al.size(); i++) {
			if (i > 0) {
				al.get(i).setOffset(prev);
			}
			if (isDbioLog) {
				// 新ログならサイズがそのままバイト数
				prev += al.get(i).getSize();
			} else {
				// 旧trace log なら８進数３つで１バイト
				prev += al.get(i).getByteSize() * 3;
			}
		}

		return al;
	}

	public static List<FieldInfo> getFieldInfoNew(File f) throws IOException {
		return getFieldInfo(f, true);
	}

}
