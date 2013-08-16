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

public class Login extends Activity {
	

	private Button tkphotoButton;
	// private RadioButton loginButton;
	// private RadioButton visitorButton;
	private EditText usrnameEditText;
	private EditText passwordEditText;
	private RadioGroup loginchoiceRadioGroup;

	
	private Toast showToast;
	// private RadioButton selectedRadioButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
						} else {
							usrnameEditText.setEnabled(false);
							passwordEditText.setEnabled(false);
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
                     //1.1 password declined!
					// 1.2 Password accepted!
				showToast=	Toast.makeText(getApplicationContext(),
							"Log in Successfully!", Toast.LENGTH_SHORT);
                 showToast.setGravity(Gravity.CENTER, 0, 0);
                 LinearLayout toastView=(LinearLayout)showToast.getView();
                 ImageView imgToast=new ImageView(getApplicationContext());
                 imgToast.setImageResource(R.drawable.success);
                 toastView.addView(imgToast,0);
                 showToast.show();
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



}
