package com.jbcc.MQTool.compare;

import java.io.IOException;
import java.util.List;

import com.jbcc.MQTool.util.FieldInfo;

public abstract class ComparableLog {

	/**
	 * コンペア用文字列リストの取得
	 * @param LOG_PATH ログベースディレクトリ
	 * @param fileKey ファイルのキー情報
	 * @param fileName ファイル名
	 * @return コンペア用文字列リスト
	 * @throws IOException
	 */
	public abstract List<String> getCompareLog(String LOG_PATH,
			String[] fileKey, String fileName) throws IOException;

	protected List<FieldInfo> fieldInfo = null;

	/**
	 * フィールド情報を取得
	 *
	 * @return フィールド情報
	 */
	public List<FieldInfo> getFieldInfo() {
		return fieldInfo;
	}
}
