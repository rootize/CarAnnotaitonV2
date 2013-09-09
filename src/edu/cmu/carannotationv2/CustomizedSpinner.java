package edu.cmu.carannotationv2;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Spinner;

public class CustomizedSpinner extends Spinner {

public CustomizedSpinner(Context context) {
    super(context);
    
}

public CustomizedSpinner(Context context, AttributeSet attrs){
	super(context,attrs);
}

public CustomizedSpinner(Context context, AttributeSet attrs, int defStyle)
{ super(context, attrs, defStyle); }

@Override
public void setSelection(int position, boolean animate) {
    ignoreOldSelectionByReflection();
    super.setSelection(position, animate);
}

private void ignoreOldSelectionByReflection() {
    try {
        Class<?> c = this.getClass().getSuperclass().getSuperclass().getSuperclass();
        Field reqField = c.getDeclaredField("mOldSelectedPosition");
        reqField.setAccessible(true);
        reqField.setInt(this, -1);
    } catch (Exception e) {
        Log.d("Exception Private", "ex", e);
        // TODO: handle exception
    }
}

@Override
public void setSelection(int position) {
    ignoreOldSelectionByReflection();
    super.setSelection(position);
}

}