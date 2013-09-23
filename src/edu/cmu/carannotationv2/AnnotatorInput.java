package edu.cmu.carannotationv2;

import java.util.ArrayList;

import android.util.Log;

public class AnnotatorInput {
	private static final int CAP=5;
	private ArrayList<RectInfo> rectTosend;
	private ArrayList<String> make;
	private ArrayList<String> model;
	private String mCurrentPhotoPath;
	private String mImageFileName;
	private String usr;
	private ScaleRatio mScaleRatio;
	private boolean wifiStatus;
	public AnnotatorInput(){
		this.setRectTosend(new ArrayList<RectInfo>(CAP));
		this.setMake(new ArrayList<String>(CAP));
		this.setModel(new ArrayList<String>(CAP));
		
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
	
	public void  update(RectInfo ri,String sMake, String sModel) {
		rectTosend.add(ri);
		if (sMake==null) {
			make.add("null");
		}else {
			make.add(sMake);
		}
		if (sModel==null) {
			model.add("null");
		}else {
			model.add(sModel);
		}
		
	}

	public void addPath(String mCurrentPhotoPath) {
		this.mCurrentPhotoPath=mCurrentPhotoPath;
		Log.d("Path", mCurrentPhotoPath);
	}

	public void addImgName(String mImageFileName) {
		this.mImageFileName=mImageFileName;
		Log.d("ImgName", mImageFileName);
		
	}

	public String getImgName() {
		
		return mImageFileName;
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

	
	
}
