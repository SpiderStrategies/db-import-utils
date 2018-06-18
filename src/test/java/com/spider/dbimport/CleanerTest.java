package com.spider.dbimport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;

import com.spider.dbimport.clean.DataSetCleaner;
import com.spider.dbimport.read.CSVFileReader;
import com.spider.dbimport.read.ReaderFactory;

public class CleanerTest {

	@Test
	public void test() throws Exception {
		DataSet ds = ReaderFactory.createReader("src/test/java/GuitarSales.xlsx").read();
		FileInputStream is = new FileInputStream("src/test/java/rules.cfg");
		DataSet cleaningData = new CSVFileReader(is).read();
		DataSetCleaner cleaner = new DataSetCleaner(cleaningData);
		cleaner.clean(ds);
		assertNull(ds.data.get(3).get("ESP LTD EC-203"));
	}

}
