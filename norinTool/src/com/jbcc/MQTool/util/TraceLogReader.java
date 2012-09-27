package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;

public class TraceLogReader extends LineReader {


	private List<FieldInfo> fields = null;

	private StringBuilder sb = new StringBuilder();
	private int[] offset = null;

	public static void main(String[] args) {
		try {
			String TRACE_BASE = PropertyLoader.getDirProp().getProperty(
					"basedir")
					+ File.separator
					+ PropertyLoader.getDirProp().getProperty("logbase")
					+ File.separator
					+ PropertyLoader.getDirProp().getProperty("07_trace");
			File path = new File(TRACE_BASE);

			for (File f : path.listFiles()) {
				StdOut.write(f.getName());
				new TraceLogReader(f).getList();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getList() throws UnsupportedEncodingException,
			IOException {

		// TODO get list
		ArrayList<String> al = new ArrayList<String>();
		if (fields == null) {
			close();
			return al;
		}

		for (int i = 0; i < fields.size(); i++) {
			if (!fields.get(i).isSkip()) {
				System.out.print(fields.get(i).toString());
				StdOut.write("'" + read(i) + "'");
				al.add(read(i));
			}
		}
		close();
		return al;
	}

	public TraceLogReader(String s) throws IOException {
		super(s);
		init(new File(s));
	}

	public TraceLogReader(File f) throws IOException {
		super(f);
		init(f);
	}

	private void init(File file) throws IOException {
		// traceログのフィールド情報を取得
		fields = FieldInfo.getFieldInfo(file);
		if (fields == null) {
			return;
		}
		offset = new int[fields.size()];
		offset[0] = 0;

		// バイナリデータのバッファリング
		LineReader lr = new LineReader(file);
		String buff = null;
		while ((buff = lr.readLine()) != null) {
			sb.append(buff.replaceAll(" ", ""));
		}
		lr.close();
	}

	public String read(int i) throws UnsupportedEncodingException {
		String ret = null;
		FieldInfo f = fields.get(i);

		ret = sb.substring(f.getOffset(), f.getOffset() + f.getByteSize() * 3);
		if (f.getType().equals("CHAR")) {
			byte[] buff = Oct2String.record2bytes(ret);
			if (f.getFieldName().startsWith("X")) {
				ret = Oct2String.valueOf(buff, "Shift_JIS");
			} else {
				ret = Oct2String.valueOf(buff);
			}
		} else if (f.getType().equals("NUMBER")) {
			ret = String.valueOf(Long.parseLong(ret, 8));
		} else if (f.isSkip()) {
			// スキップ対象
		} else {
			StdOut.write("予想外の型:" + f.getType());
		}
		return ret;
	}

}
