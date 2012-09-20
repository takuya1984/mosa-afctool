package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FieldInfo {
	private String fieldName = null;
	private String type = null;
	private String size = null;
	private String fieldNameJ = null;
	private int byteSize = 0;

	private final static String CHAR = "CHAR";
	private final static String NUMBER = "NUMBER";

	private static String TABLE_INFO_PATH = "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\LOG\\資料20120911\\TABLE\\";

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

		String tableInfoFile = f.getName();
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
		System.out.println(filePath);
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

	public FieldInfo(String fieldInfo) {
		int i = 0;
		String[] values = fieldInfo.trim().replaceAll("[ \\(\\)]", "\t").split(
				"\t");
		// System.out.println(fieldInfo + ":" + values.length);
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
		if (CHAR.equals(type)) {
			i = Integer.valueOf(getSize());
			if (i <= 5) {
				i = 2;
			} else if (i <= 10) {
				i = 4;
			} else {
				i = 8;
			}
		} else {
			getSize();
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
		return getFieldName() + ":" + getType() + "(" + getSize() + ") / "
				+ getFieldNameJ();
	}

}
