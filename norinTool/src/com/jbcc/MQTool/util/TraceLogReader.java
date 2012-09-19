package com.jbcc.MQTool.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TraceLogReader extends LineReader {

	// private String DDL_BASE =
	// "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\LOG\\資料20120911\\TABLE\\";

	private static String TRASE_BASE = "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\script\\log\\07_trace\\";
	private static String WORK_FILE = "C:\\Users\\MOSA2\\Dropbox\\【農林】統合テスト支援ツール\\script\\log\\07_trace_2_byte\\work.bin";
	private File file = null;
	// private File ddlFile = null;

	private FileInputStream fis = null;

	private List<FieldInfo> fields = null;
	private int i = 0;

	public static void main(String[] args) {
		try {
			String fName = "130528.433_ICC09_OKGJ0.log";

			TraceLogReader tlr = new TraceLogReader(TRASE_BASE + fName);
			String buff = null;
			while ((buff = tlr.readNext()) != null) {
				System.out.println(buff);
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
		// String s = file.getName();
		// ddlFile = new File(DDL_BASE + "KANJOU."
		// + s.substring(s.length() - 9, s.length() - 4) + ".sql");

		// traceログのフィールド情報を取得
		fields = FieldInfo.getFieldInfo(file);
		for (FieldInfo fi : fields) {
			System.out.println(fi.toString());
		}

		// いったんバイナリファイルに出力する
		FileOutputStream fos = new FileOutputStream(WORK_FILE);
		byte[] buff = null;
		while ((buff = Oct2String.record2bytes(readLine())) != null && buff.length>0) {
			fos.write(buff, 0, buff.length);
		}
		fos.flush();
		fos.close();
		fos = null;

		//fis = new FileInputStream(WORK_FILE);

	}

	public String readNext() {
		String ret = null;
		try {
			int size = Integer.valueOf(fields.get(i).getSize()).intValue();
			byte[] buff = new byte[size];
			for (int j = 0; j < buff.length; j++) {
				// FIXME buff[j] = fis.read();
			}
			ret = Oct2String.valueOf(buff);

		} catch (Exception e) {
			e.printStackTrace();
		}
		//FIXME
		return null;
	}

}
