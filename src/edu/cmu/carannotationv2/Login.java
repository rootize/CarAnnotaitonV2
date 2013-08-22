package edu.cmu.carannotationv2;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.CountCallback;
import com.parse.CountCallback;
//import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;

//import edu.cmu.cma.MainActivity;
//import edu.cmu.cma.R;

public class Login extends Activity {

	

	
	private Button login_login_btn;
	private Button login_anonymous_btn;
	private Button ReadMe_btn;
	
	private TextView whyEmail;
	private EditText emailEditText;
	private String email;
	private ParseUser puser;
    private boolean connected_flag;
	
    private boolean check_one_out=false;
	private SharedPreferences sp;// remember the account infomation
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
      
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		sp=this.getSharedPreferences("usrInfo", Context.MODE_PRIVATE);
		
		// ***************initialize parse****************//
		Parse.initialize(this, "hR5F7PLUvr2vkKTo8gfEQRKXgOqdvc6kehlYJREq",
				"b0Fks95H8U5pE62QPWTUipzZaiRyp8iZxqCSdey0");
		// ParseFacebookUtils.initialize("622959081070950");
		// *************************************************//
		
		
		
		this.login_login_btn = (Button) findViewById(R.id.login_login_btn);
		this.login_anonymous_btn = (Button) findViewById(R.id.login_anonymous_btn);
		this.emailEditText = (EditText) findViewById(R.id.login_account);
		//this.whyEmail = (TextView) findViewById(R.id.signin_whyEmail_textview);

		// *******Maybe Comment Later!****//
		this.ReadMe_btn = (Button) findViewById(R.id.readme_btn);
		//this.singup_btn = (Button) findViewById(R.id.login_signup_btn);
		// **********************************************//

		if (!sp.getString("usrEmail", "").equals("")) {
//			Intent toMain=new Intent(Login.this,Main_screen.class);
//			toMain.putExtra("usr", sp.getString("usrEmail", ""));
			emailEditText.setText(sp.getString("usrEmail", ""));
		}
		
		
		
		
		emailEditText.setEnabled(true);
		emailEditText.requestFocus();
		addLisntersOnButton_login();
		//addLisntersOnButton_signup_btn();
		addLisntersOnButton_anony();
		
	}

	
	private void addLisntersOnButton_anony() {
		// TODO Auto-generated method stub
		login_anonymous_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String anonymoususrName="anonymous_root";
				Intent toMain=new Intent(Login.this,Main_screen.class);
				toMain.putExtra("usr", anonymoususrName);
				startActivity(toMain);
				
				
			}
		});
	}


	private void addLisntersOnButton_login() {
		// TODO Auto-generated method stub

		login_login_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
             
				email=emailEditText.getText().toString();
				if (isEmailValid(email)) {
					String showMessage = "Loged in";
					showToast(showMessage, R.drawable.success);

					//remember me!
					if (!sp.getString("usrEmail", "").equals(emailEditText.getText().toString())) {
						Editor editor=sp.edit();
						editor.putString("usrEmail", email);
	                    editor.commit();
					}
														
					Intent toMain = new Intent(Login.this,
							Main_screen.class);
					toMain.putExtra("usr", email);
				

					startActivity(toMain);}
					else {
						emailEditText.setText("");
						String showMessage="Please input valid email address";
						showToast(showMessage, R.drawable.caution);
					}
					
				}
		});
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	
	private void showToast(String show_String, int icon) {
		// TODO Auto-generated method stub
		Toast showToast = Toast.makeText(getApplicationContext(), show_String,
				Toast.LENGTH_SHORT);
		showToast.setGravity(Gravity.CENTER, 0, 0);
		LinearLayout toastView = (LinearLayout) showToast.getView();
		ImageView imgToast = new ImageView(getApplicationContext());
		imgToast.setImageResource(icon);
		toastView.addView(imgToast, 0);
		showToast.show();
	}

	
	
	private static boolean isEmailValid(String email) {
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
