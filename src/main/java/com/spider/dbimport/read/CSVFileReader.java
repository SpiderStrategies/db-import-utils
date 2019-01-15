package com.spider.dbimport.read;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import com.spider.dbimport.DataSet;

public class CSVFileReader extends AbstractReader {
	
	static Logger logger = LogManager.getLogger("CSVFileReader");
	
	String dateFormat = System.getProperty("dateFormat");

	public CSVFileReader(InputStream is) {
		super(is);
	}

	public DataSet read() throws IOException {
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(inputStream), ',');
			DataSet ds = new DataSet();
			List<String[]> allRows = reader.readAll();

			List<String> columnNames = new ArrayList<>();
			for (String columnName : allRows.get(0)) {
				columnNames.add(columnName.trim().toLowerCase());
			}
			ds.columns.addAll(columnNames);

			for (int i = 1; i<allRows.size(); i++) {
				Map<String, Object> rowData = new HashMap<>();
				String[] csvRowData = allRows.get(i);
				for (int j = 0; j<csvRowData.length; j++) {
					String csvRowColumnValue = csvRowData[j];
					rowData.put(ds.columns.get(j), csvRowColumnValue);
				}
				ds.data.add(rowData);
			}
			return ds;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	public void formatValues(Map<String,String> columnTypes, DataSet ds) {
		for(Map<String,Object> row: ds.data) {
			for(String key: row.keySet()) {
				row.put(key, getValue(row.get(key), key, columnTypes.get(key)));
			}
		}
	}
	
	private Object getValue(Object value, String columnName, String columnType) {
		if(value == null || columnType == null) {
			//if the value is null, or if the column doesn't exist in the table we're pushing to
			return null;
		}
		try {
			String strVal = "" + value.toString();
			if(columnType.contains("char") || columnType.contains("text")) {
				return strVal;
			}
			else if(columnType.contains("date") && !"".equals(strVal)) {
				return new SimpleDateFormat(dateFormat).parse(strVal);
			}
			else if(columnType.contains("bit")) {
				return Boolean.getBoolean(strVal);
			}
			else {
				//it's a numeric column type in the db, but we don't care about dollar symbols or commas from the CSV
				strVal = strVal.replaceAll("\\$", "");//remove dollar symbols
				strVal = strVal.replaceAll(",", "");//remove commas
				return "".equals(strVal) || "undefined".equals(strVal) ? null : Double.parseDouble(strVal);
			}
		}
		catch(Exception e) {
			logger.error("Couldn't parse value. Column:" + columnName + ", Value:" + value, e);
			return null;
		}
	}
	
	public String getType() {
		return "CSV";
	}

}
