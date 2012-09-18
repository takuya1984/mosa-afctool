package com.jbcc.MQTool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class TraceLogReader extends LineReader {

	public TraceLogReader(String s) throws FileNotFoundException {
		super(s);
	}

	public TraceLogReader(File f) throws FileNotFoundException {
		super(f);
	}

	public void readFields(List<FieldInfo> list) {

	}
}
