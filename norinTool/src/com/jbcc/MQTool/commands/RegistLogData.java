package com.jbcc.MQTool.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.jbcc.MQTool.controller.ToolCommand;
import com.jbcc.MQTool.util.Utility;

/**
 * ログデータを登録する IDが重複する場合はupdate IDが存在しない場合はinsertになる
 * 
 * @author jetbrand
 * 
 */
public class RegistLogData extends ToolCommand {

	@Override
	public int execute(String[] args) throws Exception {

		// insert update なので merge文
		String sql = RESOURCE.getSql("ResistLogData1");

		// 引数を辞書かしてSQL文作成
		Map<String, String> datas = Utility.splitArgs(args);
		String dataid = datas.get("id");
		datas.remove("id");
		String insertQuery = insertQuery(datas);
		String updateQuery = Utility.updateQuery(datas);

		// preparedstatementのパラメータ作成
		Collection<String> values = new ArrayList<String>();
		// 条件句のID
		values.add(dataid);
		// update句の値
		values.addAll(datas.values());
		// insert句の値
		values.addAll(datas.values());

		// SQL実行
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

		// インサート時 IDは自動採番にする
		cols.append(",ID ) values (");
		cols.append(places.toString());
		cols.append(" ,(select nvl((select Max(id) from kanri),0) from dual) + 1)");

		return "(" + cols.toString();

	}
}
