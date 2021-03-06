package edu.cmu.carannotationv2;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.graphics.Rect;

public class AnnotationInfo {
	private static final String POBJECTNAME_STRING = "Annotation";
	private static final String RECTLEFT_STRING = "rectLeft";
	private static final String RECTRIGHT_STRING = "rectRight";
	private static final String RECTTOP_STRING = "rectTop";
	private static final String RECTBOTTOM_STRING = "rectBottom";
	private static final String MAKEID_STRING = "makeId";
	private static final String MODELID_STRING = "modelId";

	private int makeid;
	private int modelid;
	private double rectLeft;
	private double rectRight;
	private double rectTop;
	private double rectBottom;

	private boolean isSent;

	public AnnotationInfo() {
		

	}
	
	public AnnotationInfo(JSONObject object) {
		// TODO Auto-generated constructor stub
		try {
			makeid = object.getInt(MAKEID_STRING);
			modelid = object.getInt(MODELID_STRING);
			rectLeft = object.getDouble(RECTLEFT_STRING);
			rectRight = object.getDouble(RECTRIGHT_STRING);

			rectTop = object.getDouble(RECTTOP_STRING);
			rectBottom = object.getDouble(RECTBOTTOM_STRING);

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setRect(double l, double t, double r, double b) {
		this.rectLeft = l;
		this.rectTop = t;
		this.rectRight = r;
		this.rectBottom = b;

	}

	public void setRect(Rect r) {
		this.rectLeft = r.left;
		this.rectTop = r.top;
		this.rectRight = r.right;
		this.rectBottom = r.bottom;

	}

	public void setMake(String makeString) {
		// do some hash mapping! here!

	}

	public void setModel(String modelString) {
		// do some hash mapping here !

	}

	public void setRectRatio(Object rectSize) {
		// TODO Auto-generated method stub

	}

	public void setRect(Rect lastRect, ScaleRatio mScaleRatio) {
		this.rectLeft = lastRect.left * mScaleRatio.getW_scalefactor();
		this.rectRight = lastRect.right * mScaleRatio.getW_scalefactor();
		this.rectTop = lastRect.top * mScaleRatio.getH_scalefactor();
		this.rectBottom = lastRect.bottom * mScaleRatio.getH_scalefactor();

	}

	public boolean sendtoParse(ParseObject usrParseObject,
			ParseObject imageParseObject) {
		// TODO Auto-generated method stub
		ParseObject annoObject = new ParseObject(POBJECTNAME_STRING);
		annoObject.add(MAKEID_STRING, makeid);
		annoObject.add(MODELID_STRING, modelid);
		annoObject.add(RECTBOTTOM_STRING, rectBottom);
		annoObject.add(RECTLEFT_STRING, rectLeft);
		annoObject.add(RECTRIGHT_STRING, rectRight);
		annoObject.add(RECTTOP_STRING, rectTop);
		annoObject.put("createBy", usrParseObject);
		annoObject.put("on", imageParseObject);

		annoObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				if (arg0 == null) {
					isSent = true;
				} else {
					isSent = false;
				}
			}
		});

		return isSent;
	}

	public Object saveOffline() throws JSONException {
		// TODO Auto-generated method stub
		JSONObject annoObject = new JSONObject(POBJECTNAME_STRING);
		annoObject.put(MAKEID_STRING, makeid);
		annoObject.put(MODELID_STRING, modelid);
		annoObject.put(RECTBOTTOM_STRING, rectBottom);
		annoObject.put(RECTLEFT_STRING, rectLeft);
		annoObject.put(RECTRIGHT_STRING, rectRight);
		annoObject.put(RECTTOP_STRING, rectTop);
		return annoObject;
	}
}
