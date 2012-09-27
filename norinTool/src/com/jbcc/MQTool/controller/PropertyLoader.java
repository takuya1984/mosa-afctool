package com.jbcc.MQTool.controller;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class PropertyLoader extends Properties {

	private static final long serialVersionUID = 1L;
	private static HashMap<String, Properties> map = new HashMap<String, Properties>();

	public static void main(String[] args) {
		System.out.println(getDirProp().getProperty("ddl"));
	}

	public static Properties getDirProp() {
		return getProperies("directory.properties");
	}

	public static Properties getProperies(String path) {
		Properties ret = null;
		try {
			ret = map.get(path);
			if (ret == null) {
				ret = new PropertyLoader(path);
				map.put(path, ret);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private PropertyLoader(String propFilename) {
		InputStream stream = this.getClass().getClassLoader().
				getResourceAsStream(propFilename);
		if (stream == null) {
			throw new RuntimeException(propFilename + "が見つかりません。");
		}
		try {
			loadFromXML(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				stream.close();
			} catch (Throwable t) {
			}
		}
	}

}
