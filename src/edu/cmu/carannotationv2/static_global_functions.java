package edu.cmu.carannotationv2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class static_global_functions {

	
	public static void ShowToast_short(Context context, String showString,int icon){
		
		
			Toast showToast = Toast.makeText(context, showString,
					Toast.LENGTH_SHORT);
			showToast.setGravity(Gravity.BOTTOM, 0, 0);
			LinearLayout toastView = (LinearLayout) showToast.getView();
			ImageView imgToast = new ImageView(context);
			imgToast.setImageResource(icon);
			toastView.addView(imgToast, 0);
			showToast.show();
		

	}
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	
}
