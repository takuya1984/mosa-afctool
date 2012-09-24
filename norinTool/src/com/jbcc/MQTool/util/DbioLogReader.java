package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;

public class DbioLogReader extends LineReader {

	private static String DBIO_BASE = PropertyLoader.getDirProp().getProperty(
			"08_dbio");

	private List<FieldInfo> fields = null;

	private StringBuilder sb = new StringBuilder();
	private int[] offset = null;

	private final int OFFSET = 87;// 固定オフセット(無視する部分)

	public static void main(String[] args) {
		try {
			File path = new File(DBIO_BASE);
			String buff = null;
			for (File f : path.listFiles()) {
				DbioLogReader dlr = new DbioLogReader(f);

				// TODO ここからDbioLog読み込みを実装

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		fields = FieldInfo.getFieldInfo(file);
		if (fields == null) {
			return;
		}
		offset = new int[fields.size()];
		offset[0] = 0;

		// データのバッファリング

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
		} else if (f.getFieldName().equals("MAINT")) {
			byte[] buff = Oct2String.record2bytes(ret);
			ret = Oct2String.valueOf(buff);
		}
		return ret;
	}

}
