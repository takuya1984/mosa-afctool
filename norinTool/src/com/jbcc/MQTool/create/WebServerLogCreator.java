package com.jbcc.MQTool.create;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.EntryPoint;
import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.LineReader;

/**
 * WebServerログ登録クラス.
 *
 */
public class WebServerLogCreator {
	public static void main(String[] args) {
		try {
			new WebServerLogCreator().createLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ログのDB登録.
	 * @param file 対象ファイル
	 * @throws IOException
	 */
	public void createLog() throws IOException {
		String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logtemp")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("02_web")
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
		String date = "";String time = "";String msec = "";
		String functioncd = "";
		String logcd = "log_cd=2";
		String clcd = "";
		String opecd = "";
		String denbuncd = "";
		String clientSerialNumber = "";
		String continueDenbunFlg = "";
		String transactionNumber = "";
		String multiDenbunType = "";
		String denbunKind = "";
		boolean creatflg = false;
		while ((buff = reader.readLine()) != null) {
			tokens = buff.split(" {1,}");
			// 上り下り判定
			if (file.getName().endsWith("1.dat"))
				upDown = "1";
			else
				upDown = "2";

			// ログ時間
			if (buff.indexOf("TRACE") > -1) {
				date = tokens[0].replaceAll("/", "");
				time = tokens[1].split(",")[0].replaceAll(":", "");
				msec = tokens[1].substring(9, 12);
				logOutputDate = date + time + msec;
				continue;
			}
			
			// functionID
			if (buff.indexOf("<ns1:RequestMessage") > -1) {
				String[] functionkeies = buff.split("/");
				functioncd = functionkeies[functionkeies.length - 2].replaceAll("Service", "");
				continue;
			}
			
			// ヘッダー情報
			if (buff.indexOf("<strComUpHeadDt>") > -1 || buff.indexOf("<strbufComDwHeadDt>") > -1) {
				header = buff.replaceFirst(".*<str.?.?.?Com..HeadDt>","").replaceAll("/str.?.?.?Com..HeadDt>","");
				clcd = header.substring(0, 5);
				opecd = header.substring(5, 6);
				denbuncd = header.substring(6, 13);
				clientSerialNumber = header.substring(13, 17);
				continueDenbunFlg = header.substring(17, 18);
				multiDenbunType = header.substring(18, 19);
				
				// 下りの場合
				if (upDown.indexOf("2") > -1) {
					transactionNumber = header.substring(22, 30);
					denbunKind = header.substring(20, 22);
				}
				
				creatflg = true;
				break;
			}
		}
		reader.close();
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
					"log_data_file=".concat(file.getName()),
					"function_cd=".concat(functioncd),
					"multi_denbun_type=".concat(multiDenbunType),
					"denbun_kind=".concat(denbunKind)
					});
	}
}
