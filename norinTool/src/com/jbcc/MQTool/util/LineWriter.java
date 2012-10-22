package com.jbcc.MQTool.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LineWriter extends BufferedWriter {

	public LineWriter(String path) throws IOException {
		super(new FileWriter(path));
		StdOut.writeDebug(path);
	}

	public LineWriter(String path, String encode) throws IOException {
		super(new OutputStreamWriter(new FileOutputStream(path), encode));
		StdOut.writeDebug(path);
	}

	public LineWriter(File path, String encode) throws IOException {
		super(new OutputStreamWriter(new FileOutputStream(path), encode));
		StdOut.writeDebug(path.getPath());
	}

	public void writeLine(String str) throws IOException {
		write(str);
		newLine();
	}
}
