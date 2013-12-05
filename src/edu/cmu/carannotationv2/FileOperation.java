package edu.cmu.carannotationv2;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;



/**
 *  ������������������������������������������MODE_PRIVATE���������������������
 * @author chuckzhang
 *
 */
public class FileOperation  {
	

	public static boolean delete( Context context,String name)
	{
		return context.deleteFile(name);
	}

	/**
	 * ������������������������
	 * 
	 * @param fileName ���������
	 * @return ������������
	 */
	public static boolean exist( Context context,String fileName){

		try {
			
	     FileInputStream	fis = context.openFileInput(fileName);
	   
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}  
	}  

	public static boolean existInternalFile(Context context,String fileName){
		
		File file=context.getFileStreamPath(fileName);
		return file.exists();
		
	}
	
	public static String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append((line ));
	        }
	    } catch (IOException e) {
//	        Log.w("LOG", e.getMessage());
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
//	            Log.w("LOG", e.getMessage());
	        }
	    }
	    return sb.toString();
	}
	
	/** 
	 * ������������������ 
	 *  
	 * @param fileName ��������� 
	 * @return ������������ 
	 */  
	public static String read(Context context,String fileName){  
		FileInputStream fis;
		try {
//			fis = context.openFileInput(fileName);
			fis=new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}  
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  

		byte[] buf = new byte[1024];  
		int len = 0;  

		//���������������������������������������---ByteArrayOutputStream  
		try {
			while ((len = fis.read(buf)) != -1) {  
				baos.write(buf, 0, len);  
			}
			
			fis.close();  
			baos.close();  
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}  

		//������������������������������  
		return baos.toString();  

	}  

	/** 
	 * ������������ 
	 *  
	 * @param fileName ��������� 
	 * @param content  ������������ 
	 * @return ������������������������
	 */  
	public static boolean save(Context context,String fileName, String content){  
		FileOutputStream fos;
		try {
			
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}  
		try {
			fos.write(content.getBytes());
			fos.close();  
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return false;
	}
	/** 
	 * ������������������������ 
	 *  
	 * @param fileName ��������� 
	 * @return ������������ 
	 */  
	public static String readfromExternal(File file){  
		FileInputStream fis;
		try {
//			fis = context.openFileInput(fileName);
			fis=new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}  
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  

		byte[] buf = new byte[1024];  
		int len = 0;  

		//���������������������������������������---ByteArrayOutputStream  
		try {
			while ((len = fis.read(buf)) != -1) {  
				baos.write(buf, 0, len);  
			}
			
			fis.close();  
			baos.close();  
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}  

		//������������������������������  
		return baos.toString();  

	}  
	
	public static boolean saveCostomizedDir(Context context,String fileName,File dirPath, String content){  

		try {
			File saveFile=new File(dirPath,fileName);

			FileWriter outFileWriter=new FileWriter(saveFile);
			outFileWriter.write(content);
			outFileWriter.close();
//			OutputStreamWriter outputStreamWriter=new OutputStreamWriter(openfile)
			/*OutputStream outputStream=new BufferedOutputStream(new FileOutputStream(saveFile.getAbsolutePath(),true));
			outputStream.flush();
			outputStream.close();*/
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}  
		return true;

	}
	
	public FileOperation() {
	}
	
	public static File transferRawtoFile(Context context,int csvID,String dir,String filename) throws IOException {
		// TODO Auto-generated method stub
		InputStream isInputStream = context.getResources().openRawResource(csvID);
		File make_model_file_dir = context.getDir(dir, Context.MODE_PRIVATE);
		File privateFile = new File(make_model_file_dir, filename);
		FileOutputStream osFileOutputStream = new FileOutputStream(
				privateFile);
		byte[] buffer = new byte[4096];
		int bytesRead;
		while ((bytesRead = isInputStream.read(buffer)) != -1) {
			osFileOutputStream.write(buffer, 0, bytesRead);

		}
		osFileOutputStream.close();
		isInputStream.close();
       return privateFile;
	}

	
}