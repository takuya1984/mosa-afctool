import com.jbcc.MQTool.controller.EntryPoint;

public class RegistDataTest {

	private static String[] record = {
		"RegistLogData_d",
		"id=9999",
		"LOG_CD=999",
		"UP_DOWN_CD=1",
		"LOG_OUTPUT_DATE=2012/11/09 20:18:00",
		"FUNCTION_CD=function",
		"CL_CD=",
		"OPE_CD=0",
		"DENBUN_CD=",
		"EMP_NO=",
		"HOST_DATE=",
		"CLIENT_SERIAL_NUMER=",
		"CONTINUE_DENBUN_FLG=",
		"MULTI_DENBUN_TYPE=",
		"DENBUN_KIND=",
		"TRANSACTION_NUMBER=",
		"LOG_TABLE_NAME=",
		"LOG_DATA_FILE=FILENAME"
	};

	public static void main(String[] args) {
		EntryPoint.main(record);
		EntryPoint.main(record);
	}
}
