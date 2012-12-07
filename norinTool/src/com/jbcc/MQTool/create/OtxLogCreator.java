package com.jbcc.MQTool.create;

import java.io.File;
import java.io.IOException;

import com.jbcc.MQTool.controller.EntryPoint;
import com.jbcc.MQTool.controller.PropertyLoader;
import com.jbcc.MQTool.util.FileUtil;
import com.jbcc.MQTool.util.LineReader;

/**
 * OTX系ログ登録クラス.
 *
 */
public class OtxLogCreator {
	private static boolean debug = false;
	public String mode = null;
	
	public static void main(String[] args) {
		try {
			if (debug)
				new OtxLogCreator().createLog("4");
			else
				new OtxLogCreator().createLog(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ログのDB登録.
	 * @param file 対象ファイル
	 * @throws IOException
	 */
	public void createLog(String pmode) throws Exception {
		mode = pmode;
		String key = "";
		if ("3".equals(pmode)) {
			key = "03_otx-css";
		} else if ("4".equals(pmode)) {
			key = "04_otx-onl";
		} else if ("5".equals(pmode)) {
			key = "05_apinfo";
		} else if ("6".equals(pmode)) {
			key = "06_aphost";
		}

		String INPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logtemp")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty(key)
				+ File.separator;
		String OUPUT_BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logbase")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty(key)
				+ File.separator;
		File target = new File(INPUT_BASE);
		File[] files = target.listFiles();
		for (File file : files) {
			int ret = createLog(file);
			if (ret == 0) {
				// ファイル移動
				FileUtil copy = new FileUtil();
				copy.copy(INPUT_BASE + "/" + file.getName(), OUPUT_BASE + "/" + file.getName());
				file.delete();
			}

		}
	}

	public int createLog(File file) throws Exception {
		if (!file.exists()) {
			return 1;
		}
		LineReader reader = new LineReader(file);

		String buff = null;// 読み込みバッファ
		String[] tokens = null;
		String upDown = "";
		String header = "";
		String logOutputDate = "";
		String date = "";String time = "";String msec = "";
		String functioncd = "";
		String logcd = "log_cd=" + mode;
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
			if (buff.indexOf("上り") > -1)
				upDown = "1";
			else
				upDown = "2";
			
			// ヘッダー情報
			if (buff.indexOf("共通ヘッダ部") > -1) {
				header = buff.replaceFirst(".*共通ヘッダ部=", "");
				date = tokens[0].replaceAll("/", "");
				time = tokens[1].split(",")[0].replaceAll(":", "");
				msec = tokens[1].substring(9, 12);
				logOutputDate = date + time + msec;

				clcd = header.substring(0, 5);
				opecd = header.substring(5, 6);
				denbuncd = header.substring(6, 13);
				clientSerialNumber = header.substring(13, 17);
				continueDenbunFlg = header.substring(17, 18);
				multiDenbunType = header.substring(18, 19);
				
				// 下りの場合
				if (upDown.equals("2")) {
					transactionNumber = header.substring(22, 30);
					denbunKind = header.substring(20, 22);
				}
				
				creatflg = true;
				continue;
			}
			// functionID
			if (buff.indexOf(" Ope=") > -1) {
				functioncd = buff.replaceAll(".*Ope=", "").split(" {1,}")[0];
				continue;
			}
		}
		reader.close();
		if (creatflg) {
			EntryPoint entry = new EntryPoint();
			return entry.execute(
				new String[]{
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
		return 1;
	}
}
