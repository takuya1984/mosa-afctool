package com.jbcc.MQTool.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jbcc.MQTool.controller.PropertyLoader;


public class GetCompareLog {
	

	/**
	 * ログを指定文字で区切りリストに格納し返します
	 * 
	 * @param path　　　      ログファイル格納パス
	 * @param fileName  ログファイル名
	 * @param key       ログ取得キー
	 * @param cutNumber 後ろから削除する文字数
	 * @return
	 * @throws IOException
	 */
	public static List<String> getTabDelimitedLogData(String path, String fileName, String key, String delimited,int cutNumber,boolean sqlFlg) throws IOException {
		List<String> lines = null;
		if (sqlFlg) {
			lines = getLogs(path,fileName,key,cutNumber);
		} else {
			lines = getLog(path,fileName,key,cutNumber);
		}
		
		List<String> data = new ArrayList<String>();
		for (String line : lines) {
			// 指定文字で区切りリストに格納
			data.addAll(Arrays.asList(line.split(delimited, -1)));
		}
		return data;
	}
	
	/**
	 * ログを１文字ずつ区切りリストに格納し返します
	 * 
	 * @param path　　　      ログファイル格納パス
	 * @param fileName  ログファイル名
	 * @param key       ログ取得キー
	 * @param cutNumber 後ろから削除する文字数
	 * @return
	 * @throws IOException
	 */
	public static List<String> getFixedLengthDataList(String path, String fileName, String key, int cutNumber) throws IOException {
		
		// オンライン上り電文でのみ使用。１ファイルに１行の為
		String line = getLog(path,fileName,key,cutNumber).get(0);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < line.length(); i++) {
			// １文字づつ区切りリストに格納
			list.add(line.substring(i, i+1));
		}
		return list;
	}
	
	/**
	 * ログファイルから対象行のデータを取得します。
	 * 
	 * @param path　　　      ログファイル格納パス
	 * @param fileName  ログファイル名
	 * @param key       ログ取得キー
	 * @param cutNumber 後ろから削除する文字数
	 * @return
	 * @throws IOException
	 */
	private static List<String> getLog(String path, String fileName, String key, int cutNumber) throws IOException {
		String BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logbase")
				+ File.separator;
		
		LineReader lR = new LineReader(BASE + path + fileName);
		String line = lR.readLineByKey(key);
		List<String> lines = new ArrayList<String>();
		if (line != null){
			if (cutNumber != 0) {
				// 対象行の後ろの不要部分を削除
				line = catString(line,cutNumber);
			}
			// 対象行の前の不要部分を削除
			lines.add(line.substring(line.toUpperCase().indexOf(key.toUpperCase())+key.length(),line.length())); 
		}
		return lines;
	}
	
	/**
	 * ログファイルから対象行のデータを取得します。
	 * 
	 * @param path　　　      ログファイル格納パス
	 * @param fileName  ログファイル名
	 * @param key       ログ取得キー
	 * @param cutNumber 後ろから削除する文字数
	 * @return
	 * @throws IOException
	 */
	private static List<String> getLogs(String path, String fileName, String key, int cutNumber) throws IOException {
		String BASE = PropertyLoader.getDirProp().getProperty(
				"basedir")
				+ File.separator
				+ PropertyLoader.getDirProp().getProperty("logbase")
				+ File.separator;

		LineReader lR = new LineReader(BASE + path + fileName);
		List<String> lines = lR.readLinesByKey(key);
		
		int i = 0;
		for (String line : lines) {
			if (line != null) {
				if (cutNumber != 0) {
					// 対象行の後ろの不要部分を削除
					line = catString(line,cutNumber);
				}
				// 対象行の前の不要部分を削除
				lines.set(i,line.substring(line.toUpperCase().indexOf(key.toUpperCase())+key.length(),line.length())); 
				i++;
			}
		}
		return lines;
	}
	
	
	private static String catString(String line,int cutNumber) {
		if (cutNumber == -1) {
			// SOAP電文閉じタグを削除
			if (line.indexOf("</") > -1)
				cutNumber = line.length() - line.indexOf("</");
			else
				cutNumber = 0;
		}
		return line.substring(0, line.length() - cutNumber);
	}
}
