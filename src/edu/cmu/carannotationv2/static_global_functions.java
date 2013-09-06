package edu.cmu.carannotationv2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseFile;
import com.parse.ParseObject;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.ConnectivityManager;
import android.provider.Settings;

import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class static_global_functions {

	public static void ShowToast_short(Context context, String showString,
			int icon) {

		Toast showToast = Toast.makeText(context, showString,
				Toast.LENGTH_SHORT);
		showToast.setGravity(Gravity.BOTTOM, 0, 0);
		LinearLayout toastView = (LinearLayout) showToast.getView();
		ImageView imgToast = new ImageView(context);
		imgToast.setImageResource(icon);
		toastView.addView(imgToast, 0);
		showToast.show();

	}

	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	public static JSONArray remove(final int idx, final JSONArray from) {
		final List<JSONObject> objs = asList(from);
		objs.remove(idx);

		final JSONArray ja = new JSONArray();
		for (final JSONObject obj : objs) {
			ja.put(obj);
		}

		return ja;
	}

	public static List<JSONObject> asList(final JSONArray ja) {
		final int len = ja.length();
		final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
		for (int i = 0; i < len; i++) {
			final JSONObject obj = ja.optJSONObject(i);
			if (obj != null) {
				result.add(obj);
			}
		}
		return result;
	}

	public static void transfer_Json_Pobject(ParseObject pobject,
			JSONObject tem_item /*,String lati, String longti*/) {
		// TODO Auto-generated method stub
		// pobject = new ParseObject("annotation_info");
		try {
			pobject.put("usr", tem_item.getString("usr"));

			// if (global_info_count != 0) {

			for (int i = 0; i < tem_item.getInt("globalcount"); i++) {
				pobject.put("make_" + i, tem_item.getString("make_" + i));
				pobject.put("model_" + i, tem_item.getString("model_" + i));

				pobject.put("Rect_Top_" + i, tem_item.getInt("Rect_Top_" + i));
				pobject.put("Rect_Left_" + i, tem_item.getInt("Rect_Left_" + i));
				pobject.put("Rect_Bottom_" + i,
						tem_item.getInt("Rect_Bottom_" + i));
				pobject.put("Rect_Rigth_" + i,
						tem_item.getInt("Rect_Rigth_" + i));

			}

			pobject.put("Location_Lati", tem_item.getString("Location_Lati"));
			pobject.put("Location_Longti",
					tem_item.getString("Location_Longti"));
			
			

			pobject.put("focalLength", tem_item.getString("focalLength"));
			pobject.put("flash", tem_item.getString("flash"));
			pobject.put("exposuretime", tem_item.getString("exposuretime"));
			pobject.put("image_make", tem_item.getString("image_make"));
			pobject.put("imagemodel", tem_item.getString("imagemodel"));
			pobject.put("whitebalance", tem_item.getString("whitebalance"));

			pobject.put("imgName", tem_item.get("imgName"));

			// imageStream=new FileInputStream(mCurrentPhotoPath);
			// final byte[] data = IOUtils.toByteArray(imageStream);
			// final byte[] data=imageStream.
			Bitmap bm = BitmapFactory
					.decodeFile(tem_item.getString("savepath"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
			byte[] data = baos.toByteArray();
			// ParseFile imgFile = new ParseFile(imageFileName
			// + JPEG_FILE_SUFFIX, data);
			ParseFile imgFile = new ParseFile(tem_item.getString("imgName"),
					data);

			pobject.put("imagefile", imgFile);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//
	public static boolean wifi_connection(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
//		boolean is3G=false; = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//				.isConnectedOrConnecting();
		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		if (isWifi) {
			return true;

		} else {
			return false;
		}
	}

	public static void setAutoOrientationEnabled(ContentResolver resolver, boolean enabled)
	{
	  Settings.System.putInt(resolver, Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
	}

}
