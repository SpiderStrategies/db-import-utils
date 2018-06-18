package com.spider.dbimport.read;

import java.io.IOException;
import java.io.InputStream;

import com.spider.dbimport.DataSet;

public abstract class AbstractReader {
	
	InputStream inputStream;

	public AbstractReader(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public abstract DataSet read() throws IOException;

	public abstract String getType();

}
