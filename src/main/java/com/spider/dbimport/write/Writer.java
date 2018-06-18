package com.spider.dbimport.write;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.spider.dbimport.DataSet;
import com.spider.dbimport.JdbcUtils;

public class Writer {
	
	static Logger logger = LogManager.getLogger("Writer");
	
	public static String buildInsertSql(TreeSet<String> columnNames, String table) {

		String comma = "";
		String insertSql = "INSERT INTO " + table + " (";
		String placeholderSql = "";
		for(String column: columnNames) {
			insertSql += comma + "["+column+"]";
			placeholderSql += comma + "?";
			comma = ",";
		}
		
		return insertSql + ") VALUES(" + placeholderSql + ")";
	}
	
	public static void write(String jdbcUrl, String sql, DataSet ds, TreeSet<String> columnNames, Map<String,String> columnTypes, String table) throws Exception {
		
		List<Object[]> allValues = new ArrayList<>();
		for(Map<String,Object> row: ds.data) {
			//the only columns in the spreadsheet that we care about are those that have exact name matches in the db table
			Object[] rowArgs = new Object[columnNames.size()];
			int i = 0;
			for(String column: columnNames) {
				rowArgs[i++] = row.get(column);
			}

			allValues.add(rowArgs);
		}
		
		List<Object[]> valuesForBatchInsert = new ArrayList<>();
		for(int i = 0; i < allValues.size(); i++) {
			valuesForBatchInsert.add(allValues.get(i));
			if(i > 0 && i%10000==0) {
				//after every 10000 records, do an insert
				logger.info("Inserting 10K records out of " + allValues.size());
				JdbcUtils.batchInsert(jdbcUrl, sql, columnNames, valuesForBatchInsert);
				valuesForBatchInsert.clear();
			}
		}
		if(!valuesForBatchInsert.isEmpty()) {
			
			//finally, insert any remaining records
			logger.info("Final insert");
			JdbcUtils.batchInsert(jdbcUrl, sql, columnNames, valuesForBatchInsert);
		}
		

		logger.info("Ignoring data source columns:");
		for(String column: ds.columns) {
			if(!columnNames.contains(column)) {
				logger.info(column);
			}
		}
	}

}
