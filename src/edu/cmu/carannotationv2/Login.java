package edu.cmu.carannotationv2;


import com.parse.Parse;
import com.parse.ParseAnalytics;


import android.os.Bundle;
import android.provider.Settings;

import android.R.integer;
import android.app.Activity;

import android.content.Context;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;



public class Login extends Activity {
	
	
	public  static final String USR_TOMAIN_INTENT="usrname";
	
	
	
	
	private static final String ANONYMOUS_USR="anonymous_root";
	
// private CheckBox anonCheckBox;
	private TextView anonTextView;
	private Button login_login_btn; // Login button
//	private Button login_anonymous_btn; // Anonymous access button
//	private Button ReadMe_btn; // btn for more information
	private EditText emailEditText; // edit text for input email
	private String email; // email string
    private boolean isAnonLogin=false;
	private SharedPreferences sp; // remember the account information

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		
		
		
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		//this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		setContentView(R.layout.activity_login);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		sp = this.getSharedPreferences("usrInfo", Context.MODE_PRIVATE);

		// ***************initialize parse****************//
		Parse.initialize(this, "hR5F7PLUvr2vkKTo8gfEQRKXgOqdvc6kehlYJREq",
				"b0Fks95H8U5pE62QPWTUipzZaiRyp8iZxqCSdey0");
		ParseAnalytics.trackAppOpened(getIntent());
		// *************************************************//

		
		this.emailEditText = (EditText) findViewById(R.id.login_account);
		

		emailEditText.setText(sp.getString("usrEmail", ""));

		emailEditText.setEnabled(true);
		emailEditText.requestFocus();
		addLisnterOnButton_login();
		addLisnterOnCheckBox();
		

	}
	
	
	//set on screen orientation

// Listener on  button
	
	private void addLisnterOnCheckBox() {
		anonTextView=(TextView)findViewById(R.id.login_anonymous_checkbox);
		
//		if (android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.JELLY_BEAN) {
//			final float scale = this.getResources().getDisplayMetrics().density;
//			anonCheckBox.setPadding(anonCheckBox.getPaddingLeft() + (int)(10.0f * scale + 25.0f),
//					anonCheckBox.getPaddingTop(),
//					anonCheckBox.getPaddingRight(),
//					anonCheckBox.getPaddingBottom());
//		}
//Todo: First of all check the version, then use the code below to ensure everything

		
		
//		anonCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					isAnonLogin=true;
//					emailEditText.setEnabled(false);
//				}else {
//					isAnonLogin=false;
//					emailEditText.setEnabled(true);
//				}
//				
//			}
//		});
		anonTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int textColor=anonTextView.getCurrentTextColor();
				if (textColor==android.graphics.Color.WHITE) {
					anonTextView.setText("Proceed as Anonymous User");
					anonTextView.setTextColor(android.graphics.Color.RED);
					isAnonLogin=true;
					emailEditText.setEnabled(false);	
				}
				if (textColor==android.graphics.Color.RED) {
					anonTextView.setTextColor(android.graphics.Color.WHITE);
					anonTextView.setText(getResources().getString(R.string.login_anonymous_string_onchkbx));
					isAnonLogin=false;
					emailEditText.setEnabled(true);
				}
				
			}
		});
		
	}

	private void addLisnterOnButton_login() {

		login_login_btn=(Button)findViewById(R.id.login_btn);
		login_login_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
				if (isAnonLogin) {
					anonTextView.setText("     Logging in ...     ");
					dispathIntenttoMainScreen(ANONYMOUS_USR);
				}else {
					email = emailEditText.getText().toString();
					if (static_global_functions.isEmailValid(email)) {
						anonTextView.setText("     Logging in ...     ");
						String showMessage = "Logged in successfully!";
						static_global_functions.ShowToast_short(
								getApplicationContext(), showMessage,
								R.drawable.success);
						// Remember me when changes made 
						if (!sp.getString("usrEmail", "").equals(
								email)) {
							Editor editor = sp.edit();
							editor.putString("usrEmail", email);
							editor.commit();
						}
						
						dispathIntenttoMainScreen(email);
					} else {
						emailEditText.setText("");
						emailEditText.requestFocus();
						String showMessage = "Please input valid email address!";
						static_global_functions.ShowToast_short(getApplicationContext(), showMessage, R.drawable.caution);
					}

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

	
	
	private  void dispathIntenttoMainScreen(String e){
		
		Intent toMain = new Intent(Login.this, Main_screen.class);
		toMain.putExtra(USR_TOMAIN_INTENT, e);
		startActivity(toMain);
	 //   
		finish();
	}
//	private void addLisntersOnButton_ReadMe() {
//		// TODO Auto-generated method stub
//
//		ReadMe_btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent toReadMeActivity = new Intent(Login.this, readme.class);
//				startActivity(toReadMeActivity);
//			}
//		});
//
//	}
//// Listener on anonymous button
//	private void addLisntersOnButton_anony() {
//		// TODO Auto-generated method stub
//		login_anonymous_btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				String anonymoususrName = "anonymous_root";
//				Intent toMainActivity = new Intent(Login.this, Main_screen.class);
//				toMainActivity.putExtra("usr", anonymoususrName);
//				startActivity(toMainActivity);
//				finish();
//
//			}
//		});
//	}


}
