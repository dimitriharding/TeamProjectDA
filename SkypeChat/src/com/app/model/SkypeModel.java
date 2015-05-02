package com.app.model;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SkypeModel {
	private static final OpenOption CREATE = null;
	private static final OpenOption APPEND = null;

	public String hashMessage(String sender, String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String line = ""+sender +": "+message+"";

		md.update(line.getBytes("UTF-8")); // Change this to "UTF-16" if needed
		byte[] digest = md.digest();
		
		return digest.toString();
	}
	
	public void userDatabase(String hashMessage) throws IOException{
		 
		//Convert the string to a byte array
		byte [] data = hashMessage.getBytes();
		Path filePath = Paths.get("./logfile.txt");
		
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(filePath, CREATE, APPEND))){
			out.write(data, 0, data.length);
		}
		
        /**
         * File tempFile = null;
        
        BufferedWriter writer = null;
        try {
            tempFile = File.createTempFile("MyTempFile", ".tmp");
            writer = new BufferedWriter(new FileWriter(tempFile));
            writer.write(hashMessage);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try{
                if(writer != null) writer.close();
            }catch(Exception ex){}
        }
        System.out.println("Stored data in temporary file.");
         */
    }
    
}

