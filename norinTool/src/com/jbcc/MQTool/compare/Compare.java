package com.jbcc.MQTool.compare;

import java.io.IOException;
import java.util.List;

public interface Compare {
	public List<String> getCompareLog(final String LOG_PATH, String[] fileKye,String fileName) throws IOException;
}
