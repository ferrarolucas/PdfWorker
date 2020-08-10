package com.pdfutil.util;

import java.io.FileOutputStream;

public class FileHelper {

	public static void write(String filePath, byte[] content) {
		if(content != null && filePath != null) {
			try {
				FileOutputStream fos = new FileOutputStream(filePath);
				fos.write(content);
				fos.close();
				
			} catch (Exception e) {
				System.err.println("Ocorreu um erro ao escrever o arquivo " + filePath);
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
