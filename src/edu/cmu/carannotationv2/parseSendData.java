package edu.cmu.carannotationv2;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseObject;

public class parseSendData {
 
	
	private static final String USER="User";
	private static final String IMAGE_INFO="ImageData";
	private static final String ANNOTATION_INFO="Annotation";
	private String imgPathString;
    private  UserInfo pUser;
    private  ImageMeta pImageData;
    private List<AnnotationInfo> pAnnotationInfoList;


public parseSendData(){
	pUser=new UserInfo();
	pImageData=new ImageMeta();
	pAnnotationInfoList=new ArrayList<AnnotationInfo>();
}



public void addUsr(String emailString){
	pUser.setUserEmail(emailString);
}
public void addImageData(String mcurrentSavingPath){
	this.imgPathString=mcurrentSavingPath;
	pImageData.setMetaData(mcurrentSavingPath);
	
}

}
