package edu.cmu.carannotationv2;

import android.os.Bundle;
import android.provider.MediaStore;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Login extends Activity {
    private static final int CAMERA_REQUEST=1888;
	
	private Button tkphotoButton;
//	private RadioButton loginButton;
//	private RadioButton visitorButton;
	private EditText usrnameEditText;
	private EditText passwordEditText;
	private RadioGroup loginchoiceRadioGroup;
//	private RadioButton selectedRadioButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		this.tkphotoButton = (Button) findViewById(R.id.take_photo_initial);
//		this.loginButton = (RadioButton) findViewById(R.id.login);
//		this.visitorButton = (RadioButton) findViewById(R.id.anno);
		this.usrnameEditText = (EditText) findViewById(R.id.facebook_account);
		this.passwordEditText = (EditText) findViewById(R.id.facebook_password);

		this.loginchoiceRadioGroup = (RadioGroup) findViewById(R.id.loginorvisitor);
		// this.selectedRadioButton=(RadioButton)findViewById(R.id.anno);
		//this.selectedRadioButton = null;
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
					// do password thing!

					// Also show the process dialog
				} else {
					// without login
				}
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, ImageFileContentProvider.CONTENT_URI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==CAMERA_REQUEST&&resultCode==RESULT_OK) {
			
		}
	}
   
	
}
