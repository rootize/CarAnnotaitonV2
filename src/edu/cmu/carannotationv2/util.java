package edu.cmu.carannotationv2;

import java.util.Calendar;

 public class util {
public static String setFileNamebyDate(){
	Calendar c=Calendar.getInstance();
	
	return set2Digits(c.get(Calendar.YEAR))
	+ set2Digits( c.get(Calendar.MONTH))
	+ set2Digits(c.get(Calendar.DAY_OF_MONTH))
	+ set2Digits( c.get(Calendar.HOUR_OF_DAY))
	+ set2Digits( c.get(Calendar.MINUTE));
}
public static String set2Digits(int x){
	
	return String.format("%02d", x);
}
}
