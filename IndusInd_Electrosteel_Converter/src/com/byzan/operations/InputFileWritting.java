package com.byzan.operations;

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

public class InputFileWritting implements PathConstant {

    private static SecretKey loadSecretKey() throws Exception {
        String encodedKey = key; // from PathConstant
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key must be 256-bit (32 bytes), but got " + keyBytes.length);
        }

        return new SecretKeySpec(keyBytes, "AES");
    }

    public String fileWritting(ArrayList<ArrayList<String>> validatedData, File inputFile) {
        String fileName = null;
        FileOutputStream fileOut = null;

        try {
            // CSV headers
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

            // 1. Build CSV in memory
            StringBuilder sb = new StringBuilder();

            // Header row
            for (int i = 0; i < headers.length; i++) {
                sb.append(escapeCsv(headers[i]));
                if (i < headers.length - 1) sb.append(",");
            }
            sb.append("\n");

            // Data rows
            for (ArrayList<String> rowData : validatedData) {
                for (int j = 0; j < rowData.size(); j++) {
                    sb.append(escapeCsv(rowData.get(j)));
                    if (j < rowData.size() - 1) sb.append(",");
                }
                sb.append("\n");
            }

            byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

            // 2. Encrypt CSV
            SecretKey secretKey = loadSecretKey();

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // 3. Write IV + encrypted CSV
            String originalName = inputFile.getName();
            int dotIndex = originalName.lastIndexOf(".");
            if (dotIndex > 0) {
                originalName = originalName.substring(0, dotIndex);
            }
            fileName = originalName + ".csv.enc";
            File fileOutput = new File(OUTPUT + fileName);

            fileOut = new FileOutputStream(fileOutput);
            fileOut.write(iv); // prepend IV

            try (CipherOutputStream cos = new CipherOutputStream(fileOut, cipher)) {
                cos.write(csvBytes);
            }

        } catch (Exception e) {
            fileName = null;
            MyLogger.error("Error in writing encrypted CSV output file: " + e.toString());
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
        }

        return fileName;
    }

    // Utility method to escape CSV values
    private String escapeCsv(String value) {
        if (value == null) return "";
        boolean hasComma = value.contains(",");
        boolean hasQuote = value.contains("\"");
        boolean hasNewline = value.contains("\n") || value.contains("\r");

        if (hasComma || hasQuote || hasNewline) {
            value = value.replace("\"", "\"\""); // escape quotes
            return "\"" + value + "\"";
        }
        return value;
    }
}
