package com.ocularminds.eduzie.common;

import java.security.SecureRandom;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.URL;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.math.BigInteger;
import java.nio.channels.FileChannel;
import javax.servlet.http.Part;

public class FileUtil{

  static SecureRandom random = new SecureRandom();

  public static String nextText() {
       return new BigInteger(130, random).toString(32);
  }

  public static Map<String,String> params(final Part part) {

	 Map<String,String> form = new HashMap<String,String>();
	 for(String content : part.getHeader("content-disposition").split(";")) {

		System.out.println(content);
		if(!content.trim().startsWith("filename")) {
			 //form.put(content.substring(0,content.indexOf('=')),content.substring(content.indexOf('=') + 1).trim().replace("\"", ""));
		 }
	 }
     return form;
  }

  public static String name(final Part part) {

  	 for (String content : part.getHeader("content-disposition").split(";")) {

		 System.out.println(content);
  		 if (content.trim().startsWith("filename")) {
  			 return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
  		 }
  	 }
     return null;
  }

  public static String extension(String file){

	  if(file == null) return "";
	  int extensionPos = file.lastIndexOf('.');
	  int lastUnixPos = file.lastIndexOf('/');
	  int lastWindowsPos = file.lastIndexOf('\\');
	  int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

	  int index = lastSeparator > extensionPos ? -1 : extensionPos;
	  if (index == -1) {
		  return "";
	  } else {
		  return file.substring(index + 1);
      }
  }

   public static String extension(File file) {

	  String fileName = file.getName();
	  return extension(fileName);

    }

  public static void copy(File source, File dest)throws IOException {

	FileChannel ic = null;
	FileChannel oc = null;
	try {

		ic = new FileInputStream(source).getChannel();
		oc = new FileOutputStream(dest).getChannel();
		oc.transferFrom(ic, 0, ic.size());

	} finally {
		ic.close();
		oc.close();
	}
   }

   public static byte[] loadFromDisk(String f) {

   	   ArrayList records = new ArrayList();
   	   BufferedInputStream bis;
   	   byte[] bytes = null;
   	   if(f == null) return null;

   	   try {
   		   bis = new BufferedInputStream(new FileInputStream(new File(f)));
   	   } catch (FileNotFoundException fe) {
   		   System.out.println("INFO: <<FileUtil:loadFromDisk>>\nImage file does not Exist.");
   		   return null;
   	   }

   	   int data;
   	   try {
   		   while ((data = bis.read()) != -1) {
   			   records.add(new Byte((byte) data));
   		   }

   		   bytes = new byte[records.size()];
   		   for (int i = 0; i < bytes.length; i++) {
   			   bytes[i] = ((Byte)records.get(i)).byteValue();
   		   }
   	   }catch (IOException ioe) {

   		   System.out.println("INFORMATION, <<loadFromDisk>> Could not retrieve image.");
   		   System.out.println(ioe);
   	   }

   	   return bytes;
   }

   public static String readFromUrl(String url){

	   String scrapped = "";
	   try{

		   URL u = new URL(url);
           scrapped = new String(Files.readAllBytes(Paths.get(u.toURI())));

	   }catch(Exception e){
		   e.printStackTrace();
	   }

	   return scrapped;
   }

   public static boolean isFileExists(String file){

	   boolean exists = false;
	   try{

			File f = new File(file);
			exists = f.exists();
	   	}catch(Exception e){
	   		e.printStackTrace();
	   }

	   return exists;
   }

   public static void append(String file,String data){

		 try{

			File f = new File(file);
			if(!f.exists()){
				f.createNewFile();
			}

			//true = append file
			FileWriter fw = new FileWriter(f.getName(),true);
			BufferedWriter br = new BufferedWriter(fw);
			br.write(data);
			br.close();

	   }catch(Exception e){
		  e.printStackTrace();
	   }
   }

   public static void writeToDisk(String dataURL, String fileName) {

       String fileExtension = extension(fileName);
       /*    fileRootNameWithBase = './uploads/' + fileName,
           filePath = fileRootNameWithBase,
           fileID = 2,
           fileBuffer;

       // @todo return the new filename to client
       while (fs.existsSync(filePath)) {
           filePath = fileRootNameWithBase + '(' + fileID + ').' + fileExtension;
           fileID += 1;
       }

       dataURL = dataURL.split(',').pop();
       fileBuffer = new Buffer(dataURL, 'base64');
       fs.writeFileSync(filePath, fileBuffer);

       console.log('filePath', filePath);*/
   }
}