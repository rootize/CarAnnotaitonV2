package edu.cmu.carannotationv2;

import java.io.File;
import java.util.Calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;

public class Utils {
	private static  final String ImageNamePrefix="IMG_";
	private static  final String ImageNameSuffix=".jpg"; 
	public static String setFileNamebyDate(){
		Calendar c=Calendar.getInstance();

		return set2Digits(c.get(Calendar.YEAR))
				+ set2Digits( c.get(Calendar.MONTH))
				+ set2Digits(c.get(Calendar.DAY_OF_MONTH))
				+ set2Digits( c.get(Calendar.HOUR_OF_DAY))
				+ set2Digits( c.get(Calendar.MINUTE))+set2Digits(c.get(Calendar.SECOND))+set2Digits(c.get(Calendar.MILLISECOND));
	}
	public static String set2Digits(int x){

		return String.format("%02d", x);
	}

	public static boolean isWifi(Context context){
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		if (isWifi) {
			return true;

		} else {
			return false;
		}
	}
	public static boolean isScreenLocked(Context context){
		boolean screenRotationLocked;
		if (android.provider.Settings.System.getInt(context.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
			screenRotationLocked = false;
		} else {
			GlobalFuns.setAutoOrientationEnabled(
					context.getContentResolver(), true);
			screenRotationLocked = true;
		}
		return screenRotationLocked;
	}
	public static boolean fileExistanceInternal(Context context, String fname,String folder){

		File make_model_file_dir = context.getDir(folder, Context.MODE_PRIVATE);
		File privateFile = new File(make_model_file_dir, fname);
		//    File file = getBaseContext().getFileStreamPath(fname);
		return privateFile.exists();
	}

	public static String createImageName(){
		String timeStamp=setFileNamebyDate();
		return ImageNamePrefix+timeStamp+ImageNameSuffix;
	}
	
	public static boolean  turn1toture(String x) {

		int num = Integer.parseInt(x);
		if (1 == num) {
			return true;
		} else {
			return false;
		}

	}
}
