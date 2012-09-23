package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class TraceLogReader extends LineReader {

	private static String TRACE_BASE = "/Users/MOSA/Dropbox/【農林】統合テスト支援ツール/script/log/07_trace/";
	private File file = null;

	private List<FieldInfo> fields = null;

	private StringBuilder sb = new StringBuilder();
	private int[] offset = null;

	public static void main(String[] args) {
		try {
			File path = new File(TRACE_BASE);

			String[] fNames = { "130528.402_ICC09_OKOS0.log",
					"130529.421_ICC09_OJKF0.log" };

			for (File f : path.listFiles()) {
				System.out.println(f.getName());
				TraceLogReader tlr = new TraceLogReader(f);
				if (tlr.fields == null) {
					continue;
				}

				for (int i = 0; i < tlr.fields.size(); i++) {
					System.out.print(tlr.fields.get(i).toString());
					System.out.println("'" + tlr.read(i) + "'");
				}
				tlr.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TraceLogReader(String s) throws IOException {
		super(s);
		file = new File(s);
		init();
	}

	public TraceLogReader(File f) throws IOException {
		super(f);
		file = f;
		init();
	}

	private void init() throws IOException {
		// traceログのフィールド情報を取得
		fields = FieldInfo.getFieldInfo(file);
		if (fields == null) {
			System.out.println("can't get field info:" + file.getName());
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
		} else if (f.getFieldName().equals("MAINT")) {
			byte[] buff = Oct2String.record2bytes(ret);
			ret = Oct2String.valueOf(buff);
		}
		return ret;
	}

}
