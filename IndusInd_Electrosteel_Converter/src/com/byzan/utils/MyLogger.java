package com.byzan.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLogger {

	static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");//logger output filename
	static DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");//logger date
	static String slashChar=null;
	static String fileName=null;
	static String fileDirectoryPath=null;
	static PrintWriter pw =null;
	static File fileWriter=null;
	static String formattedDate =null;
	static String message=null;
	

	public static void createLogFile(String fullFilePath){
		slashChar=checkPathforSlash(fullFilePath);
		String[] getfileName=fullFilePath.split(slashChar);
		fileName = getfileName[getfileName.length-1];
		fileDirectoryPath=getDirectoryPathExceptFileName(getfileName);
		fileWriter=new File(fileDirectoryPath);

		formattedDate = sdf.format(new Date());

		generateOutputFile("a","b"); //dummy value a and b are passed
	}


	private static void generateOutputFile(String logType,String logMsg) {		
		
		String outputFileName=null;	
		formattedDate = sdf.format(new Date());
		
		try {
			if(!fileWriter.exists()){
				fileWriter.mkdirs();
				outputFileName=getOutputFileName(fileName);	
			}else{
				outputFileName=getOutputFileName(fileName);	
			}
			
			FileWriter writer = new FileWriter(fileWriter+slashChar+outputFileName, true);
			pw = new PrintWriter(writer);

			if(logType.equals("info") || logType.equals("error") || logType.equals("debug") || logType.equals("warn")){
				if(logType.equals("info")){
					pw.println("INFO   "+ dateFormat.format(new Date())+" : " +message);
					System.out.println("INFO   "+ dateFormat.format(new Date())+" : " +message);
					pw.close();
				}
				else if(logType.equals("error")){
					pw.println("ERROR  "+ dateFormat.format(new Date())+" : " +message);
					System.out.println("ERROR  "+ dateFormat.format(new Date())+" : " +message);
					pw.close();
				}
				else if(logType.equals("debug")){
					pw.println("DEBUG  "+ dateFormat.format(new Date())+" : " +message);
					System.out.println("DEBUG  "+ dateFormat.format(new Date())+" : " +message);
					pw.close();
				}
				else if(logType.equals("warn")){
					pw.println("WARN   "+ dateFormat.format(new Date())+" : " +message);
					System.out.println("WARN   "+ dateFormat.format(new Date())+" : " +message);
					pw.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private  static String getOutputFileName(String opfileName) {
		
		String generatedFileName=null;
		
		if(!fileName.contains(".")){
			generatedFileName="LogFile"+"_"+formattedDate+".log";
		}else{
			String[]  beforeDot=fileName.split("\\.");
			generatedFileName= beforeDot[0]+"_"+formattedDate+"."+beforeDot[1];
		}
		return generatedFileName;
	}


	private static String getDirectoryPathExceptFileName(String[] getfileName) {
		StringBuilder filePath=new StringBuilder();	

		if(fileName.contains(".")){
			for(int i=0;i<getfileName.length-1;i++){
				filePath.append(new StringBuilder(getfileName[i]));
				filePath.append(slashChar);
			}
		}else{
			for(int i=0;i<getfileName.length;i++){
				filePath.append(new StringBuilder(getfileName[i]));
				filePath.append(slashChar);
			}
		}
		return filePath.toString();
	}


	private static String checkPathforSlash(String fullFilePath) {
		String slashChar=null;
		if(fullFilePath.contains("/")){
			slashChar="/";
		}
		else if(fullFilePath.contains("\\")){
			slashChar="\\\\";
		}
		return slashChar;
	}

	public static void info(String msg){
		message=msg;
		generateOutputFile("info",message);
	}

	public static void error(String msg){
		message=msg;
		generateOutputFile("error",message);
	}

	public static void debug(String msg){
		message=msg;
		generateOutputFile("debug",message);
	}

	public static void warn(String msg){
		message=msg;
		generateOutputFile("warn",message);
	}
}
