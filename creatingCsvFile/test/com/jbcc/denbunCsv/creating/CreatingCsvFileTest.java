package com.jbcc.denbunCsv.creating;

import org.junit.Test;

public class CreatingCsvFileTest {

	@Test
	public void test() {
//		String inputPath = "/Users/tyoshida/Desktop/�d��/�_��/20120409_����/20120409_����/�d���d�l��/01100_�R���[�쐬/0110050_5�͓d���݌v/0110051_1502121�d���d�l.xls";
		String inputPath = "/Users/tyoshida/Desktop/�d��/�_��/20120409_����/20120409_����/�d���d�l��/";
		
		String outputPath = "/Users/tyoshida/desktop/�d��/�o�͐�/";
		
		
		CreatingCsvFile.main(new String[] {inputPath,outputPath});
	}

}
