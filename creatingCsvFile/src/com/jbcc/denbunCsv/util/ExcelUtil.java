package com.jbcc.denbunCsv.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import com.jbcc.denbunCsv.constant.DenbunCsvConstant;

public class ExcelUtil {
	
	/**
	 * セルの値を取得
	 * @param row
	 * @param i
	 * @return
	 */
	public static String getCellData(HSSFRow row,int i,FormulaEvaluator evaluator ) {  
		HSSFCell cell = row.getCell(i);	
		 if (cell == null) {
			 return "" ; 
		 } else {
			 return getCellValue(cell,evaluator).replace(DenbunCsvConstant.CRLF, " ").replace(DenbunCsvConstant.LF, " ");
		 }
	}
	
	/**
	 * セルの値を取得
	 * @param cell
	 * @param evaluator
	 * @return
	 */
	private static String getCellValue(Cell cell,FormulaEvaluator evaluator) {
		switch(cell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				return "";
			case Cell.CELL_TYPE_STRING:
				return cell.getRichStringCellValue().getString();
			case Cell.CELL_TYPE_BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case Cell.CELL_TYPE_NUMERIC:
				DataFormatter formatter = new DataFormatter();
				return formatter.formatCellValue(cell);
			case Cell.CELL_TYPE_FORMULA:
				return getCellValue(evaluator.evaluateInCell(cell),null);
		  	default:
		  		return "";
		}
	}
}
