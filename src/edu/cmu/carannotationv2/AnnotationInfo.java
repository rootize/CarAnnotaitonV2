package edu.cmu.carannotationv2;

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
public void setMake(String makeString){
	//do some hash mapping! here!
	
}
public void setModel(String modelString){
	// do some hash mapping here !
	
}
}
