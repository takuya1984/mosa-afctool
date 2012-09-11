package com.jbcc.MQTool.util;

public class StdOut {

	public static boolean isDebug = false;

	public static void write(String msg) {
		System.out.println(msg);
	}

	public static void writeDebug(String msg) {
		if (isDebug) {
			System.out.println(msg);
		}
	}

}
