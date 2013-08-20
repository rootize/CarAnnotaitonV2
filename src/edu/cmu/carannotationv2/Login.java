package edu.cmu.carannotationv2;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;

import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;

public class Login extends Activity {

	private Button tkphotoButton;
	private Button signup_btn;
	private Button getaccount_btn;
	private String usrname;
	private String usrpass;
	// private RadioButton loginButton;
	// private RadioButton visitorButton;
	private EditText usrnameEditText;
	private EditText passwordEditText;
	private RadioGroup loginchoiceRadioGroup;

	private ParseUser puser;

	// private Toast showToast;
	// private RadioButton selectedRadioButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// ***************initialize parse****************//
		Parse.initialize(this, "hR5F7PLUvr2vkKTo8gfEQRKXgOqdvc6kehlYJREq",
				"b0Fks95H8U5pE62QPWTUipzZaiRyp8iZxqCSdey0");
		// *************************************************//
		this.tkphotoButton = (Button) findViewById(R.id.take_photo_initial);
		// this.loginButton = (RadioButton) findViewById(R.id.login);
		// this.visitorButton = (RadioButton) findViewById(R.id.anno);
		this.usrnameEditText = (EditText) findViewById(R.id.facebook_account);
		this.passwordEditText = (EditText) findViewById(R.id.facebook_password);

		this.loginchoiceRadioGroup = (RadioGroup) findViewById(R.id.loginorvisitor);
		// this.selectedRadioButton=(RadioButton)findViewById(R.id.anno);
		// this.selectedRadioButton = null;
		// usrnameEditText.setFocusable(false);//if this edittext is gray?
		usrnameEditText.setEnabled(false);
		passwordEditText.setEnabled(false);
		addLisntersOnButton();

		
		
//		signup_btn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
////				Intent signupIntent=new Intent(Login.this,sign_up.class);
////				startActivity(signupIntent);
//				
//			}
//		});
	}

	private void addLisntersOnButton() {
		// TODO Auto-generated method stub

		loginchoiceRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						if (R.id.login == checkedId) {
							usrnameEditText.setEnabled(true);
							passwordEditText.setEnabled(true);
							signup_btn.setEnabled(true);
							getaccount_btn.setEnabled(true);

						} else {
							usrnameEditText.setEnabled(false);
							passwordEditText.setEnabled(false);
							signup_btn.setEnabled(false);
							getaccount_btn.setEnabled(false);
						}
					}
				});

		tkphotoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int selectedId = loginchoiceRadioGroup
						.getCheckedRadioButtonId();
				// selectedRadioButton=(RadioButton)findViewById(selectedId);
				if (selectedId == R.id.login) {
					// 1 do password thing!
					// 1.1 password declined!
					// 1.2 Password accepted!
					ParseUser.logInInBackground(usrnameEditText.getText()
							.toString(), passwordEditText.getText().toString(),
							new LogInCallback() {

								@Override
								public void done(ParseUser arg0,
										ParseException arg1) {
									// TODO Auto-generated method stub

									if (arg0 != null) {
										// login successfully!
//										Intent toMain = new Intent(Login.this, Main_screen.class);
//										startActivity(toMain);
									} else {
										if (arg1.getCode() == ParseException.CONNECTION_FAILED) {

											// wifi not working!
											String showMessage = "Wifi not working now, system will update your information later!";
											showToast(showMessage,
													R.drawable.error);

										} else {
											if (!queryCredentials(usrnameEditText
													.getText().toString())) {
												// usr doesn't exist!
												// clear all edit text
												String showMessage = "Invalid password/usrname!";
												showToast(showMessage,
														R.drawable.error);
											}

										}

									}
								}

							});

					// Also show the process dialog

				} else {
					// without login
				}

				Intent toMain = new Intent(Login.this, Main_screen.class);
				startActivity(toMain);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private boolean queryCredentials(String username) {
		ParseQuery<ParseUser> queryuserlist = ParseUser.getQuery();
		queryuserlist.whereEqualTo("username", username);
		try {
			// attempt to find a user with the specified credentials.
			return (queryuserlist.count() != 0) ? true : false;
		} catch (ParseException e) {
			return false;
		}
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

}
