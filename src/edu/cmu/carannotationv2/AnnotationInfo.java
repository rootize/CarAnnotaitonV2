package edu.cmu.carannotationv2;

import android.graphics.Rect;

public class AnnotationInfo {
private static final String RECTLEFT_STRING="rectLeft";
private static final String RECTRIGHT_STRING="rectRight";
private static final String RECTTOP_STRING="rectTop";
private static final String RECTBOTTOM_STRING="rectBottom";
private static final String MAKEID_STRING="makeId";
private static final String MODELID_STRING="modelId";

private int makeid;
private int modelid;
private double rectLeft;
private double rectRight;
private double rectTop;
private double rectBottom;

public void setRect(double l,double t,double r,double b){
	this.rectLeft=l;
	this.rectTop=t;
	this.rectRight=r;
	this.rectBottom=b;
	
}
public void setRect(Rect r){
	this.rectLeft=r.left;
	this.rectTop=r.top;
	this.rectRight=r.right;
	this.rectBottom=r.bottom;
	
}

public void setMake(String makeString){
	//do some hash mapping! here!
	
}
public void setModel(String modelString){
	// do some hash mapping here !
	
}
public void setRectRatio(Object rectSize) {
	// TODO Auto-generated method stub
	
}
public void setRect(Rect lastRect, ScaleRatio mScaleRatio) {
	this.rectLeft=lastRect.left*mScaleRatio.getW_scalefactor();
	this.rectRight=lastRect.right*mScaleRatio.getW_scalefactor();
	this.rectTop= lastRect.top*mScaleRatio.getH_scalefactor();
	this.rectBottom=lastRect.bottom*mScaleRatio.getH_scalefactor();
	
}
}
