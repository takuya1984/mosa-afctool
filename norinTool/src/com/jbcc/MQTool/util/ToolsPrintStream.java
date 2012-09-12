package com.jbcc.MQTool.util;

import java.io.PrintStream;

public class ToolsPrintStream extends PrintStream {

	public ToolsPrintStream() {
		//デフォルトでは標準出力する
		super(System.out);
	}

	/**
	 * 必要に応じて、PrintStreamの出力先を変えられるようにする
	 * 今のとこ要件にない
	 */
}
