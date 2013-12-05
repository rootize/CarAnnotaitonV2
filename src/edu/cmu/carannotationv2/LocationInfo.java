package edu.cmu.carannotationv2;

import java.util.List;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;

public class LocationInfo {
	private LocationManager locationManager=null;
	//private MyLocationListener locationListener=null;
	private Context context;
	private String locationLatString;
	private String locationLongString;

	public LocationInfo(Context context){
		this.context=context;
		locationManager = (LocationManager)context.   getSystemService(Context.LOCATION_SERVICE);
		getLoation();  
		//	locationListener=new MyLocationListener();

	}

	public  void getLoation(){

		Location bestResult=null;
		int minDistance=500 ;
		int minTime=600000;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;
		List<String> matchingProviders = locationManager.getAllProviders();
		for (String provider: matchingProviders) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();

				if ((time > minTime && accuracy < bestAccuracy)) {
					bestResult = location;
					bestAccuracy = accuracy;
					bestTime = time;
				}
				else if (time < minTime && 
						bestAccuracy == Float.MAX_VALUE && time > bestTime){
					bestResult = location;
					bestTime = time;
				}
			}
		}
		locationLatString= bestResult.getLatitude()+"";
		locationLongString=bestResult.getLongitude()+"";
	}

	public String getLocationLong(){
		return locationLongString;
	}
	public String getLocationLati(){
		return locationLatString;
	}

	//private Boolean displayGpsStatus() {  
	//	  ContentResolver contentResolver =   context.getContentResolver();
	//	   
	//	  boolean gpsStatus = Settings.Secure  
	//	  .isLocationProviderEnabled(contentResolver,   
	//	  LocationManager.GPS_PROVIDER);  
	//	  if (gpsStatus) {  
	//	   return true;  
	//	  
	//	  } else {  
	//	   return false;  
	//	  }  
	//	 }  
}
