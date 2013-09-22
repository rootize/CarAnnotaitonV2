package edu.cmu.carannotationv2;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import edu.cmu.carannotationv2.Main_screen.UploadFileThread;

import android.R.bool;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BackgroundUploading extends Service{

	
	
	private AlarmManagerBroadcastReceiver alarm;
	private EncryptedData ed_service;
	private boolean isloggedin;
//	public BackgroundUploading() {
//		super("BackgroundUploading");
//		ed_service = new EncryptedData(getApplicationContext(), R.raw.key,
//				R.raw.plaintext);
//		alarm=new AlarmManagerBroadcastReceiver();
//	}


	
	 public void startRepeatingTimer() {
	     Context context = this.getApplicationContext();
	     if(alarm != null){
	      alarm.SetAlarm(context);
	     }else{
	      //Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
	     }
	    }
	     
	    public void cancelRepeatingTimer(){
	     Context context = this.getApplicationContext();
	     if(alarm != null){
	      alarm.CancelAlarm(context);
	     }else{
	     // Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
	     }
	    }
	     
	    public void onetimeTimer(){
	     Context context = this.getApplicationContext();
	     if(alarm != null){
	      alarm.setOnetimeTimer(context);
	     }else{
	     // Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
	     }
	    }

	//private final IBinder mBinder=new MyBinder();
	private JSONArray offline_JsonArray;
	private String parseClassNameString;
	private String offline_filename="offline";

	
	private void login_global_usr() {
		ParseUser.logInInBackground(ed_service.getCipherTextUserName(),
				ed_service.getCipherTextUserPassword(), new LogInCallback() {

					@Override
					public void done(ParseUser usr, ParseException e) {
						if (usr != null) {
							//Log.d("Login_before", "Successfully");
							isloggedin = true;
						} else {
							//Log.d("error logging in ", e.toString());
							isloggedin = false;
							static_global_functions.ShowToast_short(getApplicationContext(), "Remote server not responding, save to local memory", R.drawable.caution);
						}

					}
				});

	}
	private void recursive_upload() {
		if (offline_JsonArray.length() > 0) {

			JSONObject tem_item;
			try {
				tem_item = (JSONObject) offline_JsonArray.get(0);

				ParseObject pobject = new JSONdata(tem_item)
						.formatParseObject(parseClassNameString);
				pobject.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException arg0) {
                    if (arg0==null) {
                    	offline_JsonArray = static_global_functions.remove(0,
								offline_JsonArray);
						recursive_upload();
					}else {
						static_global_functions.ShowToast_short(getApplicationContext(), "Error occured during uploading, will try later", R.drawable.caution);
					}
						
					}

				});
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("Main_Screen", "JSON array null");
			}

		} else {
			static_global_functions.ShowToast_short(getApplicationContext(),
					"Previous data uploaded!", R.drawable.success);
			Log.d("All files", "Send successfully");
			FileOperation.delete(getApplicationContext(), offline_filename);
		}
	}




	private void check_upload_localData() {
		//if (isLoggedin) {
			new UploadFileThread().start();
		//}

	}
	
	class UploadFileThread extends Thread {
		public UploadFileThread() {
		}

		@Override
		public void run() {

			String offline_string = FileOperation.read(getApplicationContext(),
					offline_filename);
			if (offline_string == null) {
				// do nothing
			} else {
				try {
					offline_JsonArray = new JSONArray(offline_string);
				} catch (JSONException e) {
					e.printStackTrace();
				}

								recursive_upload();
			

				}

			}

		}

//	@Override
//	protected void onHandleIntent(Intent intent) {
//		// TODO Auto-generated method stub
//		
//		
//	new Uploading().execute();
//		
//	}
//	

	
	private class Uploading extends AsyncTask<String, Void, Boolean>{

		@Override
		protected Boolean doInBackground(String... params) {
			login_global_usr();
			if (isloggedin) {
				check_upload_localData();
			}
			
			return null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("UploadingSevice", "called!");
		new Uploading().execute();
		return Service.START_STICKY;
	}
	
	
	
}
