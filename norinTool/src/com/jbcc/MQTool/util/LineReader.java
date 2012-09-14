package com.jbcc.MQTool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class LineReader extends BufferedReader {

	public static void main(String[] args) {
		// test
		try {
			String s = "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\script\\log\\01_client\\2012-04-09_135749_00000_2_2301010_2.dat";
			LineReader lr = null;

			lr = new LineReader(s);

			while ((s = lr.readLineByKey("Err", 5)) != null) {
				StdOut.write(s);
			}

			lr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * デフォルトサイズのバッファーでバッファリングされた、文字型入力ストリームを作成します。
	 *
	 * @param s
	 *            対象ファイルパス
	 * @throws FileNotFoundException
	 *             ファイルが見つからなかった場合
	 */
	public LineReader(String s) throws FileNotFoundException {
		super(new FileReader(s));
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
		super(new FileReader(f));
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

		// EOFならクローズ
		if (ret == null) {
			close();
		}
		return ret;
	}

	/**
	 * 行をスペースで区切り、columnIndex番目の要素にkeyが出現した行を返します。
	 *
	 * @param key
	 *            検索キー
	 * @param columnIndex
	 *            行の列要素番号(1から)
	 * @return
	 *         行のcolumnIndex番目にkeyの出現する行の内容を含む文字列、ただし行の終端文字は含めない。ストリームの終わりに達している場合は
	 *         null
	 * @throws IOException
	 *             入出力エラーが発生した場合
	 */
	public String readLineByKey(String key, int columnIndex) throws IOException {

		String ret = null, work = null;
		StringTokenizer token = null;
		while ((ret = readLine()) != null) {
			token = new StringTokenizer(ret);
			if (token.countTokens() < columnIndex) {
				continue;
			}

			for (int i = 0; i < columnIndex; i++) {
				work = token.nextToken();
			}

			if (work.indexOf(key) >= 0) {
				break;
			}
		}

		// EOFならクローズ
		if (ret == null) {
			close();
		}

		return ret;
	}
}
