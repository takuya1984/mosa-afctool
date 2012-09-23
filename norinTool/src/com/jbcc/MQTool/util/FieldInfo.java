package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldInfo {
	// properties
	private String fieldName = null;
	private String type = null;
	private String fieldNameJ = null;
	private String size = null;
	private int offset = 0;
	private boolean isPrimary = false;

	// const
	private final static String NUMBER = "NUMBER";
	private static FieldInfo GLOBAL = new FieldInfo(
			"GLOBAL SKIP(8)スキップ項目GLOBAL");
	private static FieldInfo MAINT = new FieldInfo("MAINT SKIP(1)スキップ項目MAINT");

	// リリース時には変更が必要
	private static String TABLE_INFO_PATH = "/Users/MOSA/Dropbox/【農林】統合テスト支援ツール/LOG/資料20120911/TABLE/";

	// private static String TRACE_LOG_BASE =
	// "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\script\\log\\07_trace\\";

	/**
	 * ファイルからフィールド定義情報を読み込む
	 *
	 * @param tableInfoFile
	 *            SQLファイルパス
	 * @return フィールド情報リスト
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 */
	public static List<FieldInfo> getFieldInfo(File f) throws IOException {

		ArrayList<FieldInfo> al = new ArrayList<FieldInfo>();
		HashMap<String, FieldInfo> hm = new HashMap<String, FieldInfo>();

		String tableInfoFile = f.getName();
		/**
		 * テーブル構造ファイル：KANJOU.XXXXX.sql<br>
		 * トレースファイル：hhMMss.SSS_YYYYY_XXXXX.log
		 */
		String xxxxx = tableInfoFile.substring(tableInfoFile.length() - 9,
				tableInfoFile.length() - 4);
		String filePath = TABLE_INFO_PATH + "KANJOU." + xxxxx + ".sql";

		if (!new File(filePath).exists()) {
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

		// プライマリキーを除いてGLOBAL,MAINTをadd
		for (int i = 0; i < al.size(); i++) {
			fi = al.get(i);
			if (fi.isPrimary) {
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

		// 開始オフセットのセット
		int prev = 0;// 1つ前のオフセット
		for (int i = 0; i < al.size(); i++) {
			if (i > 0) {
				al.get(i).setOffset(prev);
			}
			prev += al.get(i).getByteSize() * 3;
		}

		return al;
	}

	/**
	 * フィールド名称比較<br/>
	 * 内部でしか意味を持たないためcomparableを継承せず、publicにはしない。
	 *
	 * @param fi2
	 *            比較対象
	 * @return FiledName同士のcompare結果
	 */
	private int compareTo(FieldInfo fi2) {
		return this.getFieldName().compareTo(fi2.getFieldName());
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * コンストラクタ
	 *
	 * @param fieldInfo
	 *            フィールド情報
	 */
	public FieldInfo(String fieldInfo) {
		int i = 0;
		String[] values = fieldInfo.trim().replaceAll("[ \\(\\)]", "\t")
				.split("\t");

		setFieldName(values[i++]);
		setType(values[i++]);
		setSize(values[i++]);
		if (i < values.length) {
			setFieldNameJ(values[i++]);
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public int getByteSize() {
		int i = 0;
		if (NUMBER.equals(type)) {
			i = Integer.valueOf(getSize());
			if (i <=5) {
				i = 2;
			} else if (i <=10) {
				i = 4;
			} else {
				i = 8;
			}
		} else {
			i = Integer.parseInt(getSize());
		}
		return i;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size.split(",")[0];
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFieldNameJ() {
		return fieldNameJ;
	}

	public void setFieldNameJ(String fieldNameJ) {
		this.fieldNameJ = fieldNameJ;
	}

	public String toString() {
		return getFieldName() + ":" + getType() + "(" + getSize() + ") offset:"
				+ getOffset() + " " + isPrimary() + " / " + getFieldNameJ();
	}

	private void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	private boolean isPrimary() {
		return isPrimary;
	}

}
