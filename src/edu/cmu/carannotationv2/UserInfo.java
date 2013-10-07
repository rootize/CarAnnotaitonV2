package edu.cmu.carannotationv2;

public class UserInfo {
private static final String POBJECTNAME="User";
private static final String PEmail="emailId";
private String userEmailString;
public UserInfo(){
	
}
public UserInfo(String emailString){
this.userEmailString=emailString;
}

public void setUserEmail(String emailString ){
	this.userEmailString=emailString;
}





}
