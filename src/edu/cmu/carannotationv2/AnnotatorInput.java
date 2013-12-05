package edu.cmu.carannotationv2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.parse.ParseFile;
import com.parse.ParseObject;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class AnnotatorInput {
//	private static final String SENTFILEDIR_STRING="/CarAnnotationSentFiles";
	private static  int num_annotation=0;
	private ArrayList<RectInfo> rectTosend;
	private ArrayList<String> make;
	private ArrayList<String> model;
	private String mCurrentPhotoPath;
	private String mCurrentPhotoName;
	private String usr;
	private  ScaleRatio mScaleRatio;
	private String locationinfo;
	private boolean wifiStatus;
	private JSONObject appUserJsonObject;
	private JSONObject imageInfoJsonObject;
	private JSONArray annotaitonInfoJsonArray;
	private static final String LOCATIONSP="offline_location_carannotationV2";
	private static final String LONGSP="offline_long";
	private static final String LATISP="offline_lati";
	private SharedPreferences offlineLocationSP;


	
//	public AnnotatorInput(){
//		this.setRectTosend(new ArrayList<RectInfo>(CAP));
//		this.setMake(new ArrayList<String>(CAP));
//		this.setModel(new ArrayList<String>(CAP));
//
//	}

   public boolean scaleRatioDefined() {
	   if (mScaleRatio==null) {
		   return false;
		
	}else {
		return true;
	}
	
}
	
	public  AnnotatorInput(String usrString) {
		num_annotation=0;
		appUserJsonObject=new JSONObject();
		imageInfoJsonObject=new JSONObject();
		annotaitonInfoJsonArray=new JSONArray();
		try {
			appUserJsonObject.put("email", usrString);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public AnnotatorInput(File file) {
		// TODO Auto-generated constructor stub
		
		try {
			JSONObject tempJsonObject=new JSONObject(FileOperation.readfromExternal(file));
			
			appUserJsonObject= new JSONObject(tempJsonObject.getString("Usr"));
			imageInfoJsonObject=new JSONObject(tempJsonObject.getString("ImageInfo"));
			annotaitonInfoJsonArray=new JSONArray(tempJsonObject.getString("annotaitonInfo"));
			
			
//			annotaitonInfoJsonArray=new JSONArray(tempJsonObject);
			
			
			imageInfoJsonObject=tempJsonObject.getJSONObject("annotationInfo");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
//		parseObject.put("Usr", appUserJsonObject.toString());
//		parseObject.put("ImageInfo", imageInfoJsonObject.toString());
//		parseObject.put("annotaitonInfo", annotaitonInfoJsonArray.toString());
		
	}

	public void setImageUploadedOnline(boolean isWifi){
		try {
			imageInfoJsonObject.put("wifiEnabled", isWifi);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	public void setImageLocationInfo(String Long,String Lati) {
		try {
			imageInfoJsonObject.put("locationLong", Long);
			imageInfoJsonObject.put("locationLat", Lati);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void setImageInfo(String photoPathString, String photoNameString){
		mCurrentPhotoPath=photoPathString;
		mCurrentPhotoName=photoNameString;
		setImageNameInfo();
		setImageExifInfo();
	}
	
	
	public void setImageInfo(String photoPathString){
		mCurrentPhotoPath=photoPathString;
		File tempFile=new File(mCurrentPhotoPath);
		mCurrentPhotoName=tempFile.getName();
		//add image file later, currently just add image path
		setImageNameInfo();
		setImageExifInfo();
	
	}
private void setImageNameInfo(){
	try {
		imageInfoJsonObject.put(ParseAtributes.IMG_NAME, mCurrentPhotoName);
		imageInfoJsonObject.put(ParseAtributes.IMG_FILE, mCurrentPhotoPath);
		
	} catch (Exception e) {
		// TODO: handle exception
	}
}
	
	private void setImageExifInfo() {
		if (mCurrentPhotoPath!=null) {
			try {
				ExifInterface photoExifInterface=new ExifInterface(mCurrentPhotoPath);
				imageInfoJsonObject.put(ParseAtributes.FOCAL_LENGTH,
						(photoExifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)));
				imageInfoJsonObject.put(ParseAtributes.FLASH,
						Utils.turn1toture(photoExifInterface.getAttribute(ExifInterface.TAG_FLASH)));
				imageInfoJsonObject.put(ParseAtributes.EXPOTIME,
						Double.parseDouble(photoExifInterface.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)));
				imageInfoJsonObject.put(ParseAtributes.CAM_MAKE,
						photoExifInterface.getAttribute(ExifInterface.TAG_MAKE));
				imageInfoJsonObject.put(ParseAtributes.CAM_MODEL,
						photoExifInterface.getAttribute(ExifInterface.TAG_MODEL));
				imageInfoJsonObject.put(ParseAtributes.WH_BLN,
						photoExifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE));
				imageInfoJsonObject.put(ParseAtributes.IMAGE_WIDTH, photoExifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH,0));
				imageInfoJsonObject.put(ParseAtributes.IMAGE_HEIGHT, photoExifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0));
				imageInfoJsonObject.put(ParseAtributes.CREATEDATE_STRING, photoExifInterface.getAttribute(ExifInterface.TAG_DATETIME));
				imageInfoJsonObject.put(ParseAtributes.WIFI_STATUS, wifiStatus);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else {
			Log.d("Error", "Image null!");
		}
		

	}

	public void updateLocationInfo(Context context){


		if (Utils.isWifi(context)) {
			LocationInfo locationInfo=new LocationInfo(context);
			setImageLocationInfo(locationInfo.getLocationLong(), locationInfo.getLocationLati());
			SharedPreferences.Editor editor=offlineLocationSP.edit();
			editor.putString(LONGSP, locationInfo.getLocationLong());
			editor.putString(LATISP, locationInfo.getLocationLati());
			editor.commit();
		}else {
			setImageLocationInfo(offlineLocationSP.getString(LONGSP, ""), offlineLocationSP.getString(LATISP, ""));
		}


	}
	public ArrayList<RectInfo> getRectTosend() {
		return rectTosend;
	}

	public void setRectTosend(ArrayList<RectInfo> rectTosend) {
		this.rectTosend = rectTosend;
	}

	public ArrayList<String> getMake() {
		return make;
	}

	public void setMake(ArrayList<String> make) {
		this.make = make;
	}

	public ArrayList<String> getModel() {
		return model;
	}

	public void setModel(ArrayList<String> model) {
		this.model = model;
	}

	public void  updateAnnotation(RectInfo ri,String sMake, String sModel) {
		JSONObject temp=new JSONObject();
		try {
			temp.put(ParseAtributes.MAKE, sMake);
			temp.put(ParseAtributes.MODEL, sModel);
			temp.put(ParseAtributes.RIGHT, ri.getRectRight()*mScaleRatio.getW_scalefactor());
			temp.put(ParseAtributes.BOTTOM, ri.getRectBottom()*mScaleRatio.getH_scalefactor());
			temp.put(ParseAtributes.TOP, ri.getRectUpper()*mScaleRatio.getH_scalefactor());
			temp.put(ParseAtributes.LEFT, ri.getRectLeft()*mScaleRatio.getW_scalefactor());
			
			annotaitonInfoJsonArray.put(num_annotation, temp);
		
			num_annotation=num_annotation+1;
			
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void addPath(String mCurrentPhotoPath) {
		this.mCurrentPhotoPath=mCurrentPhotoPath;
		Log.d("Path", mCurrentPhotoPath);
	}

	public void addImgName(String mImageFileName) {
		this.mCurrentPhotoName=mImageFileName;
		Log.d("ImgName", mImageFileName);

	}

	public String getImgName() {

		return mCurrentPhotoName;
	}

	public String getImgPath() {

		return mCurrentPhotoPath;
	}

	public void addUsr(String usr_name) {
		this.usr=usr_name;

	}

	public String getUsr(){
		return this.usr;
	}

	public void addScaleRatio(ScaleRatio scaleRatio) {
		// TODO Auto-generated method stub
		this.setScaleRatio(new ScaleRatio(scaleRatio.getW_scalefactor(), scaleRatio.getH_scalefactor()));
		

		
	}


	public ScaleRatio getScaleRatio() {
		return mScaleRatio;
	}

	public void setScaleRatio(ScaleRatio mScaleRatio) {
		this.mScaleRatio = mScaleRatio;
	}

	public void addWifiStatus(boolean wifi_connected) {
		setWifiStatus(wifi_connected);

	}

	public boolean isWifiStatus() {
		return wifiStatus;
	}

	public void setWifiStatus(boolean wifiStatus) {
		this.wifiStatus = wifiStatus;
	}

	public String getLocationinfo() {
		return locationinfo;
	}

	public void setLocationinfo(String locationinfo) {
		this.locationinfo = locationinfo;
	}

	public ParseObject export2ParseObject(String theClassName) {
		// TODO Auto-generated method stub
		ParseObject pObject=new ParseObject(theClassName);
		addImgFile(pObject);
		addJsonInfo(pObject);
		return pObject;
	}
	private void addJsonInfo(ParseObject parseObject){
		
		parseObject.put("Usr", appUserJsonObject.toString());
		parseObject.put("ImageInfo", imageInfoJsonObject.toString());
		parseObject.put("annotaitonInfo", annotaitonInfoJsonArray.toString());
	}
	
	private void addImgFile(ParseObject tempObject) {
		try {
			

			FileInputStream isf=new FileInputStream(new File(mCurrentPhotoPath));
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
					mCurrentPhotoName, data);
			tempObject.put(ParseAtributes.IMG_FILE, imgFile);
			data=null;
			System.gc();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void savetoOfflineFile(Context context) {
		// TODO Auto-generated method stub
		File saveFolder=new File(Environment.getExternalStorageDirectory().toString(),Main_screen.SENTFILEDIR_STRING);
		if (!saveFolder.exists()) {
			if (!saveFolder.mkdirs()) {
				Log.e("MainScreen", "Cannot create folder saving sent files");

			}
		}
		String tempFile=Utils.setFileNamebyDate();
		FileOperation.saveCostomizedDir(context,
				tempFile,saveFolder,
				convert2JsonString());

	
	}

public String convert2JsonString(){
	try {
		JSONObject tempObject=new JSONObject();
		tempObject.put("Usr", appUserJsonObject.toString());
		tempObject.put("ImageInfo", imageInfoJsonObject.toString());
		tempObject.put("annotaitonInfo", annotaitonInfoJsonArray.toString());
		return tempObject.toString();
	} catch (Exception e) {
		// TODO: handle exception
		return null;
	}
	
}

}
