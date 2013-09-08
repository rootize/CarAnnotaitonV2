package edu.cmu.carannotationv2;

public class imgSize {
private int imgWidth;
private int imgHeight;
private double aspectRatioWH;
public int getImgWidth() {
	return imgWidth;
}
public void setImgWidth(int imgWidth) {
	this.imgWidth = imgWidth;
}
public int getImgHeight() {
	return imgHeight;
}
public void setImgHeight(int imgHeight) {
	this.imgHeight = imgHeight;
}
public double getAspectRatioWH() {
	return aspectRatioWH;
}
public void setAspectRatioWH(double aspectRatioWH) {
	this.aspectRatioWH = aspectRatioWH;
}
public void setAspectRatioWH(){
	this.aspectRatioWH=(double)imgWidth/imgHeight;
}
}
