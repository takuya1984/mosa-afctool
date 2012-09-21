package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TraceLogReader extends LineReader {

	// private String DDL_BASE =
	// "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\LOG\\資料20120911\\TABLE\\";

	private static String TRASE_BASE = "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\script\\log\\07_trace\\";
	// private static String WORK_FILE =
	// "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\script\\log\\07_trace_2_byte\\work.bin";
	private File file = null;

	private List<FieldInfo> fields = null;

	private StringBuilder sb = new StringBuilder();
	private int[] offset = null;

	public static void main(String[] args) {
		try {
			String[] fNames = { "130528.402_ICC09_OKOS0.log",
					"130529.421_ICC09_OJKF0.log" };

			for (String fName : fNames) {
				TraceLogReader tlr = new TraceLogReader(TRASE_BASE + fName);

				for (int i = 0; i < tlr.fields.size(); i++) {
					// System.out.println(readNext());
					System.out.println(tlr.read(i));
				}
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
		offset = new int[fields.size()];
		offset[0] = 0;
		for (int i = 0; i < fields.size(); i++) {
			System.out.println(fields.get(i).toString());
		}

		// バイナリデータのバッファリング
		LineReader lr = new LineReader(file);
		String buff = null;
		while ((buff = lr.readLine()) != null) {
			sb.append(buff.replaceAll(" ", ""));
		}

	}

	public String read(int i) {
		String ret = null;
		try {
			FieldInfo f = fields.get(i);

			// FIXME
			if(f.getType().equals("SKIP")){
				return null;
			}

			byte[] buff = Oct2String.record2bytes(sb.substring(f.getOffset(), f
					.getOffset()
					+ f.getByteSize()*3));
			ret = Oct2String.valueOf(buff);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

}
