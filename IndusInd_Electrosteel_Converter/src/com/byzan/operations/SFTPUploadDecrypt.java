package com.byzan.operations;
import com.byzan.utils.MyLogger;
import com.byzan.utils.PathConstant;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class SFTPUploadDecrypt implements PathConstant {

    // Load AES-256 key from Base64 string
    private static SecretKey loadSecretKey() throws Exception {
        String encodedKey = key; // key from PathConstant
        if (encodedKey == null) {
            throw new IllegalArgumentException("AES key not defined!");
        }
        byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("AES key must be 256-bit (32 bytes)");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Ensure remote directory exists, create recursively if needed
    private void ensureRemoteDir(ChannelSftp channelSftp, String remoteDir) throws Exception {
        String[] folders = remoteDir.split("/");
        String currentPath = "";
        for (String folder : folders) {
            if (folder.isEmpty()) continue;
            currentPath += "/" + folder;
            try {
                channelSftp.cd(currentPath);
            } catch (Exception e) {
                channelSftp.mkdir(currentPath);
                channelSftp.cd(currentPath);
            }
        }
    }

    /**
     * Uploads an AES-256 encrypted file to SFTP, decrypts it in memory,
     * deletes local encrypted file and removes encrypted file from SFTP after decryption.
     *
     * @param localEncryptedFilePath Local encrypted file path (.enc)
     * @return true if successful
     */
    public boolean uploadAndDecrypt(String localEncryptedFilePath) {
        String host = Host;
        int port = Integer.parseInt(Port);
        String username = Username;
        String password = Password;
        String remoteDir = RemoteDir;

        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            SecretKey secretKey = loadSecretKey();

            // 1. Open SFTP session
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // 2. Ensure remote folder exists
            ensureRemoteDir(channelSftp, remoteDir);

            // 3. Read local encrypted file into memory
            byte[] encryptedBytes;
            File localFile = new File(localEncryptedFilePath);
            try (FileInputStream fis = new FileInputStream(localFile);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, read);
                }
                encryptedBytes = baos.toByteArray();
            }

            // 4. Upload encrypted file to SFTP
            try (InputStream is = new ByteArrayInputStream(encryptedBytes)) {
                channelSftp.put(is, localFile.getName());
            }
            MyLogger.info("Encrypted file uploaded: " + localEncryptedFilePath);

            // 5. Delete local encrypted file after upload
            if (localFile.delete()) {
                MyLogger.info("Local encrypted file deleted: " + localEncryptedFilePath);
            } else {
                MyLogger.error("Failed to delete local encrypted file: " + localEncryptedFilePath);
            }

            // 6. Decrypt in memory
            ByteArrayInputStream bais = new ByteArrayInputStream(encryptedBytes);

            // Read IV (first 16 bytes)
            byte[] iv = new byte[16];
            if (bais.read(iv) != 16) throw new IOException("Unable to read IV from encrypted file");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            String decryptedFileName = localFile.getName().replace(".enc", "");

            // 7. Write decrypted file to SFTP
            try (CipherInputStream cis = new CipherInputStream(bais, cipher);
                 OutputStream decryptedOut = channelSftp.put(decryptedFileName)) {

                byte[] buffer = new byte[4096];
                int read;
                while ((read = cis.read(buffer)) != -1) {
                    decryptedOut.write(buffer, 0, read);
                }
            }

            MyLogger.info("File decrypted successfully on SFTP: " + decryptedFileName);

            // 8. Delete encrypted file from SFTP after successful decryption
            try {
            	
            	channelSftp.rm(localFile.getName()); 
                MyLogger.info("Encrypted file deleted from SFTP: " + localFile.getName());
            } catch (Exception e) {
                MyLogger.error("Failed to delete encrypted file from SFTP: " + e.toString());
            }

            return true;

        } catch (Exception e) {
            MyLogger.error("Upload & Decrypt failed: " + e.toString());
            e.printStackTrace();
            return false;
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) channelSftp.disconnect();
            if (session != null && session.isConnected()) session.disconnect();
        }
    }
}