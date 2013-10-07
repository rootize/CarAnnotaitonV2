package edu.cmu.carannotationv2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.parse.ParseObject;

public class parseSendData {
	private static final String OFFLINEFILE = "OfflineFile";
	private static final String USER = "User";
	private static final String IMAGE_INFO = "ImageData";
	private static final String ANNOTATION_INFO = "Annotation";
	private String imgPathString;
	private UserInfo pUser;
	private ImageMeta pImageData;
	private List<AnnotationInfo> pAnnotationInfoList;
	private final Context context;

	public parseSendData(Context context) {
		this.context = context;
		pUser = new UserInfo();
		pImageData = new ImageMeta();
		pAnnotationInfoList = new ArrayList<AnnotationInfo>();
	}

	public void addUsr(String emailString) {
		pUser.setUserEmail(emailString);
	}

	public void setImageData(String mcurrentSavingPath) {
		this.imgPathString = mcurrentSavingPath;
		pImageData.setMetaData(mcurrentSavingPath);

	}

	public void setOfflineSign() {
		pImageData.setOfflineTag();
	}

	public void addAnnoatation(AnnotationInfo ai) {
		pAnnotationInfoList.add(ai);
	}

	// add function --read from database

	public boolean sendOnLine() {

		ParseObject usrParseObject = pUser.sendtoParse();
		ParseObject imageParseObject = pImageData.sendtoParse(usrParseObject);

		int brokenAt = sendAnnotationInfoList(usrParseObject, imageParseObject);
		if (brokenAt < pAnnotationInfoList.size()) {

			return false;
		} else {
			return true;
		}
	}

	private int sendAnnotationInfoList(ParseObject usrParseObject,
			ParseObject imageParseObject) {

		for (int i = 0, length = pAnnotationInfoList.size(); i < length; i++) {

			if (!pAnnotationInfoList.get(i).sendtoParse(usrParseObject,
					imageParseObject)) {
				return i;
			}
		}
		return pAnnotationInfoList.size();
	}

	public void saveOffLine() {
		try {
			JSONObject userJsonObject = pUser.saveOffline();
			JSONObject imgJsonObject = pImageData.saveOffline();
			JSONArray annotationJsonArray = new JSONArray();
			for (int i = 0, length = pAnnotationInfoList.size(); i < length; i++) {
				annotationJsonArray.put(i, pAnnotationInfoList.get(i)
						.saveOffline());
			}
			JSONObject fullInfoJsonObject = new JSONObject();
			fullInfoJsonObject.putOpt("usr", userJsonObject);
			fullInfoJsonObject.putOpt("imagemeta", imgJsonObject);
			fullInfoJsonObject.put("anno", annotationJsonArray);

			saveNewJsonObject(fullInfoJsonObject);
		} catch (Exception e) {
			Log.d("parseSendData", "saveOffline error!");
		}

	}

	private void saveNewJsonObject(JSONObject fullInfoJsonObject) {
		// TODO Auto-generated method stub
		try {

			JSONArray storedJsonArray;
			String temp = FileOperation.read(this.context, OFFLINEFILE);
			if (temp == null) {
				storedJsonArray = new JSONArray();
			} else {

				storedJsonArray = new JSONArray(temp);

			}

			storedJsonArray.put(fullInfoJsonObject);
			// Using Thread?
			FileOperation.save(this.context, OFFLINEFILE,
					storedJsonArray.toString());

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
