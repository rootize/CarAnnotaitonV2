package edu.cmu.carannotationv2;


import com.parse.Parse;
import com.parse.ParseAnalytics;


import android.os.Bundle;

import android.app.Activity;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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



public class Login extends Activity {
	private static final String ANONYMOUS_USR="anonymous_root";
	
private CheckBox anonCheckBox;
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
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	
// Listener on  button
	
	private void addLisnterOnCheckBox() {
		anonCheckBox=(CheckBox)findViewById(R.id.login_anonymous_checkbox);
		
		
//Todo: First of all check the version, then use the code below to ensure everything
//		final float scale = this.getResources().getDisplayMetrics().density;
//		anonCheckBox.setPadding(anonCheckBox.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
//				anonCheckBox.getPaddingTop(),
//				anonCheckBox.getPaddingRight(),
//				anonCheckBox.getPaddingBottom());
		
		
		anonCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isAnonLogin=true;
					emailEditText.setEnabled(false);
				}else {
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
					dispathIntenttoMainScreen(ANONYMOUS_USR);
				}else {
					email = emailEditText.getText().toString();
					if (static_global_functions.isEmailValid(email)) {
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
		toMain.putExtra("usr", e);
		startActivity(toMain);
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
