package com.jbcc.MQTool.create;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.EntryPoint;
import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;

public class ClientLogCreator {
	public static void main(String[] args) {
		try {
			new ClientLogCreator().createLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createLog() throws IOException {
		String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logtemp")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("01_client")
				+ File.separator;
		File target = new File(INPUT_BASE);
		File[] files = target.listFiles();
		for (File file : files) {
			createLog(file);
		}
	}

	public void createLog(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		LineReader reader = new LineReader(file);

		String buff = null;// 読み込みバッファ
		String[] tokens = null;
		String upDown = "";
		String header = "";
		String logOutputDate = "";
		String date = "";String time = "";
		String logcd = "log_cd=1";
		String clcd = "";
		String opecd = "";
		String denbuncd = "";
		String clientSerialNumber = "";
		String continueDenbunFlg = "";
		String transactionNumber = "";
		boolean creatflg = false;
		while ((buff = reader.readLine()) != null) {
			tokens = buff.split(" {1,}");
			if (tokens.length < 4)
				continue;

			// 上り下り判定
			if (buff.indexOf("送信") > -1)
				upDown = "1";
			else
				upDown = "2";

			// オペレーションコード
			String ope = tokens[4];
			if ("Com".equals(ope)) {
				header = buff.replaceFirst(".* Com ", "");
				date = tokens[0].replaceAll("/", "");
				time = tokens[1].split(",")[0].replaceAll(":", "");
				logOutputDate = date + time;
				clcd = header.substring(0, 5);
				opecd = header.substring(5, 6);
				denbuncd = header.substring(6, 13);
				clientSerialNumber = header.substring(13, 17);
				continueDenbunFlg = header.substring(17, 18);
				
				// 下りの場合
				if (upDown.indexOf("2") > -1)
					transactionNumber = header.substring(22, 30);
				
				creatflg = true;
				break;
			}
			reader.close();
		}
		if (creatflg)
			EntryPoint.main(new String[]{
					"RegistLogData", 
					logcd, 
					"log_output_date=".concat(logOutputDate), 
					"up_down_cd=".concat(upDown), 
					"cl_cd=".concat(clcd), 
					"ope_cd=".concat(opecd), 
					"denbun_cd=".concat(denbuncd), 
					"client_serial_number=".concat(clientSerialNumber), 
					"continue_denbun_flg=".concat(continueDenbunFlg), 
					"transaction_number=".concat(transactionNumber), 
					"log_data_file=".concat(file.getName())});
	}
}
