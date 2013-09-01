package edu.cmu.carannotationv2;

import java.util.ArrayList;

public class AnnotatorInput {
	private static final int CAP=5;
	private ArrayList<RectInfo> rectTosend;
	private ArrayList<String> make;
	private ArrayList<String> model;
	private String mCurrentPhotoPath;
	private String mImageFileName;
	private String usr;
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
		make.add(sMake);
		model.add(sModel);
	}

	public void addPath(String mCurrentPhotoPath) {
		this.mCurrentPhotoPath=mCurrentPhotoPath;
	}

	public void addImgName(String mImageFileName) {
		this.mImageFileName=mImageFileName;
		
	}

	public Object getImgName() {
		
		return this.mImageFileName;
	}

	public Object getImgPath() {
		
		return this.mCurrentPhotoPath;
	}

	public void addUsr(String usr_name) {
		this.usr=usr_name;
		
	}
	
	public String getUsr(){
		return this.usr;
	}

}
