package edu.cmu.carannotationv2;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.R.integer;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class FileUploadThread extends Thread {
	private static final String SENTFILEDIR_STRING="/CarAnnotationSentFiles";
	private static final String offline_filename = "offline";
	private static final String offline_filename_bk = "offline_bk";
	private static final String infoDir = "InfoDir";
	private JSONArray offline_JsonArray;
	private EncryptedData ed;
	private int total;
	public  static  int counter = 0;
	public static int total_number=0;
	public static int total_change=0;
	Context context;
	public static  File[] singleFiles;
	public FileUploadThread(Context context, EncryptedData ed) {
		this.context = context;
		this.ed = ed;
		/*String offline_string = FileOperation.read(context, offline_filename);
		try {
			offline_JsonArray = new JSONArray(offline_string);

			total = offline_JsonArray.length();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public void run() {
		try {
			uploadDistributedFiles();
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.run();
	}

	private   void uploadDistributedFiles() throws JSONException {
		File mydir =new File(Environment.getExternalStorageDirectory().toString(), SENTFILEDIR_STRING);
		total_number = mydir.listFiles().length;
		total_change=total_number;
		counter=0;
		if (total_number > 0) {
			singleFiles = mydir.listFiles();

		       recursive_upload();
		}
		else if(total_number==0)
		{
			mydir.delete();
		}


	}

//	private synchronized void uploadSingleFile(File file,int num) {
//		String single_item_String = FileOperation.readfromExternal(file);
//		if (single_item_String != null) {
//			try {
//
//				JSONObject jObject = new JSONObject(single_item_String);
//				ParseObject pObject = new JSONdata(jObject)
//				.formatParseObject(ed.getCipherTextClassName());
//
//				pObject.saveInBackground(new SaveCallback() {
//
//					@Override
//					public void done(ParseException arg0) {
//						if (arg0 == null) {
//							/*file.delete();*/
//							//							singleFiles[0].delete();
//							static_global_functions.ShowToast_short(
//									context,
//									String.format(
//											"%d out of %d have been sent successfully!",
//											counter, total), R.drawable.success);
//							//							FileOperation.delete(context, singleFiles[counter].getAbsolutePath());
//							counter = counter + 1;
//						}else {
//							Log.d("Failed", "save in background");
//						}
//
//					}
//				});
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else {
//			file.delete();
//		}
//
//	}

	private void recursive_upload() throws JSONException {
		
		Log.d("FileUploadThread", "recursive_upload");

		if (total_change >0) {

			String single_item_String = FileOperation.readfromExternal(singleFiles[total_change-1]);
			JSONObject jObject = new JSONObject(single_item_String);
			ParseObject pObject = new JSONdata(jObject)
			.formatParseObject(ed.getCipherTextClassName());

			pObject.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException arg0) {
					synchronized (this) {
						if (arg0 == null) {

							singleFiles[total_change-1].delete();
							counter=counter+1;
							Log.d("counter", "successful:" + counter);
							static_global_functions.ShowToast_short(context,
									String.format(
											"%d out of %d have been sent",
											counter, total_number), R.drawable.success);

							
						} else {
							counter=counter+1;
							Log.d("counter", "faile:" + counter);
							static_global_functions.ShowToast_short(context,
									String.format(
											"%d out of %d failed",
											counter, total_number), R.drawable.caution);
						}
						total_change=total_change-1;
						try {
							recursive_upload();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});

		

		} else {
			static_global_functions.ShowToast_short(context,
					String.format(
							"%d out of %d have been sent to server, the failed will be sent next time if possible",
							counter, total_number), R.drawable.success);
			

			
		}

	}
}
