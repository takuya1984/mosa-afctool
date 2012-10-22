package com.jbcc.denbunCsv.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
	
	
	/**
	 * CSV出力
	 * @param outCsvData
	 * @param fileName
	 * @throws IOException
	 */
	public static void creatingCsv(StringBuilder outCsvData, String fileName) throws IOException {
		
		FileOutputStream file = new FileOutputStream(fileName + ".csv");;
		OutputStreamWriter out = new OutputStreamWriter(file, "UTF-8");
		out.write(outCsvData.toString());
		out.close(); 
	}
	
	/**
	 * 指定パス配下ファイルパスを取得
	 * @param param
	 * @param filePathList
	 */
	public static void getPath(String param, List<String> filePathList){
		File f = new File(param);
		File[] dir = f.listFiles();
		if(dir == null) System.exit(9);
		for(File dirs : dir){
			if(dirs.isFile()){
				if (checkXlsFile(dirs.getAbsolutePath()))
					filePathList.add(dirs.getAbsolutePath());
			} else {
				getPath(dirs.getAbsolutePath(),filePathList);
			}
		}
	}
	
	/**
	 * ファイルがエクセルか判定
	 * @param path
	 * @return
	 */
	public static boolean checkXlsFile(String path) {
		Pattern p = Pattern.compile("xls.?$");
		Matcher m = p.matcher(path);
		if (m.find()) {
			return true;
		}
		return false;
	}
	
	/**
	 * パスの最後の文字が’/’か判定し、以外の場合’/’を追加
	 * @param path
	 * @return
	 */
	public static String setLastBackslash(String path) {
		
		if(!path.endsWith("/")) path = path + "/"; 
		
		return path;
	}
}
