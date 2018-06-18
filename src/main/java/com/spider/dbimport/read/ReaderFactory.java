package com.spider.dbimport.read;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ReaderFactory {
	
	public static AbstractReader createReader(String path) throws FileNotFoundException {
		FileInputStream is = new FileInputStream(path);
		if(path.toLowerCase().endsWith("csv") || path.toLowerCase().endsWith("txt") || path.toLowerCase().endsWith(".cfg")) {
			return new CSVFileReader(is);
		}
		return new ExcelFileReader(is);
	}
	
	public static AbstractReader createConfigFileReader(String path) throws FileNotFoundException {
		return new CSVFileReader(ReaderFactory.class.getClassLoader().getResourceAsStream(path));
	}

}
