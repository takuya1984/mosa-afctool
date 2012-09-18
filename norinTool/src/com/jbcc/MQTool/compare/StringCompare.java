package com.jbcc.MQTool.compare;

import java.util.List;

import com.jbcc.MQTool.util.StdOut;

public class StringCompare {

	public StringCompare() {
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
		i++;

		if (s1 == null && s2 == null) {
			return null;
		} else if (s1 != null && s2 != null && s1.equals(s2)) {
			sb = new StringBuilder("○");
		} else {
			sb = new StringBuilder("×");
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
	 */
	public void compareAll(List<String> o, List<String> n) {

		String s = null;
		int i = 0;// oとnのlengthが違うかもしれないのでカウンタ

		while ((s = compareNext(o, n, i++)) != null) {
			StdOut.write(s);
		}
	}
}
