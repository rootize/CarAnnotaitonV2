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
private static final String loc_makefile="locmake";
private static final String loc_modelfile="locmodel";

 public MMdata(Context context,int makefileid,int modelfileid) { 
	 this.context=context;
	 try {
		 if (FileOperation.existInternalFile(context, loc_makefile) && FileOperation.existInternalFile(context, loc_modelfile)) {
			 makeJsonArray=getJsonArrayfromFile(loc_makefile);
			 modelJsonArray=getJsonArrayfromFile(loc_modelfile); 
			 
		}else{
			 makeJsonArray=getJsonArrayfromRaw(makefileid,loc_makefile);
			 modelJsonArray=getJsonArrayfromRaw(modelfileid,loc_modelfile);  
			 
		}
			 
		 
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
 
 
 
 private JSONArray getJsonArrayfromRaw(int id,String locFileName) throws Exception{
	 InputStream stream = context.getResources().openRawResource(id);
	 String readString=FileOperation.convertStreamToString(stream);
	 FileOperation.save(context, locFileName, readString);
	 stream.close();
	 JSONObject tempObject=new JSONObject(readString);
	
	 return turnObject2Array(tempObject);
 }
 private JSONArray getJsonArrayfromFile(String Filename)throws Exception{
	 String readString=FileOperation.read(context, Filename);
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
 public List<String> getSingleChildList(String makeName){
	 String makeId=makeHashMap.get(makeName);
	 return modelDisplayHashMap.get(makeId);
	 
 }
 
 public String getMakeId(String makeName){
	 return makeHashMap.get(makeName);
	 
 }
 public String getModelId(String modelHashKey){
	 
	 return modelHashMap.get(modelHashKey);
 }
 

}
