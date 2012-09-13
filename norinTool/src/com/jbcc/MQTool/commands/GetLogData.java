package com.jbcc.MQTool.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.util.StdOut;

/**
 * ���O�f�[�^��o�^���� ID���d������ꍇ��update ID�����݂��Ȃ��ꍇ��insert�ɂȂ�
 * 
 * @author jetbrand
 * 
 */
public class GetLogData extends ToolCommand {

	private String[] COLUMN_NAMES = { "ID", "LOG_CD", "UP_DOWN_CD",
			"LOG_OUTPUT_DATE", "FUNCTION_CD", "CL_CD", "OPE_CD", "DENBUN_CD",
			"EMP_NO", "HOST_DATE", "CLIENT_SERIAL_NUMBER",
			"CONTINUE_DENBUN_FLG", "MULTI_DENBUN_TYPE", "DENBUN_KIND",
			"TRANSACTION_NUMBER", "LOG_TABLE_NAME", "LOG_DATE_NAME" };

	@Override
	public void execute(String[] args) throws Exception {

		// insert update �Ȃ̂� merge��
		String sql = RESOURCE.getSql("GetLogList1");

		// where��̍쐬
		String strOr = "";
		StringBuilder whereQuery = new StringBuilder();
		List<String> vals = new ArrayList<String>();
		String[] queryArgs = Arrays.copyOfRange(args, 1, args.length);
		for (String arg : queryArgs) {

			// and������i2��ڈȍ~�j
			whereQuery.append(strOr);
			strOr = " Or ";

			whereQuery.append(" ID ");

			if (arg.indexOf('*') > 0) {
				whereQuery.append(" like ? ");
				vals.add(arg.replace("*", "%"));
			} else {
				whereQuery.append(" = ? ");
				vals.add(arg);
			}

		}

		if (vals.size() > 0) {
			whereQuery = new StringBuilder("Where " + whereQuery.toString());
		}

		// SQL���s
		List<Map<String, Object>> result = RESOURCE.selectDB(
				MessageFormat.format(sql, whereQuery.toString()),
				vals.toArray());

		StringBuilder output = new StringBuilder();
		boolean isColNamePrinted = false;
		for (Map<String, Object> data : result) {

			// ���ڂ̓J�������o��
			if (!isColNamePrinted) {
				for (String colName : COLUMN_NAMES) {
					output.append(colName);
					output.append("\t");

				}
				output.append("\n");
				isColNamePrinted = true;
			}

			// ���݂̍s�̃f�[�^�o��
			for (String colName : COLUMN_NAMES) {
				output.append(data.get(colName));
				output.append("\t");

			}

			output.append("\n");

		}

		StdOut.write(output.toString());

	}

}
