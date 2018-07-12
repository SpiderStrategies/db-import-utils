package com.spider.dbimport.clean;

import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.spider.dbimport.DataSet;

public class DataSetCleaner {
	
	DataSet cleanConfig;
	static Logger logger = LogManager.getLogger("DataSetCleaner");
	
	public DataSetCleaner(DataSet cleanConfig) {
		this.cleanConfig = cleanConfig;

		logger.info("Loading rules:\n");
		for(Map<String,Object> cleanConfigItem: cleanConfig.data) {
			if(cleanConfigItem.get("value").equals("null")) {
				//if the value specified in the "value" column of the cleaning rules is "null", 
				//interpret it as the value null, not the string "null"
				cleanConfigItem.put("value", null);
			}
			logger.info(cleanConfigItem);
		}
	}
	
	public void clean(DataSet ds) {
		for(Map<String,Object> row: ds.data) {
			//for each row of the data set check out the cleanConfig to see what values to transform
			for(String key: row.keySet()) {
				for(Map<String,Object> cleanConfigItem: cleanConfig.data) {
					//DataSet objects store column names as lowercase for comparison in other areas of this project, thus, toLowerCase()
					String lowercaseFieldNameRegex = ((String) cleanConfigItem.get("fieldnameregex")).toLowerCase();
					if(key.matches(lowercaseFieldNameRegex)) {
						//this data item key matches the current cleanconfigitem's fieldnameregex, we may need to transform the value
						Object value = row.get(key);
						if(value != null 
								&& value instanceof String 
								&& ((String) value).matches((String) cleanConfigItem.get("fieldvalueregex"))) {
							row.put(key, cleanConfigItem.get("value"));
						}
					}
				}
			}
		}
	}
}
