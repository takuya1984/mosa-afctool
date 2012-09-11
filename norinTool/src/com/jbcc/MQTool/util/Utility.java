package com.jbcc.MQTool.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utility {

	public static String updateQuery(Map<String, String> args) {

		StringBuilder sb = new StringBuilder();
		String splitter = "";
		for (Map.Entry<String, String> arg : args.entrySet()) {
			sb.append(splitter);
			splitter = ",";
			sb.append(arg.getKey() + " = ?");
		}
		return sb.toString();
	}

	public static Map<String, String> splitArgs(String[] args) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		for (String arg : args) {
			String[] tmp = arg.split("=");
			if (tmp.length < 2) {
				continue;
			}

			result.put(tmp[0].toLowerCase().trim(), tmp[1].trim());
		}

		return result;
	}
}
