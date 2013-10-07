package edu.cmu.carannotationv2;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseObject;

public class parseSendData {

	private static final String USER = "User";
	private static final String IMAGE_INFO = "ImageData";
	private static final String ANNOTATION_INFO = "Annotation";
	private String imgPathString;
	private UserInfo pUser;
	private ImageMeta pImageData;
	private List<AnnotationInfo> pAnnotationInfoList;

	public parseSendData() {
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
public void addAnnoatation(AnnotationInfo ai){
	pAnnotationInfoList.add(ai);
}

//add function --read from database

public boolean sendOnLine() {
	// 1 send usr
	// 2. send image data
	// 3. send annotation data
	ParseObject usrParseObject=pUser.sendtoParse();
	ParseObject imageParseObject=pImageData.sendtoParse(usrParseObject);
	
	
	
	return sendAnnotationInfoList(usrParseObject,imageParseObject);
}

private boolean sendAnnotationInfoList(ParseObject usrParseObject, ParseObject imageParseObject) {
	
    for (int i = 0 ,  length=pAnnotationInfoList.size(); i < length; i++) {
		
	}
	return false;
}
	
	
}
