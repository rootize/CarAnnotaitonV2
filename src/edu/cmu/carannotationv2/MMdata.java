package edu.cmu.carannotationv2;

import java.io.File;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class MMdata {
	private Context context;
private JSONArray makeJsonArray;
private JSONArray modelJsonArray;
 public MMdata(Context context,int makefileid,int modelfileid) { 
	 this.context=context;
	 try {
		 makeJsonArray=getJsonArrayfromRaw(makefileid);
		 modelJsonArray=getJsonArrayfromRaw(modelfileid);   
	} catch (Exception e) {
		// TODO: handle exception
	}
	 
		
		
	
}
 private JSONArray getJsonArrayfromRaw(int id) throws Exception{
	 InputStream stream = context.getResources().openRawResource(id);
	 String readString=FileOperation.convertStreamToString(stream);
	 JSONObject tempObject=new JSONObject(readString);
	 stream.close();
	 return turnObject2Array(tempObject);
 }
 
 private JSONArray turnObject2Array(JSONObject input) throws Exception{
	 String temp=input.getString("results");
	 return new JSONArray(temp);
	 
 }
 public MMdata(){
	 
	 //Do nothing
 }
 
// public boolean readMakeJsonFile() throws Exception{
//	 String makeJsonString=FileOperation.read(context, makeJsonFile.getAbsolutePath());
//	 if (makeJsonString!=null) {
//		 JSONObject tempObject=new JSONObject(makeJsonString);
//		 String arrayString=tempObject.getString("results");
//		 makeJsonArray=new JSONArray(arrayString);
//		return true;
//	}else {
//		return false;
//	}
// }
//	
// public boolean readModelJsonFile() throws Exception {
//	 String modelJsonString=FileOperation.read(context, modelJsonFile.getAbsolutePath());
//	 if (modelJsonString!=null) {
//		 JSONObject tempObject=new JSONObject(modelJsonString);
//		 String arrayString=tempObject.getString("results");
//		 modelJsonArray=new JSONArray(arrayString);
//		return true;
//	}else {
//		return false;
//	}
//}
}
