package edu.cmu.carannotationv2;

import android.app.Activity;
import android.content.Intent;

public class Main_screen extends Activity{
	private static final int CAMERA_REQUEST=1888;

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
