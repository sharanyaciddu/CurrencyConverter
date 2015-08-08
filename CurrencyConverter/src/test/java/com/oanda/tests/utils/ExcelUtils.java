/**
 * @author sharanya
 */
package com.oanda.tests.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Utility methods that help with Excel files.
 * 
 * @author sharanya
 *
 */
public class ExcelUtils {

	/**
	 * Reads and returns data from the excel file from sheet sheetName. If
	 * sheetName is null, it reads the first sheet.
	 * 
	 * @param excelFilename
	 * @param sheetName
	 * @return
	 * @throws Exception
	 * @author sharanya
	 */
	public static List<List<HSSFCell>> readDataFromFile(String excelFilename,
			String sheetName) throws Exception {
		List<List<HSSFCell>> workSheetData = new ArrayList<List<HSSFCell>>();

		FileInputStream fis = null;
		try {
			//
			// Create a FileInputStream that will be use to read the
			// excel file.
			//
			fis = new FileInputStream(excelFilename);

			//
			// Create an excel workbook from the file system.
			//
			HSSFWorkbook workbook = new HSSFWorkbook(fis);

			HSSFSheet sheet;
			// If the sheet name is null, get the first sheet.
			if (sheetName == null) {
				sheet = workbook.getSheetAt(0);
			} else {
				sheet = workbook.getSheet(sheetName);
			}

			//
			// When we have a sheet object in hand we can use iterator on
			// each sheet's rows and on each row's cells. We store the
			// data read on an ArrayList so that we can printed the
			// content of the excel to the console.
			//
			Iterator<?> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				HSSFRow row = (HSSFRow) rows.next();
				Iterator<?> cells = row.cellIterator();

				List<HSSFCell> data = new ArrayList<HSSFCell>();
				while (cells.hasNext()) {
					HSSFCell cell = (HSSFCell) cells.next();
					data.add(cell);
				}

				workSheetData.add(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return workSheetData;
	}

	// This method creates a file name with the following format
	// ScreenShot/Date/time_classname_testname.png
	// ScreenShot is a folder
	// Date is a folder
	// time_classname_testname.png is a file
	// Date format is yyyyMMdd
	// time format is HHmmssSSS
	// className and methodName special characters ".][" are replaced with "_"
	
	public static String getScreenShotFileName(String className, String methodName) {
		DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		DateFormat dateFormat1 = new SimpleDateFormat("HHmmssSSS");
		String now = dateFormat1.format(new Date());
		String today = dateFormat2.format(new Date());
		String fileName;
		System.out.println("Method Name:" + methodName);
		System.out.println("Class Name:" + className);
		if (methodName != null)
			fileName = className + "." + methodName;
		else
			fileName = className;
		fileName = "target/Screenshots/" + today + "/" + now + '_' 
				+ fileName.replaceAll("\\.|\\[|\\]", "_") + ".png";
		System.out.println("name:" + fileName);
		return fileName;
	}

	
	/**
	 * Overloaded method that will pass sheetname as null (Reads from the first
	 * sheet).
	 * 
	 * @param excelFileName
	 * @return
	 * @throws Exception
	 * @author sharanya
	 */
	public static List<List<HSSFCell>> readDataFromFile(String excelFileName)
			throws Exception {
		return readDataFromFile(excelFileName, null);
	}
}
