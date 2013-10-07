package edu.cmu.carannotationv2;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class UserInfo {
private static final String POBJECTNAME="User";
private static final String PEmail="emailId";
private String userEmailString;
private int sendState;
private ParseObject fetchedUser;
public UserInfo(){
	
}
public UserInfo(String emailString){
this.userEmailString=emailString;
}

public void setUserEmail(String emailString ){
	this.userEmailString=emailString;
}
public ParseObject sendtoParse() {
	// TODO Auto-generated method stub
	ParseObject pObject=new ParseObject(POBJECTNAME);
	pObject.put(PEmail, userEmailString);
	pObject.saveInBackground(new SaveCallback() {
		
		@Override
		public void done(ParseException arg0) {
			if (arg0==null) {
				sendState=1;
			}else {
				if (arg0.equals("error log")) {
					//get data from server
					sendState=2;
				}else {
					sendState=3;
				}
			}
		}
	});
	
	switch (sendState) {
	case 1:
		return pObject;
		
	case 2:
		return fetchedUser;
	case 3: 
	default:
	     return null;
	}
}





}
