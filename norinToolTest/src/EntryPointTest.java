import org.junit.Test;

import com.jbcc.MQTool.controller.EntryPoint;

public class EntryPointTest {

	@Test
	public void LogMerge() {
		EntryPoint
				.main(new String[] { "RegistLogData_d", "id=15", "log_cd=30" });

	}

	@Test
	public void getLogID() {
		System.out.println(System.getProperty("file.encoding"));
		EntryPoint.main(new String[] { "GetLogList", "log_cd=100" });
	}

	@Test
	public void getLogData() {
		EntryPoint.main(new String[] { "GetLogData_d" });

	}

}
