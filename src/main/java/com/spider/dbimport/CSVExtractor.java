package com.spider.dbimport;

import com.spider.crypt.Decryptor;
import com.spider.dbimport.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CSVExtractor {

	public static void main(String[] args) throws Exception {
		String dateFormat = System.getProperty("dateFormat");
		DateFormat df = new SimpleDateFormat(dateFormat);
		String aesKey = args[0];
		String jdbcUrl = args[1];
		String sqlPassword = new Decryptor(aesKey).doWork(args[2]);
		jdbcUrl += sqlPassword;
		String sqlScriptFile = args[3];
		String sql = determineSQLToRun(sqlScriptFile);
		List<Map<String,Object>> results = JdbcUtils.createJdbcTemplate(jdbcUrl).queryForList(sql);

		FileWriter fw = new FileWriter(new File(args[4]));
		Set<String> headers = results.get(0).keySet();

		fw.write("".join(",", headers));
		fw.write("\n");

		for(Map<String,Object> row: results) {
			StringBuilder sb = new StringBuilder();
			String comma = "";
			for(String header: headers) {
				sb.append(comma);
				if(header.toLowerCase().contains("date")) {
					sb.append(df.format((row.get(header))));
				}
				else {
					String colVal = (String) row.get(header);
					if(colVal != null) {
						colVal = colVal.replaceAll("\"", "");
						sb.append("\"" + colVal + "\"");
					}
				}
				comma = ",";
			}
			sb.append("\n");
			fw.write(sb.toString());
		}
		fw.close();
	}
	
	public static String determineSQLToRun(String filePath) throws Exception {
		return Utils.readFileAsString(filePath).trim();
	}
}
