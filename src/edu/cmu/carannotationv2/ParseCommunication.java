package edu.cmu.carannotationv2;

import android.content.Context;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ParseCommunication {
	EncryptedData ed;
	private Context context;
	private boolean flag_isLoggedin;
	
	public ParseCommunication(Context context, EncryptedData ed){
		this.context=context;
		this.ed=ed;
		

	}
	
	public boolean isLoggedin(){
		return flag_isLoggedin;
	}
	public boolean loggingIn() {
		ParseUser.logInInBackground(ed.getCipherTextUserName(),
				ed.getCipherTextUserPassword(), new LogInCallback() {

			@Override
			public void done(ParseUser usr, ParseException e) {
				if (usr != null) {
					Log.d("Login_before", "Successfully");
					flag_isLoggedin = true;

				} else {
					Log.d("error logging in ", e.toString());
					flag_isLoggedin = false;
					//					GlobalFuns
					//					.ShowToast_short(
					//							context,
					//							"Remote server not responding, save to local memory",
					//							R.drawable.caution);
				}

			}
		});
		return flag_isLoggedin;
	}
}
