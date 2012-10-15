package com.jbcc.MQTool.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LineWriter extends BufferedWriter {

	public LineWriter(String path) throws IOException {
		super(new FileWriter(path));
	}

	public void writeLine(String str) throws IOException {
		write(str);
		newLine();
	}
}
