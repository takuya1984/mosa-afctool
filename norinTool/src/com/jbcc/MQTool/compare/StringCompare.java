package com.jbcc.MQTool.compare;

import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.jbcc.MQTool.util.FieldInfo;
import com.jbcc.MQTool.util.StdOut;

public class StringCompare {

	private int nonCompareNo = -1;
	private Iterator<Integer> it = null;
	private List<FieldInfo> fields = null;

	public StringCompare() {
	}

	public void setFieldInfo(List<FieldInfo> fields) {
		this.fields = fields;
	}

	/**
	 * ２つのList oとnの現在の要素を比較する。
	 *
	 * @return 比較結果
	 */
	private String compareNext(List<String> o, List<String> n, int i) {

		StringBuilder sb = null;

		String s1 = null;
		String s2 = null;
		if (i < o.size()) {
			s1 = o.get(i);
		}

		if (i < n.size()) {
			s2 = n.get(i);
		}
		if (i == -1 && it.hasNext()) {
			nonCompareNo = (Integer) it.next();
		}

		if (s1 == null && s2 == null) {
			return null;
		} else if (i == nonCompareNo) {
			sb = new StringBuilder("－");
			if (it.hasNext()) {
				nonCompareNo = (Integer) it.next();
			}
		} else if (s1 != null && s2 != null && s1.equals(s2)) {
			sb = new StringBuilder("○");
		} else {
			sb = new StringBuilder("×");
		}

		if (this.fields != null) {
			if (i < fields.size()) {
				sb.append("\t").append(fields.get(i).getFieldNameJ())
						.append("\t");
			} else {
				sb.append("\t").append("null").append("\t");
			}
		}

		sb.append("\t").append(s1).append("\t").append(s2);

		return sb.toString();
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
	 */
	public void compareAll(List<String> o, List<String> n,
			TreeSet<Integer> nonCompareList) {

		String s = null;
		int i = 0;// oとnのlengthが違うかもしれないのでカウンタ
		it = nonCompareList.iterator();

		while ((s = compareNext(o, n, i++)) != null) {
			StdOut.write(s);
		}
	}
}
