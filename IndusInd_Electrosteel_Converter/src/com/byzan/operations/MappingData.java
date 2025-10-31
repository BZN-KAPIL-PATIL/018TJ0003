package com.byzan.operations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.byzan.utils.BZNUtils;
import com.byzan.utils.MyLogger;
import com.byzan.utils.PathConstant;

public class MappingData implements PathConstant{

	public static ArrayList<ArrayList<String>> dataMapping(ArrayList<ArrayList<String>> fileData, ArrayList<ArrayList<String>> validationFileData, File inputFile)
	{

		ArrayList<ArrayList<String>> mappedData = new ArrayList<>();
		try
		{
			for(int i =1; i < fileData.size() ;i++)
			{
				ArrayList<String> rowData = fileData.get(i);
				ArrayList<String> SingleMappedData = new ArrayList<>();
				int position = 0;


				//CUSTOMER CODE
				SingleMappedData.add(validationFileData.get(1).get(6));

				//BUYER CODE
				position = Integer.parseInt(validationFileData.get(2).get(5))-1;
				SingleMappedData.add(rowData.get(position));

				//AMOUNT
				position = Integer.parseInt(validationFileData.get(3).get(5))-1;
				DecimalFormat df2 = new DecimalFormat("0.00");
				String amount1 = df2.format(Double.parseDouble(rowData.get(position).replace(",", "")));
				//String amount  = amount1.replace(".", "");
				System.out.println(amount1);
				SingleMappedData.add(amount1);

				//DATE
				position = Integer.parseInt(validationFileData.get(4).get(5))-1;
				Date df = null;
				try {
					df = new SimpleDateFormat(InputFileDateFormat).parse(rowData.get(position).replace(",", ""));
					
					   // Convert to "dd/MM/yyyy" format
					String formattedDate  = new SimpleDateFormat(outputFileDateFormat).format(df);

				    // If you want to replace the value in rowData
					SingleMappedData.add(formattedDate);
				} catch (ParseException e) {
					SingleMappedData.clear();
					MyLogger.error("Error while parsing date format kindly check the date format in input file");
					MyLogger.info(inputFile.getName()+"  moved to failure folder");
					try {
						BZNUtils.moveFile(INPUT+inputFile.getName(), FAILURE+inputFile.getName());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					e.printStackTrace();
				}

				//UTR
				position = Integer.parseInt(validationFileData.get(5).get(5))-1;
				SingleMappedData.add(rowData.get(position));

				//ECL ACCOUNT NUMBER
				position = Integer.parseInt(validationFileData.get(6).get(5))-1;
				SingleMappedData.add(rowData.get(position));

				//REMITTER IFSCCODE
				position = Integer.parseInt(validationFileData.get(7).get(5))-1;
				SingleMappedData.add(rowData.get(position));

				//8	REMITTER Account Number
				position = Integer.parseInt(validationFileData.get(8).get(5))-1;
				SingleMappedData.add(rowData.get(position));

				//9	REMITTER NAME
				position = Integer.parseInt(validationFileData.get(9).get(5))-1;
				SingleMappedData.add(rowData.get(position));

				//10	PAYMENT PRODUCT CODE
				position = Integer.parseInt(validationFileData.get(10).get(5))-1;
				SingleMappedData.add(rowData.get(position));

				//11	BENEFICIARY BANK CODE
				SingleMappedData.add(validationFileData.get(11).get(6));

				// 12 profit centre (7 chars from position 7)
				position = Integer.parseInt(validationFileData.get(12).get(5)) - 1;
				SingleMappedData.add(rowData.get(position).substring(6, 13));

				// 13 RC (next 2 chars)
				position = Integer.parseInt(validationFileData.get(13).get(5)) - 1;
				SingleMappedData.add(rowData.get(position).substring(13, 15));

				// 14 Spl GL (next 1 char)
				position = Integer.parseInt(validationFileData.get(14).get(5)) - 1;
				SingleMappedData.add(rowData.get(position).substring(15, 16));

				// 15 Doc type as per location (next 2 chars)
				position = Integer.parseInt(validationFileData.get(15).get(5)) - 1;
				SingleMappedData.add(rowData.get(position).substring(16, 18));

				// 16 CMS Code (next 4 chars)
				position = Integer.parseInt(validationFileData.get(16).get(5)) - 1;
				SingleMappedData.add(rowData.get(position).substring(18, 22));
				
				if(SingleMappedData.size() > 0 && SingleMappedData!=null)
				{
					mappedData.add(SingleMappedData);
				}
			}
		}catch(Exception e)
		{
			mappedData.clear();
			MyLogger.error("Error while mapping data from input file ");
			MyLogger.info("File  moved to failure folder");
			e.printStackTrace();
			try {
				BZNUtils.moveFile(INPUT+inputFile.getName(), FAILURE+inputFile.getName());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		}
		return mappedData;

	}
}
