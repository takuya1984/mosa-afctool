package com.jbcc.MQTool.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.util.Utility;

/**
 * ���O�f�[�^��o�^���� ID���d������ꍇ��update ID�����݂��Ȃ��ꍇ��insert�ɂȂ�
 * 
 * @author jetbrand
 * 
 */
public class RegistLogData extends ToolCommand {

	@Override
	public int execute(String[] args) throws Exception {

		// insert update �Ȃ̂� merge��
		String sql = RESOURCE.getSql("ResistLogData1");

		// ����������������SQL���쐬
		Map<String, String> datas = Utility.splitArgs(args);
		String dataid = datas.get("id");
		datas.remove("id");
		String insertQuery = insertQuery(datas);
		String updateQuery = Utility.updateQuery(datas);

		// preparedstatement�̃p�����[�^�쐬
		Collection<String> values = new ArrayList<String>();
		// �������ID
		values.add(dataid);
		// update��̒l
		values.addAll(datas.values());
		// insert��̒l
		values.addAll(datas.values());

		// SQL���s
		int counts = RESOURCE.updateDB(
				MessageFormat.format(sql, updateQuery, insertQuery),
				values.toArray());

		return counts;
	}

	private String insertQuery(Map<String, String> args) {

		StringBuilder cols = new StringBuilder();
		StringBuilder places = new StringBuilder();
		String splitter = "";
		for (Map.Entry<String, String> arg : args.entrySet()) {
			cols.append(splitter);
			places.append(splitter);
			splitter = ",";
			cols.append(arg.getKey());
			places.append("?");
		}

		// �C���T�[�g�� ID�͎����̔Ԃɂ���
		cols.append(",ID ) values (");
		cols.append(places.toString());
		cols.append(" ,(select nvl((select Max(id) from kanri),0) from dual) + 1)");

		return "(" + cols.toString();

	}
}
