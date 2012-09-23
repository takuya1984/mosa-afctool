package com.jbcc.MQTool.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LineReader extends BufferedReader {

	public static void main(String[] args) {
		// test
		try {
			String s = "/Users/Shared/Dropbox/Dropbox/【農林】統合テスト支援ツール/LOG/01_client/2012-04-09_135749_00000_2_2301010_2.dat";
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
	 * @throws UnsupportedEncodingException
	 */
	public LineReader(String s) throws FileNotFoundException,
			UnsupportedEncodingException {
		super(new InputStreamReader(new FileInputStream(s), "UTF-8"));
	}

	/**
	 * デフォルトサイズのバッファーでバッファリングされた、文字型入力ストリームを作成します。
	 *
	 * @param f
	 *            対象ファイル
	 * @throws FileNotFoundException
	 *             ファイルが見つからなかった場合
	 * @throws UnsupportedEncodingException
	 */
	public LineReader(File f) throws FileNotFoundException,
			UnsupportedEncodingException {
		super(new InputStreamReader(new FileInputStream(f), "UTF-8"));
	}

	public LineReader(File f, String charSet) throws FileNotFoundException,
			UnsupportedEncodingException {
		super(new InputStreamReader(new FileInputStream(f), charSet));
	}

	public LineReader(String s, String charSet) throws FileNotFoundException,
			UnsupportedEncodingException {
		super(new InputStreamReader(new FileInputStream(s), charSet));
	}

	/**
	 *
	 */
	public String readLine() throws IOException {
		String ret = super.readLine();

		// EOFならクローズ
		if (ret == null) {
			close();
		}
		return ret;
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
			if (ret.toUpperCase().indexOf(key.toUpperCase()) >= 0) {
				break;
			}
		}

		return ret;
	}

	/**
	 *
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public List<String> readLinesByKey(String key) throws IOException {
		String ret = null;
		List<String> rets = new ArrayList<String>();
		while ((ret = readLineByKey(key)) != null) {
			rets.add(ret);
		}
		return rets;
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

			// 大文字、小文字を判断しない
			if (work.toUpperCase().indexOf(key.toUpperCase()) >= 0) {
				break;
			}
		}

		return ret;
	}

	/**
	 *
	 * @param key
	 * @param columnIndex
	 * @return
	 * @throws IOException
	 */
	public List<String> readLinesByKey(String key, int columnIndex)
			throws IOException {
		String ret = null;
		List<String> rets = new ArrayList<String>();
		while ((ret = readLineByKey(key, columnIndex)) != null) {
			rets.add(ret);
		}
		return rets;
	}
}
