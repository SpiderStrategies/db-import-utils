package com.spider.dbimport;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JdbcUtils {
	
	static Logger logger = LogManager.getLogger("JdbcUtils");
	
	static {
		try {
			//for the time being we're just using SQL Server
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}
		catch(Exception e) {
			logger.error("Error loading SQL Server driver", e);
			System.exit(0);
		}
	}
	
	public static void deleteExisting(String table, String jdbcUrl) {
		JdbcUtils.run("DELETE FROM " + table, jdbcUrl);
	}

	public static void getDbTableColumns(String jdbcUrl, String table, TreeSet<String> dbTableColumnNames, Map<String,String> dbTableColumnTypes) throws Exception {
		Connection conn = getConnection(jdbcUrl);
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet rs = meta.getColumns(null,  null, table, null);
		while(rs.next()) {
			dbTableColumnNames.add(rs.getString("COLUMN_NAME").toLowerCase());
			dbTableColumnTypes.put(rs.getString("COLUMN_NAME").toLowerCase(),rs.getString("TYPE_NAME").toLowerCase());
		}
		conn.close();
	}
	
	public static void run(String sql, String jdbcUrl) {
		createJdbcTemplate(jdbcUrl).execute(sql);
	}
	
	public static void batchInsert(String jdbcUrl, String sql, TreeSet<String> columnNames, List<Object[]> args) throws Exception {
		JdbcTemplate jt = createJdbcTemplate(jdbcUrl);
		try {
			jt.batchUpdate(sql, args);
		}
		catch(Exception e) {
			logger.error("Error executing update", e);

			if(e.getMessage().contains("String or binary data would be truncated")) {
				//when doing a batch update and you receive an error regarding data being truncated, you have no idea
				//which row of your batch actually blew things up.  The following forces your import to go through
				//1 at a time and print out the actual failing record when that single insert blows up
				for(Object[] rowArgs: args) {
					try {
						jt.update(sql, rowArgs);
					}
					catch(Exception e2) {
						for(Object rowArg: rowArgs) {
							logger.info(rowArg);
						}

					}
				}
			}

			System.exit(0);
		}
	}
	
	public static JdbcTemplate createJdbcTemplate(String jdbcUrl) {
		DriverManagerDataSource ds = new DriverManagerDataSource(jdbcUrl);
		return new JdbcTemplate(ds);
	}
	
	private static Connection getConnection(String jdbcUrl) {
		try {  
			return DriverManager.getConnection(jdbcUrl);  
		}  
		catch (Exception e) {  
			e.printStackTrace();  
		} 
		return null;
	}
}
