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
	 * CSV�o��
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
	 * �w��p�X�z���t�@�C���p�X���擾
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
	 * �t�@�C�����G�N�Z��������
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
	 * �p�X�̍Ō�̕������f/�f�����肵�A�ȊO�̏ꍇ�f/�f��ǉ�
	 * @param path
	 * @return
	 */
	public static String setLastBackslash(String path) {
		
		if(!path.endsWith("/")) path = path + "/"; 
		
		return path;
	}
}
