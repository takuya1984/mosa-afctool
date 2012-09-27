import org.junit.Test;

import com.jbcc.MQTool.controller.EntryPoint;

public class EntryPointTest {

//	@Test
	public void LogMerge() {
		EntryPoint
				.main(new String[] { "RegistLogData_d", "id=15", "log_cd=30" });

	}

//	@Test
	public void getLogID() {
		System.out.println(System.getProperty("file.encoding"));
		EntryPoint.main(new String[] { "GetLogList", "log_cd=100" });
	}

//	@Test
	public void getLogData() {
		EntryPoint.main(new String[] { "GetLogData_d" });

	}

//	@Test
	public void CompareMerge() {
		EntryPoint
				.main(new String[] { "RegistCompareData_d", "MASTER_ID=1", "KEY=TESTKEY", "ITEM_NAME=TESTITEM1", "UP_DOWN_CD=2"});

	}

//	@Test
	public void CompareMerge_update() {
		EntryPoint
				.main(new String[] { "RegistCompareData_d", "ID=2", "MASTER_ID=2", "KEY=TESTKEY2", "ITEM_NAME=TESTITEM2", "UP_DOWN_CD=3"});

	}

//	@Test
	public void CompareMerge_update2() {
		EntryPoint
				.main(new String[] { "RegistCompareData_d", "ID=4", "MASTER_ID=3", "KEY=TESTKEY3", "ITEM_NAME=TESTITEM3", "UP_DOWN_CD=1"});

	}
	@Test
	public void Compare1() {
		EntryPoint
				.main(new String[] { "CompareData_d", "264", "270"});

	}

}
