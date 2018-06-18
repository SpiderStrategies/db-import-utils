package com.spider.dbimport;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.spider.crypt.Decryptor;

import com.spider.dbimport.clean.DataSetCleaner;
import com.spider.dbimport.read.AbstractReader;
import com.spider.dbimport.read.CSVFileReader;
import com.spider.dbimport.read.ReaderFactory;
import com.spider.dbimport.write.Writer;

public class Main {
	
	static Logger logger = LogManager.getLogger("Main");

	public static void main(String[] args) throws Exception {
		String aesKey = args[0];
		String jdbcUrl = args[1];
		String sqlPassword = new Decryptor(aesKey).doWork(args[2]);
		jdbcUrl += sqlPassword;
		String table = args[3];
		String sourceFilePath = args[4];
		boolean deleteAllBeforeInsert = args.length < 6 || "true".equals(args[5]); //if blank or if "true" we delete all existing records
		DataSet cleaningData = ReaderFactory.createConfigFileReader("rules.cfg").read();
		
		TreeSet<String> dbTableColumnNames = new TreeSet<>();
		Map<String,String> dbTableColumnTypes = new HashMap<>();
		
		JdbcUtils.getDbTableColumns(jdbcUrl, table, dbTableColumnNames, dbTableColumnTypes);
		
		AbstractReader reader = ReaderFactory.createReader(sourceFilePath);
		DataSet ds = reader.read();
		if(reader.getType().equals("CSV")) {
			//CSV files don't have dates formatted as dates, etc, so do asses db column types and format values
			((CSVFileReader) reader).formatValues(dbTableColumnTypes, ds);
		}
		
		DataSetCleaner cleaner = new DataSetCleaner(cleaningData);
		cleaner.clean(ds);

		if(deleteAllBeforeInsert) {
			//delete all existing records by default, unless last program arg was "false"
			logger.info("Deleting existing records");
			JdbcUtils.deleteExisting(table, jdbcUrl);
		}
		else {
			logger.info("Appending to existing records");
		}
		
		logger.info("Removing irrelevant db columns");
		removeIrrelevantColumns(dbTableColumnNames, ds);
		
		logger.info("Writing new records");
		String insertSql = Writer.buildInsertSql(dbTableColumnNames, table);
		Writer.write(jdbcUrl, insertSql, ds, dbTableColumnNames, dbTableColumnTypes, table);
	}
	
	private static void removeIrrelevantColumns(TreeSet<String> dbTableColumnNames, DataSet ds) {
		TreeSet<String> columnsToRemove = new TreeSet<>();
		for(String dbColumn: dbTableColumnNames) {
			if(!ds.columns.contains(dbColumn)) {
				logger.info("Ignoring db column: " + dbColumn);
				columnsToRemove.add(dbColumn);
			}
		}
		dbTableColumnNames.removeAll(columnsToRemove);
	}
}
