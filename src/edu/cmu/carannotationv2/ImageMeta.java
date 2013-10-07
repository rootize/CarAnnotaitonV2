package edu.cmu.carannotationv2;

import java.util.Date;

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
			
		} catch (Exception e) {
			Log.d("ImageData", "Exif not found");
		}
		
		
		

	}
	
	public void setOfflineTag(){
		this.lati=this.lati+"off";
		this.longti=this.longti+"off";
	}

}
