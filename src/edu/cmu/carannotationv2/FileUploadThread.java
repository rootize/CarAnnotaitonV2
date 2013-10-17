package edu.cmu.carannotationv2;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.content.Context;
import android.util.Log;
import android.view.View;

public class FileUploadThread extends Thread {
	private static final String offline_filename = "offline";
	private static final String offline_filename_bk="offline_bk";
	private JSONArray offline_JsonArray;
	private EncryptedData ed;
	private int total;
	private int counter=0;;
	Context context;

	public FileUploadThread(Context context,EncryptedData ed) {
		this.context = context;
		this.ed=ed;
		String offline_string = FileOperation.read(context, offline_filename);
		try {
			offline_JsonArray = new JSONArray(offline_string);
			
			total=offline_JsonArray.length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			recursive_upload();
		} catch (Exception e) {
			// TODO: handle exception
		}
	

		super.run();
	}

	
	
	private void recursive_upload() throws JSONException {
		// TODO Auto-generated method stub
		String offline_string = FileOperation.read(context, offline_filename);
		Log.d("FileUploadThread", "recursive_upload");
		if (offline_string != null) {

			
				offline_JsonArray = new JSONArray(offline_string);
				if (offline_JsonArray.length()>0) {
					Log.d("recursive_upload", "something in offline JsonArray");
					JSONObject itemJsonObject=(JSONObject)offline_JsonArray.get(0);
					ParseObject pObject=new JSONdata(itemJsonObject).formatParseObject(ed.getCipherTextClassName());
					//UploadingDependentItems udi=new UploadingDependentItems();
					//udi.sendingMultipleobjects(itemJsonObject);
					
					/*ParseObject userObject=new JSONdata(itemJsonObject).split2user();
					ParseObject imageObject=new JSONdata(itemJsonObject).split2img();
					ParseObject annotationObject=new JSONdata(itemJsonObject).split2anno();*/
					
					
					
					pObject.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException arg0) {
							
							if (arg0==null) {
								offline_JsonArray=static_global_functions.remove(0, offline_JsonArray);
								FileOperation.delete(context,
										offline_filename);
								
								counter=counter+1;
								Log.d("counter", ""+counter);
								static_global_functions.ShowToast_short(context,
										String.format("%d out of %d have been sent", counter,total), R.drawable.success);
								Log.d("All files", "Send successfully");

								File oldFile=context.getFileStreamPath(offline_filename);
								File bkupFile=context.getFileStreamPath(offline_filename_bk);
								oldFile.renameTo(bkupFile);
								FileOperation.save(context,
										offline_filename,
										offline_JsonArray.toString());
								FileOperation.delete(context, offline_filename_bk);
								try {
									recursive_upload();
								} catch (Exception e) {
									// TODO: handle exception
								}
								
								
							}else {
								
							}
						}
					});
				
				}
				else {
					FileOperation.delete(context, offline_filename);
					static_global_functions.ShowToast_short(context, "success!", R.drawable.success);
				}

		}else {
			static_global_functions.ShowToast_short(context,
					"Previous data uploaded!", R.drawable.success);
			Log.d("All files", "Send successfully");

			FileOperation.delete(context, offline_filename);
			
		}
	}
}
