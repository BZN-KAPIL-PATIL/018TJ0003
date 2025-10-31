package com.byzan.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;






public class BZNUtils 
{
	static Connection con = null;
	static PreparedStatement ps = null;
	static ResultSet rs = null;
	static ResultSetMetaData rsmd = null;
	public static RandomAccessFile raFileT = null;
	public static FileChannel fileChannelT = null;
	public static FileLock fileLockT = null;
	public static boolean isFileLockedT = false;

	

	
	
		
	public static boolean isNum(String str)
	{
		String regexStr = "[0-9./]+";
		Pattern pattern = Pattern.compile(regexStr);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	public static boolean deleteFile(String filePath)
	{
		try
		{
			File fl = new File(filePath);
			if ((fl.exists()) && (fl.canWrite())) {
				System.gc();
				if(new File(filePath).delete()){
				    System.out.println("OK");
				}
				//fl.delete();
			}
		
			return !fl.exists();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//BaseLogger.info("Error while deleting file "+filePath+"." + e.getMessage());
		}
		return false;
	}

	public static boolean moveFile(String source, String destination) throws IOException
	{
		File src = new File(source);
		File dest = new File(destination);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		new StringWriter();
		try
		{
			if ((src.exists()) && (src.canRead()) && (src.canWrite()) && ((!dest.exists()) || (dest.canWrite())))
			{
				
				System.gc();
				fis = new FileInputStream(src);
				fos = new FileOutputStream(dest);

				byte[] b = new byte[1024];
				int read = 0;
				while ((read = fis.read(b)) != -1) {
					fos.write(b, 0, read);
				}
				fos.close();
				fis.close();

				src.delete();
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			e.getMessage();
			return false;
		}
		finally
		{
			if (fos != null) 
			{
				fos.close();
			}
			if (fis != null) 
			{
				fis.close();
			}
		}
		return false;
	}

	public static boolean isEmpty(String str)
	{
		return (str == null) || (str.isEmpty()) || (str.equalsIgnoreCase("")) || (str.trim().equalsIgnoreCase(""));
	}

	public static String lpad(String s, int n, char c)
	{
		s = s == null ? "" : s;
		String lps = s;
		for (int i = 0; i < n - s.length(); i++) {
			lps = c + lps;
		}
		return lps;
	}

	public static String rpad(String s, int n, char c)
	{
		String rps = s;
		for (int i = 0; i < n - s.length(); i++) {
			rps = rps + c;
		}
		return rps;
	}


	public static boolean isAlphanumeric (String str) 
	{
		String regex = "^[a-zA-Z0-9-\\|./& ]+$";
		 
		Pattern pattern = Pattern.compile(regex);
		 
		
		 Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	public static boolean checkbeneName(String checkData){
		Pattern pattern = Pattern.compile("^[ A-Za-z0-9-./ &]*$");
		if(pattern.matcher(checkData).find())
		{
			return true;
		}
		return false;
	}

	public static boolean copyFile(String source, String destination)
			throws IOException
			{
		File src = new File(source);
		File dest = new File(destination);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		Writer wr = new StringWriter();
		try
		{
			if ((src.exists()) && (src.canRead()) && ((!dest.exists()) || (dest.canWrite())))
			{
				fis = new FileInputStream(src);
				fos = new FileOutputStream(dest);

				byte[] b = new byte[1024];
				int read = 0;
				while ((read = fis.read(b)) != -1) {
					fos.write(b, 0, read);
				}
				return true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(new PrintWriter(wr));
			return false;
		}
		finally
		{
			if (fos != null) {
				fos.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return false;
			}

	public static boolean isAlpha (String str) {
		int c = 0;
		for (int i = 0; i < str.length(); i++) {
			c = str.charAt(i);
			if (((c < 65) || (c > 122)) && (c != 32)) {
				return false;
			}
		}
		return true;
	}

	
}