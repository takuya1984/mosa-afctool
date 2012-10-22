package com.jbcc.denbunCsv.creating;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import com.jbcc.denbunCsv.constant.DenbunCsvConstant;
import com.jbcc.denbunCsv.util.ExcelUtil;
import com.jbcc.denbunCsv.util.FileUtil;

public class CreatingCsvFile extends DenbunInfo {

	public static void main (String[] args) {
		
		if (args.length < 2) {
			// 引数が指定されていない
			System.exit(-1);
			return;
		}

		String inputPath = args[0];
		String outputPath = args[1];
		List<String> filePathList = new ArrayList<String>();
		if (FileUtil.checkXlsFile(inputPath)) {
			// エクセルファイルを指定した場合
			filePathList.add(inputPath);
		} else {
			// 指定ディレクトリパスの最後に’/’があるか
			inputPath = FileUtil.setLastBackslash(inputPath);
			// 指定ディレクトリ内のエクセルファイルパスを取得
			FileUtil.getPath(inputPath,filePathList);
		}
		// CSV出力先パスの最後に’/’があるか
		outputPath = FileUtil.setLastBackslash(outputPath);
		
		try {
			for (String filePsth : filePathList) {
				// CSV作成
				getDenbunData(filePsth,outputPath);
			}
		} catch (IOException e) {
			System.exit(-2);
			e.printStackTrace();
			return;
		}
		
		System.exit(0);
	}

	/**
	 * 電文CSVを作成します
	 * @param filePsth
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void getDenbunData(String filePsth,String outputPath) throws IOException {
		
		POIFSFileSystem filein = new POIFSFileSystem(new FileInputStream(filePsth));
        HSSFWorkbook wb = new HSSFWorkbook(filein);
        CreationHelper crateHelper = wb.getCreationHelper();
        FormulaEvaluator evaluator = crateHelper.createFormulaEvaluator();
        // 電文IDをファイル名から取得
        String fileName = filePsth.substring(filePsth.lastIndexOf("/")+1,filePsth.length());
        // 電文IDの取得
        String denbunID = getDenbunId(fileName);
        
        List<StringBuilder> outPutDataList = new ArrayList<StringBuilder>();
        List<String> denbunUpDwList = new ArrayList<String>();
        int upCount = 0;
        int dwCount = 0;
        for (int i = 0;i < wb.getNumberOfSheets(); i++) {
        	// シートを取得
            HSSFSheet sheet = wb.getSheetAt(i);
            HSSFRow row = sheet.getRow(2);
            // 電文仕様シートか判定
            if (row == null ||  (ExcelUtil.getCellData(row,0,evaluator).indexOf("電文仕様") < 0 &&
            				     ExcelUtil.getCellData(row,1,evaluator).indexOf("電文仕様") < 0)) {
            	continue;
            }
            
            // データ取得行
            int rowNo = 5;

            // 電文が上りか下りかを取得
            String upDw = getUpDw(sheet,rowNo,evaluator);
            if (upDw.equals("")) continue;
                
            // CSV出力行を取得
            rowNo = getStartRow(sheet,rowNo,evaluator);
            
            StringBuilder outCsvData = new StringBuilder();
            while((sheet.getRow(rowNo) != null) &&
            		((!ExcelUtil.getCellData(sheet.getRow(rowNo),1,evaluator).equals("")) ||
            		(sheet.getRow(rowNo+1) != null &&
            		!ExcelUtil.getCellData(sheet.getRow(rowNo+1),1,evaluator).equals("")))) {
            	row = sheet.getRow(rowNo);
            	// 項目名、属性、桁数を取得
            	String name = ExcelUtil.getCellData(row,1,evaluator);
            	String type = ExcelUtil.getCellData(row,2,evaluator);
            	String length = ExcelUtil.getCellData(row,3,evaluator);
            	// 項目名、属性、桁数のいずれかが未記入の場合、次の行へ移動
            	if(name.equals("") || type.equals("") || length.equals("")) {
            		rowNo++;
            		continue;
            	}
            	// CSV出力行を作成
	            outCsvData.append(ExcelUtil.getCellData(row,0,evaluator) + DenbunCsvConstant.DELIMITER);
	            outCsvData.append(name + DenbunCsvConstant.DELIMITER);
	            outCsvData.append(type + DenbunCsvConstant.DELIMITER);
	            outCsvData.append(length + DenbunCsvConstant.DELIMITER);
	            outCsvData.append(ExcelUtil.getCellData(row,4,evaluator) + DenbunCsvConstant.DELIMITER);
	            outCsvData.append(ExcelUtil.getCellData(row,5,evaluator) + DenbunCsvConstant.DELIMITER);
	            outCsvData.append(ExcelUtil.getCellData(row,6,evaluator) +
	            				  ExcelUtil.getCellData(row,7,evaluator) + DenbunCsvConstant.CRLF);
	            rowNo++;
            }
            
            // CSV出力情報をリストに設定
            outPutDataList.add(outCsvData);
            // CSV出力情報が上りか下りかを設定
            denbunUpDwList.add(upDw);
            // 上り下りのシート数をカウント
            if (upDw.equals("_1")) {
            	upCount++;
            } else if (upDw.equals("_2")) {
            	dwCount++;
            }
        }
        
        // CSV出力
        int upNo = 1;
        int dwNo = 1;
        for(int i = 0; i < outPutDataList.size(); i++ ) {
        	StringBuilder outCsvData = outPutDataList.get(i);
        	String upDw = denbunUpDwList.get(i);
        	String no = "";
        	// 上り、下りが複数シートか判定
        	if (upCount > 1 && upDw.equals("_1")){
        		no = no + "-" + upNo;
        		upNo++;
        	} else if (dwCount > 1 && upDw.equals("_2")){
        		no = no + "-" + dwNo;
        		dwNo++;
        	}
        	FileUtil.creatingCsv(outCsvData,outputPath + denbunID + upDw + no);
        }
	}
}
