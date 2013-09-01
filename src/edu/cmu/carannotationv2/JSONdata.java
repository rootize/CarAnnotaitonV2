package edu.cmu.carannotationv2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseFile;
import com.parse.ParseObject;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

public class JSONdata {
	private static final int MAX_ELEMETNS = 5;
	private ScaleRatio scaleRatio;
	private JSONObject jsonObject;
	
	private SharedPreferences locSP;
	private String mCurrentPhotoPath;
	private ExifInterface exif;

	public JSONdata() {

	}

	public JSONdata(DrawImageView mImageView, 
			AnnotatorInput annotatorInput, boolean wifi_connected,
			Context context) {
		calculateScaleRatio(mImageView);
		getInfoFromAnnotatorInput(annotatorInput);
		try {
			exif = new ExifInterface(jsonObject.getString(ParseAtributes.IMG_PATH));
			getInfoFromExif(wifi_connected);
		} catch (Exception e) {

		}

		
		

	}

	private void getInfoFromAnnotatorInput(AnnotatorInput annotatorInput) {

		ArrayList<RectInfo> tempRectInfos = annotatorInput.getRectTosend();
		ArrayList<String> tempMakes = annotatorInput.getMake();
		ArrayList<String> tempModels = annotatorInput.getModel();

		int annoNum = tempRectInfos.size();
		try {
			jsonObject.put(ParseAtributes.IMG_NAME, annotatorInput.getImgName());
			jsonObject.put(ParseAtributes.IMG_PATH, annotatorInput.getImgPath());
			jsonObject.put(ParseAtributes.USR, annotatorInput.getUsr());
			
			for (int i = 0; i < MAX_ELEMETNS; i++) {
				if (i < annoNum) {

					jsonObject.put(ParseAtributes.TOP+i, tempRectInfos.get(i)
							.getRectUpper()*scaleRatio.getH_scalefactor());
					jsonObject.put(ParseAtributes.LEFT+i, tempRectInfos.get(i).getRectLeft()*scaleRatio.getW_scalefactor());
					jsonObject.put(ParseAtributes.BOTTOM+i, tempRectInfos.get(i).getRectBottom()*scaleRatio.getH_scalefactor());
					jsonObject.put(ParseAtributes.RIGHT+i, tempRectInfos.get(i).getRectRight()*scaleRatio.getW_scalefactor());
                    jsonObject.put(ParseAtributes.MAKE+i, tempMakes.get(i).toString());
                    jsonObject.put(ParseAtributes.MODEL+i, tempModels.get(i).toString());
				} else {
					jsonObject.put(ParseAtributes.TOP+i, ParseAtributes.NULL_STRING);
					jsonObject.put(ParseAtributes.LEFT+i, ParseAtributes.NULL_STRING);
					jsonObject.put(ParseAtributes.BOTTOM+i, ParseAtributes.NULL_STRING);
					jsonObject.put(ParseAtributes.RIGHT+i, ParseAtributes.NULL_STRING);
					jsonObject.put(ParseAtributes.MAKE+i, ParseAtributes.NULL_STRING);
                    jsonObject.put(ParseAtributes.MODEL+i, ParseAtributes.NULL_STRING);

				}
			}
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}

	private void calculateScaleRatio(DrawImageView div) {

		this.scaleRatio = new ScaleRatio(div, mCurrentPhotoPath);

	}

	private void getInfoFromExif(boolean wifi_connected) {
		if (wifi_connected) {
			getInfo_online();
		} else {
			getInfo_offline();
		}
		getNonLocInfoFromExif();

	}

	private void getNonLocInfoFromExif() {
		try {
			jsonObject.put(ParseAtributes.FOCAL_LENGTH,
					exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
			jsonObject.put(ParseAtributes.FLASH,
					exif.getAttribute(ExifInterface.TAG_FLASH));
			jsonObject.put(ParseAtributes.EXPOTIME,
					exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME));
			jsonObject.put(ParseAtributes.CAM_MAKE,
					exif.getAttribute(ExifInterface.TAG_MAKE));
			jsonObject.put(ParseAtributes.CAM_MODEL,
					exif.getAttribute(ExifInterface.TAG_MODEL));
			jsonObject.put(ParseAtributes.WH_BLN,
					exif.getAttribute(ExifInterface.TAG_WHITE_BALANCE));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void getInfo_offline() {
		try {
			jsonObject.put(ParseAtributes.LATI_LOCATION,
					locSP.getString(ParseAtributes.LATI_LOCATION, ""));
			jsonObject.put(ParseAtributes.LONGTI_LOCATION,
					locSP.getString(ParseAtributes.LONGTI_LOCATION, ""));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getInfo_online() {
		String lati = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
				+ exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
		String longti = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
				+ exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
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

	public ScaleRatio getScaleRatio(){
		return this.scaleRatio;
	}
	public void setScaleRatio(ScaleRatio scaleRatio) {
		this.scaleRatio = scaleRatio;
	}

	
	public ParseObject formatParseObject(){
		ParseObject tempObject=new ParseObject(ParseAtributes.PARSE_CLASS_NAME);
		convertToParseObject(tempObject);
		addImgFile(tempObject);
		
		return tempObject;
	}

	private void addImgFile(ParseObject tempObject) {
		try {
		Bitmap bm = BitmapFactory
				.decodeFile(jsonObject.getString(ParseAtributes.IMG_PATH));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
		byte[] data = baos.toByteArray();
		// ParseFile imgFile = new ParseFile(imageFileName
		// + JPEG_FILE_SUFFIX, data);
		ParseFile imgFile;
		
			imgFile = new ParseFile(jsonObject.getString(ParseAtributes.IMG_NAME),
					data);
			tempObject.put(ParseAtributes.IMG_FILE, imgFile);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		
		
	}

	private void convertSingleJsonItem(String itemName,ParseObject po, JSONObject jo){
		try {
			po.put(itemName, jo.get(itemName));
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	private void convertToParseObject(ParseObject pobject) {
		try {
			
			convertSingleJsonItem(ParseAtributes.USR,pobject,jsonObject);
			for (int i = 0; i < MAX_ELEMETNS; i++) {
			convertSingleJsonItem(ParseAtributes.MAKE+i, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.MODEL+i, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.TOP+i, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.LEFT+i, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.BOTTOM+i, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.RIGHT+i, pobject, jsonObject);
			}


			
			convertSingleJsonItem(ParseAtributes.LATI_LOCATION, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.LONGTI_LOCATION, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.FLASH, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.FOCAL_LENGTH, pobject, jsonObject);
			convertSingleJsonItem(ParseAtributes.EXPOTIME, pobject, jsonObject);
            convertSingleJsonItem(ParseAtributes.CAM_MAKE, pobject, jsonObject);
            convertSingleJsonItem(ParseAtributes.CAM_MODEL, pobject, jsonObject);
            convertSingleJsonItem(ParseAtributes.WH_BLN, pobject, jsonObject);
            convertSingleJsonItem(ParseAtributes.IMG_NAME, pobject, jsonObject);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
}
