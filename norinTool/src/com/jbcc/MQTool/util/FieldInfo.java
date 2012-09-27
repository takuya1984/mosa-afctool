package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.controller.ToolException;

public class FieldInfo {
	// リリース時には変更が必要
	private static String TABLE_INFO_PATH = PropertyLoader.getDirProp()
			.getProperty("basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("ddl");

	// properties
	private String fieldName = null;
	private String type = null;
	private String fieldNameJ = null;
	private int size = 0;
	private int offset = 0;
	private boolean isPrimary = false;

	// const
	private final static String NUMBER = "NUMBER";
	private final static String SKIP = "SKIP";

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

	private static List<FieldInfo> getFieldInfo(File f, boolean isNew)
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
			System.out.println("can't get field info:" + filePath);
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
		for (int i = 0; i < al.size(); i++) {
			fi = al.get(i);
			if (fi.isPrimary) {
				continue;
			}

			// GLOBAL,MAINTを入れていいか？
			if (!isNew && !al.contains(GLOBAL) && fi.compareTo(GLOBAL) >= 0) {
				al.add(i, GLOBAL);
			}
			if (!isNew && !al.contains(MAINT) && fi.compareTo(MAINT) >= 0) {
				al.add(i, MAINT);
			}
		}
		// 最後までGLOBAL,MAINTが挿入されていなかったら？
		if (!isNew && !al.contains(GLOBAL)) {
			al.add(GLOBAL);
		}
		if (!isNew && !al.contains(MAINT)) {
			al.add(MAINT);
		}

		// 開始オフセットのセット
		int prev = 0;// 1つ前のオフセット
		for (int i = 0; i < al.size(); i++) {
			if (i > 0) {
				al.get(i).setOffset(prev);
			}
			if (isNew) {
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
			i = getSize();
			if (i <= 5) {
				i = 2;
			} else if (i <= 10) {
				i = 4;
			} else {
				i = 8;
			}
		} else {
			i = getSize();
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

	public int getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = Integer.valueOf(size.split(",")[0]);
	}

	public String getFieldNameJ() {
		return fieldNameJ;
	}

	public void setFieldNameJ(String fieldNameJ) {
		this.fieldNameJ = fieldNameJ;
	}

	public String toString() {
		return getFieldName() + ":" + getType() + "(" + getSize() + ") offset:"
				+ getOffset() + " PK:" + isPrimary() + " / " + getFieldNameJ();
	}

	private void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	private boolean isPrimary() {
		return isPrimary;
	}

	public boolean isSkip() {
		return getType().equals(SKIP);
	}
}
