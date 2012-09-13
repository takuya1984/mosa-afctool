package com.jbcc.MQTool.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.util.StdOut;
import com.jbcc.MQTool.util.Utility;

/**
 * ���O�f�[�^��o�^���� ID���d������ꍇ��update ID�����݂��Ȃ��ꍇ��insert�ɂȂ�
 * 
 * @author jetbrand
 * 
 */
public class GetLogList extends ToolCommand {

	@Override
	public void execute(String[] args) throws Exception {

		// insert update �Ȃ̂� merge��
		String sql = RESOURCE.getSql("GetLogList1");

		// ����������������SQL���쐬
		Map<String, String> datas = Utility.splitArgs(args);

		// where��̍쐬
		String strAnd = "";
		StringBuilder whereQuery = new StringBuilder();
		List<String> vals = new ArrayList<String>();
		for (Map.Entry<String, String> key : datas.entrySet()) {

			// and������i2��ڈȍ~�j
			whereQuery.append(strAnd);
			strAnd = " and ";

			whereQuery.append(key.getKey());
			String val = key.getValue();
			if (val.indexOf('*') > 0) {
				whereQuery.append(" like ? ");
				vals.add(val.replace("*", "%"));
			} else {
				whereQuery.append(" = ? ");
				vals.add(val);
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
		for (Map<String, Object> data : result) {
			output.append(data.get("ID"));

			output.append("\n");

		}
		StdOut.write(output.toString());
	}

}
