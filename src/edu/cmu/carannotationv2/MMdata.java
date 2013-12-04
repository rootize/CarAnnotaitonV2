package edu.cmu.carannotationv2;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

//import org.apache.commons.collections.MultiMap;
//import org.apache.commons.collections.map.MultiValueMap;

public class MMdata {
	private Context context;
private JSONArray makeJsonArray;
private JSONArray modelJsonArray;
public List<String> makenameGroup;
public HashMap<String, String> makeHashMap; //<Makename, MakeObjectid >
public HashMap<String,  String>modelHashMap;// <MakeObjectId+ModelName,modelObjectID>
public HashMap<String, List<String>>modelDisplayHashMap;
//private HashMap<String, HashMap<String, String>> modelHashMap;//<makeobjectid,<modelname,modelobjectid>>
 public MMdata(Context context,int makefileid,int modelfileid) { 
	 this.context=context;
	 try {
		 makeJsonArray=getJsonArrayfromRaw(makefileid);
		 modelJsonArray=getJsonArrayfromRaw(modelfileid);   
	} catch (Exception e) {
		// TODO: handle exception
	}

}
 
 public HashMap<String, List<String>> getModelDisplayHashmap() {
	 modelDisplayHashMap=new HashMap<String, List<String>>();
	 if (modelJsonArray!=null) {
		 for (int i = 0; i < modelJsonArray.length(); i++) {
			 try {
				JSONObject singgleObject=modelJsonArray.getJSONObject(i);
				String keyString=singgleObject.getJSONObject("Make").getString("objectId");
				List<String> modelNameList=new ArrayList<String>();
				if (modelDisplayHashMap.get(keyString)==null) {
					
					modelNameList.add(singgleObject.getString("name"));
					modelDisplayHashMap.put(keyString, modelNameList);
				}else {
					modelNameList=modelDisplayHashMap.get(keyString);
					modelNameList.add(singgleObject.getString("name"));
					modelDisplayHashMap.remove(keyString);
					modelDisplayHashMap.put(keyString, modelNameList);
				}
				 
				 
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
		 return modelDisplayHashMap;
		
	}else {
		return null;
	}
	
}
 public List<String> getMakeGroup(){
	 if (makenameGroup==null) {
		getMakeHashMap();
		
	}
	 return makenameGroup;
 }
 public HashMap<String, String> getMakeHashMap(){
	 makenameGroup=new ArrayList<String>();
	 makeHashMap=new HashMap<String, String>();
	 if (makeJsonArray!=null) {
		for (int i = 0; i < makeJsonArray.length(); i++) {
			try {
				JSONObject singleObject=makeJsonArray.getJSONObject(i);
				makeHashMap.put(singleObject.getString("name"), singleObject.getString("objectId"));
				makenameGroup.add(singleObject.getString("name"));
			} catch (Exception e) {
				// TODO: handle exception
			}	
		}
		return makeHashMap;
	}else {
		return null;
	}
 }
 
 public HashMap<String,  String> getModelHashMap() {
	 modelHashMap=new HashMap<String, String>();
	 if (modelJsonArray!=null) {
		 for (int j = 0; j < modelJsonArray.length(); j++) {
			 try {
					JSONObject singleObject=modelJsonArray.getJSONObject(j);
					modelHashMap.put(singleObject.getJSONObject("Make").getString("objectId")+singleObject.getString("name"), singleObject.getString("objectId"));
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
		return modelHashMap;
		 
	}else {
		return null;
	}
	
}
 
 
 
 private JSONArray getJsonArrayfromRaw(int id) throws Exception{
	 InputStream stream = context.getResources().openRawResource(id);
	 String readString=FileOperation.convertStreamToString(stream);
	 stream.close();
	 JSONObject tempObject=new JSONObject(readString);
	
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
