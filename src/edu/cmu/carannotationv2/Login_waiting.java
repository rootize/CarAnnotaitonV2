package edu.cmu.carannotationv2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class Login_waiting extends Activity {
private static final int TIMER_RUNTIME=5000;
	private ProgressBar mProgressBar;
    private boolean mActive;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_waiting);
		mProgressBar=(ProgressBar)findViewById(R.id.login_waiting_pgBar);
		
		final Thread timerThread = new Thread() {
	          @Override
	          public void run() {
	              mActive = true;
	              try {
	                  int waited = 0;
	                  while(mActive && (waited < TIMER_RUNTIME)) {
	                      sleep(200);
	                      if(mActive) {
	                          waited += 200;
	                          updateProgress(waited);
	                      }
	                  }
	          } catch(InterruptedException e) {
	              // do nothing
	          } finally {
	              onContinue();
	          }
	        }
	     };
	     timerThread.start();
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	   public void updateProgress(final int timePassed) {
	       if(null != mProgressBar) {
	           // Ignore rounding error here
	           final int progress = mProgressBar.getMax() * timePassed / TIMER_RUNTIME;
	           mProgressBar.setProgress(progress);
	       }
	   }

	public void onContinue() {
	     // perform any final actions here
	   }
	
	
	
}
