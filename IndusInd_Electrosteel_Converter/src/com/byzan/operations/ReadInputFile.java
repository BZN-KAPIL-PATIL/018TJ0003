package com.byzan.operations;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.byzan.utils.MyLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class ReadInputFile {

	
	 public static ArrayList<ArrayList<String>> readExcelFile(File inputFile) {
	        ArrayList<ArrayList<String>> data = new ArrayList<>();
	        FileInputStream fis = null;
	        Workbook workbook = null;

	        try {
	        	 fis = new FileInputStream(inputFile);
	             workbook = new XSSFWorkbook(fis);

	             Sheet sheet = workbook.getSheetAt(0);
	             DataFormatter formatter = new DataFormatter(); 

	             for (Row row : sheet) {
	                 ArrayList<String> rowData = new ArrayList<>();
	                 boolean isRowEmpty = true;

	                 for (Cell cell : row) {
	                     String value = formatter.formatCellValue(cell).trim();

	                     if (!value.isEmpty()) {
	                         isRowEmpty = false;
	                     }
	                     rowData.add(value);
	                 }

	                 if (!isRowEmpty) {
	                     data.add(rowData);
	                 }
	             }

	        } catch (IOException e) {
	            MyLogger.error("Error reading Excel file: " + inputFile +"  ERRROR : "+e.toString());
	        } finally {
	            try {
	            	   if (workbook != null) {
	                       try {
	                           workbook.getClass().getMethod("close").invoke(workbook);
	                       } catch (NoSuchMethodException ignore) {
	                           // method not available in old POI, ignore
	                       } catch (Exception e) {
	                    	   MyLogger.error( "Error closing workbook ERRROR : "+e.toString() );
	                       }
	                   }
	                if (fis != null) {
	                    fis.close();
	                }
	            } catch (IOException e) {
	            	 MyLogger.error("Error closing Excel resources ERRROR : "+e.toString());
	            }
	        }
	        return data;
	    }
}
