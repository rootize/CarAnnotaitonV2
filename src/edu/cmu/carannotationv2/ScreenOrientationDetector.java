package edu.cmu.carannotationv2;

import android.content.Context;
import android.provider.Settings;

public class ScreenOrientationDetector {
	Context context;

	public ScreenOrientationDetector(Context context) {
		this.context = context;
	}

	public boolean isLocked() {
		if (android.provider.Settings.System.getInt(
				context.getContentResolver(),
				Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean unLockScreen() {
		StaticGlobalFunctions.setAutoOrientationEnabled(
				context.getContentResolver(), true);
		return true;

	}

	public boolean LockScreen() {
		StaticGlobalFunctions.setAutoOrientationEnabled(
				context.getContentResolver(), false);
		return false;
	}
}
