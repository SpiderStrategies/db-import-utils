package com.spider.dbimport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.spider.crypt.Decryptor;

import com.spider.dbimport.utils.Utils;

public class SQLRunner {
	
	static Logger logger = LogManager.getLogger("SQLRunner");

	public static void main(String[] args) throws Exception {
		String aesKey = args[0];
		String jdbcUrl = args[1];
		String sqlPassword = new Decryptor(aesKey).doWork(args[2]);
		jdbcUrl += sqlPassword;
		String sqlScriptFile = args[3];
		String[] sqlStatements = determineSQLToRun(sqlScriptFile);
		
		for(String stmt: sqlStatements) {
			//one SQL statement might have multiple lines
			String[] sqlLines = stmt.split("\n");
			StringBuilder sb = new StringBuilder();
			for(String sqlLine: sqlLines) {
				//ignore lines starting with --
				if(!sqlLine.startsWith("--")) {
					//for logging purposes add newline chars
					sb.append(sqlLine + "\n");
				}
			}
			logger.info("Running sql: " + sb.toString());
			JdbcUtils.run(sb.toString(), jdbcUrl);
		}
	}
	
	public static String[] determineSQLToRun(String filePath) throws Exception {
		String input = Utils.readFileAsString(filePath);
		input = input.replaceAll("--.*\n", "");//ignore lines beginning with --
		String[] output = input.split(";");//each sql command separated by semicolons
		return output;
	}
}
