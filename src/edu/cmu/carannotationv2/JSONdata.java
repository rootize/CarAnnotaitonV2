package edu.cmu.carannotationv2;

import java.util.ArrayList;

import org.json.JSONObject;

public class JSONdata {
	private static final int MAX_ELEMETNS = 5;
	private ScaleRatio scaleRatio;
	private JSONObject jsonObject;
	private int rectCounts;
	private ArrayList<RectInfo> rectsImageView;
	private ArrayList<RectInfo> rectsBitmap;

	private String[] make;
	private String[] model;

	public ScaleRatio getScaleRatio() {
		return scaleRatio;
	}

	public void setScaleRatio(ScaleRatio scaleRatio) {
		this.scaleRatio = scaleRatio;
	}

}
