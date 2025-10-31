package com.byzan.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.byzan.utils.BZNUtils;
import com.byzan.utils.MyLogger;

public class ReadValidationFile {

	
	public static ArrayList<ArrayList<String>> validationFileReading(String validationfilepath)
	{
		ArrayList<ArrayList<String>> inPayFileData = new ArrayList<>();
		BufferedReader br = null;
		String line = null;
		

		try
		{
			br = new BufferedReader(new FileReader(validationfilepath));
			while((line = br.readLine())!= null)
			{
				if(!line.trim().isEmpty())
				{
					String[] splitValues = line.split(",");
					List<String> data = Arrays.asList(splitValues);
					ArrayList<String> data1=new ArrayList<String>(data);
					
					
					inPayFileData.add(data1);	
				}
				

			}

		}catch(Exception e)
		{
			e.printStackTrace();MyLogger.error(e.toString());	;
			MyLogger.error("Error in validation file reading "+e.toString());		
			inPayFileData.clear();
		}finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();MyLogger.error(e.toString());	;
			}
		}

		return inPayFileData;


	}
}
