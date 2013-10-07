package edu.cmu.carannotationv2;

import java.io.File;
import java.util.Date;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.R.integer;
import android.graphics.Path;
import android.media.ExifInterface;
import android.util.Log;

public class ImageMeta {
	private static final String POBJECTNAME = "ImageInfo";
	private static final String IMAGEFILE_STRING = "imageFile";   // file
	private static final String IMAGEWIDTH_STRING = "imageWidth"; // number
	private static final String IMAGEHEIGHT_STRING = "imageHeight";// number
	private static final String EXPOSURETIME_STRING = "exposureTime";// number
	private static final String FLASH_STRING = "flash";// boolean
	private static final String FOCALLENGTH_STRING = "focalLength";// string
	private static final String WHITEBALA_STRING = "whitebalance"; // number
	private static final String CAMERAMAKE_STRING = "cameraMake";// string
	private static final String CAMERAMODEL_STRING = "cameraModel";// string
	private static final String TIME_STRING = "madeAt";// date
	private static final String LOCATIONLAT_STRING = "locationLat"; // string
	private static final String LOCATIONLONG_STRING = "locationLong";// string

	
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	private int imagewidth;
	private int imageheight;
	private int exposuretime;
	private boolean flash;
	private String focallength;
	private int whitebalance;
	private String cameraMake;
	private String cameraModel;
	private String imageDate;
	private String lati;
	private String longti;
	private ExifInterface exif;
	private String imageNameString;

	
	private int sendState;
	public ParseObject sendtoParse(ParseObject usrParseObject) {
		ParseObject imgObject=new ParseObject(POBJECTNAME);
		imgObject.add(IMAGEFILE_STRING, imageNameString);
		imgObject.add(IMAGEWIDTH_STRING, imagewidth);
		imgObject.add(IMAGEHEIGHT_STRING, imageheight);
		imgObject.add(EXPOSURETIME_STRING, exposuretime);
		imgObject.add(FLASH_STRING, flash);
		imgObject.add(FOCALLENGTH_STRING, focallength);
		imgObject.add(WHITEBALA_STRING, whitebalance);
		imgObject.add(CAMERAMAKE_STRING, cameraMake);
		imgObject.add(CAMERAMODEL_STRING, cameraModel);
		imgObject.add(TIME_STRING, imageDate);
		imgObject.add(LOCATIONLAT_STRING, lati);
		imgObject.add(LOCATIONLONG_STRING, longti);
		imgObject.put("createdBy", usrParseObject);
		imgObject.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
			if (arg0==null) {
				sendState=1;
			}else {
				sendState=0;
			}
				
			}
		});
		switch (sendState) {
		case 1:
			return imgObject;
			
		case 0:
		default:
			return null;
		}
	}
	
	
	public void setMetaData(String mcurrentSavingPath) {
		try {
			exif = new ExifInterface(mcurrentSavingPath);
            imagewidth=exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            imageheight=exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            exposuretime=exif.getAttributeInt(ExifInterface.TAG_EXPOSURE_TIME, 0);
            whitebalance=exif.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE,0);
            flash=exif.getAttributeInt(ExifInterface.TAG_FLASH, 0)==0?false:true;
            focallength=exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
            cameraMake=exif.getAttribute(ExifInterface.TAG_MAKE);
            cameraModel=exif.getAttribute(ExifInterface.TAG_MODEL);
            
          
            lati=exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)+exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            longti=exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)+exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
			if (mcurrentSavingPath!=null) {
				String[] splitPathStrings=mcurrentSavingPath.split(File.separator);
				imageNameString=splitPathStrings[splitPathStrings.length-1];
			}else {
				Log.d("ImageMeta", "Current input path is null");
				//not important!
				imageNameString="Unkown"+JPEG_FILE_SUFFIX;
				
			}
            
            
		} catch (Exception e) {
			Log.d("ImageData", "Exif not found");
		}
		
		
		

	}
	
	public void setOfflineTag(){
		this.lati=this.lati+"off";
		this.longti=this.longti+"off";
	}

	

}
