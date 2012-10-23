package com.jbcc.denbunCsv.creating;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import com.jbcc.denbunCsv.util.ExcelUtil;

public abstract class DenbunInfo {
	
	/**
	 * ファイル名より電文IDを取得
	 * @param fileName
	 * @return
	 */
	public static String getDenbunId(String fileName) {
		
		String[] file = fileName.split("_");
        
        if (file.length == 2 && file[1].length() > 7) {
        	return file[1].substring(0,7);
        } else if (file.length == 3 && file[2].length() > 7) {
        	return file[2].substring(0,7);
        }
		return "";
	}
	
	/**
	 * 電文シートが上りか下りかを判定
	 * @param sheet
	 * @param rowNo
	 * @return
	 */
	public static String getUpDw(HSSFSheet sheet,int rowNo,FormulaEvaluator evaluator){
		
		boolean flg = true;
		while (flg) {
			HSSFRow rowData = sheet.getRow(rowNo);
			if (rowData == null) return "";
			String cell = ExcelUtil.getCellData(rowData,0,evaluator);
			if (cell.indexOf("上り") >= 0){
				return "_1";
			} else if(cell.indexOf("下り") >= 0) {
				return "_2";
			}
			rowNo++;
		}
		return "";
	}
	
	/**
	 * 電文情報記載の行数を取得
	 * @param sheet
	 * @param rowNo
	 * @param evaluator
	 * @return
	 */
	public static int getStartRow(HSSFSheet sheet,int rowNo,FormulaEvaluator evaluator){
		boolean flg = true;
		while (flg) {
			String cell = ExcelUtil.getCellData(sheet.getRow(rowNo),1,evaluator);
			if(cell.indexOf("項目") >= 0) {
				flg = false;
			}
			rowNo++;
		}
		return rowNo;
	}
}
