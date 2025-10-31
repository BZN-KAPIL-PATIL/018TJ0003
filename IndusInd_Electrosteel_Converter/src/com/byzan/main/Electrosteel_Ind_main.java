package com.byzan.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.byzan.operations.CreateFolder;
import com.byzan.operations.InputFileValidation;
import com.byzan.operations.InputFileWritting;
import com.byzan.operations.MappingData;
import com.byzan.operations.ReadInputFile;
import com.byzan.operations.ReadValidationFile;
import com.byzan.operations.SFTPUploadDecrypt;
import com.byzan.utils.BZNUtils;
import com.byzan.utils.MyLogger;
import com.byzan.utils.PathConstant;



public class Electrosteel_Ind_main implements PathConstant{

	public static void main(String[] args) {
		
		
		if (!AuditLog.isEmpty()) 
		{
			MyLogger.createLogFile(AuditLog);
		}
		else 
		{
			MyLogger.error("Path for the Logs in properties file not found.");
		}
		// TODO Auto-generated method stub
		MyLogger.info("!----------------------Application Started-------------------!");
		Electrosteel_Ind_main main = new Electrosteel_Ind_main();
		main.run();

		///12345678901234567890123456789012
	}

	private void run() {
		// TODO Auto-generated method stub

		int folderReturnValue = CreateFolder.FolderCreation(); // create all required folder

		if(folderReturnValue ==1)
		{
			File file = new File(INPUT);
			File[] files = file.listFiles();

			if(files.length > 0)
			{
				ArrayList<ArrayList<String>> validationFileData=  ReadValidationFile.validationFileReading(ValidationFilePath);
				if(validationFileData.size() >0)
				{

					for(File inputFile :files)
					{
						MyLogger.info("Started processing input file : "+inputFile.getName());
						// reading input file
						ArrayList<ArrayList<String>> fileData1 = ReadInputFile.readExcelFile(inputFile);
						if(fileData1.size() >0 && fileData1!=null)
						{
							ArrayList<ArrayList<String>> MappedData =	MappingData.dataMapping(fileData1, validationFileData,inputFile);
							if(MappedData.size() > 0 && MappedData !=null)
							{
								InputFileValidation v = new InputFileValidation();
								ArrayList<ArrayList<String>> validatedData = v.validation(MappedData,validationFileData,inputFile);

								if(validatedData.size() > 0)
								{
									//System.out.println(validatedData.size());
									InputFileWritting w = new InputFileWritting();
									String outputFileName = w.fileWritting(validatedData,inputFile);
									if(outputFileName!=null)
									{
										MyLogger.info("Output file written successfully "+outputFileName);
										try {
											

											SFTPUploadDecrypt su = new SFTPUploadDecrypt();	
											boolean uploadFlag = su.uploadAndDecrypt(OUTPUT+outputFileName);
											if(uploadFlag == true)
											{
												BZNUtils.moveFile(INPUT+inputFile.getName(), SUCCESS+inputFile.getName());
											}
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								}
							}

						}else
						{
							try {
								BZNUtils.moveFile(INPUT+inputFile.getName(), FAILURE+inputFile.getName());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}else
			{
				MyLogger.info("Input folder does not contain any file to process");
			}
		}else
		{
			MyLogger.error("Folder creation failed");
		}

	}

}
