package edu.cmu.carannotationv2;





//by sequence: upper left bottom right
public class RectInfo {
private int rectUpper;
private int rectLeft;
private int rectBottom;
private int rectRight;
public int getRectUpper() {
	return rectUpper;
}
public void setRectUpper(int rectUpper) {
	this.rectUpper = rectUpper;
}
public int getRectLeft() {
	return rectLeft;
}
public void setRectLeft(int rectLeft) {
	this.rectLeft = rectLeft;
}
public int getRectBottom() {
	return rectBottom;
}
public void setRectBottom(int rectBottom) {
	this.rectBottom = rectBottom;
}
public int getRectRight() {
	return rectRight;
}
public void setRectRight(int rectRight) {
	this.rectRight = rectRight;
}

public RectInfo(int upper,int left,int bottom,int right){
	this.rectUpper=upper;
	this.rectLeft=left;
	this.rectBottom=bottom;
	this.rectRight=right;
}

}
