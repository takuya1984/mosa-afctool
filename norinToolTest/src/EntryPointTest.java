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
//	@Test
	public void Compare1() {
//		EntryPoint
//		.main(new String[] { "CompareData", "265", "272"});
		EntryPoint
		.main(new String[] { "CompareData", "1", "139"});

	}
//	@Test
	public void Compare2_JYOGAI() {
		EntryPoint
				.main(new String[] { "CompareData_d", "1", "190"});
	}
	
	@Test
	public void Compare2() {
//		EntryPoint
//				.main(new String[] { "CompareData", "12", "2012-04-09-135759_00124_2_3104019_1.dat", "2012-08-02-191154501_01089_2_1108019_1.dat"});
//
//		EntryPoint
//				.main(new String[] { "CompareData", "53", "2012-08-17-143122_55128_2_2302010_1.dat", "2012-04-06-142856007_00099_2_2302010_1.dat"});
//		
//		EntryPoint
//				.main(new String[] { "CompareData", "46", "2012-08-17-143122_55128_2_2302010_1.dat", "2012-08-17-143122_55128_2_2302010_2.dat"});
//		
		EntryPoint
				.main(new String[] { "CompareData", "78", "130529.421_ICC09_OJKF0.log", "20120817162128_1_IDB73_ODEN0.dat"});
//
//		EntryPoint
//				.main(new String[] { "CompareData", "78", "130528.357_ICC09_S_T_N.log", "20120817162128_1_IDB73_ODEN0.dat"});
//		
//		EntryPoint
//				.main(new String[] { "CompareData", "910", "2012-04-06-142846_00099_1_0801030_sql.dat", "2012-04-06-142846_00099_1_0801030_sql.dat"});
//		
//		EntryPoint
//				.main(new String[] { "CompareData", "109", "2012-04-06-142847_00099_1_0801030_sql.dat", "2012-04-06-142846_00099_1_0801030_sql.dat"});

	}

}
