package com.jbcc.MQTool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LineReader {

	private BufferedReader br = null;

	/**
	 * デフォルトサイズのバッファーでバッファリングされた、文字型入力ストリームを作成します。
	 *
	 * @param s
	 *            対象ファイルパス
	 * @throws FileNotFoundException
	 *             ファイルが見つからなかった場合
	 */
	public LineReader(String s) throws FileNotFoundException {
		init(new File(s));
	}

	/**
	 * デフォルトサイズのバッファーでバッファリングされた、文字型入力ストリームを作成します。
	 *
	 * @param f
	 *            対象ファイル
	 * @throws FileNotFoundException
	 *             ファイルが見つからなかった場合
	 */
	public LineReader(File f) throws FileNotFoundException {
		init(f);
	}

	private void init(File f) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(f));
	}

	/**
	 * テキスト行を読み込みます。1 行の終端は、改行 ('\n') か、復帰 ('\r')、または復行とそれに続く改行のいずれかで認識されます。
	 *
	 * @return 行の内容を含む文字列、ただし行の終端文字は含めない。ストリームの終わりに達している場合は null
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 */
	public String readLine() throws IOException {
		return br.readLine();
	}

	/**
	 * keyが出現するまでテキスト行を読み込みます。1 行の終端は、改行 ('\n') か、復帰
	 * ('\r')、または復行とそれに続く改行のいずれかで認識されます。
	 *
	 * @param key
	 *            探索キー
	 * @return keyの出現する行の内容を含む文字列、ただし行の終端文字は含めない。ストリームの終わりに達している場合は null
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 */
	public String readLineByKey(String key) throws IOException {

		String ret = null;

		while ((ret = readLine()) != null) {
			if (ret.indexOf(key) >= 0) {
				break;
			}
		}

		return ret;
	}
}
