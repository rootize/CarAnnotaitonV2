package edu.cmu.carannotationv2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

//import org.apache.commons.io.filefilter.FalseFileFilter;
import org.json.JSONException;
import org.json.JSONObject;
import com.parse.ParseFile;
import com.parse.ParseObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

public class JSONdata {
	private double w_sr=1;
	private double h_sr=1;
	
	private static final int SAVED_WIDTH=1920;
	private static final int SAVED_HEIGHT=1440;
	public static final String SP_STRING = "loc_info";
	private static final int MAX_ELEMETNS = 5;
	//private static final int SAMPLESIZE=1;
	// private ScaleRatio scaleRatio;
	private JSONObject jsonObject;
	private String mCurrentPhotoPath;
	private SharedPreferences locSP;
	private ExifInterface exif;
	private String lati_longti;
    
//	
	private void setScaleFactor(
			String mCurrentPhotoPath2) {
		
		

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		//w_sr=photo
		w_sr=(double)SAVED_WIDTH/photoW;
		h_sr=(double)SAVED_HEIGHT/photoH;
		
	}

	
	public JSONdata() {

	}

	public JSONObject getJsonObject() {
		return this.jsonObject;
	}

	public JSONdata(JSONObject jo) {
		this.jsonObject = jo;
	}

	public JSONdata(AnnotatorInput annotatorInput, Context context) {
		jsonObject = new JSONObject();
		locSP = context.getSharedPreferences(SP_STRING, Context.MODE_PRIVATE);
		
		mCurrentPhotoPath = annotatorInput.getImgPath();
		lati_longti=annotatorInput.getLocationinfo();
		
		getInfoFromAnnotatorInput(annotatorInput);
		
		
		try {
			exif = new ExifInterface(mCurrentPhotoPath);

			getInfoFromExif(annotatorInput.isWifiStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getInfoFromAnnotatorInput(AnnotatorInput annotatorInput) {

		ArrayList<RectInfo> tempRectInfos = annotatorInput.getRectTosend();
		ArrayList<String> tempMakes = annotatorInput.getMake();
		ArrayList<String> tempModels = annotatorInput.getModel();
		ScaleRatio sr = annotatorInput.getScaleRatio();
		int annoNum = tempRectInfos.size();
		try {
			jsonObject
					.put(ParseAtributes.IMG_NAME, annotatorInput.getImgName());
			jsonObject
					.put(ParseAtributes.IMG_PATH, annotatorInput.getImgPath());
			jsonObject.put(ParseAtributes.USR, annotatorInput.getUsr());

			for (int i = 0; i < MAX_ELEMETNS; i++) {
				if (i < annoNum) {

					jsonObject.put(ParseAtributes.TOP + i, tempRectInfos.get(i)
							.getRectUpper() * sr.getH_scalefactor()*h_sr);
					jsonObject.put(ParseAtributes.LEFT + i, tempRectInfos
							.get(i).getRectLeft() * sr.getW_scalefactor()*w_sr);
					jsonObject.put(ParseAtributes.BOTTOM + i, tempRectInfos
							.get(i).getRectBottom() * sr.getH_scalefactor()*h_sr);
					jsonObject.put(
							ParseAtributes.RIGHT + i,
							tempRectInfos.get(i).getRectRight()
									* sr.getW_scalefactor()*w_sr);
					jsonObject.put(ParseAtributes.MAKE + i, tempMakes.get(i)
							.toString());
					jsonObject.put(ParseAtributes.MODEL + i, tempModels.get(i)
							.toString());
				} else {
					jsonObject.put(ParseAtributes.TOP + i,
							ParseAtributes.NULL_NUM);
					jsonObject.put(ParseAtributes.LEFT + i,
							ParseAtributes.NULL_NUM);
					jsonObject.put(ParseAtributes.BOTTOM + i,
							ParseAtributes.NULL_NUM);
					jsonObject.put(ParseAtributes.RIGHT + i,
							ParseAtributes.NULL_NUM);
					jsonObject.put(ParseAtributes.MAKE + i,
							ParseAtributes.NULL_STRING);
					jsonObject.put(ParseAtributes.MODEL + i,
							ParseAtributes.NULL_STRING);

				}
			}

		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	private void getInfoFromExif(boolean wifi_connected) {
		if (wifi_connected) {
			getInfo_online();
		} else {
			getInfo_offline();
		}
		getNonLocInfoFromExif();

	}

	private boolean turn1toture(String x) {

		int num = Integer.parseInt(x);
		if (1 == num) {
			return true;
		} else {
			return false;
		}

	}

	private void getNonLocInfoFromExif() {
		try {
			
			if (exif.getLatLong(null)!=false) {
			float[] b=new float[2];
			exif.getLatLong(b);
			jsonObject.put(ParseAtributes.LATI_LOCATION,
					(b[0]+""));
			jsonObject.put(ParseAtributes.LONGTI_LOCATION,
					(b[1]+""));
			
			
			}
			
			jsonObject.put(ParseAtributes.FOCAL_LENGTH,
				(exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)));
			jsonObject.put(ParseAtributes.FLASH,
					turn1toture(exif.getAttribute(ExifInterface.TAG_FLASH)));
			jsonObject.put(ParseAtributes.EXPOTIME,
					Double.parseDouble(exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)));
			jsonObject.put(ParseAtributes.CAM_MAKE,
					exif.getAttribute(ExifInterface.TAG_MAKE));
			jsonObject.put(ParseAtributes.CAM_MODEL,
					exif.getAttribute(ExifInterface.TAG_MODEL));
			jsonObject.put(ParseAtributes.WH_BLN,
					exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE));
			jsonObject.put(ParseAtributes.IMAGE_WIDTH, exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH,0));
			jsonObject.put(ParseAtributes.IMAGE_HEIGHT, exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0));
//			
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	private void getInfo_offline() {
		try {
			jsonObject.put(ParseAtributes.LATI_LOCATION,
					locSP.getString(ParseAtributes.LATI_LOCATION, "")
							+ ParseAtributes.GPS_OFFLINE);
			jsonObject.put(ParseAtributes.LONGTI_LOCATION,
					locSP.getString(ParseAtributes.LONGTI_LOCATION, "")
							+ ParseAtributes.GPS_OFFLINE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getInfo_online() {
		String lati ;
		String longti;
		
		String []lati_longti_array=lati_longti.split(" ");
		Log.d("Longti_lati", lati_longti);
		//Log.d(longti_l, msg)
		if (lati_longti_array.length==2) {
			lati=lati_longti_array[0];
			longti=lati_longti_array[1];
		}else {
			lati=" ";
			longti=" ";
		}
		
//		if () {
//			
//		}
		try {
			jsonObject.put(ParseAtributes.LATI_LOCATION, lati);
			jsonObject.put(ParseAtributes.LONGTI_LOCATION, longti);
			Editor editor = locSP.edit();
			editor.putString(ParseAtributes.LATI_LOCATION, lati);
			editor.putString(ParseAtributes.LONGTI_LOCATION, longti);
			editor.commit();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ParseObject formatParseObject(String parseClassName) {
		ParseObject tempObject = new ParseObject(
				parseClassName);
		convertToParseObject(tempObject);
		addImgFile(tempObject);

		return tempObject;
	}

	private void addImgFile(ParseObject tempObject) {
		try {
			
			BitmapFactory.Options options=new BitmapFactory.Options();
			//options.inSampleSize=SAMPLESIZE;
			options.outHeight=SAVED_HEIGHT;
			options.outWidth=SAVED_WIDTH;
			
			options.inSampleSize=1;
			FileInputStream isf=new FileInputStream(new File(jsonObject.getString(ParseAtributes.IMG_PATH)));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b=new byte[1024];
			int bytesRead;
			while (( bytesRead=isf.read(b))!=-1) {
				baos.write(b,0,bytesRead);
				
			}
			byte[] data = baos.toByteArray();
			isf.close();
			ParseFile imgFile;

			imgFile = new ParseFile(
					jsonObject.getString(ParseAtributes.IMG_NAME), data);
			tempObject.put(ParseAtributes.IMG_FILE, imgFile);
			data=null;
			System.gc();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void convertSingleJsonItem(String itemName, ParseObject po,
			JSONObject jo) {
		try {
			po.put(itemName, jo.get(itemName));
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}

	private void convertToParseObject(ParseObject pobject) {
		//ParseACL defaultACL = new ParseACL();
		// defaultACL.setPublicReadAccess(false);
		// ParseACL.setDefaultACL(defaultACL, true);

		try {

			convertSingleJsonItem(ParseAtributes.USR, pobject, jsonObject);
			for (int i = 0; i < MAX_ELEMETNS; i++) {
				convertSingleJsonItem(ParseAtributes.MAKE + i, pobject,
						jsonObject);
				convertSingleJsonItem(ParseAtributes.MODEL + i, pobject,
						jsonObject);
				convertSingleJsonItem(ParseAtributes.TOP + i, pobject,
						jsonObject);
				convertSingleJsonItem(ParseAtributes.LEFT + i, pobject,
						jsonObject);
				convertSingleJsonItem(ParseAtributes.BOTTOM + i, pobject,
						jsonObject);
				convertSingleJsonItem(ParseAtributes.RIGHT + i, pobject,
						jsonObject);
			}

			convertSingleJsonItem(ParseAtributes.LATI_LOCATION, pobject,
					jsonObject);
			convertSingleJsonItem(ParseAtributes.LONGTI_LOCATION, pobject,
					jsonObject);
			convertSingleJsonItem(ParseAtributes.FLASH, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.FOCAL_LENGTH, pobject,
					jsonObject);
			convertSingleJsonItem(ParseAtributes.EXPOTIME, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.CAM_MAKE, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.CAM_MODEL, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.WH_BLN, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.IMG_NAME, pobject, jsonObject);
            convertSingleJsonItem(ParseAtributes.IMAGE_HEIGHT, pobject, jsonObject);
            convertSingleJsonItem(ParseAtributes.IMAGE_WIDTH,pobject, jsonObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
