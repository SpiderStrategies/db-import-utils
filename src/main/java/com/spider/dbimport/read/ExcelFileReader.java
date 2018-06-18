package com.spider.dbimport.read;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.spider.dbimport.DataSet;
	
public class ExcelFileReader extends AbstractReader {

	public ExcelFileReader(InputStream is) {
		super(is);
	}

	public DataSet read() throws IOException {
		try {
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet firstSheet = workbook.getSheetAt(0);

			DataSet ds = new DataSet();
			Row row1 = firstSheet.getRow(0);
			Iterator<Cell> row1CellIterator = row1.cellIterator();
			while (row1CellIterator.hasNext()) {
				Cell cell = row1CellIterator.next();
				ds.columns.add(cell.getStringCellValue().trim().toLowerCase());
			}

			Iterator<Row> iterator = firstSheet.iterator();
			iterator.next();//skip the first row

			while (iterator.hasNext()) {
				Map<String,Object> rowData = new HashMap<>();
				Row nextRow = iterator.next();

				for (int i = 0; i < ds.columns.size(); i++) {
					Cell cell = nextRow.getCell(i);
					rowData.put(ds.columns.get(i), cell != null ? getCellValue(cell) : null);
				}
				ds.data.add(rowData);
			}

			workbook.close();
			return ds;
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	private Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case Cell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue();
			case Cell.CELL_TYPE_NUMERIC:
				return getNumericCellValue(cell);
		}
		return null;
	}
	
	private Object getNumericCellValue(Cell cell) {
		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			 return cell.getDateCellValue();
		}
		else {
			NumberFormat format = NumberFormat.getIntegerInstance();
			format.setGroupingUsed(false);
			return format.format(cell.getNumericCellValue());
		}
	}

	public String getType() {
		return "Excel";
	}
}