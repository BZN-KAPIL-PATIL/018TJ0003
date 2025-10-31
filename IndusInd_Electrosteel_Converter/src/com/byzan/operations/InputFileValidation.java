package com.byzan.operations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.byzan.utils.BZNUtils;
import com.byzan.utils.MyLogger;
import com.byzan.utils.PathConstant;

public class InputFileValidation implements PathConstant{

	public ArrayList<ArrayList<String>> validation(ArrayList<ArrayList<String>> mappedData, ArrayList<ArrayList<String>> validationFileData, File inputFile)
	{

		ArrayList<ArrayList<String>> validatedData = new ArrayList<>();
		try
		{

			block:
				for(int i =0 ; i <mappedData.size();i++)
				{
					ArrayList<String> singleData = new ArrayList<>();
					ArrayList<String> rowData = mappedData.get(i);

					for(int j =1; j <validationFileData.size(); j++)
					{
						ArrayList<String> validationSingleRow=validationFileData.get(j);

						String mandateOptional   = validationSingleRow.get(4);
						int maxlength            = Integer.parseInt(validationSingleRow.get(3).replace(",", ""));
						String dataType          = validationSingleRow.get(2);						
						String specialCharRemove = validationSingleRow.get(9);

						boolean dataTypeFlag = false;
						String Data_To_Validate=rowData.get(j-1).trim();

						if(mandateOptional.equalsIgnoreCase("M"))
						{
							if(!Data_To_Validate.isEmpty())
							{
								if(specialCharRemove.equalsIgnoreCase("Y"))
								{
									Data_To_Validate = Data_To_Validate.replaceAll("[^a-zA-Z0-9]", "");
								}

								dataTypeFlag =  checkDataType(Data_To_Validate,dataType);
								if(dataTypeFlag==true)
								{
									if(validationSingleRow.get(1).equalsIgnoreCase("Amount"))
									{
										maxlength =+3; // 2 char for double and one char for . // 123.00
									}
									if(Data_To_Validate.length()>maxlength)
									{
										Data_To_Validate = Data_To_Validate.substring(0,maxlength);
									}
									
									singleData.add(Data_To_Validate);
								}else
								{
									validatedData.clear();
									singleData.clear();
									MyLogger.error("Data type for field '"+validationSingleRow.get(1)+"' is not "+ dataType+"for UTR number -"+rowData.get(4));
									try {
										BZNUtils.moveFile(INPUT+inputFile.getName(), FAILURE+inputFile.getName());
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									MyLogger.error(inputFile.getName() +" File moved to failure folder");
									break block;
								}	

							}else
							{
								validatedData.clear();
								singleData.clear();
								try {
									BZNUtils.moveFile(INPUT+inputFile.getName(), FAILURE+inputFile.getName());
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								MyLogger.error("Mandatory field '"+validationSingleRow.get(1)+"' is empty for UTR  number -"+rowData.get(4));
								MyLogger.error(inputFile.getName() +" File moved to failure folder");
								break block;
							}
						}else if(mandateOptional.equalsIgnoreCase("O"))
						{
							if(specialCharRemove.equalsIgnoreCase("Y"))
							{
								Data_To_Validate = Data_To_Validate.replaceAll("[^a-zA-Z0-9]", "");
							}

							dataTypeFlag =  checkDataType(Data_To_Validate,dataType);
							if(dataTypeFlag==true)
							{
								if(validationSingleRow.get(1).equalsIgnoreCase("Amount"))
								{
									maxlength =+3; // 2 char for double and one char for . // 123.00
								}
								if(Data_To_Validate.length()>maxlength)
								{
									Data_To_Validate = Data_To_Validate.substring(0,maxlength);
								}
								
								singleData.add(Data_To_Validate);
							}else
							{
								validatedData.clear();
								singleData.clear();
								MyLogger.error("Data type for field '"+validationSingleRow.get(1)+"' is not "+ dataType+"for UTR number -"+rowData.get(4));
								try {
									BZNUtils.moveFile(INPUT+inputFile.getName(), FAILURE+inputFile.getName());
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								MyLogger.error(inputFile.getName() +" File moved to failure folder");
								break block;
							}	

						}

					}
					validatedData.add(singleData);
				}
		}catch(Exception e)
		{
			
			validatedData.clear();
			MyLogger.error("Error while validating  data from input file ");
			MyLogger.info("File  moved to failure folder");
			e.printStackTrace();
			try {
				BZNUtils.moveFile(INPUT+inputFile.getName(), FAILURE+inputFile.getName());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return validatedData;

	}

	private boolean checkDataType(String data_To_Validate, String dataType) {

		boolean dataFlag = false;
		if(dataType.equalsIgnoreCase("A"))
		{
			dataFlag = BZNUtils.isAlpha(data_To_Validate);
		}else if(dataType.equalsIgnoreCase("AN"))
		{
			dataFlag = BZNUtils.isAlphanumeric(data_To_Validate);

		}else if(dataType.equalsIgnoreCase("N"))
		{
			dataFlag = BZNUtils.isNum(data_To_Validate);
		}

		return dataFlag;
	}
}
