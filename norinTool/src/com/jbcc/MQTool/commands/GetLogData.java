package com.jbcc.MQTool.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.util.StdOut;

/**
 * ログデータを登録する IDが重複する場合はupdate IDが存在しない場合はinsertになる
 * 
 * @author jetbrand
 * 
 */
public class GetLogData extends ToolCommand {

	private String[] COLUMN_NAMES = { "ID", "LOG_CD", "UP_DOWN_CD",
			"LOG_OUTPUT_DATE", "FUNCTION_CD", "CL_CD", "OPE_CD", "DENBUN_CD",
			"EMP_NO", "HOST_DATE", "CLIENT_SERIAL_NUMBER",
			"CONTINUE_DENBUN_FLG", "MULTI_DENBUN_TYPE", "DENBUN_KIND",
			"TRANSACTION_NUMBER", "LOG_TABLE_NAME", "LOG_DATA_FILE" };

	@Override
	public void execute(String[] args) throws Exception {

		// insert update なので merge文
		String sql = RESOURCE.getSql("GetLogList1");

		// where句の作成
		String strOr = "";
		StringBuilder whereQuery = new StringBuilder();
		List<String> vals = new ArrayList<String>();
		String[] queryArgs = Arrays.copyOfRange(args, 1, args.length);
		for (String arg : queryArgs) {

			// andをつける（2回目以降）
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

		// SQL実行
		List<Map<String, Object>> result = RESOURCE.selectDB(
				MessageFormat.format(sql, whereQuery.toString()),
				vals.toArray());

		StringBuilder output = new StringBuilder();
		boolean isColNamePrinted = false;
		for (Map<String, Object> data : result) {

			// 一回目はカラム名出力
			if (!isColNamePrinted) {
				for (String colName : COLUMN_NAMES) {
					output.append(colName);
					output.append("\t");

				}
				output.append("\n");
				isColNamePrinted = true;
			}

			// 現在の行のデータ出力
			for (String colName : COLUMN_NAMES) {
				output.append(data.get(colName));
				output.append("\t");

			}

			output.append("\n");

		}

		StdOut.write(output.toString());

	}

}
