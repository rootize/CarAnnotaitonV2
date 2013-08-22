package edu.cmu.carannotationv2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.callback.Callback;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Sign_up extends Activity {
	private EditText signup_email_EditText;
	private EditText signup_nickname_EditText;
	private Button signup_signup_btn;
	private String email_from_signin;
	private boolean withEmail;

	private final static String PASSWORD = "1111";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_data);

		// Receive intent from the sign in part
		// actually the two parts are the same!
		signup_email_EditText = (EditText) findViewById(R.id.signup_email_edittext);

		signup_nickname_EditText = (EditText) findViewById(R.id.signup_nickname_edittext);
		signup_signup_btn = (Button) findViewById(R.id.signup_signip_btn);

		Intent receiver = getIntent();

		withEmail = receiver.getBooleanExtra("withEmail", false);

		if (withEmail) {
			email_from_signin = receiver.getStringExtra("usrEmail");
			signup_email_EditText.setText(email_from_signin);
			// signup_nickname_EditText.setFocusable(true);
			signup_nickname_EditText.requestFocus();
		} else {
			email_from_signin = "";
			// signup_email_EditText.setFocusable(true);
			signup_email_EditText.requestFocus();
		}

		// currently wifi is not available , please login as annonymous
		WifiManager wifi_connect = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if (wifi_connect.isWifiEnabled()) {
			LoadButtonClickListenr();
		} else {
			String showMessage = "Sorry, but current wifi is not avaliable, you could either chooes previsouly used account or login as anonymous";
			showToast(showMessage, R.drawable.error);

		}

	}

	private void LoadButtonClickListenr() {
		// TODO Auto-generated method stub
		signup_signup_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (signup_email_EditText.getText().toString().equals("")
						&& signup_nickname_EditText.getText().toString()
								.equals("")) {

					String showMessage = "Please type in your email address and nick name";
					showToast(showMessage, R.drawable.error);

				}

				else if ((signup_email_EditText.getText().toString().equals(""))) {
					String showMessage = "Email address can NOT be empty";
					showToast(showMessage, R.drawable.error);

				} else if ((signup_nickname_EditText.getText().toString()
						.equals(""))) {

					String showMessage = "Please give yourself a nick name!";
					showToast(showMessage, R.drawable.error);
				} else if (!isEmailValid(signup_email_EditText.getText()
						.toString())) {
					String showMessage = "Invalid email address, please check";
					signup_email_EditText.setText("");
				} else {
					// check if the current email address is registered or not!

					int reusltflage = queryCredentials(signup_email_EditText
							.getText().toString());
					String showMessage = null;
					switch (reusltflage) {
					case 2:
						showMessage = "A problem occured during registring : (";
						showToast(showMessage, R.drawable.error);
						break;

					case 1:

						// Is that ok to show dialog?
						// yes, use this email to login
						// no, come back to sign up page
						showMessage = "Emailed is already used!";
						showToast(showMessage, R.drawable.error);
						break;

					case 0:
						ParseObject usr = new ParseObject("usrAccount");
						// here store email as unique id
						// store nick-name as "optional " information
						
						
						usr.put("email", signup_email_EditText.getText()
								.toString());
                         Log.d("sign_up.java", signup_email_EditText.getText()
 								.toString());
						usr.put("nick-name", signup_nickname_EditText.getText()
								.toString());

						usr.saveInBackground(new SaveCallback() {

							@Override
							public void done(ParseException arg0) {
								// TODO Auto-generated method stub
								if (arg0 == null) {
									String showMessage = "Signed up successfully!";
									

									showToast(showMessage, R.drawable.success);
								}else {
									Log.d("sign_up.java", " error code="+arg0.getCode());
								}
							}
						});
						// usr.saveInBackground(new () {
						//
						// @Override
						// public void done(ParseException arg0) {
						// // TODO Auto-generated method stub
						// String showMessage="Signed up successfully!";
						//
						// showToast(showMessage, R.drawable.success);
						//
						// }
						//
						// });

						Intent toMainActivity = new Intent(Sign_up.this,
								Main_screen.class);
						toMainActivity.putExtra("usrEmail",
								signup_email_EditText.getText().toString());
						toMainActivity.putExtra("nick-name",
								signup_nickname_EditText.getText().toString());
						startActivity(toMainActivity);

						break;
					default:

						break;
					}

				}

			}
		});

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	private int queryCredentials(String username) {
		ParseQuery<ParseUser> queryuserlist = ParseUser.getQuery();
		queryuserlist.whereEqualTo("username", username);
		try {
			// attempt to find a user with the specified credentials.
			return (queryuserlist.count() != 0) ? 1 : 0;
		} catch (ParseException e) {
			return 2;
		}
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

	/*
	 * private void showDialog(String showString) {
	 * 
	 * AlertDialog.Builder wifi_perference = new AlertDialog.Builder(
	 * sign_up.this); wifi_perference.setTitle("Warning");
	 * wifi_perference.setMessage(showString);
	 * wifi_perference.setIcon(R.drawable.caution);
	 * 
	 * wifi_perference.setPositiveButton("OK", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { Intent
	 * toSignUp = new Intent(Login.this, sign_up.class);
	 * toSignUp.putExtra("usrEmail", emailEditText.getText() .toString());
	 * toSignUp.putExtra("withEmail", true); startActivity(toSignUp);
	 * 
	 * } });
	 * 
	 * wifi_perference.setNegativeButton("Retry", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub emailEditText.setText("");
	 * emailEditText.setEnabled(true); } });
	 * wifi_perference.setCancelable(true); AlertDialog wifi_dialog =
	 * wifi_perference.create(); wifi_dialog.show();
	 * 
	 * }
	 */

}
