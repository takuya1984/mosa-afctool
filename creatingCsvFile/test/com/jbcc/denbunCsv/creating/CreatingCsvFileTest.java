package com.jbcc.denbunCsv.creating;

import org.junit.Test;

public class CreatingCsvFileTest {

	@Test
	public void test() {
//		String inputPath = "/Users/tyoshida/Desktop/仕事/農林/20120409_資料/20120409_資料/電文仕様書/01100_審査票作成/0110050_5章電文設計/0110051_1502121電文仕様.xls";
		String inputPath = "/Users/tyoshida/Desktop/仕事/農林/20120409_資料/20120409_資料/電文仕様書/";
		
		String outputPath = "/Users/tyoshida/desktop/仕事/出力先/";
		
		
		CreatingCsvFile.main(new String[] {inputPath,outputPath});
	}

}
