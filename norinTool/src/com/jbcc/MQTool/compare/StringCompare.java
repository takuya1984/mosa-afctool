package com.jbcc.MQTool.compare;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.jbcc.MQTool.util.FieldInfo;
import com.jbcc.MQTool.util.StdOut;

public class StringCompare {

	private List<FieldInfo> fields = null;

	public StringCompare() {
	}

	public void setFieldInfo(ComparableLog[] comparableLogs) {
		for (ComparableLog cLog : comparableLogs) {
			if ((fields = cLog.getFieldInfo()) != null) {
				break;
			}
		}
	}

	/**
	 * ２つのList oとnの現在の要素を比較する。
	 *
	 * @param o
	 *            比較元(旧)
	 * @param n
	 *            比較元(新)
	 * @param i
	 *            比較オブジェクトの位置インデックス
	 * @param nonCompareList
	 *            除外リスト
	 * @return 比較結果のをタブ区切りにした文字列
	 * @throws UnsupportedEncodingException
	 */
	private String compareNext(List<String> o, List<String> n, int i,
			List<Integer> nonCompareList) throws UnsupportedEncodingException {

		StringBuilder sb = null;

		String s1 = null;
		String s2 = null;
		if (i < o.size()) {
			s1 = o.get(i);
		}

		if (i < n.size()) {
			s2 = n.get(i);
		}

		if (s1 == null && s2 == null) {
			return null;

		} else if (nonCompareList.contains(i)) {
			// 除外リストにあったら比較はしない
			sb = new StringBuilder("-");

		} else if (s1 != null && s2 != null && s1.equals(s2)) {
			sb = new StringBuilder("○");
		} else {
			sb = new StringBuilder("×");
		}

		// フィールド名称
		if (this.fields != null) {
			if (i < fields.size()) {
				sb.append("\t").append(fields.get(i).getFieldNameJ())
						.append("\t");
			} else {
				// フィールド名リストより項目が多い場合
				sb.append("\t").append("null").append("\t");
			}
		}

		// バイト数表示
		sb.append("\t").append(getByteInfo(s1));
		sb.append("\t").append(getByteInfo(s2));

		return sb.toString();
	}

	/**
	 * バイト数表示<br/>
	 * nullの場合は"(null) null"の固定文字列
	 *
	 * @param s
	 *            対象文字列
	 * @return バイト数表示を追加した文字列
	 * @throws UnsupportedEncodingException
	 */
	private String getByteInfo(String s) throws UnsupportedEncodingException {
		if (s == null) {
			return "(null) null";
		}
		return "(" + s.getBytes("Shift-JIS").length + ") " + s;
	}

	/**
	 * ２つのList oとnを全量比較し、結果を出力する。<br>
	 *
	 * @param o
	 *            比較元(旧)
	 * @param n
	 *            比較先(新)
	 * @param nonCompareList
	 *            比較除外項目リスト
	 * @throws UnsupportedEncodingException
	 */
	public void compareAll(List<String> o, List<String> n,
			List<Integer> nonCompareList) throws UnsupportedEncodingException {

		String s = null;
		int i = 0;// oとnのlengthが違うかもしれないのでカウンタ

		while ((s = compareNext(o, n, i++, nonCompareList)) != null) {
			StdOut.write(s);
		}
	}
}
