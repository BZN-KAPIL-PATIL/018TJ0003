package com.byzan.operations;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.byzan.utils.BZNUtils;
import com.byzan.utils.MyLogger;
import com.byzan.utils.PathConstant;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;

public class InputFileWritting1 implements PathConstant {

    private static SecretKey loadSecretKey() throws Exception {
       /* Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("Configuration.properties")) {
            props.load(fis);
        }

        String encodedKey = props.getProperty("aes.secret.key");
        if (encodedKey == null) {
            throw new IllegalArgumentException("aes.secret.key not found in config.properties");
        }*/
    	String encodedKey = key;
        // Decode from Base64
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key must be 256-bit (32 bytes), but got " + keyBytes.length);
        }

        return new SecretKeySpec(keyBytes, "AES");
    }

    public String fileWritting(ArrayList<ArrayList<String>> validatedData, File inputFile) {
        String fileName = null;
        Workbook workbook = null;
        FileOutputStream fileOut = null;

        try {
            // Create workbook and sheet
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // 2. Write header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "CUSTOMER CODE",
                    "BUYER CODE",
                    "AMOUNT",
                    "DATE",
                    "UTR",
                    "ECL ACCOUNT NUMBER",
                    "REMITTER IFSCCODE",
                    "REMITTER Account Number",
                    "REMITTER NAME",
                    "PAYMENT PRODUCT CODE",
                    "BENEFICIARY BANK CODE",
                    "profit centre",
                    "RC",
                    "Spl GL",
                    "Doc type as per location",
                    "CMS Code"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // Write rows
            for (int i = 0; i < validatedData.size(); i++) {
            	Row row = sheet.createRow(i + 1);
                ArrayList<String> rowData = validatedData.get(i);

                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData.get(j));
                }
            }

            // Auto-size columns
            for (int i = 0; i < validatedData.get(0).size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Output encrypted file
            fileName = inputFile.getName()+".enc";
            File fileOutput = new File(OUTPUT + fileName);

            // 1. Write workbook to memory first
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            byte[] excelBytes = baos.toByteArray();

            // 2. Load AES key from properties file
            SecretKey secretKey = loadSecretKey();

            // 3. Generate random IV (16 bytes for AES-CBC)
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 4. Init Cipher AES-256-CBC
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // 5. Write IV + encrypted data to file
            fileOut = new FileOutputStream(fileOutput);
            fileOut.write(iv); // prepend IV for later decryption
            try (CipherOutputStream cos = new CipherOutputStream(fileOut, cipher)) {
                cos.write(excelBytes);
            }

        } catch (Exception e) {
            fileName = null;
            MyLogger.error("Error in writing encrypted Excel output file: " + e.toString());

            try {
                BZNUtils.moveFile(INPUT + inputFile.getName(), FAILURE + inputFile.getName());
                if (fileName != null) {
                    BZNUtils.deleteFile(OUTPUT + fileName);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (fileOut != null) fileOut.close();
            } catch (IOException e) {
                MyLogger.error("Exception occurred while closing file stream: " + e.toString());
            }

            if (workbook != null) {
			    try {
			        workbook.getClass().getMethod("close").invoke(workbook);
			    } catch (NoSuchMethodException ignore) {
			        // method not available in old POI, ignore
			    } catch (Exception e) {
			        MyLogger.error("Error closing workbook ERRROR : " + e.toString());
			    }
			}
        }

        return fileName;
    }
}
