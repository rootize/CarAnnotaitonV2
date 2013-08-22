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
import android.widget.EditText;




// no parse email verification used, just get the email address as an unique id for each 
// usr
public class Login extends Activity {

	private Button login_login_btn; // Login button
	private Button login_anonymous_btn; // Anonymous access button
	private Button ReadMe_btn; // btn for more information
	private EditText emailEditText; // edit text for input email
	private String email; // email string

	private SharedPreferences sp; // remember the account information

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		sp = this.getSharedPreferences("usrInfo", Context.MODE_PRIVATE);

		// ***************initialize parse****************//
		Parse.initialize(this, "hR5F7PLUvr2vkKTo8gfEQRKXgOqdvc6kehlYJREq",
				"b0Fks95H8U5pE62QPWTUipzZaiRyp8iZxqCSdey0");
		ParseAnalytics.trackAppOpened(getIntent());
		// *************************************************//

		this.login_login_btn = (Button) findViewById(R.id.login_login_btn);
		this.login_anonymous_btn = (Button) findViewById(R.id.login_anonymous_btn);
		this.emailEditText = (EditText) findViewById(R.id.login_account);
		this.ReadMe_btn = (Button) findViewById(R.id.readme_btn);

		emailEditText.setText(sp.getString("usrEmail", ""));

		emailEditText.setEnabled(true);
		emailEditText.requestFocus();
		addLisntersOnButton_login();
		// addLisntersOnButton_signup_btn();
		addLisntersOnButton_anony();
		addLisntersOnButton_ReadMe();

	}
// Listener on question button
	private void addLisntersOnButton_ReadMe() {
		// TODO Auto-generated method stub

		ReadMe_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent toReadMeActivity = new Intent(Login.this, readme.class);
				startActivity(toReadMeActivity);
			}
		});

	}
// Listener on anonymous button
	private void addLisntersOnButton_anony() {
		// TODO Auto-generated method stub
		login_anonymous_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String anonymoususrName = "anonymous_root";
				Intent toMainActivity = new Intent(Login.this, Main_screen.class);
				toMainActivity.putExtra("usr", anonymoususrName);
				startActivity(toMainActivity);
				finish();

			}
		});
	}

	private void addLisntersOnButton_login() {

		login_login_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

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

					Intent toMain = new Intent(Login.this, Main_screen.class);
					toMain.putExtra("usr", email);
					startActivity(toMain);
					finish();
				} else {
					emailEditText.setText("");
					emailEditText.requestFocus();
					String showMessage = "Please input valid email address!";
					static_global_functions.ShowToast_short(getApplicationContext(), showMessage, R.drawable.caution);
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

	

}
