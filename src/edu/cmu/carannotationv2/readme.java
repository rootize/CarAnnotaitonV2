package edu.cmu.carannotationv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class readme extends Activity {
//private TextView multilineTextView;
private Button   btn_ok;

/* (non-Javadoc)
 * @see android.app.Activity#onCreate(android.os.Bundle)
 */
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.readme_layout);
	
	//multilineTextView=(TextView)findViewById(R.id.audioTextView);
	btn_ok=(Button)findViewById(R.id.btn_readme_OK);
	btn_ok.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent backToLogin =new Intent(readme.this,Login.class);
			startActivity(backToLogin);
		}
	});
	
}


	
}
