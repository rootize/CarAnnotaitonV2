package edu.cmu.carannotationv2;

import android.graphics.BitmapFactory;

public class ScaleRatio {

	private double w_scalefactor;
	private double h_scalefactor;
	
	public double getW_scalefactor() {
		return w_scalefactor;
	}
	
	public double getH_scalefactor() {
		return h_scalefactor;
	}
	public ScaleRatio(DrawImageView iv, String bitmappath){
	int targetH=iv.getHeight();
	int targetW=iv.getWidth();
	BitmapFactory.Options bOptions=new BitmapFactory.Options();
	bOptions.inJustDecodeBounds=true;
	BitmapFactory.decodeFile(bitmappath, bOptions);
	int sourceH=bOptions.outHeight;
	int sourceW=bOptions.outWidth;
	w_scalefactor=1;
	h_scalefactor=1;
	
	if (targetH > 0 && targetW > 0) {
		w_scalefactor = (double) sourceW / targetW;
		h_scalefactor = (double) sourceH / targetH;

	}
		
	}
	public ScaleRatio(double w,double h){
		this.w_scalefactor=w;
		this.h_scalefactor=h;
	}
}
