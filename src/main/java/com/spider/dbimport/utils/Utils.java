package com.spider.dbimport.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class Utils {
	
	public static void writeFile(String content, String file) throws Exception {
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.close();
	}
	
	public  static String readFileAsString(String path) throws Exception {
		StringBuilder sb = new StringBuilder();
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		while((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		fr.close();
		return sb.toString();
	}

}
