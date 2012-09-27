package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;

public class DbioLogReader extends LineReader {

	private static final String SJIS = "Shift-JIS";
	private static String DBIO_BASE = PropertyLoader.getDirProp().getProperty(
			"basedir")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("logbase")
			+ File.separator
			+ PropertyLoader.getDirProp().getProperty("08_dbio");

	private List<FieldInfo> fields = null;

	private byte[] record = null;
	private int[] offset = null;

	private final int OFFSET = 87;// 固定オフセット(無視する部分)

	public static void main(String[] args) {
		try {
			File path = new File(DBIO_BASE);
			StdOut.write(path.toString());
			for (File f : path.listFiles()) {
				new DbioLogReader(f).getList();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getList() throws UnsupportedEncodingException,
			IOException {
		ArrayList<String> al = new ArrayList<String>();
		if (fields == null) {
			return al;
		}
		// ここからDbioLog読み込みを実装
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).isSkip()) {
				continue;
			}
			al.add(read(i));
			StdOut.write(fields.get(i).toString() + ":'" + al.get(i) + "'");
		}
		close();
		return al;
	}

	public DbioLogReader(String s) throws IOException {
		super(s);
		init(new File(s));
	}

	public DbioLogReader(File f) throws IOException {
		super(f);
		init(f);
	}

	private void init(File file) throws IOException {
		// traceログのフィールド情報を取得
		StdOut.write(file.getPath());
		fields = FieldInfo.getFieldInfoNew(file);
		if (fields == null) {
			return;
		}
		offset = new int[fields.size()];
		offset[0] = 0;

		// データのバッファリング
		LineReader lr = new LineReader(file);
		record = lr.readLine().getBytes(SJIS);
		lr.close();
		StdOut.write(record.length + ":" + new String(record, SJIS));

		slideOffset(0, OFFSET);

	}

	private void slideOffset(int idx, int len) {
		int j = 0;
		for (int i = idx; i < fields.size(); i++) {
			j = fields.get(i).getOffset();
			fields.get(i).setOffset(j + len);
		}
	}

	public String read(int i) throws UnsupportedEncodingException {
		FieldInfo f = fields.get(i);
		int size = f.getSize();
		String s = null;
		if (f.getType().equals("NUMBER")) {
			s = new String(record, f.getOffset(), 1);
			if (s.equals("+") || s.equals("-")) {
				slideOffset(i + 1, 1);
				size++;
			}
			s = new String(record, f.getOffset(), size, SJIS);
			s = s.replaceAll("^\\+0{0,}", "");
			s = s.replaceAll("^\\-0{0,}", "-");
			if (s.equals("")) {
				s = "0";
			}
		} else {
			s = new String(record, f.getOffset(), size, SJIS);
		}
		return s;
	}

}
