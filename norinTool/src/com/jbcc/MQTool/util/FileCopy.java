package com.jbcc.MQTool.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * ファイルコピークラス.
 *
 */
public class FileCopy {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileCopy copy = new FileCopy();
		copy.copy("/Users/kohgami/tmp/mail.txt", "/Users/kohgami/tmp/mail.txt2");
	}

	public void copy(String srcfile, String destfile) throws IOException {
		
		@SuppressWarnings("resource")
		FileChannel srcChannel = new FileInputStream(srcfile).getChannel();
		@SuppressWarnings("resource")
		FileChannel destChannel = new FileOutputStream(destfile).getChannel();
		try {
			srcChannel.transferTo(0, srcChannel.size(), destChannel);
		} finally {
			srcChannel.close();
			destChannel.close();
		}
	}
}
