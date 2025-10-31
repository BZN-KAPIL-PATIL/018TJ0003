package com.byzan.utils;

import java.util.ResourceBundle;


public interface PathConstant {

static ResourceBundle	rbBundle	= ResourceBundle.getBundle("Configuration");
	
	
	static String INPUT = rbBundle.getString("INPUT").trim();
	static String OUTPUT = rbBundle.getString("OUTPUT").trim();
	static String SUCCESS = rbBundle.getString("SUCCESS").trim();
	static String FAILURE = rbBundle.getString("FAILURE").trim();
	static String AuditLog = rbBundle.getString("AuditLog").trim();
	static String ValidationFilePath = rbBundle.getString("ValidationFilePath").trim();
	
	
	static String InputFileDateFormat = rbBundle.getString("InputFileDateFormat").trim();
	static String outputFileDateFormat = rbBundle.getString("outputFileDateFormat").trim();
	static String key = rbBundle.getString("key").trim();
	
	
	static String Host = rbBundle.getString("Host").trim();
	static String Port = rbBundle.getString("Port").trim();
	static String Username = rbBundle.getString("Username").trim();
	static String Password = rbBundle.getString("Password").trim();
	static String RemoteDir = rbBundle.getString("RemoteDir").trim();
	

	
	
	
	
	
	
	
	
}
